package com.magicrealms.magicchat.core.listener;

import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.ConnectionState;
import com.github.retrooper.packetevents.protocol.PacketSide;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatMessage;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSystemChatMessage;
import com.magicrealms.magicchat.core.entity.Member;
import com.magicrealms.magicchat.core.message.builder.MessageBuilder;
import com.magicrealms.magicchat.core.message.entity.AbstractMessage;
import com.magicrealms.magicchat.core.message.entity.ExclusiveMessage;
import com.magicrealms.magicchat.core.message.entity.ChannelMessage;
import com.magicrealms.magicchat.core.message.enums.MessageType;
import com.magicrealms.magicchat.core.store.MemberStorage;
import com.magicrealms.magicchat.core.store.MessageHistoryStorage;
import com.magicrealms.magiclib.common.message.helper.AdventureHelper;
import com.magicrealms.magiclib.common.packet.annotations.PacketListener;
import com.magicrealms.magiclib.common.packet.annotations.Send;
import org.apache.commons.lang3.StringUtils;

import static com.magicrealms.magicchat.core.MagicChatConstant.DIALOG_PATH;

/**
 * @author Ryan-0916
 * @Desc 聊天监听器
 * @date 2025-04-08
 */
@PacketListener
@SuppressWarnings("unused")
public class ChatListener {

    /**
     * 处理服务器接收到玩家聊天数据包时的逻辑
     * 数据包协议 PLAY {@link ConnectionState#PLAY}
     * 数据包发送者 SERVER {PacketSide#CLIENT}
     * 数据包名称 CHAT_MESSAGE
     * 对应数据包 {@link PacketType.Play.Server#CHAT_MESSAGE}
     * 将玩家的聊天信息发送至频道内
     */
    @Send(state = ConnectionState.PLAY,
            side = PacketSide.SERVER,
            name = "CHAT_MESSAGE",
            priority = PacketListenerPriority.LOWEST)
    public void onChat(PacketReceiveEvent event) {
        if (event.isCancelled()) { return; }
        event.setCancelled(true);
        WrapperPlayClientChatMessage chatMessage
                = new WrapperPlayClientChatMessage(event);
        AbstractMessage message = new MessageBuilder(event.getUser().getUUID(),
                chatMessage.getMessage())
                .build(MessageType.CHANNEL);
        Member member = MemberStorage.getInstance().retrieveMember(event.getPlayer());
        member.chat((ChannelMessage) message);
    }

    /**
     * 处理服务器接收到玩家聊天数据包时的逻辑
     * 数据包协议 PLAY {@link ConnectionState#PLAY}
     * 数据包发送者 SERVER {@link PacketSide#SERVER}
     * 数据包名称 SYSTEM_CHAT_MESSAGE
     * 对应数据包 {@link PacketType.Play.Server#SYSTEM_CHAT_MESSAGE}
     * 将系统消息发送至玩家消息履历
     */
    @Send(state = ConnectionState.PLAY,
            side = PacketSide.SERVER,
            name = "SYSTEM_CHAT_MESSAGE",
            priority = PacketListenerPriority.LOWEST)
    public void onSystemChat(PacketSendEvent event) {
        /* 事件已被拦截的消息不进行处理 */
        if (event.isCancelled()) { return; }
        WrapperPlayServerSystemChatMessage chatMessage = new WrapperPlayServerSystemChatMessage(event);
        /* ActionBar 消息将不进行拦截 */
        if (chatMessage.isOverlay()) { return; }
        String msg = AdventureHelper.serializeComponent(chatMessage.getMessage());
        /* 如果消息为 Dialog 消息将不进行拦截 */
        if (StringUtils.contains(msg, DIALOG_PATH)) {
            return;
        }
        AbstractMessage message = new MessageBuilder(null, msg
        ).build(MessageType.EXCLUSIVE);
        Member member = MemberStorage.getInstance().retrieveMember(event.getPlayer());
        MessageHistoryStorage.getInstance().addMessageToMember(member, (ExclusiveMessage) message);
    }

}
