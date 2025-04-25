package com.magicrealms.magicchat.core.entity;

import com.magicrealms.magicchat.core.MagicChat;
import com.magicrealms.magicchat.core.channel.entity.AbstractChannel;
import com.magicrealms.magicchat.core.enums.BlockingState;
import com.magicrealms.magicchat.core.listener.SelectorListener;
import com.magicrealms.magicchat.core.message.entity.AbstractMessage;
import com.magicrealms.magicchat.core.message.entity.ExclusiveMessage;
import com.magicrealms.magicchat.core.message.entity.ChannelMessage;
import com.magicrealms.magicchat.core.message.entity.channel.AbstractToppingMessage;
import com.magicrealms.magicchat.core.message.entity.channel.ToppingAllMessage;
import com.magicrealms.magicchat.core.message.entity.channel.ToppingMessage;
import com.magicrealms.magicchat.core.message.entity.exclusive.SelectorMessage;
import com.magicrealms.magicchat.core.message.entity.exclusive.TypewriterMessage;
import com.magicrealms.magicchat.core.store.MessageHistoryStorage;
import com.magicrealms.magiclib.common.message.helper.TypewriterHelper;
import com.magicrealms.magiclib.common.utils.SerializationUtils;
import com.magicrealms.magiclib.paper.dispatcher.NMSDispatcher;
import it.unimi.dsi.fastutil.Pair;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.magicrealms.magicchat.core.MagicChatConstant.*;

/**
 * @author Ryan-0916
 * @Desc 成员类
 * @date 2025-04-03
 */
@Getter
@SuppressWarnings("unused")
public class Member {

    /* 聊天频道 */
    private @NotNull AbstractChannel channel;
    /* 成员编号 */
    private final UUID memberId;
    /* 成员名称 */
    private final String memberName;
    /* 阻塞状态 */
    private volatile BlockingState blockingState;
    /* 打印机所占用的线程 */
    private volatile BukkitTask typewriterTask;
    /* 选择器监听器 */
    private SelectorListener selectorListener;
    /* 消息撤回模式 */
    private boolean undoMessagesMode;

    public Member(Player player, @NotNull AbstractChannel channel) {
        this.memberId = player.getUniqueId();
        this.memberName = StringUtils.upperCase(player.getName());
        this.channel = channel;
        /* 初始化玩家频道 */
        this.channel.joinChannel(this);
    }

    /* 自动开关消息撤回模式 */
    public boolean toggleUndoMessagesMode() {
        this.undoMessagesMode = !this.undoMessagesMode;
        return this.undoMessagesMode;
    }

    private boolean isBlocking() {
        return blockingState != null;
    }

    public void startBlocking(BlockingState state) {
        this.blockingState = state;
    }

    public void stopBlocking() {
        this.blockingState = null;
        /* 移除 Task */
        if (typewriterTask != null
                && !typewriterTask.isCancelled()) {
            typewriterTask.cancel();
            typewriterTask = null;
        }
        if (selectorListener != null) {
            selectorListener.unregister();
            selectorListener = null;
        }
    }

    /**
     * 重置玩家的频道
     * 将其从当前频道移除并加入新的频道
     * @param channel 新的频道对象
     */
    public void resetChannel(@NotNull AbstractChannel channel) {
        this.channel.leaveChannel(this);
        channel.joinChannel(this);
        this.channel = channel;
    }

    /**
     * 给玩家发送私有消息
     * @param message 消息内容
     */
    public void sendMessage(ExclusiveMessage message) {
        /* 如果是打印机消息则单独处理 */
        if (message instanceof TypewriterMessage typewriterMessage) {
            sendTypewriterMessage(typewriterMessage);
            return;
        }
        addExclusiveMessageToMemberHistory(message);
        resetChatDialog();
    }

    /**
     * 将专属消息添加至玩家消息记录
     * @param message 消息内容
     */
    private void addExclusiveMessageToMemberHistory(ExclusiveMessage message) {
        message.setSentTime(System.currentTimeMillis());
        /* 将当前成员的私密消息添加到消息历史中 */
        MessageHistoryStorage.getInstance()
                .addMessageToMember(this, message);
        /* 同步聊天记录至 Redis 队列 */
        CompletableFuture.runAsync(() ->
                MagicChat.getInstance().getRedisStore().rSetValue(
                MessageFormat.format(MEMBER_MESSAGE_HISTORY, memberName),
                MAX_HISTORY_SIZE,
                SerializationUtils.serializeByBase64(message)
        ));
        /* 重置聊天对话状态，以便进行下一条消息 */
        resetChatDialog();
    }

    /**
     * 重置玩家的聊天框
     * 它将在获取频道中的聊天信息并与私人信息相结合
     * 重组成新的聊天框并发包给玩家
     */
    public void resetChatDialog() {
        if (isBlocking()) {
            if (blockingState == BlockingState.SELECTOR && selectorListener != null) {
                asyncResetChatDialog(selectorListener.getSelector().getContent());
            }
            return;
        }
        asyncResetChatDialog(null);
    }

    private void asyncResetChatDialog(@Nullable String appendContent) {
        CompletableFuture.supplyAsync(() -> {
            Player player = Bukkit.getPlayer(memberId);
            if (player == null || !player.isOnline()) {
                return null;
            }
            return Pair.of(player, getMessageHistory(player));
        }).thenAccept(pair -> {
            if (pair != null) {
                List<String> messages = new ArrayList<>(pair.right());
                Optional.ofNullable(appendContent).ifPresent(messages::add);
                NMSDispatcher.getInstance().resetChatDialog(pair.left(), messages);
            }
        });
    }

