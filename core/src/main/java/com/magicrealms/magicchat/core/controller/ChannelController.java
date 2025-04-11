package com.magicrealms.magicchat.core.controller;

import com.magicrealms.magicchat.core.channel.entity.AbstractChannel;
import com.magicrealms.magicchat.core.entity.Member;
import com.magicrealms.magicchat.core.message.builder.MessageBuilder;
import com.magicrealms.magicchat.core.message.entity.AbstractMessage;
import com.magicrealms.magicchat.core.message.entity.exclusive.TypewriterMessage;
import com.magicrealms.magicchat.core.message.enums.MessageType;
import com.magicrealms.magicchat.core.store.ChannelStorage;
import com.magicrealms.magicchat.core.store.MemberStorage;
import com.magicrealms.magiclib.common.command.annotations.Command;
import com.magicrealms.magiclib.common.command.annotations.CommandListener;
import com.magicrealms.magiclib.common.command.enums.PermissionType;
import com.magicrealms.magiclib.paper.MagicLib;
import com.magicrealms.magiclib.paper.dispatcher.MessageDispatcher;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Ryan-0916
 * @Desc 说明
 * @date 2025-04-09
 */
@CommandListener
@SuppressWarnings("unused")
public class ChannelController {

    @Command(text = "^join\\s\\S+$", permissionType = PermissionType.PLAYER)
    public void test(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Member member = MemberStorage.getInstance().retrieveMember(player);
        AbstractChannel abstractChannel = ChannelStorage.getInstance().retrieveChannel(args[1]);
        member.resetChannel(abstractChannel);
        MessageDispatcher.getInstance().sendMessage(MagicLib.getInstance(), sender, "切换成功");
    }

    @Command(text = "^test$", permissionType = PermissionType.PLAYER)
    public void test2(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Member member = MemberStorage.getInstance().retrieveMember(player);
        AbstractMessage message = new MessageBuilder(null, "它们曾经是王国的卫士。每一个守护者的存在，都意味着它们与这片土地有着不解的联系。它们的灵魂被锁在遗迹中，无法超生，也无法回归。它们不是简单的敌人，而是被历史和悲剧所困的幽灵。如果你不小心触碰到它们的禁忌，才会引发一场无谓的战斗。")
                .setPrefix("艾莉亚（点点头，神情复杂）：")
                .setPrintTick(5 * 20)
                .build(MessageType.TYPEWRITER);
        member.sendTypewriterMessage((TypewriterMessage) message);
    }

}
