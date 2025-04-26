package com.magicrealms.magicchat.core.message.builder;

import com.magicrealms.magicchat.core.member.Member;
import com.magicrealms.magicchat.core.message.ExclusiveMessage;
import com.magicrealms.magicchat.core.message.option.AbstractOption;
import com.magicrealms.magicchat.core.message.option.CommandOption;
import com.magicrealms.magicchat.core.message.option.FollowUpMessageOption;
import com.magicrealms.magicchat.core.message.enums.OptionType;
import com.magicrealms.magiclib.common.command.records.ExecutableCommand;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.function.Function;

/**
 * @author Ryan-0916
 * @Desc OptionBuilder 类用于构建选项对象，允许根据不同的选项类型生成不同的选项。
 * 它使用链式方法来设置不同的功能，如后续消息和命令执行功能。
 * @date 2025-04-22
 */
@SuppressWarnings("unused")
public class OptionBuilder {

    private final String text; // 选项的文本内容
    private final String selectedText; // 选项被选中时的文本内容
    private Function<Member, @NotNull ExclusiveMessage> followUpMessageFunction; // 用于生成后续消息的函数
    private Function<Member, @NotNull List<ExecutableCommand>> commandFunction; // 用于生成命令列表的函数

    public OptionBuilder(String text, String selectedText) {
        this.text = text;
        this.selectedText = selectedText;
    }

    /**
     * 设置后续消息的生成函数。
     * 该函数将根据选中的成员生成一个后续消息对象。
     * @param followUpMessageFunction 用于生成后续消息的函数
     * @return 当前 OptionBuilder 实例，用于链式调用
     */
    public OptionBuilder setFollowUpMessageFunction(Function<Member, @NotNull ExclusiveMessage> followUpMessageFunction) {
        this.followUpMessageFunction = followUpMessageFunction;
        return this;
    }

    /**
     * 设置命令的生成函数。
     * 该函数将根据选中的成员生成一个包含多个可执行命令的列表。
     * @param commandFunction 用于生成命令列表的函数
     * @return 当前 OptionBuilder 实例，用于链式调用
     */
    public OptionBuilder setCommandFunction(Function<Member, @NotNull List<ExecutableCommand>> commandFunction) {
        this.commandFunction = commandFunction;
        return this;
    }

    /**
     * 根据指定的选项类型构建不同类型的选项对象。
     * @param type 选项类型，决定构建哪种具体的选项
     * @return 构建好的 AbstractOption 子类实例
     */
    public AbstractOption build(OptionType type) {
        return switch (type) {
            case COMMAND -> new CommandOption(text, selectedText, commandFunction); // 构建命令选项
            case FOLLOW_UP_MESSAGE -> new FollowUpMessageOption(text, selectedText, followUpMessageFunction); // 构建后续消息选项
        };
    }
}
