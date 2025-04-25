package com.magicrealms.magicchat.core.store;

import com.magicrealms.magicchat.core.MagicChat;
import com.magicrealms.magicchat.core.bungee.RetractInfo;
import com.magicrealms.magicchat.core.channel.entity.AbstractChannel;
import com.magicrealms.magicchat.core.channel.entity.Channel;
import com.magicrealms.magicchat.core.member.Member;
import com.magicrealms.magicchat.core.message.entity.ChannelMessage;
import com.magicrealms.magicchat.core.message.entity.ExclusiveMessage;
import com.magicrealms.magiclib.common.utils.SerializationUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.magicrealms.magicchat.core.MagicChatConstant.*;

/**
 * @author Ryan-0916
 * @Desc
 * 聊天记录存储类
 * 该类用于存储和管理频道和成员的消息历史记录。
 * 通过该类可以初始化消息历史、添加消息并控制历史记录的最大长度。
 * @date 2025-04-08
 */
@SuppressWarnings("unused")
public class MessageHistoryStorage {

    private static volatile MessageHistoryStorage INSTANCE;

    private final MessageHistoryRepository<String, ChannelMessage>
            channelStorage = new MessageHistoryRepository<>();

    private final MessageHistoryRepository<String, ExclusiveMessage>
            memberStorage = new MessageHistoryRepository<>();

    public static MessageHistoryStorage getInstance() {
        if (INSTANCE == null) {
            synchronized (MessageHistoryStorage.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MessageHistoryStorage();
                }
            }
        }
        return INSTANCE;
    }


    /**
     * 初始化成员的消息历史记录
     * 该方法首先检查成员是否已有历史记录。如果没有历史记录，则从 Redis 获取成员的聊天记录，并进行初始化。
     * 如果成员的历史记录存在，则跳过初始化步骤。
     * @param member 成员对象，包含成员的信息（如成员ID、成员名称等）
     */
    public void initializeHistory(Member member) {
        this.initializeHistory(memberStorage, member.getMemberName(),
                String.format(MEMBER_MESSAGE_HISTORY,
                member.getMemberName()));
    }

    /**
     * 初始化频道的消息历史记录
     * 该方法首先检查频道是否已有历史记录。如果没有历史记录，则从 Redis 获取频道的聊天记录，并进行初始化。
     * 如果频道的历史记录存在，则跳过初始化步骤。
     * @param channel 频道对象，包含频道的信息（如频道名称等）
     */
    public void initializeHistory(Channel channel) {
        this.initializeHistory(memberStorage, channel.getChannelName(),
                String.format(CHANNEL_MESSAGE_HISTORY,
                        channel.getChannelName()));
    }

    public void addMessage(Member member, ExclusiveMessage msg) {
        memberStorage.addMessage(member.getMemberName(), msg);
    }

    public void addMessage(Channel channel, ChannelMessage msg) {
        channelStorage.addMessage(channel.getChannelName(), msg);
    }

    public List<ExclusiveMessage> getMessageHistory(Member member) {
        return memberStorage.getMessages(member.getMemberName());
    }

    public List<ChannelMessage> getMessageHistory(AbstractChannel channel) {
        return channelStorage.getMessages(channel.getChannelName());
    }

    private <T, M> void initializeHistory(MessageHistoryRepository<T, M> storage,
                                          T key,
                                          String redisKey) {
        /* 如果消息历史已经存在，则直接返回 */
        if (storage.containsKey(key)) {
            return;
        }
        Optional<List<String>> history = MagicChat.getInstance()
                .getRedisStore()
                .getAllValue(redisKey);
        if (history.isEmpty()) {
            storage.initialize(key);
            return;
        }
        /* 如果有历史消息，使用这些消息初始化历史记录 */
        storage.initializeWithMessages(key, history.get().stream().map(
                SerializationUtils::<M>deserializeByBase64
        ).collect(Collectors.toList()));
    }

















    /**
     * 撤回渠道消息
     * @param channel 渠道
     * @param retractInfo 渠道消息
     */
    public void retractChannelMessage(Channel channel, RetractInfo retractInfo) {
//        if (!channelStorage.containsKey(channel.getChannelName())) {
//            throw new NullPointerException("频道 '" + channel.getChannelName() + "' 没有初始化消息记录存储");
//        }
//        for (ChannelMessage msg : channelMessageHistory.get(channel.getChannelName())) {
//            if (msg.getMessageId().equals(retractInfo.retractMessageId())) { // 假设按 messageId 查找
//                msg.setRetracted(true);
//                msg.setRetractedBy(retractInfo.receivedBy());
//                break;
//            }
//        }
    }






}
