package com.magicrealms.magicchat.core.command;

import com.magicrealms.magicchat.core.channel.entity.AbstractChannel;
import com.magicrealms.magicchat.core.entity.Member;
import com.magicrealms.magicchat.core.message.builder.MessageBuilder;
import com.magicrealms.magicchat.core.message.entity.exclusive.TypewriterMessage;
import com.magicrealms.magicchat.core.message.enums.MessageType;
import com.magicrealms.magicchat.core.store.ChannelStorage;
import com.magicrealms.magicchat.core.store.MemberStorage;
import com.magicrealms.magiclib.common.command.annotations.Command;
import com.magicrealms.magiclib.common.command.annotations.CommandListener;
import com.magicrealms.magiclib.common.command.enums.PermissionType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Ryan-0916
 * @Desc 频道部分指令
 * @date 2025-04-09
 */
@CommandListener
@SuppressWarnings("unused")
public class ChannelController {

    @Command(text = "^join\\s\\S+", permissionType = PermissionType.PLAYER)
    public void join(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Member member = MemberStorage.getInstance().retrieveMember(player);
        AbstractChannel abstractChannel = ChannelStorage.getInstance().retrieveChannel(args[1]);
        member.resetChannel(abstractChannel);
    }


    @Command(text = "^test$", permissionType = PermissionType.PLAYER)
    public void test(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Member member = MemberStorage.getInstance().retrieveMember(player);
        TypewriterMessage message = (TypewriterMessage) new MessageBuilder(player.getUniqueId(), "再见了妈妈今晚我就要远航，我是大傻逼，我是一个大大的傻逼，我是一个大大大大大大大大傻逼")
                .setPrefix("刘一鸣:")
                .setPrintTick(6 * 20)
                .build(MessageType.TYPEWRITER);
        member.sendMessage(message);
    }
}
