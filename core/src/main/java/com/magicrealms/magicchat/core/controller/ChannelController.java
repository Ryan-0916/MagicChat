package com.magicrealms.magicchat.core.controller;

import com.magicrealms.magicchat.core.channel.entity.AbstractChannel;
import com.magicrealms.magicchat.core.entity.Member;
import com.magicrealms.magicchat.core.message.builder.MessageBuilder;
import com.magicrealms.magicchat.core.message.builder.OptionBuilder;
import com.magicrealms.magicchat.core.message.entity.AbstractMessage;
import com.magicrealms.magicchat.core.message.entity.exclusive.SelectorMessage;
import com.magicrealms.magicchat.core.message.entity.exclusive.TypewriterMessage;
import com.magicrealms.magicchat.core.message.enums.MessageType;
import com.magicrealms.magicchat.core.message.enums.OptionType;
import com.magicrealms.magicchat.core.store.ChannelStorage;
import com.magicrealms.magicchat.core.store.MemberStorage;
import com.magicrealms.magiclib.common.command.annotations.Command;
import com.magicrealms.magiclib.common.command.annotations.CommandListener;
import com.magicrealms.magiclib.common.command.enums.PermissionType;
import com.magicrealms.magiclib.common.command.records.ExecutableCommand;
import com.magicrealms.magiclib.paper.MagicLib;
import com.magicrealms.magiclib.paper.dispatcher.MessageDispatcher;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;

/**
 * @author Ryan-0916
 * @Desc 说明
 * @date 2025-04-09
 */
@CommandListener
@SuppressWarnings("unused")
public class ChannelController {

    @Command(text = "^join\\s\\S+$", permissionType = PermissionType.PLAYER)
    public void join(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Member member = MemberStorage.getInstance().retrieveMember(player);
        AbstractChannel abstractChannel = ChannelStorage.getInstance().retrieveChannel(args[1]);
        member.resetChannel(abstractChannel);
        MessageDispatcher.getInstance().sendMessage(MagicLib.getInstance(), sender, "切换成功");
    }

    @Command(text = "^test$", permissionType = PermissionType.PLAYER)
    public void test(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Member member = MemberStorage.getInstance().retrieveMember(player);
        AbstractMessage abstractMessage = new MessageBuilder(null,
                "&R你好，有一些问题想问您，请选择下列选项")
                .setPrefix("&R&m&l------------------------------------\n")
                .setSuffix("&R\n\n滚轮选择您的选项按F确认您的选项\n\n\n&m&l------------------------------------")
                .setPrintTick(5 * 20)
                .setOptions(List.of(
                       new OptionBuilder("&R<GRAY>请选择我，我是选项1",
                               "&R<RED>>>>请选择我我是选项1")
                               .setCommandFunction(m -> {
                                   Player player1 = Bukkit.getPlayer(m.getMemberId());
                                   if (player1 == null) {
                                       return List.of();
                                   }

                                   if ("Ryan0916".equals(player1.getName())) {
                                       return List.of(
                                               ExecutableCommand.ofConsole("say 我就知道你是帅哥"),
                                               ExecutableCommand.ofConsole("say 你比那个 Liannai 帅多了")
                                               );
                                   }

                                   return List.of(
                                           ExecutableCommand.ofConsole("say 丑逼"),
                                           ExecutableCommand.ofConsole("say 丑逼 x 2")
                                   );
                               }).build(OptionType.COMMAND),
                        new OptionBuilder("<GRAY>请选择我，我是选项2",
                                "<RED>>>>请选择我我是选项2")
                                .setCommandFunction(m -> {
                                    Player player1 = Bukkit.getPlayer(m.getMemberId());
                                    if (player1 == null) {
                                        return List.of();
                                    }

                                    if ("Ryan0916".equals(player1.getName())) {
                                        return List.of(
                                                ExecutableCommand.ofConsole("say 我就知道你是帅哥"),
                                                ExecutableCommand.ofConsole("say 你比那个 Liannai 帅多了")
                                        );
                                    }

                                    return List.of(
                                            ExecutableCommand.ofConsole("say 丑逼"),
                                            ExecutableCommand.ofConsole("say 丑逼 x 2")
                                    );
                                }).build(OptionType.COMMAND)
                )).build(MessageType.SELECTOR);
        member.sendMessage((SelectorMessage) abstractMessage);
    }


}
