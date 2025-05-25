package com.magicrealms.magicchat.core.command;

import com.magicrealms.magicchat.core.BukkitMagicChat;
import com.magicrealms.magicchat.api.channel.AbstractChannel;
import com.magicrealms.magicchat.api.channel.Channel;
import com.magicrealms.magicchat.api.member.Member;
import com.magicrealms.magiclib.bukkit.command.annotations.Command;
import com.magicrealms.magiclib.bukkit.command.annotations.CommandListener;
import com.magicrealms.magiclib.bukkit.command.enums.PermissionType;
import com.magicrealms.magiclib.common.enums.ParseType;
import com.magicrealms.magiclib.core.dispatcher.MessageDispatcher;
import org.bukkit.entity.Player;
import java.util.UUID;

import static com.magicrealms.magicchat.common.MagicChatConstant.YML_CONFIG;
import static com.magicrealms.magicchat.common.MagicChatConstant.YML_LANGUAGE;

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
        Member member = BukkitMagicChat.getInstance().getMemberManager().getMember(sender);
        AbstractChannel abstractChannel = BukkitMagicChat.getInstance().getChannelManager()
                .getChannel(args[1]);
        member.resetChannel(abstractChannel);
    }

    @Command(text = "^ToggleUndo", permissionType = PermissionType.PERMISSION,
            permission = "magic.command.magicchat.all||magic.command.magicchat.retract")
    public void toggleUndo(Player sender, String[] args) {
        if (!BukkitMagicChat.getInstance().getConfigManager().getYmlValue(YML_CONFIG
                , "Model.Retract.Enable", false, ParseType.BOOLEAN)) {
            return;
        }
        Member member = BukkitMagicChat.getInstance().getMemberManager().getMember(sender);
        MessageDispatcher.getInstance().sendMessage(BukkitMagicChat.getInstance(),
                sender, String.format(BukkitMagicChat.getInstance().getConfigManager().getYmlValue(
                        YML_LANGUAGE, String.format("PlayerMessage.Success.ToggleUndo%s",
                                member.toggleUndoMessagesMode() ? "Enable": "UnEnable"))));
    }

    @Command(text = "^Retract\\s\\S+$", permissionType = PermissionType.PLAYER)
    public void RetractMessage(Player sender, String[] args) {
        if (!sender.hasPermission("magic.command.magicchat.all")
                && !sender.hasPermission("magic.command.magicchat.retract")
        ) {
            return;
        }
        if (!BukkitMagicChat.getInstance().getConfigManager().getYmlValue(YML_CONFIG
                , "Model.Retract.Enable", false, ParseType.BOOLEAN)) {
            return;
        }
        Member member = BukkitMagicChat.getInstance().getMemberManager().getMember(sender);
        if (!member.isUndoMessagesMode()) {
            return;
        }
        /* 公共频道 */
        if ((member.getChannel() instanceof Channel channel)) {
            channel.retractMessage(UUID.fromString(args[1]), sender.getUniqueId());
            MessageDispatcher.getInstance().sendMessage(BukkitMagicChat.getInstance(),
                    sender, String.format(BukkitMagicChat.getInstance().getConfigManager().getYmlValue(
                            YML_LANGUAGE, "PlayerMessage.Success.Retract")));
            return;
        }
        MessageDispatcher.getInstance().sendMessage(BukkitMagicChat.getInstance(),
                sender, String.format(BukkitMagicChat.getInstance().getConfigManager().getYmlValue(
                        YML_LANGUAGE, "PlayerMessage.Error.RetractExclusiveChannel")));
    }
}
