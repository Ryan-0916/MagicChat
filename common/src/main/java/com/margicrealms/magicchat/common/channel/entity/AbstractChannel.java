package com.margicrealms.magicchat.common.channel.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

/**
 * @author Ryan-0916
 * @Desc 抽象频道类
 * @date 2025-03-13
 */
@Getter
@AllArgsConstructor
public abstract class AbstractChannel {
    protected String channelId;
    protected String channelName;

    public abstract void joinChannel(Player player);
    public abstract void leaveChannel(Player player);


}
