package com.magicrealms.magicchat.core.format;

import com.magicrealms.magicchat.core.format.entity.FormatDecoration;
import com.magicrealms.magicchat.core.format.entity.MessageFormatConfig;
import com.magicrealms.magiclib.common.utils.StringUtil;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Ryan-0916
 * @Desc 说明
 * @date 2025-04-24
 */
public class MessageComponentBuilder {

    private final MessageDecorator decorator = new MessageDecorator();

    public String buildMessageComponent(MessageFormatConfig config, String originalMsg,
                                        UUID messageUUID) {
        originalMsg =  originalMsg.replaceAll("(</?[a-zA-Z][^>]*>)", "\\\\$1");

        String eventMsg = new StringBuilder()
                .append(config.getMessage().getPrefix())
                /* Message 部分 */
                .append(decorator.decorateWithEvent(originalMsg, config.getMessage().getEvent()))
                /* 前缀部分 */
                .insert(0,
                        config.getPrefixes().stream()
                                .map(this::buildDecoration)
                                .collect(Collectors.joining()))
                /* 后缀部分 */
                .append(config.getSuffixes().stream()
                        .map(this::buildDecoration)
                        .collect(Collectors.joining())).toString();

        System.out.println(eventMsg);

        return StringUtil.replacePlaceholder(eventMsg, "message_id", messageUUID.toString());
    }

    private String buildDecoration(FormatDecoration decoration) {
        return decorator.decorateWithEvent(decoration.getText(), decoration.getEvent());
    }
}
