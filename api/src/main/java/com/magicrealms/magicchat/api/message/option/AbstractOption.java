package com.magicrealms.magicchat.api.message.option;

import com.magicrealms.magicchat.api.member.Member;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Ryan-0916
 * @Desc 抽象选项类
 * 该类表示消息中的选项，每个选项包括显示的文本和被选中时的文本。
 * 通过该类，开发者可以自定义选项的行为，例如在选中时显示不同的文本。
 * 该类为具体的选项实现提供了基础，具体实现类可以根据需要覆盖 `selected()` 方法来定义选中后的行为。
 * @date 2025-04-21
 */
@Getter
public abstract class AbstractOption {

    /* 选项的默认显示文本 */
    private final String text;

    /* 选项被选中时显示的文本 */
    private final String selectedText;

    /* 标记当前选项是否被选中 */
    @Setter
    private boolean isSelected;

    /**
     * 选中该选项后的行为处理方法。
     * @param member 选中的成员对象，包含当前选项的相关信息。实现类应根据该对象的属性，
     *               实现具体的选中行为（如更新界面、执行指令等）。
     */
    public abstract void selected(Member member);

    /**
     * 构造一个抽象选项对象
     * @param text        选项的默认显示文本
     * @param selectedText 选项被选中时显示的文本
     */
    public AbstractOption(String text,
                          String selectedText) {
        this.text = text;
        this.selectedText = selectedText;
    }
}
