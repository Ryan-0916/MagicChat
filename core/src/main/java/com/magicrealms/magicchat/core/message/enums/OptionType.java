package com.magicrealms.magicchat.core.message.enums;

/**
 * OptionType 枚举类用于定义不同的选项类型。
 * 每个选项类型决定了如何处理选项的行为，例如是执行命令还是生成后续消息。
 * @author Ryan-0916
 * @Desc  定义了两种选项类型：命令和后续消息。
 * @date 2025-04-22
 */
public enum OptionType {
    /**
     * 代表执行命令类型的选项。
     * 该类型的选项会触发相关的命令执行行为。
     */
    COMMAND,

    /**
     * 代表后续消息类型的选项。
     * 该类型的选项会触发生成和发送后续消息的行为。
     */
    FOLLOW_UP_MESSAGE
}
