package com.magicrealms.magicchat.api.channel.bungee;

import com.magicrealms.magicchat.api.MagicChat;
import com.magicrealms.magicchat.api.message.ChannelMessage;
import com.magicrealms.magiclib.common.utils.GsonUtil;
import com.magicrealms.magiclib.common.utils.SerializationUtils;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author Ryan-0916
 * @Desc 跨服渠道消息管理器 - 采用 Redis 订阅/发布模式
 * @date 2025-04-07
 */
@SuppressWarnings("unused")
public class BungeeActionManager extends JedisPubSub {

    private final MagicChat PLUGIN;
    private final Consumer<ChannelMessage> SEND_LISTENER;
    private final Consumer<MessageRetract> RETRACT_LISTENER;
    private final Consumer<String> SUBSCRIBE_LISTENER;
    private final Consumer<String> UNSUBSCRIBE_LISTENER;

    private int taskId;
    private Jedis connection;

    private BungeeActionManager(Builder builder) {
        Objects.requireNonNull(builder.plugin);
        Objects.requireNonNull(builder.sendListener);
        Objects.requireNonNull(builder.retractListener);
        Objects.requireNonNull(builder.channel);
        Objects.requireNonNull(builder.host);
        this.PLUGIN = builder.plugin;
        this.SEND_LISTENER = builder.sendListener;
        this.RETRACT_LISTENER = builder.retractListener;
        this.SUBSCRIBE_LISTENER = builder.subscribeListener;
        this.UNSUBSCRIBE_LISTENER = builder.unSubscribeListener;
        this.taskId = -1;

        try {
            connection = new Jedis(builder.host, builder.port, 60000);
            if (builder.passwordModel) {
                connection.auth(builder.password);
            }
            subscribe(builder.channel);
        } catch (Exception e) {
            PLUGIN.getLoggerManager().error("订阅 Redis 服务异常", e);
        }
    }

    public void subscribe(String channel) {
        taskId = Bukkit.getScheduler().runTaskAsynchronously(PLUGIN,
                () -> connection.subscribe(this, channel)).getTaskId();
    }

    @Override
    public void unsubscribe() {
        if (taskId != -1 && Bukkit.getScheduler().isCurrentlyRunning(taskId)) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
        if (isSubscribed()) {
            super.unsubscribe();
        }
    }

    /**
     * 当取到消息后的处理
     * @param channel 频道名
     * @param message 消息
     */
    @Override
    public void onMessage(String channel, String message) {
        Optional.ofNullable(GsonUtil.fromJson(message, BungeeAction.class))
                .ifPresent(action -> {
                    switch (action.actionType()) {
                        case SEND -> SEND_LISTENER.accept(SerializationUtils.deserializeByBase64(action
                                        .serializeBase64Msg()));
                        case RETRACT -> RETRACT_LISTENER.accept(action.messageRetract());
                    }
                });
    }

    /**
     * 初始化订阅时候的处理
     * @param channel 频道名
     * @param subscribedChannels 订阅数量
     */
    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        /* 初始化订阅时候的处理 */
        if (SUBSCRIBE_LISTENER == null) {
            return;
        }
        SUBSCRIBE_LISTENER.accept(channel);
    }

    /**
     * 取消订阅时候的处理
     * @param channel 频道名
     * @param subscribedChannels 订阅数量
     */
    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        Runnable task = () -> {
            if (connection != null && connection.isConnected()) {
                connection.close();
            }
        };
        executorService.schedule(task, 1, TimeUnit.SECONDS);
        executorService.shutdown();
        /* 取消订阅时候的处理 */
        if (UNSUBSCRIBE_LISTENER == null) {
            return;
        }
        UNSUBSCRIBE_LISTENER.accept(channel);
    }

    public static class Builder {
        private MagicChat plugin;
        private String channel;
        private String host;
        private int port;
        private String password;
        private boolean passwordModel;
        private Consumer<ChannelMessage> sendListener;
        public Consumer<MessageRetract> retractListener;
        private Consumer<String> subscribeListener;
        private Consumer<String> unSubscribeListener;

        public Builder plugin(MagicChat plugin) {
            this.plugin = plugin;
            return this;
        }

        public Builder channel(String channel) {
            this.channel = channel;
            return this;
        }

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder passwordModel(boolean passwordModel) {
            this.passwordModel = passwordModel;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder sendListener(Consumer<ChannelMessage> sendListener) {
            this.sendListener = sendListener;
            return this;
        }

        public Builder retractListener(Consumer<MessageRetract> retractListener) {
            this.retractListener = retractListener;
            return this;
        }

        public Builder subscribeListener(Consumer<String> subscribeListener) {
            this.subscribeListener = subscribeListener;
            return this;
        }

        public Builder unSubscribeListener(Consumer<String> unSubscribeListener) {
            this.unSubscribeListener = unSubscribeListener;
            return this;
        }

        public BungeeActionManager build() {
            return new BungeeActionManager(this);
        }
    }

}
