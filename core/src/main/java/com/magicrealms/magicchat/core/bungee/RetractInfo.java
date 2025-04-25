package com.magicrealms.magicchat.core.bungee;

import java.util.UUID;

/**
 * @author Ryan-0916
 * @Desc 说明
 * @date 2025-04-25
 */
public record RetractInfo(UUID retractMessageId, UUID receivedBy) {

    public static RetractInfo of(UUID retractMessageId, UUID receivedBy) {
        return new RetractInfo(retractMessageId, receivedBy);
    }

}
