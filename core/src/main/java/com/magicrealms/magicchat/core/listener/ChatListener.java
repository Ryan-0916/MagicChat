package com.magicrealms.magicchat.core.listener;

import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.ConnectionState;
import com.github.retrooper.packetevents.protocol.PacketSide;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import com.magicrealms.magicchat.api.member.Member;
import com.magicrealms.magicchat.api.message.builder.MessageBuilder;
import com.magicrealms.magicchat.api.message.AbstractMessage;
import com.magicrealms.magicchat.api.message.ChannelMessage;
import com.magicrealms.magicchat.api.message.ExclusiveMessage;
import com.magicrealms.magicchat.api.message.enums.MessageType;
import com.magicrealms.magicchat.core.BukkitMagicChat;
import com.magicrealms.magiclib.bukkit.message.helper.AdventureHelper;
import com.magicrealms.magiclib.bukkit.packet.PacketListener;
import com.magicrealms.magiclib.bukkit.packet.Send;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static com.magicrealms.magicchat.common.MagicChatConstant.DIALOG_PATH;

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
        BukkitMagicChat.getInstance()
                .getMemberManager()
                .getMember(event.getPlayer())
                .resetChatDialog();
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
        AbstractMessage message = new MessageBuilder(null, msg
        ).build(MessageType.EXCLUSIVE);
        Member member = BukkitMagicChat
                .getInstance()
                .getMemberManager()
                .getMember(event.getPlayer());
        member.sendMessage((ExclusiveMessage) message);
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onChatEvent(AsyncChatEvent event) {
        Member member = BukkitMagicChat
                .getInstance()
                .getMemberManager()
                .getMember(event.getPlayer());
        AbstractMessage message = new MessageBuilder(event
                .getPlayer().getUniqueId(),
                AdventureHelper.serializeComponent(event.message()))
                .build(MessageType.CHANNEL);
        member.chat((ChannelMessage) message);
        event.setCancelled(true);
    }

}
