package com.magicrealms.magicchat.core.channel;

import com.magicrealms.magicchat.core.MagicChat;
import com.magicrealms.magicchat.core.bungee.BungeeMessage;
import com.magicrealms.magicchat.core.bungee.RetractInfo;
import com.magicrealms.magicchat.core.member.Member;
import com.magicrealms.magicchat.core.bungee.BungeeChannelMessageManager;
import com.magicrealms.magicchat.core.message.AbstractMessage;
import com.magicrealms.magicchat.core.message.ChannelMessage;
import com.magicrealms.magicchat.core.store.MessageHistoryStorage;
import com.magicrealms.magiclib.common.enums.ParseType;
import com.magicrealms.magiclib.common.utils.GsonUtil;
import com.magicrealms.magiclib.common.utils.SerializationUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.magicrealms.magicchat.core.MagicChatConstant.*;

/**
 * @author Ryan-0916
 * @Desc 公共频道
 * @date 2025-04-07
 */
public class Channel extends AbstractChannel{

    @NotNull
    private final List<Member> members;

    private BungeeChannelMessageManager bungeeChannelMessageManager;

    public Channel(String channelName) {
        super(channelName);
        this.members = new ArrayList<>();
    }

    @Override
    public void sendMessage(Member sender, ChannelMessage message) {
        String base64Message = SerializationUtils.serializeByBase64(message);

        /* 发送通知推送 */
        MagicChat.getInstance().getRedisStore().publishValue(String.format(BUNGEE_CHANNEL_CHAT,
                channelName), GsonUtil.toJson(BungeeMessage.ofSend(base64Message)));
        /* 同步聊天记录至 Redis 队列 */
        CompletableFuture.runAsync(() -> {
                    /* 记录日志 TODO 未来将这一块挪至 Velocity 日志管理 */
                    MagicChat.getInstance().getLoggerManager().info(
                            MagicChat.getInstance().getConfigManager()
                                    .getYmlValue(YML_CONFIG, "Model.Log.Chat"),
                            this.channelName,
                            sender.getMemberName(),
                            message.getOriginalContent()
                    );
                    /* 同步消息 */
                    MagicChat.getInstance().getRedisStore().rSetValue(String.format(CHANNEL_MESSAGE_HISTORY,
                            channelName), MAX_HISTORY_SIZE, base64Message);
                }
        );
    }

    @Override
    public void joinChannel(Member member) {
        if (members.isEmpty()) { subscribe(); }
        members.add(member);
        member.resetChatDialog();
    }

    @Override
    public void leaveChannel(Member member) {
        members.remove(member);
        if (members.isEmpty()) {
            unsubscribe();
        }
    }

    public void subscribe() {
        MagicChat plugin = MagicChat.getInstance();
        String host = plugin.getConfigManager().getYmlValue(YML_REDIS, "DataSource.Host"), password = plugin.getConfigManager().getYmlValue(YML_REDIS, "DataSource.Password");
        int port = plugin.getConfigManager().getYmlValue(YML_REDIS, "DataSource.Port", 6379, ParseType.INTEGER);
        boolean redisPasswordModel = plugin.getConfigManager().getYmlValue(YML_REDIS, "DataSource.PasswordModel", false, ParseType.BOOLEAN);
        bungeeChannelMessageManager = new BungeeChannelMessageManager.Builder().channel(
                        String.format(BUNGEE_CHANNEL_CHAT,
                                channelName))
                .plugin(plugin)
                .host(host)
                .port(port)
                .passwordModel(redisPasswordModel)
                .password(password)
                .sendListener(message -> {
                    /* 捕获通知推送 尝试将消息添加至聊天记录中 */
                    MessageHistoryStorage.getInstance().addMessage(this, message);
                    /* 重置每个人的聊天框 */
                    for (Member member : members) {
                        member.resetChatDialog();
                    }
                })
                .retractListener(retractInfo -> {
                    /* 捕获通知推送 尝试将消息添加至聊天记录中 */
                    MessageHistoryStorage.getInstance()
                            .retractChannelMessage(this, retractInfo);
                    /* 重置每个人的聊天框 */
                    for (Member member : members) {
                        member.resetChatDialog();
                    }
                })
                .build();
    }

    public void unsubscribe() {
        Optional.ofNullable(bungeeChannelMessageManager)
                .ifPresent(BungeeChannelMessageManager::unsubscribe);
    }

    /* 撤回消息 */
    public void retractMessage(UUID messageId, UUID receiverBy) {
        /* 回收本地缓存消息 */
        MagicChat.getInstance().getRedisStore().publishValue(String.format(BUNGEE_CHANNEL_CHAT,
                channelName), GsonUtil.toJson(BungeeMessage.ofRetract(RetractInfo.of(messageId, receiverBy))));
        /* 回收 Redis 队列中的消息 */
        CompletableFuture.runAsync(() ->
        {
            /* TODO: 加锁 不推荐使用，推荐使用从队列中移除并新增，可能出现线程安全问题 */
            /* 获取队列中的全部消息 */
            Optional<List<String>> history = MagicChat.getInstance().getRedisStore()
                    .getAllValue(String.format(CHANNEL_MESSAGE_HISTORY, channelName));
            if (history.isEmpty()) {
                return;
            }
            List<AbstractMessage> updatedMessages = history.get().stream()
                    .map(SerializationUtils::<ChannelMessage>deserializeByBase64)
                    .peek(msg -> {
                        if (msg.getMessageId().equals(messageId) && !msg.isRetracted()) {
                            msg.setRetracted(true);
                            msg.setRetractedBy(receiverBy);
                        }
                    })
                    .collect(Collectors.toList());
            String[] serializedMessages = updatedMessages.stream()
                    .map(SerializationUtils::serializeByBase64)
                    .toArray(String[]::new);
            MagicChat.getInstance().getRedisStore().rSetValue(
                    String.format(CHANNEL_MESSAGE_HISTORY, channelName),
                    MAX_HISTORY_SIZE,
                    serializedMessages
            );
        });
    }
}
