package com.magicrealms.magicchat.core.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
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
import com.magicrealms.magiclib.common.packet.annotations.Receive;
import com.magicrealms.magiclib.common.packet.annotations.Send;
import net.kyori.adventure.text.Component;
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
     * 数据包协议 PLAY {@link PacketType.Protocol}
     * 数据包发送者 CLIENT {@link PacketType.Sender}
     * 数据包ID 6
     * 对应数据包 {@link PacketType.Play.Client#CHAT}
     * 将玩家的聊天信息发送至频道内
     */
    @Receive(protocol = PacketType.Protocol.PLAY,
            sender = PacketType.Sender.CLIENT,
            packetId = 6,
            priority = ListenerPriority.LOWEST)
    public void onChat(PacketEvent event) {
        if (event.isCancelled()) {
            return;
        }
        event.setCancelled(true);
        AbstractMessage message = new MessageBuilder(event
                .getPlayer().getUniqueId(), event.getPacket().getStrings().read(0))
                .build(MessageType.CHANNEL);
        Member member = MemberStorage.getInstance().retrieveMember(event.getPlayer());
        member.chat((ChannelMessage) message);
    }

    /**
     * 处理服务器接收到玩家聊天数据包时的逻辑
     * 数据包协议 PLAY {@link PacketType.Protocol}
     * 数据包发送者 SERVER {@link PacketType.Sender}
     * 数据包ID 108
     * 对应数据包 {@link PacketType.Play.Server#SYSTEM_CHAT}
     * 将系统消息发送至玩家消息履历
     */
    @Send(protocol = PacketType.Protocol.PLAY,
            sender = PacketType.Sender.SERVER,
            packetId = 108,
            priority = ListenerPriority.LOWEST)
    public void onSystemChat(PacketEvent event) {
        WrappedChatComponent wrappedChatComponent
                = event.getPacket().getChatComponents().read(0);
        String wrappedChatComponentJson = wrappedChatComponent.getJson();
        if (StringUtils.contains(wrappedChatComponentJson, DIALOG_PATH)) {
            return;
        }
        Component component = AdventureHelper.getGson().deserialize(wrappedChatComponentJson);
        String msg = AdventureHelper.serializeComponent(component);
        AbstractMessage message = new MessageBuilder(null, msg
        ).build(MessageType.EXCLUSIVE);
        Member member = MemberStorage.getInstance().retrieveMember(event.getPlayer());
        MessageHistoryStorage.getInstance().addMessageToMember(member, (ExclusiveMessage) message);
    }

}
