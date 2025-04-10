package com.magicrealms.magicchat.core.controller;

import com.magicrealms.magicchat.core.channel.entity.AbstractChannel;
import com.magicrealms.magicchat.core.entity.Member;
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
public class ChannelController {


    @Command(text = "^join\\s\\S+$", permissionType = PermissionType.PLAYER)
    public void test(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Member member = MemberStorage.getInstance().retrieveMember(player);
        AbstractChannel abstractChannel = ChannelStorage.getInstance().retrieveChannel(args[1]);
        member.resetChannel(abstractChannel);

        MessageDispatcher.getInstance().sendMessage(MagicLib.getInstance(), sender, "切换成功");
    }
}
