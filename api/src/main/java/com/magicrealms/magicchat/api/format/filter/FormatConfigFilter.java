package com.magicrealms.magicchat.api.format.filter;

import com.magicrealms.magicchat.api.format.entity.MessageFormatConfig;
import com.magicrealms.magicchat.api.format.enums.FormatRole;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;

/**
 * @author Ryan-0916
 * @Desc 格式化过滤器
 * @date 2025-04-24
 */
public class FormatConfigFilter {

    public static final FormatConfigFilter INSTANCE = new FormatConfigFilter();
    private FormatConfigFilter(){}

    public Boolean filter(MessageFormatConfig config, Player player) {
        return config.getRole() == FormatRole.PLAYER
                || (config.getRole() == FormatRole.OP && player.isOp())
                || (config.getRole() == FormatRole.PERMISSION &&
                (StringUtils.isBlank(config.getPermissionNode()) || player.hasPermission(config.getPermissionNode())));
    }
}
