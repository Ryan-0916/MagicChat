package com.magicrealms.magicchat.api.message.option;

import com.magicrealms.magicchat.api.member.Member;
import com.magicrealms.magicchat.api.message.ExclusiveMessage;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * FollowUpMessageOption 类表示一个对话选项，它在用户选择时会触发后续消息的发送。
 * 该类继承自 AbstractOption，并通过提供的 Function 来确定用户选择后的消息内容。
 * @author Ryan-0916
 * @Desc 该类用于定义在选项被选择时，通过指定的逻辑生成并发送消息。
 * @date 2025-04-22
 */
public class FollowUpMessageOption extends AbstractOption {

    private final Function<Member, @NotNull ExclusiveMessage> selectedFunction;

    /**
     * 构造一个 FollowUpMessageOption 对象。
     * @param text          选项的默认显示文本。当该选项未被选中时显示此文本。
     * @param selectedText  选项被选中时显示的文本。
     * @param selectedFunction 用于生成后续消息的函数。此函数接收一个 Member 对象并返回一个 ExclusiveMessage 对象，
     *                         该消息将发送给用户。
     */
    public FollowUpMessageOption(String text, String selectedText,
                                 Function<Member, @NotNull ExclusiveMessage> selectedFunction) {
        super(text, selectedText);
        this.selectedFunction = selectedFunction;
    }

    /**
     * 当该选项被选择时调用此方法，该方法会生成一个 ExclusiveMessage 并发送给指定的成员。
     * @param member 被选中的成员对象，表示对话中的当前用户。
     */
    @Override
    public void selected(Member member) {
        ExclusiveMessage message = selectedFunction.apply(member);
        member.sendMessage(message);
    }
}
