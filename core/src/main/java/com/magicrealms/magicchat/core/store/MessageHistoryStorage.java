package com.magicrealms.magicchat.core.store;

import com.magicrealms.magicchat.core.MagicChat;
import com.magicrealms.magicchat.core.bungee.RetractInfo;
import com.magicrealms.magicchat.core.channel.entity.AbstractChannel;
import com.magicrealms.magicchat.core.channel.entity.Channel;
import com.magicrealms.magicchat.core.entity.Member;
import com.magicrealms.magicchat.core.exception.HistoryStorageAlreadyExistsException;
import com.magicrealms.magicchat.core.message.entity.ExclusiveMessage;
import com.magicrealms.magicchat.core.message.entity.ChannelMessage;
import com.magicrealms.magiclib.common.utils.SerializationUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
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

    /* 频道消息历史记录 */
    private final Map<String, ConcurrentLinkedQueue<ChannelMessage>> channelMessageHistory;

    /* 成员私信消息历史记录 */
    private final Map<UUID, ConcurrentLinkedQueue<ExclusiveMessage>> memberMessageHistory;

    private MessageHistoryStorage() {
        channelMessageHistory = new ConcurrentHashMap<>();
        memberMessageHistory = new ConcurrentHashMap<>();
    }

    /**
     * 获取单例实例
     * 使用双重检查锁定实现线程安全的单例模式。
     * @return MessageHistoryStorage 实例
     */
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
     * 初始化一个新的频道消息记录存储
     * 为指定频道创建一个空的消息记录队列，避免重复创建。
     * @param channel 频道对象，用于获取频道名称并检查是否已经存在历史记录
     * @throws HistoryStorageAlreadyExistsException 如果该频道已有历史记录
     */
    private void initializeChannelMessageHistory(AbstractChannel channel) {
        initializeMessageHistory(channel.getChannelName(), channelMessageHistory);
    }

    /**
     * 初始化一个新的频道消息记录存储并同步消息内容
     * 除了初始化消息记录存储外，还将指定的消息历史同步到该频道的消息记录中。
     * 如果该频道已经有历史记录，抛出 {@link HistoryStorageAlreadyExistsException}
     * @param channel 频道对象，用于获取频道名称并检查是否已经存在历史记录
     * @param msgHistory 频道的历史消息列表，将被同步到该频道的消息记录中
     * @throws HistoryStorageAlreadyExistsException 如果该频道已有历史记录
     */
    private void initializeChannelMessageHistoryWithMessages(AbstractChannel channel, List<ChannelMessage> msgHistory) {
        initializeMessageHistory(channel.getChannelName(), channelMessageHistory);
        msgHistory.forEach(e -> addMessageToChannel(channel, e));
    }

    /**
     * 初始化成员消息记录存储
     * 为指定成员创建一个空的消息记录队列，避免重复创建。
     * 如果该成员已有消息历史记录，抛出 {@link HistoryStorageAlreadyExistsException}
     * @param member 成员对象，用于获取成员ID并检查是否已经存在历史记录
     * @throws HistoryStorageAlreadyExistsException 如果该成员已有历史记录
     */
    private void initializeMemberMessageHistory(Member member) {
        initializeMessageHistory(member.getMemberId(), memberMessageHistory);
    }

    /**
     * 初始化成员消息记录存储并同步消息内容
     * 除了初始化消息记录存储外，还将指定的消息历史同步到该成员的消息记录中。
     * @param member 成员对象，用于获取成员ID并检查是否已经存在历史记录
     * @param msgHistory 成员的历史消息列表，将被同步到该成员的消息记录中
     * @throws HistoryStorageAlreadyExistsException 如果该成员已有历史记录
     */
    private void initializeMemberMessageHistoryWithMessages(Member member, List<ExclusiveMessage> msgHistory) {
        initializeMessageHistory(member.getMemberId(), memberMessageHistory);
        msgHistory.forEach(e -> addMessageToMember(member, e));
    }

    /**
     * 公共方法：初始化消息历史记录存储
     * 用于初始化频道或成员的消息记录存储。
     * @param key 存储的唯一标识符（频道名或成员ID）
     * @param historyMap 存储映射
     * @param <T> Key的类型
     * @param <M> 存储的消息类型
     * @throws HistoryStorageAlreadyExistsException 如果频道已经初始化消息记录存储
     */
    private <T, M> void initializeMessageHistory(T key, Map<T, ConcurrentLinkedQueue<M>> historyMap) {
        if (historyMap.containsKey(key)) {
            throw new HistoryStorageAlreadyExistsException("已存在消息历史记录！");
        }
        historyMap.put(key, new ConcurrentLinkedQueue<>());
    }

    /**
     * 向指定频道添加消息记录
     * 将一条公共消息添加到指定频道的消息历史记录中。
     * @param channel 频道
     * @param message 需要添加的公共消息
     * @throws NullPointerException 如果频道未初始化消息记录存储
     */
    public void addMessageToChannel(AbstractChannel channel, ChannelMessage message) {
        if (!channelMessageHistory.containsKey(channel.getChannelName())) {
            throw new NullPointerException("频道 '" + channel.getChannelName() + "' 没有初始化消息记录存储");
        }
        addMessageToHistory(channelMessageHistory.get(channel.getChannelName()), message);
    }

    /**
     * 撤回渠道消息
     * @param channel 渠道
     * @param retractInfo 渠道消息
     */
    public void retractChannelMessage(Channel channel, RetractInfo retractInfo) {
        if (!channelMessageHistory.containsKey(channel.getChannelName())) {
            throw new NullPointerException("频道 '" + channel.getChannelName() + "' 没有初始化消息记录存储");
        }
        for (ChannelMessage msg : channelMessageHistory.get(channel.getChannelName())) {
            if (msg.getMessageId().equals(retractInfo.retractMessageId())) { // 假设按 messageId 查找
                msg.setRetracted(true);
                msg.setRetractedBy(retractInfo.receivedBy());
                break;
            }
        }
    }

    /**
     * 向指定成员添加私信消息记录
     * 将一条私信消息添加到指定成员的消息历史记录中。
     * 如果指定成员没有初始化，抛出 {@link NullPointerException}
     * @param member 成员
     * @param message 需要添加的私信消息
     * @throws NullPointerException 如果成员未初始化消息记录存储
     */
    public void addMessageToMember(Member member, ExclusiveMessage message) {
        if (!memberMessageHistory.containsKey(member.getMemberId())) {
            throw new NullPointerException("玩家 '" + member.getMemberName() + "' 没有初始化消息记录存储");
        }
        addMessageToHistory(memberMessageHistory.get(member.getMemberId()), message);
    }

    /**
     * 公共方法：向消息队列中添加消息
     * 用于添加消息并控制消息队列的最大长度。
     * 如果队列超过最大长度，将移除最早的消息。
     * @param historyQueue 消息队列
     * @param message 要添加的消息
     * @param <T> 消息类型
     */
    private <T> void addMessageToHistory(ConcurrentLinkedQueue<T> historyQueue, T message) {
        historyQueue.add(message);
        if (historyQueue.size() > MAX_HISTORY_SIZE) {
            historyQueue.poll();
        }
    }

    /**
     * 销毁指定频道的消息记录存储
     * 删除指定频道的消息历史记录。
     * @param channel 频道
     * @throws NullPointerException 如果频道没有初始化消息记录存储
     */
    public void destroyHistory(AbstractChannel channel) {
        if (!channelMessageHistory.containsKey(channel.getChannelName())) {
            throw new NullPointerException("频道 '" + channel.getChannelName() + "' 没有初始化消息记录存储");
        }
        channelMessageHistory.remove(channel.getChannelName());
    }

    /**
     * 销毁指定成员的消息记录存储
     * 删除指定成员的消息历史记录。
     * @param member 成员
     * @throws NullPointerException 如果成员没有初始化消息记录存储
     */
    public void destroyHistory(Member member) {
        if (!memberMessageHistory.containsKey(member.getMemberId())) {
            throw new NullPointerException("玩家 '" + member.getMemberName() + "' 没有初始化消息记录存储");
        }
        memberMessageHistory.remove(member.getMemberId());
    }

    /**
     * 初始化成员的消息历史记录
     * 该方法首先检查成员是否已有历史记录。如果没有历史记录，则从 Redis 获取成员的聊天记录，并进行初始化。
     * 如果成员的历史记录存在，则跳过初始化步骤。
     * @param member 成员对象，包含成员的信息（如成员ID、成员名称等）
     */
    public void initializeHistory(Member member) {
        /* 如果成员的消息历史已经存在，则直接返回 */
        if (memberMessageHistory.containsKey(member.getMemberId())) {
            return;
        }
        /* 同步聊天记录，从 Redis 获取该成员的历史消息 */
        Optional<List<String>> history = MagicChat.getInstance().getRedisStore()
                .getAllValue(MessageFormat.format(MEMBER_MESSAGE_HISTORY,
                        member.getMemberName()));
        /* 如果没有聊天记录，初始化空的消息历史 */
        if (history.isEmpty()) {
            initializeMemberMessageHistory(member);
            return;
        }
        /* 如果有历史消息，使用这些消息初始化成员的历史记录 */
        initializeMemberMessageHistoryWithMessages(member, history.get().stream()
                .map(SerializationUtils::<ExclusiveMessage>deserializeByBase64).collect(Collectors.toList()));
    }

    /**
     * 初始化频道的消息历史记录
     * 该方法首先检查频道是否已有历史记录。如果没有历史记录，则从 Redis 获取频道的聊天记录，并进行初始化。
     * 如果频道的历史记录存在，则跳过初始化步骤。
     * @param channel 频道对象，包含频道的信息（如频道名称等）
     */
    public void initializeHistory(AbstractChannel channel) {
        /* 如果频道的消息历史已经存在，则直接返回 */
        if (channelMessageHistory.containsKey(channel.getChannelName())) {
            return;
        }
        /* 同步聊天记录，从 Redis 获取该频道的历史消息 */
        Optional<List<String>> history = MagicChat.getInstance().getRedisStore()
                .getAllValue(MessageFormat.format(CHANNEL_MESSAGE_HISTORY,
                        channel.getChannelName()));
        /* 如果没有聊天记录，初始化空的消息历史 */
        if (history.isEmpty()) {
            initializeChannelMessageHistory(channel);
            return;
        }
        /* 如果有历史消息，使用这些消息初始化频道的历史记录 */
        initializeChannelMessageHistoryWithMessages(channel, history.get().stream()
                .map(SerializationUtils::<ChannelMessage>deserializeByBase64).collect(Collectors.toList()));
    }

    /**
     * 获取指定频道的消息历史记录
     * @param channel 要查询消息历史记录的频道
     * @return 返回指定频道的消息历史记录，如果没有消息历史记录则返回空的列表
     */
    public List<ChannelMessage> getChannelMessageHistory(AbstractChannel channel) {
        ConcurrentLinkedQueue<ChannelMessage> messages = channelMessageHistory.get(channel.getChannelName());
        /* 如果存在历史记录，则返回它们；否则返回空列表 */
        return messages != null ? new ArrayList<>(messages) : new ArrayList<>();
    }

    /**
     * 获取与指定成员之间的消息历史记录
     * @param member 要查询消息历史记录的成员
     * @return 返回与指定成员的消息历史记录，如果没有消息历史记录则返回空的列表
     */
    public List<ExclusiveMessage> getMemberMessageHistory(Member member) {
        ConcurrentLinkedQueue<ExclusiveMessage> messages = memberMessageHistory.get(member.getMemberId());
        /* 如果存在历史记录，则返回它们；否则返回空列表 */
        return messages != null ? new ArrayList<>(messages) : new ArrayList<>();
    }
}
