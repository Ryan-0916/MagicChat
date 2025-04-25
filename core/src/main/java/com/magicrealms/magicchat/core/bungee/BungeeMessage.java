package com.magicrealms.magicchat.core.bungee;


/**
 * @author Ryan-0916
 * @date 2025-04-25
 */

public record BungeeMessage(BungeeMessageEvent event, String serializeBase64Msg,
                            RetractInfo retractInfo) {

    public static BungeeMessage ofSend(String serializeBase64Msg) {
        return new BungeeMessage(BungeeMessageEvent.SEND, serializeBase64Msg, null);
    }

    public static BungeeMessage ofRetract(RetractInfo retractInfo) {
        return new BungeeMessage(BungeeMessageEvent.RETRACT, null, retractInfo);
    }

}
