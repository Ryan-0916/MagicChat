package com.magicrealms.magicchat.core.format;

import com.magicrealms.magicchat.core.MagicChat;
import com.magicrealms.magicchat.core.format.entity.MessageFormatConfig;
import com.magicrealms.magicchat.core.format.filter.FormatConfigFilter;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * @author Ryan-0916
 * @Desc 格式化管理器 - 负责加载和管理消息格式配置
 * @date 2025-04-24
 */
@Getter
public class FormatManager {

    private final FormatConfigLoader configLoader;
    private final MessageComponentBuilder componentBuilder;

    public FormatManager(MagicChat plugin) {
        this.configLoader = new FormatConfigLoader(plugin);
        this.componentBuilder = new MessageComponentBuilder();
    }

    public String format(Player player, String msg, UUID messageId) {
        return configLoader.getConfigs().stream()
                .filter(e -> FormatConfigFilter.INSTANCE.filter(e, player))
                .max(Comparator.comparingInt(MessageFormatConfig::getPriority))
                .map(config -> componentBuilder.buildMessageComponent(config, msg, messageId))
                .orElse(msg);
    }
}