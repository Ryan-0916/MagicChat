package com.magicrealms.magicchat.core.command;

import com.magicrealms.magiclib.common.command.annotations.CommandListener;
import com.magicrealms.magiclib.common.command.annotations.TabComplete;
import com.magicrealms.magiclib.common.command.enums.PermissionType;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Ryan-0916
 * @Desc 频道部分命令补全
 * @date 2025-04-26
 */
@CommandListener
@SuppressWarnings("unused")
public class ChannelTabController {

    @TabComplete(text = "^\\s?$",
            permissionType = PermissionType.PERMISSION,
            permission = "magic.command.magicchat.all||magic.command.magicchat.retract")
    public List<String> first(CommandSender sender, String[] args) {
        return Stream.of("retract")
                .collect(Collectors.toList());
    }

    @TabComplete(text = "^\\S+$",
            permissionType = PermissionType.PERMISSION,
            permission = "magic.command.magicchat.all||magic.command.magicchat.retract")
    public List<String> firstTab(CommandSender sender, String[] args) {
        return Stream.of("retract")
                .filter(e ->
                        StringUtils.startsWithIgnoreCase(e, args[0]))
                .collect(Collectors.toList());
    }
}
