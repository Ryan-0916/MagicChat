package com.magicrealms.magicchat.api.format;

import com.magicrealms.magicchat.api.format.entity.FormatDecoration;
import com.magicrealms.magicchat.api.format.entity.MessageFormatConfig;
import com.magicrealms.magiclib.common.utils.StringUtil;
import com.magicrealms.magiclib.core.utils.PlaceholderUtil;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Ryan-0916
 * @Desc 消息构造器
 * @date 2025-04-24
 */
public class MessageComponentBuilder {

    private static final String MSG = "%message%";

    private final MessageDecorator decorator = new MessageDecorator();

    public String buildMessageComponent(Player player, MessageFormatConfig config, String originalMessage, UUID messageUUID) {
        String msg = PlaceholderUtil.replacePlaceholders(
                new StringBuilder()
                        .append(config.getMessage().getPrefix())
                        /* Message 部分 */
                        .append(decorator.decorateWithEvent(MSG, config.getMessage().getEvent()))
                        /* 前缀部分 */
                        .insert(0, config.getPrefixes().stream()
                                .map(this::buildDecoration)
                                .collect(Collectors.joining()))
                        /* 后缀部分 */
                        .append(config.getSuffixes().stream()
                                .map(this::buildDecoration)
                                .collect(Collectors.joining())).toString(), player);
        return StringUtil.replacePlaceholders(msg, Map.of("message", originalMessage, "message_id", messageUUID.toString()));
    }

    private String buildDecoration(FormatDecoration decoration) {
        return decorator.decorateWithEvent(decoration.getText(), decoration.getEvent());
    }
}
