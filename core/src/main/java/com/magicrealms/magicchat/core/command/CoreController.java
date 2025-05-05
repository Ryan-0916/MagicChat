package com.magicrealms.magicchat.core.command;

import com.magicrealms.magicchat.core.MagicChat;
import com.magicrealms.magiclib.bukkit.command.annotations.Command;
import com.magicrealms.magiclib.bukkit.command.annotations.CommandListener;
import com.magicrealms.magiclib.bukkit.command.enums.PermissionType;
import com.magicrealms.magiclib.core.dispatcher.MessageDispatcher;
import org.bukkit.command.CommandSender;

import java.util.Locale;

import static com.magicrealms.magicchat.core.MagicChatConstant.YML_LANGUAGE;

/**
 * @author Ryan-0916
 * @Desc 核心部分命令
 * @date 2025-04-25
 */
@CommandListener
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

    @Command(text = "^Reload\\s(?!all\\b)\\S+$", permissionType = PermissionType.CONSOLE_OR_PERMISSION,
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