    /**
     * 获取玩家的历史消息记录
     * 他将获取玩家所在频道中的聊天记录并结合私人信息聊天记录
     * 重组成新的聊天记录
     * @return 新的聊天记录合集
     */
    private List<String> getMessageHistory(Player player) {
        /* 获取当前时间戳，用于查看消息是否在有效期内 */
        long currentTime = System.currentTimeMillis();

        /* 第一步合并消息流
         * 并过滤掉自己不可见的消息
         * 将频道内的聊天记录与私有聊天记录融合
         * 并按时间降序 （注意这里是时间降序 越早的消息排序越后）
         */
        List<AbstractMessage> allHistory = Stream.concat(
                        MessageHistoryStorage.getInstance().getChannelMessageHistory(this.channel).stream(),
                        MessageHistoryStorage.getInstance().getMemberMessageHistory(this).stream())
                .filter(m ->
                        /* 私有消息并且为自己可见 */
                        (m instanceof ExclusiveMessage exclusiveMessage)
                                /* 公共消息并且有权限可见 */
                                || (m instanceof ChannelMessage channelMessage && (player.isOp()
                                || StringUtils.isBlank(channelMessage.getPermissionNode())
                                || player.hasPermission(channelMessage.getPermissionNode())))
                )
                .sorted((m1, m2) -> Long.compare(m2.getSentTime(), m1.getSentTime()))
                .toList();

        /* 第二步寻找需要置顶消息
         * 按照消息过滤出最后发送并且满足有效期内的置顶消息
         */
        Optional<AbstractToppingMessage> toppingMessage = allHistory.stream()
                .filter(m -> m instanceof AbstractToppingMessage)
                .map(m -> (AbstractToppingMessage) m)
                .filter(topMessage ->
                        (topMessage instanceof ToppingAllMessage
                                || (topMessage instanceof ToppingMessage t && t.getTarget()
                                .contains(this.memberId)))
                                && currentTime < topMessage.getSentTime() + topMessage.getKeepTick() * 50)
                .min((t1, t2) -> Integer.compare(t2.getPriority(), t1.getPriority()));
        /* 第三步用户可见的100条聊天记录并按时间升序
         * 由于聊天记录的还原越早的发送的消息要放置越前
         * 因此该步骤需要按照时间升序 越早的消息排序越前
         */
        List<AbstractMessage> message = allHistory.stream().limit(MAX_HISTORY_SIZE)
                .sorted(Comparator.comparingLong(AbstractMessage::getSentTime))
                .collect(Collectors.toList());
        /* 第四步处理置顶消息 （如若存在）
         * 聊天记录中如若存在该置顶消息需要将该消息挪至列表末尾
         * 如果不存在则需要移除第一位消息，将置顶消息挪至末尾
         */
        toppingMessage.ifPresent(e -> {
            message.remove(e);
            message.add(e);
            if (message.size() > MAX_HISTORY_SIZE) {
                message.remove(0);
            }
        });
        return message
                .stream()
                .map(msg -> msg.isRetracted() ? "<GRAY>该消息已被撤回" : msg.getContent())
                .collect(Collectors.toList());
    }

    /**
     * 聊天
     * 该方法允许当前成员以玩家的口吻向指定的频道发送公开消息。
     * 注意：此方法用于发送公开消息，并通过该成员所属的频道进行广播。
     * @param message 要发送的公开消息 {@link ChannelMessage}
     */
    public void chat(ChannelMessage message) {
        message.setSentTime(System.currentTimeMillis());
        /* 将消息发送到成员所在的频道 */
        this.channel.sendMessage(message);
    }

    public void stopTypewriterEvent(TypewriterMessage message) {
        /* 关闭阻塞模式 */
        stopBlocking();
        if (message instanceof SelectorMessage selectorMessage) {
            sendSelectorMessage(selectorMessage);
        } else {
            /* 将当前成员的私密消息添加到消息历史中 */
            addExclusiveMessageToMemberHistory(message);
        }
    }

    private void sendSelectorMessage(SelectorMessage message) {
        if (isBlocking()) { return; }
        selectorListener = new SelectorListener(this, message);
        startBlocking(BlockingState.SELECTOR);
        resetChatDialog();
        Bukkit.getPluginManager()
                .registerEvents(selectorListener, MagicChat.getInstance());
    }

    private void sendTypewriterMessage(TypewriterMessage message) {
        /* 如果正在阻塞将不会进行打印机效果 */
        if (isBlocking()) {
            if (!(message instanceof SelectorMessage selectorMessage)) {
                addExclusiveMessageToMemberHistory(message);
            }
            return;
        }
        startBlocking(BlockingState.TYPEWRITER);
        TypewriterHelper typewriterHelper = new TypewriterHelper(
                message.getPrintContent(),
                message.getPrefix(),
                message instanceof SelectorMessage selectorMessage ? "\n".repeat(selectorMessage.getOptions().size()) + message.getSuffix() :
                        message.getSuffix()
        );
        typewriterTask = Bukkit.getScheduler().runTaskTimer(MagicChat.getInstance(), () -> {
            Player player = Bukkit.getPlayer(memberId);
            /* 如果玩家已经离线或打印完整个消息，则关闭计时器任务 */
            if (player == null || !player.isOnline() || typewriterHelper.isPrinted()) {
                stopTypewriterEvent(message);
                return;
            }
            String m = typewriterHelper.print();
            List<String> history = getMessageHistory(player);
            history.add(m);
            NMSDispatcher.getInstance().resetChatDialog(player, history);
        }, 0, message.getPrintTick() / (typewriterHelper.getRealContent().length() - 1));
    }
}
