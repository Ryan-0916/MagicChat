package com.magicrealms.magicchat.core.listener;

import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.ConnectionState;
import com.github.retrooper.packetevents.protocol.PacketSide;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatMessage;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import com.magicrealms.magicchat.core.member.Member;
import com.magicrealms.magicchat.core.message.builder.MessageBuilder;
import com.magicrealms.magicchat.core.message.AbstractMessage;
import com.magicrealms.magicchat.core.message.ChannelMessage;
import com.magicrealms.magicchat.core.message.ExclusiveMessage;
import com.magicrealms.magicchat.core.message.enums.MessageType;
import com.magicrealms.magicchat.core.store.MemberStorage;
import com.magicrealms.magiclib.bukkit.message.helper.AdventureHelper;
import com.magicrealms.magiclib.bukkit.packet.PacketListener;
import com.magicrealms.magiclib.bukkit.packet.Receive;
import com.magicrealms.magiclib.bukkit.packet.Send;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static com.magicrealms.magicchat.core.MagicChatConstant.DIALOG_PATH;

/**
 * @author Ryan-0916
 * @Desc 聊天监听器
 * @date 2025-04-08
 */
@PacketListener
@SuppressWarnings("unused")
public class ChatListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Member member = MemberStorage.getInstance().retrieveMember(event.getPlayer());
        member.resetChatDialog();
    }

    @Receive(state = ConnectionState.PLAY,
            side = PacketSide.CLIENT,
            name = "CHAT_MESSAGE",
            priority = PacketListenerPriority.MONITOR)
    public void onChat(PacketReceiveEvent event) {
        if (event.isCancelled()) return;
        /* 添加聊天记录 */
        Member member = MemberStorage.getInstance()
                .retrieveMember(event.getPlayer());
        WrapperPlayClientChatMessage chatMessage =
                new WrapperPlayClientChatMessage(event);
        AbstractMessage message = new MessageBuilder(event
                .getUser()
                .getUUID(),
                chatMessage.getMessage())
                .build(MessageType.CHANNEL);
        member.chat((ChannelMessage) message);
    }

    @Send(state = ConnectionState.PLAY,
            side = PacketSide.SERVER,
            name = "CHAT_MESSAGE",
            priority = PacketListenerPriority.LOWEST)
    public void onChat(PacketSendEvent event) {
        event.setCancelled(true);
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
            priority = PacketListenerPriority.MONITOR)
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
        event.setCancelled(true);
        AbstractMessage message = new MessageBuilder(null, msg
        ).build(MessageType.EXCLUSIVE);
        Member member = MemberStorage.getInstance().retrieveMember(event.getPlayer());
        member.sendMessage((ExclusiveMessage) message);
    }
}
