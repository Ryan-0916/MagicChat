package com.magicrealms.magicchat.core.store;

import com.magicrealms.magicchat.core.exception.HistoryStorageAlreadyExistsException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.magicrealms.magicchat.core.MagicChatConstant.MAX_HISTORY_SIZE;

/**
 * @author Ryan-0916
 * @Desc 消息缓存类
 * @date 2025-04-25
 */
@SuppressWarnings("unused")
public class MessageHistoryRepository<T, M> {

    private final Map<T, ConcurrentLinkedQueue<M>>
            storage = new ConcurrentHashMap<>();

    public void initialize(T key) {
        if (storage.containsKey(key)) {
            throw new HistoryStorageAlreadyExistsException("已存在消息历史记录！");
        }
        storage.put(key, new ConcurrentLinkedQueue<>());
    }

    public boolean containsKey(T key) {
        return storage.containsKey(key);
    }

    public void initializeWithMessages(T key, List<M> messages) {
        initialize(key);
        messages.forEach(e -> addMessage(key, e));
    }

    public void addMessage(T key, M message) {
        if (!storage.containsKey(key)) {
            throw new NullPointerException(String.format("%s 没有初始化消息记录存储", key));
        }
        storage.get(key).add(message);
        if (storage.get(key).size() > MAX_HISTORY_SIZE) {
            storage.get(key).poll();
        }

    }

    public List<M> getMessages(T key) {
        ConcurrentLinkedQueue<M>
                messages = storage.get(key);
        return messages != null ? new ArrayList<>(messages) : new ArrayList<>();
    }

    public void destroy(T key) {
        if (!storage.containsKey(key)) {
            throw new NullPointerException(String.format("频道%s没有初始化消息记录存储", key));
        }
        storage.remove(key);
    }
}
