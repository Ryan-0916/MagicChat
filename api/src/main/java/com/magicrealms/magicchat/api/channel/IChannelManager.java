package com.magicrealms.magicchat.api.channel;

/**
 * @author Ryan-0916
 * @Desc 用于管理频道相关方法的接口类
 * @date 2025-05-25
 */
public interface IChannelManager {

    /**
     * 获取频道
     * @param name 频道名称
     * @return 频道
     * 如若服务器中存在此频道 - 则返回该频道
     * 如若服务器中不存在此频道 - 则创建一个新的频道返回
     */
    AbstractChannel getChannel(String name);

    void unsubscribeChannel();

    void subscribeChannel();

}
