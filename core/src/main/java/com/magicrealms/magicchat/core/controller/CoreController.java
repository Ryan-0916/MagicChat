package com.magicrealms.magicchat.core.controller;

import com.magicrealms.magicchat.core.MagicChat;
import com.magicrealms.magiclib.common.command.annotations.Command;
import com.magicrealms.magiclib.common.command.enums.PermissionType;
import com.magicrealms.magiclib.paper.dispatcher.MessageDispatcher;
import org.bukkit.command.CommandSender;

import java.util.Locale;

import static com.magicrealms.magicchat.core.MagicChatConstant.YML_LANGUAGE;

/**
 * @author Ryan-0916
 * @date 2025-04-25
 */
@SuppressWarnings("unused")
public class CoreController {

    @Command(text = "^Reload$",
            permissionType = PermissionType.CONSOLE_OR_PERMISSION,
            permission = "magic.command.magicchat.all||magic.command.magicchat.reload")
    public void reloadAll(CommandSender sender, String[] args){
        MagicChat.getInstance().getConfigManager().reloadAllConfig();
        MagicChat.getInstance().setupRedisStore();
        MessageDispatcher.getInstance()
                .sendMessage(MagicChat.getInstance(), sender,
                        MagicChat.getInstance().getConfigManager()
                                .getYmlValue(YML_LANGUAGE,
                        "PlayerMessage.Success.ReloadFile"));
    }

    @Command(text = "^Reload\\s\\S+$", permissionType = PermissionType.CONSOLE_OR_PERMISSION,
            permission = "magic.command.magicchat.all||magic.command.magicchat.reload")
    public void reload(CommandSender sender, String[] args){
        MagicChat.getInstance().getConfigManager()
                .reloadConfig(args[1], e -> {
            if (!e) {
                MessageDispatcher.getInstance().sendMessage(MagicChat.getInstance(), sender,
                        MagicChat.getInstance().getConfigManager().getYmlValue(YML_LANGUAGE,
                                "PlayerMessage.Error.ReloadFile"));
                return;
            }
            switch (args[1].toLowerCase(Locale.ROOT)) {
                case "redis" ->  MagicChat.getInstance().setupRedisStore();
                case "format" -> MagicChat.getInstance().setupFormat();
            }
            MessageDispatcher.getInstance().sendMessage(MagicChat.getInstance(), sender,
                    MagicChat.getInstance().getConfigManager()
                            .getYmlValue(YML_LANGUAGE,
                            "PlayerMessage.Success.ReloadFile"));
        });
    }
}
