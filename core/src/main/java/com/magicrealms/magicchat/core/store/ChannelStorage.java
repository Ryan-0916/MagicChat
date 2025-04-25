package com.magicrealms.magicchat.core.store;

import com.magicrealms.magicchat.core.channel.entity.AbstractChannel;
import com.magicrealms.magicchat.core.channel.entity.Channel;
import org.jetbrains.annotations.NotNull;

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
public class ChannelStorage {

    private static volatile ChannelStorage instance;

    private final Map<String, AbstractChannel> channels;

    private ChannelStorage() {
        channels = new ConcurrentHashMap<>();
    }

    public static ChannelStorage getInstance() {
        if (instance == null) {
            synchronized (ChannelStorage.class) {
                if (instance == null) {
                    instance = new ChannelStorage();
                }
            }
        }
        return instance;
    }

    /**
     * 检索公共频道
     * 私有频道禁止跨服，消息不做同步记录
     * 检查频道是否已经注册过，如果未注册，则为其创建并注册一个新的公共频道对象。
     * 如果频道已经存在，直接返回已有的频道对象。
     * @param name 频道唯一名称
     * @return 返回已注册或新创建的频道对象
     */
    @NotNull
    public AbstractChannel retrieveChannel(String name) {
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

    public void unsubscribeChannel() {
        channels.values().stream().filter(e -> e instanceof Channel)
                .forEach(e -> ((Channel) e).unsubscribe());
    }

    public void subscribeChannel() {
        channels.values().stream().filter(e -> e instanceof Channel)
                .forEach(e -> ((Channel) e).subscribe());
    }
}
