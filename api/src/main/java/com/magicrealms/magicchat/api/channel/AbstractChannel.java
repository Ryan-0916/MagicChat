package com.magicrealms.magicchat.api.channel;

import com.magicrealms.magicchat.api.member.Member;
import com.magicrealms.magicchat.api.message.ChannelMessage;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Ryan-0916
 * @Desc 抽象消息频道类
 * @date 2025-03-13
 */
@Getter
public abstract class AbstractChannel {

    /** 频道名称 */
    protected String channelName;

    public AbstractChannel(String channelName) {
        this.channelName = StringUtils.upperCase(channelName);
    }

    /**
     * 发送消息
     * 该方法是一个抽象方法，具体的发送消息操作由子类实现。
     * 子类需要定义如何实际将消息发送到目标频道或接收方。
     * @param sender 发送者
     * @param message 要发送的公共消息 {@link ChannelMessage}
     */
    public abstract void sendMessage(Member sender, ChannelMessage message);

    /**
     * 加入频道
     * 该方法用于将成员加入到指定的频道中，使该成员能够接收到该频道的消息。
     * @param member 要加入频道的成员 {@link Member}
     */
    public abstract void joinChannel(Member member);

    /**
     * 离开频道
     * 该方法用于将成员从当前频道中移除，停止接收该频道的消息。
     * @param member 要离开频道的成员 {@link Member}
     */
    public abstract void leaveChannel(Member member);
}
