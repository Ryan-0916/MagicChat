package com.magicrealms.magicchat.api.channel.bungee;

import java.util.UUID;

/**
 * @author Ryan-0916
 * @Desc 消息撤回记录类
 * @param messageId 消息ID
 * @param receivedBy 撤回者
 * @date 2025-04-25
 */
public record MessageRetract(UUID messageId, UUID receivedBy) {

    public static MessageRetract of (UUID messageId, UUID receivedBy) {
        return new MessageRetract(messageId, receivedBy);
    }

}
