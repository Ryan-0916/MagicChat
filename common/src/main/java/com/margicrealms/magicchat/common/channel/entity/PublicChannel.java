package com.margicrealms.magicchat.common.channel.entity;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author Ryan-0916
 * @Desc 公共渠道类
 * @date 2025-03-13
 */
@Getter
public class PublicChannel extends AbstractChannel {

    private final Set<UUID> subscribers;

    public PublicChannel(String channelId, String channelName) {
        super(channelId, channelName);
        subscribers = new HashSet<>();
    }

    @Override
    public void joinChannel(Player player) {
        subscribers.add(player.getUniqueId());
    }

    @Override
    public void leaveChannel(Player player) {
        subscribers.remove(player.getUniqueId());
    }

}
