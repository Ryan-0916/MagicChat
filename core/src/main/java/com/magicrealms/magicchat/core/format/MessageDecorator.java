package com.magicrealms.magicchat.core.format;

import com.magicrealms.magicchat.core.format.entity.FormatEvent;

/**
 * @author Ryan-0916
 * @Desc 说明
 * @date 2025-04-24
 */
public class MessageDecorator {
    private static final String HOVER_TAG = "<hover:show_text:'%s'>";
    private static final String CLICK_TAG = "<click:%s:'%s'>";
    private static final String INSERT_TAG = "<insert:'%s'>";

    public String decorateWithEvent(String text, FormatEvent event) {
        StringBuilder builder = new StringBuilder();

        /* 处理 Hover */
        if (event.hasHover()) {
            builder.append(String.format(HOVER_TAG,
                    String.join("<newline>", event.getHover())));
        }

        /* 处理特殊点击事件 */
        if (event.hasCommand()) {
            String command = event.getCommand().startsWith("/") ? event.getCommand() : "/" + event.getCommand();
            builder.append(String.format(CLICK_TAG, "run_command", command));
        } else if (event.hasUrl()) {
            builder.append(String.format(CLICK_TAG, "open_url", event.getUrl()));
        } else if (event.hasCopy()) {
            builder.append(String.format(CLICK_TAG, "copy_to_clipboard", event.getCopy()));
        } else if (event.hasSuggest()) {
            builder.append(String.format(CLICK_TAG, "suggest_command", event.getSuggest()));
        }

        /* 处理 Insert 事件 */
        if (event.hasInsertion()) {
            builder.append(String.format(INSERT_TAG, event.getInsertion()));
        }

        /* 处理文本 */
        builder.append(text);

        /* 添加 Insert 闭合标签 */
        if (event.hasInsertion()) {
            builder.append("</insert>");
        }

        /* 添加 Click 闭合标签 */
        if (event.hasAnyClick()) {
            builder.append("</click>");
        }

        /* 添加 Hover 闭合标签 */
        if (event.hasHover()) {
            builder.append("</hover>");
        }

        return builder.toString();
    }

}
