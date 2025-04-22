package com.magicrealms.magicchat.core.message.entity.answer;

/**
 * @author Ryan-0916
 * @Desc 说明
 * @date 2025-04-21
 */
public class CommandOption extends AbstractOption{

    public CommandOption(String text,
                         String selectedText,
                         String permissionNode) {
        super(text, selectedText, permissionNode);
    }

    @Override
    public void selected() {

    }
}
