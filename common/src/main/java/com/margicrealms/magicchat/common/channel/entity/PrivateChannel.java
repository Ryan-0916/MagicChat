package com.margicrealms.magicchat.common.channel.entity;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author Ryan-0916
 * @Desc 私有渠道类
 * @date 2025-03-13
 */
@Getter
public class PrivateChannel extends AbstractChannel {

    private UUID exclusive;

    public PrivateChannel(String channelId, String channelName, Player player) {
        super(channelId, channelName);
        joinChannel(player);
    }

    @Override
    public void joinChannel(Player player) {
        this.exclusive = player.getUniqueId();
    }

    @Override
    public void leaveChannel(Player player) {
        this.exclusive = null;
    }
}
