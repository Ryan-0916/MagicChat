package com.magicrealms.magicchat.core.entity;

import com.magicrealms.magicchat.core.MagicChat;
import com.magicrealms.magicchat.core.channel.entity.AbstractChannel;
import com.magicrealms.magicchat.core.message.entity.AbstractMessage;
import com.magicrealms.magicchat.core.message.entity.ExclusiveMessage;
import com.magicrealms.magicchat.core.message.entity.ChannelMessage;
import com.magicrealms.magicchat.core.message.entity.channel.AbstractToppingMessage;
import com.magicrealms.magicchat.core.message.entity.exclusive.TypewriterMessage;
import com.magicrealms.magicchat.core.store.MessageHistoryStorage;
import com.magicrealms.magiclib.common.utils.SerializationUtils;
import com.magicrealms.magiclib.common.utils.StringUtil;
import com.magicrealms.magiclib.paper.dispatcher.NMSDispatcher;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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

    /* 打印机所占用的线程 */
    private BukkitTask TYPEWRITER_TASK;

    /**
     * 消息阻塞
     * 动态消息将管理此状态使得此状态下的成员不会自动接收到任何频道内的其他消息
     * 此玩家的消息将有动态消息事件自动处理
     */
    private boolean blocking;

    public Member(Player player, @NotNull AbstractChannel channel) {
        this.memberId = player.getUniqueId();
        this.memberName = StringUtils.upperCase(player.getName());
        this.channel = channel;
        /* 初始化玩家频道 */
        this.channel.joinChannel(this);
    }

    public void startBlocking() {
        this.blocking = true;
    }

    public void stopBlocking() {
        this.blocking = false;
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
     * 重置玩家的聊天框
     * 它将在获取频道中的聊天信息并与私人信息相结合
     * 重组成新的聊天框并发包给玩家
     */
    public void resetChatDialog() {
        if (blocking) { return; }
        Player player = Bukkit.getPlayer(memberId);
        if (player == null || !player.isOnline()) { return; }
        NMSDispatcher.getInstance().resetChatDialog(player, getMessageHistory(player));
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
                .sorted((m1, m2) -> Long.compare(m2.getCreatedTime(), m1.getCreatedTime()))
                .toList();

        /* 第二步寻找需要置顶消息
         * 按照消息过滤出最后发送并且满足有效期内的置顶消息
         */
        Optional<AbstractToppingMessage> toppingMessage = allHistory.stream()
                .filter(m -> m instanceof AbstractToppingMessage)
                .map(m -> (AbstractToppingMessage) m)
                .filter(topMessage -> currentTime < topMessage.getCreatedTime() + topMessage.getKeepTick() * 50)
                .findFirst();
        /* 第三步用户可见的100条聊天记录并按时间升序
         * 由于聊天记录的还原越早的发送的消息要放置越前
         * 因此该步骤需要按照时间升序 越早的消息排序越前
         */
        List<AbstractMessage> message = allHistory.stream().limit(MAX_HISTORY_SIZE)
                .sorted(Comparator.comparingLong(AbstractMessage::getCreatedTime))
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
        return message.stream().map(AbstractMessage::getContent).toList();
    }

    /**
     * 发送专属消息
     * 该方法将发送一条私密消息给当前成员，并将其记录到历史消息存储中。
     * 发送完消息后，重置当前对话状态
     * @param message 要发送的私密消息  {@link ExclusiveMessage}
     */
    public void sendMessage(ExclusiveMessage message) {
        if (message instanceof TypewriterMessage typewriterMessage) {
            sendTypewriterMessage(typewriterMessage);
            return;
        }
        /* 将当前成员的私密消息添加到消息历史中 */
        MessageHistoryStorage.getInstance().addMessageToMember(this, message);

        /* 同步聊天记录至 Redis 队列 */
        MagicChat.getInstance().getRedisStore().rSetValue(MessageFormat.format(MEMBER_MESSAGE_HISTORY,
                memberName), MAX_HISTORY_SIZE, SerializationUtils.serializeByBase64(message));

        /* 重置聊天对话状态，以便进行下一条消息 */
        resetChatDialog();
    }

    /**
     * 聊天
     * 该方法允许当前成员以玩家的口吻向指定的频道发送公开消息。
     * 注意：此方法用于发送公开消息，并通过该成员所属的频道进行广播。
     * @param message 要发送的公开消息 {@link ChannelMessage}
     */
    public void chat(ChannelMessage message) {
        /* 将消息发送到成员所在的频道 */
        this.channel.sendMessage(message);
    }

    public void stopTypewriter() {
        stopBlocking();
        Optional.ofNullable(TYPEWRITER_TASK).ifPresent(task -> {
            if (!TYPEWRITER_TASK.isCancelled()) TYPEWRITER_TASK.cancel();
            TYPEWRITER_TASK = null;
        });
    }

    public void sendTypewriterMessage(TypewriterMessage message) {
        if (isBlocking()) { return; }
        startBlocking();
        /* 将消息中的标签解析为列表
         * 例如 <prefix>前缀</prefix>
         * 将会转换成 prefix::前缀
         * 此处的目的是为了支持 MiniMessage 的标签写法
         * 让截取出来的文本也不会失去 MiniMessage 的色彩或是其他
         */
        List<String> labelMsg = StringUtil.getTagsToList(message.getPrintContent());
        /* 提取去除 MiniMessage 标签后，所显示的真实内容
         * 例如<red>我是红色</red><yellow>我是黄色</yellow>
         * 那么此段内容的真实文本为：我是红色我是黄色，我们应当按照
         * 真实的文字内容计算单个文字的更换时长
         */
        String realMessage = labelMsg.stream().map(e -> {
            String[] parts = e.split("::", 2);
            return parts.length > 1 ? parts[1] : StringUtil.EMPTY;
        }).collect(Collectors.joining());
        /* 当前打印到的字符索引 */
        AtomicInteger index = new AtomicInteger();
        /* 当前构建的消息内容 */
        StringBuilder currentText = new StringBuilder();
        /* 创建一个新的计时器，用于定时更新 ActionBar 消息 */
        TYPEWRITER_TASK = Bukkit.getScheduler().runTaskTimer(MagicChat.getInstance(), () -> {
            Player player = Bukkit.getPlayer(memberId);
            /* 如果玩家已经离线或打印完整个消息，则关闭计时器任务 */
            if (player == null || !player.isOnline() || index.get() >= realMessage.length()) {
                stopTypewriter();
                return;
            }
            int i = 0;
            /* 遍历标签列表，构建当前应显示的消息内容
             * 此处是为了不让打印的内容失去 MiniMessage 的色彩或是其他
             * 如果当前输入的内容位于标签后的首位字符，那将会将当前输出内容拥有的标签拼接到构建的消息内容中 */
            for (String label : labelMsg) {
                String[] parts = label.split("::", 2);
                if (i > index.get()) {
                    break;
                } else if (i == index.get() && !parts[0].equals("prefix")) {
                    currentText.append("<").append(parts[0]).append(">");
                }
                i += parts.length >= 2 ? parts[1].length() : 0;
            }
            /* 将内容拼接到构建的消息内容中 */
            currentText.append(realMessage.charAt(index.getAndIncrement()));
            /* 构建完整的消息，包括前缀和当前消息内容，如果是未结束的文本拼接上省略号 */
            String m = message.getPrefix() + currentText + (currentText.length() == message.getPrintContent().length() ? StringUtil.EMPTY : "...");
            List<String> history = getMessageHistory(player);
            history.add(m);
            NMSDispatcher.getInstance().resetChatDialog(player, history);
        }, 0, message.getPrintTick() / (realMessage.length() - 1));
    }

}
