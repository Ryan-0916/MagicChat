package com.magicrealms.magicchat.core.command;

import com.magicrealms.magicchat.core.BukkitMagicChat;
import com.magicrealms.magiclib.bukkit.command.annotations.Command;
import com.magicrealms.magiclib.bukkit.command.annotations.CommandListener;
import com.magicrealms.magiclib.bukkit.command.enums.PermissionType;
import com.magicrealms.magiclib.core.dispatcher.MessageDispatcher;
import org.bukkit.command.CommandSender;

import java.util.Locale;

import static com.magicrealms.magicchat.common.MagicChatConstant.YML_LANGUAGE;
import static com.magicrealms.magicchat.common.MagicChatConstant.YML_REDIS;

/**
 * @author Ryan-0916
 * @Desc 核心部分命令
 * @date 2025-04-25
 */
@CommandListener
@SuppressWarnings("unused")
public class CoreController {


    private void setupCommon() {
        /* 重置 名牌 部分 */
        BukkitMagicChat.getInstance().setupFormat();
    }

    @Command(text = "^Reload$",
            permissionType = PermissionType.CONSOLE_OR_PERMISSION,
            permission = "magic.command.magicchat.all||magic.command.magicchat.reload", label = "^magicChat$")
    public void reload(CommandSender sender, String[] args){
        BukkitMagicChat.getInstance().getConfigManager()
                .reloadConfig(YML_REDIS);
        setupCommon();
        MessageDispatcher.getInstance()
                .sendMessage(BukkitMagicChat.getInstance(), sender,
                        BukkitMagicChat.getInstance().getConfigManager()
                                .getYmlValue(YML_LANGUAGE,
                                        "PlayerMessage.Success.ReloadFile"));
    }

    @Command(text = "^Reload\\sAll$",
            permissionType = PermissionType.CONSOLE_OR_PERMISSION,
            permission = "magic.command.magicchat.all||magic.command.magicchat.reload", label = "^magicChat$")
    public void reloadAll(CommandSender sender, String[] args){
        BukkitMagicChat.getInstance().getConfigManager().reloadAllConfig();
        setupCommon();
        MessageDispatcher.getInstance()
                .sendMessage(BukkitMagicChat.getInstance(), sender,
                        BukkitMagicChat.getInstance().getConfigManager()
                                .getYmlValue(YML_LANGUAGE,
                                        "PlayerMessage.Success.ReloadFile"));
    }

    @Command(text = "^Reload\\s(?!all\\b)\\S+$", permissionType = PermissionType.CONSOLE_OR_PERMISSION,
            permission = "magic.command.magicchat.all||magic.command.magicchat.reload", label = "^magicChat$")
    public void reloadBy(CommandSender sender, String[] args){
        BukkitMagicChat.getInstance().getConfigManager()
                .reloadConfig(args[1], e -> {
                    if (!e) {
                        MessageDispatcher.getInstance().sendMessage(BukkitMagicChat.getInstance(), sender,
                                BukkitMagicChat.getInstance().getConfigManager().getYmlValue(YML_LANGUAGE,
                                        "PlayerMessage.Error.ReloadFile"));
                        return;
                    }
                    switch (args[1].toLowerCase(Locale.ROOT)) {
                        case "redis" ->  BukkitMagicChat.getInstance().setupRedisStore();
                        case "format" -> BukkitMagicChat.getInstance().setupFormat();
                    }
                    MessageDispatcher.getInstance().sendMessage(BukkitMagicChat.getInstance(), sender,
                            BukkitMagicChat.getInstance().getConfigManager()
                                    .getYmlValue(YML_LANGUAGE,
                                            "PlayerMessage.Success.ReloadFile"));
                });
    }
}
