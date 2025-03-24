package com.margicrealms.magicchat.common.channel.factory;

import com.margicrealms.magicchat.common.channel.entity.AbstractChannel;
import com.margicrealms.magicchat.common.channel.enums.ChannelType;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ryan-0916
 * @Desc 频道工厂
 * @date 2025-03-13
 */
@Getter
public class ChannelFactory {

    private Map<String, AbstractChannel> channels = new HashMap<>();

    private static volatile ChannelFactory INSTANCE;

    private ChannelFactory() {}

    public static ChannelFactory getInstance() {
        if (INSTANCE == null) {
            synchronized(ChannelFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ChannelFactory();
                }
            }
        }
        return INSTANCE;
    }

    public AbstractChannel joinChannel(String channelId, String channelName, ChannelType type) {

    }

    public void leaveChannel(Player player) {

    }

}
