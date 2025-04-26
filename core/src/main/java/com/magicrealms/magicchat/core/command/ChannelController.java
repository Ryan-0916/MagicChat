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
import com.magicrealms.magiclib.common.command.annotations.Command;
import com.magicrealms.magiclib.common.command.annotations.CommandListener;
import com.magicrealms.magiclib.common.command.enums.PermissionType;
import com.magicrealms.magiclib.common.command.records.ExecutableCommand;
import com.magicrealms.magiclib.common.enums.ParseType;
import com.magicrealms.magiclib.paper.dispatcher.MessageDispatcher;
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

    @Command(text = "^test$", permissionType = PermissionType.PLAYER)
    public void test(Player sender, String[] args) {
        Member member = MemberStorage.getInstance().retrieveMember(sender);
        TypewriterMessage message = (TypewriterMessage) new MessageBuilder(sender.getUniqueId(), "“啊，一位新朋友！愿知识之光照亮你的道路。我是莱恩，一名追寻古老传说的旅行学者。也许你曾在北境的雪原听过我的名字，或是在南方的图书馆瞥见我的笔记——毕竟，这个世界的故事太多，而我的脚步从未停歇。" +
                "我的背包里总装着几本破旧的书（其中一本可能正在砸到我的后脑勺），还有总也喝不完的咖啡——别问为什么它永远是热的，这是个连我自己都解不开的谜题。" +
                "如果你好奇，我可以告诉你精灵族的诗歌如何预言了星辰的陨落，或者矮人工匠为何痴迷于给门把手雕花纹。当然，偶尔我也会接些‘小委托’：比如帮村民解读神秘符号，或是用半吊子的魔法替人找猫……（上次的猫其实一直蹲在委托人头上。）" +
                "需要建议吗？我的格言是：‘答案或许在下一座山的背面，但路上记得带够干粮。’——要一起走走看吗？”")
                .setPrefix("旅行学者·莱恩: ")
                .setPrintTick(5 * 20)
                    .setOptions(List.of(new OptionBuilder("选我", ">>>选我")
                            .setCommandFunction(e -> Collections.singletonList(ExecutableCommand.ofSelf("seed"))).build(OptionType.COMMAND)
                            )).build(MessageType.SELECTOR);
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
