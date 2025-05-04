package com.magicrealms.magicchat.core.command;

import com.magicrealms.magicchat.core.MagicChat;
import com.magicrealms.magicchat.core.channel.AbstractChannel;
import com.magicrealms.magicchat.core.channel.Channel;
import com.magicrealms.magicchat.core.member.Member;
import com.magicrealms.magicchat.core.message.builder.MessageBuilder;
import com.magicrealms.magicchat.core.message.builder.OptionBuilder;
import com.magicrealms.magicchat.core.message.exclusive.TypewriterMessage;
import com.magicrealms.magicchat.core.message.enums.MessageType;
import com.magicrealms.magicchat.core.message.enums.OptionType;
import com.magicrealms.magicchat.core.store.ChannelStorage;
import com.magicrealms.magicchat.core.store.MemberStorage;
import com.magicrealms.magiclib.bukkit.command.annotations.Command;
import com.magicrealms.magiclib.bukkit.command.annotations.CommandListener;
import com.magicrealms.magiclib.bukkit.command.enums.PermissionType;
import com.magicrealms.magiclib.bukkit.command.records.ExecutableCommand;
import com.magicrealms.magiclib.common.enums.ParseType;
import com.magicrealms.magiclib.core.dispatcher.MessageDispatcher;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.magicrealms.magicchat.core.MagicChatConstant.YML_CONFIG;
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

    @Command(text = "^run$", permissionType = PermissionType.PLAYER)
    public void run(Player sender, String[] args) {
        MagicChat.getInstance().getLogger().warning("开始处理");
        MagicChat.getInstance().getLogger().warning(String.valueOf(System.currentTimeMillis()));
        for (int i = 0; i < 1000; i++) {
            sender.sendMessage(Component.text("我是文本：" + i, NamedTextColor.GREEN));
        }
    }

    @Command(text = "^test$", permissionType = PermissionType.PLAYER)
    public void test(Player sender, String[] args) {
        Member member = MemberStorage.getInstance().retrieveMember(sender);
        TypewriterMessage message = (TypewriterMessage)
                new MessageBuilder(sender.getUniqueId(),
                        "我是晚秋，ABCABCABCABC123")
                .setPrefix("晚秋: ")
                .setPrintTick(2 * 20)
                    .setOptions(List.of(new OptionBuilder("选我", ">>>选我")
                            .setCommandFunction(e ->
                                    Collections.singletonList(ExecutableCommand.ofSelf("seed"))).build(OptionType.COMMAND),
        new OptionBuilder("选我2", ">>>选我2")
                .setCommandFunction(e -> Collections.singletonList(ExecutableCommand.ofSelf("seed"))).build(OptionType.COMMAND))).build(MessageType.SELECTOR);
        member.sendMessage(message);
    }

    @Command(text = "^ToggleUndo", permissionType = PermissionType.PERMISSION,
            permission = "magic.command.magicchat.all||magic.command.magicchat.retract")
    public void toggleUndo(Player sender, String[] args) {
        if (!MagicChat.getInstance().getConfigManager().getYmlValue(YML_CONFIG
                , "Model.Retract.Enable", false, ParseType.BOOLEAN)) {
            return;
        }
        Member member = MemberStorage.getInstance().retrieveMember(sender);
        MessageDispatcher.getInstance().sendMessage(MagicChat.getInstance(),
                sender, String.format(MagicChat.getInstance().getConfigManager().getYmlValue(
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
        if (!MagicChat.getInstance().getConfigManager().getYmlValue(YML_CONFIG
                , "Model.Retract.Enable", false, ParseType.BOOLEAN)) {
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
