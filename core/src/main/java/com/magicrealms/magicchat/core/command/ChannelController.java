package com.magicrealms.magicchat.core.command;

import com.magicrealms.magicchat.core.MagicChat;
import com.magicrealms.magicchat.core.channel.entity.AbstractChannel;
import com.magicrealms.magicchat.core.channel.entity.Channel;
import com.magicrealms.magicchat.core.entity.Member;
import com.magicrealms.magicchat.core.message.builder.MessageBuilder;
import com.magicrealms.magicchat.core.message.entity.exclusive.TypewriterMessage;
import com.magicrealms.magicchat.core.message.enums.MessageType;
import com.magicrealms.magicchat.core.store.ChannelStorage;
import com.magicrealms.magicchat.core.store.MemberStorage;
import com.magicrealms.magiclib.common.command.annotations.Command;
import com.magicrealms.magiclib.common.command.annotations.CommandListener;
import com.magicrealms.magiclib.common.command.enums.PermissionType;
import com.magicrealms.magiclib.paper.dispatcher.MessageDispatcher;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.magicrealms.magicchat.core.MagicChatConstant.YML_LANGUAGE;

/**
 * @author Ryan-0916
 * @Desc 频道部分指令
 * @date 2025-04-09
 */
@CommandListener
@SuppressWarnings("unused")
public class ChannelController {

    @Command(text = "^join\\s\\S+", permissionType = PermissionType.PLAYER)
    public void join(Player sender, String[] args) {
        Member member = MemberStorage.getInstance().retrieveMember(sender);
        AbstractChannel abstractChannel = ChannelStorage.getInstance().retrieveChannel(args[1]);
        member.resetChannel(abstractChannel);
    }


    @Command(text = "^test$", permissionType = PermissionType.PLAYER)
    public void test(Player sender, String[] args) {
        Member member = MemberStorage.getInstance().retrieveMember(sender);
        TypewriterMessage message = (TypewriterMessage) new MessageBuilder(sender.getUniqueId(), "再见了妈妈今晚我就要远航，我是大傻逼，我是一个大大的傻逼，我是一个大大大大大大大大傻逼")
                .setPrefix("刘一鸣:")
                .setPrintTick(6 * 20)
                .build(MessageType.TYPEWRITER);
        member.sendMessage(message);
    }

    @Command(text = "^ToggleUndo$", permissionType = PermissionType.PERMISSION,
            permission = "magic.command.magicchat.all||magic.command.magicchat.retract")
    public void toggleUndo(Player sender, String[] args) {
        Member member = MemberStorage.getInstance().retrieveMember(sender);
        MessageDispatcher.getInstance().sendMessage(MagicChat.getInstance(),
                sender, String.format(MagicChat.getInstance().getConfigManager().getYmlValue(
                        YML_LANGUAGE, String.format("PlayerMessage.Success.ToggleUndo%s",
                                member.toggleUndoMessagesMode() ? "Enable": "UnEnable"))));
    }

    @Command(text = "^Retract\\s\\S+$", permissionType = PermissionType.PLAYER)
    public void RetractMessage(Player sender, String[] args) {

        System.out.println("尝试撤回消息" + args[1]);

        if (!sender.hasPermission("magic.command.magicchat.all")
                && !sender.hasPermission("magic.command.magicchat.retract")
        ) {
            return;
        }
        Member member = MemberStorage.getInstance().retrieveMember(sender);
        if (!member.isUndoMessagesMode()) {
            return;
        }

        /* 公共频道 */
        if ((member.getChannel() instanceof Channel channel)) {
            channel.retractMessage(UUID.fromString(args[1]), sender.getUniqueId());
            MessageDispatcher.getInstance().sendMessage(MagicChat.getInstance(),
                    sender, String.format(MagicChat.getInstance().getConfigManager().getYmlValue(
                            YML_LANGUAGE, "PlayerMessage.Success.Retract")));
            return;
        }

        MessageDispatcher.getInstance().sendMessage(MagicChat.getInstance(),
                sender, String.format(MagicChat.getInstance().getConfigManager().getYmlValue(
                        YML_LANGUAGE, "PlayerMessage.Error.RetractExclusiveChannel")));
    }
}
