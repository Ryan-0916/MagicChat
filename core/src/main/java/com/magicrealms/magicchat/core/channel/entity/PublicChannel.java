package com.magicrealms.magicchat.core.channel.entity;

import com.magicrealms.magicchat.core.MagicChat;
import com.magicrealms.magicchat.core.entity.Member;
import com.magicrealms.magicchat.core.manage.BungeeChannelMessageManager;
import com.magicrealms.magicchat.core.message.entity.ChannelMessage;
import com.magicrealms.magicchat.core.store.MessageHistoryStorage;
import com.magicrealms.magiclib.common.enums.ParseType;
import com.magicrealms.magiclib.common.utils.SerializationUtils;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.magicrealms.magicchat.core.MagicChatConstant.*;

/**
 * @author Ryan-0916
 * @Desc 公共频道
 * @date 2025-04-07
 */
public class PublicChannel extends AbstractChannel{

    @NotNull
    private final List<Member> members;

    private BungeeChannelMessageManager bungeeChannelMessageManager;

    public PublicChannel(String channelName) {
        super(channelName);
        this.members = new ArrayList<>();
    }

    @Override
    public void sendMessage(ChannelMessage message) {
        String base64Message = SerializationUtils.serializeByBase64(message);
        /* 发送通知推送 */
        MagicChat.getInstance().getRedisStore().publishValue(MessageFormat.format(BUNGEE_CHANNEL_CHAT,
                channelName), base64Message);
        /* 同步聊天记录至 Redis 队列 */
        MagicChat.getInstance().getRedisStore().rSetValue(MessageFormat.format(CHANNEL_MESSAGE_HISTORY,
                channelName), MAX_HISTORY_SIZE, base64Message);
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

    private void subscribe() {
        MagicChat plugin = MagicChat.getInstance();
        String host = plugin.getConfigManager().getYmlValue(YML_REDIS, "DataSource.Host"), password = plugin.getConfigManager().getYmlValue(YML_REDIS, "DataSource.Password");
        int port = plugin.getConfigManager().getYmlValue(YML_REDIS, "DataSource.Port", 6379, ParseType.INTEGER);
        boolean redisPasswordModel = plugin.getConfigManager().getYmlValue(YML_REDIS, "DataSource.PasswordModel", false, ParseType.BOOLEAN);
        bungeeChannelMessageManager = new BungeeChannelMessageManager.Builder().channel(
                        MessageFormat.format(BUNGEE_CHANNEL_CHAT,
                                channelName))
                .plugin(plugin)
                .host(host)
                .port(port)
                .passwordModel(redisPasswordModel)
                .password(password)
                .messageListener(message -> {
                    /* 捕获通知推送 尝试将消息添加至聊天记录中 */
                    MessageHistoryStorage.getInstance().addMessageToChannel(this, message);
                    /* 重置每个人的聊天框 */
                    for (Member member : members) {
                        member.resetChatDialog();
                    }
                }).build();
    }

    private void unsubscribe() {
        Optional.ofNullable(bungeeChannelMessageManager).ifPresent(BungeeChannelMessageManager::unsubscribe);
    }
}
