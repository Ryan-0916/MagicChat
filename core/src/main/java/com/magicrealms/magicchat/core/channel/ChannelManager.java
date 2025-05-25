package com.magicrealms.magicchat.core.channel;

import com.magicrealms.magicchat.api.channel.AbstractChannel;
import com.magicrealms.magicchat.api.channel.Channel;
import com.magicrealms.magicchat.api.channel.IChannelManager;
import com.magicrealms.magicchat.api.message.MessageHistoryStorage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author Ryan-0916
 * @Desc
 * 频道存储类
 * 该类用于存储和管理服务器内的频道
 * 通过该类可以获取频道，创建频道，销毁频道。
 * @date 2025-04-09
 */
public class ChannelManager implements IChannelManager {

    private final Map<String, AbstractChannel> channels;

    public ChannelManager() {
        channels = new ConcurrentHashMap<>();
    }


    public void unsubscribeChannel() {
        channels.values().stream().filter(e -> e instanceof Channel)
                .forEach(e -> ((Channel) e).unsubscribe());
    }

    public void subscribeChannel() {
        channels.values().stream().filter(e -> e instanceof Channel)
                .forEach(e -> ((Channel) e).subscribe());
    }

    @Override
    public AbstractChannel getChannel(String name) {
        String channelName = name.toUpperCase();
        if (channels.containsKey(channelName)) {
            return channels.get(channelName);
        }
        Channel channel = new Channel(channelName);
        /* 同步聊天记录 */
        MessageHistoryStorage.getInstance().initializeHistory(channel);
        channels.put(channelName, channel);
        return channel;
    }
}
