package com.magicrealms.magicchat.api.channel.bungee;


import org.jetbrains.annotations.Nullable;

/**
 * @author Ryan-0916
 * @Desc 记录跨服事件动作
 * @param actionType 事件类型
 * @param serializeBase64Msg 序列化消息 - 消息信息
 * @param messageRetract 撤回日志 - 记录撤回信息
 * @date 2025-04-25
 */
public record BungeeAction(BungeeActionType actionType,
                           @Nullable String serializeBase64Msg,
                           @Nullable MessageRetract messageRetract) {

    public static BungeeAction ofSend(String serializeBase64Msg) {
        return new BungeeAction(BungeeActionType.SEND, serializeBase64Msg, null);
    }

    public static BungeeAction ofRetract(MessageRetract messageRetract) {
        return new BungeeAction(BungeeActionType.RETRACT, null, messageRetract);
    }

}
