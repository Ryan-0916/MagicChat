package com.magicrealms.magicchat.core.bungee;

import com.magicrealms.magicchat.core.MagicChat;
import com.magicrealms.magicchat.core.message.ChannelMessage;
import com.magicrealms.magiclib.common.utils.GsonUtil;
import com.magicrealms.magiclib.common.utils.SerializationUtils;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author Ryan-0916
 * @Desc 说明
 * @date 2025-04-07
 */
@SuppressWarnings("unused")
public class BungeeChannelMessageManager extends JedisPubSub {

    private final MagicChat PLUGIN;
    private final Consumer<ChannelMessage> SEND_LISTENER;
    private final Consumer<RetractInfo> RETRACT_LISTENER;
    private final Consumer<String> SUBSCRIBE_LISTENER;
    private final Consumer<String> UNSUBSCRIBE_LISTENER;
    private int taskId;
    private Jedis connection;

    private BungeeChannelMessageManager(Builder builder) {
        this.PLUGIN = builder.plugin;
        this.SEND_LISTENER = builder.sendListener;
        this.RETRACT_LISTENER = builder.retractListener;
        this.SUBSCRIBE_LISTENER = builder.subscribeListener;
        this.UNSUBSCRIBE_LISTENER = builder.unSubscribeListener;
        this.taskId = -1;

        if (PLUGIN == null) {
            throw new NullPointerException("Bungee消息 Plugin 属性不可为空");
        }

        if (SEND_LISTENER == null) {
            throw new NullPointerException("Bungee消息 SendListener 属性不可为空");
        }

        if (RETRACT_LISTENER == null) {
            throw new NullPointerException("Bungee消息 RetractListener 属性不可为空");
        }

        if (builder.channel == null) {
            throw new NullPointerException("Bungee消息 channel 属性不可为空");
        }

        if (builder.host == null) {
            throw new NullPointerException("Bungee消息 Redis Host 属性不可为空");
        }

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
        if (super.isSubscribed()) {
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
        /* 取得订阅的消息后的处理 */
        BungeeMessage bm = GsonUtil.fromJson(message, BungeeMessage.class);
        if (bm == null) {
            return;
        }
        if (bm.event() == BungeeMessageEvent.SEND) {
            SEND_LISTENER.accept(SerializationUtils.deserializeByBase64(bm.serializeBase64Msg()));
        } else if (bm.event() == BungeeMessageEvent.RETRACT){
            RETRACT_LISTENER.accept(bm.retractInfo());
        }
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
        public Consumer<RetractInfo> retractListener;
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

        public Builder retractListener(Consumer<RetractInfo> retractListener) {
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

        public BungeeChannelMessageManager build() {
            return new BungeeChannelMessageManager(this);
        }
    }

}
