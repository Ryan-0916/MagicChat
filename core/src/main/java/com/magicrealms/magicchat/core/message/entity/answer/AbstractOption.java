package com.magicrealms.magicchat.core.message.entity.answer;

import lombok.Getter;

/**
 * @author Ryan-0916
 * @Desc 说明
 * @date 2025-04-21
 */
@Getter
public abstract class AbstractOption {

    private final String text;

    private final String selectedText;

    private final String permissionNode;

    private boolean isSelected;

    public abstract void selected();

    public AbstractOption(String text,
                          String selectedText, String permissionNode) {
        this.text = text;
        this.selectedText = selectedText;
        this.permissionNode = permissionNode;
    }
}
