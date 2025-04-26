package com.magicrealms.magicchat.core;

import com.magicrealms.magicchat.core.format.FormatManager;
import com.magicrealms.magicchat.core.listener.ChatListener;
import com.magicrealms.magicchat.core.store.ChannelStorage;
import com.magicrealms.magiclib.bukkit.MagicRealmsPlugin;
import com.magicrealms.magiclib.common.enums.ParseType;
import com.magicrealms.magiclib.bukkit.manage.BungeeMessageManager;
import com.magicrealms.magiclib.bukkit.manage.CommandManager;
import com.magicrealms.magiclib.bukkit.manage.ConfigManager;
import com.magicrealms.magiclib.bukkit.manage.PacketManager;
import com.magicrealms.magiclib.common.store.RedisStore;
import com.magicrealms.magiclib.core.dispatcher.MessageDispatcher;
import lombok.Getter;
import org.bukkit.Bukkit;
import java.util.Optional;

import static com.magicrealms.magicchat.core.MagicChatConstant.*;

/**
 * @author Ryan-0916
 * @Desc 聊天插件启动类
 * @date 2025-04-01
 */
public class MagicChat extends MagicRealmsPlugin {

    @Getter
    private static MagicChat instance;

    @Getter
    private RedisStore redisStore;

    @Getter
    private BungeeMessageManager bungeeMessageManager;

    @Getter
    private FormatManager formatManager;

    public MagicChat() {
        instance = this;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        dependenciesCheck(() -> {
            loadConfig(getConfigManager());
            registerCommand(commandManager);
            registerPacketListener(packetManager);
            setupRedisStore();
            setupFormat();
            getServer().getPluginManager().registerEvents(new ChatListener(), this);
        });
    }

    @Override
    public void onDisable() {
        super.onDisable();
        unsubscribe();
    }

    public void setupFormat() {
        this.formatManager = new FormatManager(this);
    }

    public void setupRedisStore() {
        String host = getConfigManager().getYmlValue(YML_REDIS, "DataSource.Host"), password = getConfigManager().getYmlValue(YML_REDIS, "DataSource.Password");
        int port = getConfigManager().getYmlValue(YML_REDIS, "DataSource.Port", 6379, ParseType.INTEGER);
        boolean redisPasswordModel = getConfigManager().getYmlValue(YML_REDIS, "DataSource.PasswordModel", false, ParseType.BOOLEAN);
        this.redisStore = new RedisStore(host, port, redisPasswordModel ? password : null);
        this.unsubscribe();
        ChannelStorage.getInstance().subscribeChannel();
        this.bungeeMessageManager = new BungeeMessageManager.Builder().channel(BUNGEE_CHANNEL)
                .plugin(this)
                .host(host)
                .port(port)
                .passwordModel(redisPasswordModel)
                .password(password)
                .messageListener(e -> {
                    switch (e.getType()) {
                        case SERVER_MESSAGE -> MessageDispatcher.getInstance().sendBroadcast(this, e.getMessage());
                        case PLAYER_MESSAGE -> Optional.ofNullable(Bukkit.getPlayerExact(e.getRecipientName())).ifPresent(player -> MessageDispatcher.getInstance().sendMessage(this, player, e.getMessage()));
                    }
                }).build();
    }

    private void unsubscribe() {
        Optional.ofNullable(bungeeMessageManager)
                .ifPresent(BungeeMessageManager::unsubscribe);
        ChannelStorage.getInstance()
                .unsubscribeChannel();
    }

    @Override
    protected void loadConfig(ConfigManager configManager) {
        configManager.loadConfig(YML_CONFIG,
                YML_LANGUAGE,
                YML_REDIS,
                YML_FORMAT);
    }

    @Override
    protected void registerCommand(CommandManager commandManager) {
        commandManager.registerCommand(MagicChatConstant.PLUGIN_NAME, e -> {
            switch (e.cause()) {
                case NOT_PLAYER -> MessageDispatcher.getInstance().
                        sendMessage(this, e.sender(), getConfigManager().getYmlValue(YML_LANGUAGE,
                                "ConsoleMessage.Error.NotPlayer"));
                case NOT_CONSOLE -> MessageDispatcher.getInstance().
                        sendMessage(this, e.sender(), getConfigManager().getYmlValue(YML_LANGUAGE,
                                "PlayerMessage.Error.NotConsole"));
                case UN_KNOWN_COMMAND -> MessageDispatcher.getInstance().
                        sendMessage(this, e.sender(), getConfigManager().getYmlValue(YML_LANGUAGE,
                                "PlayerMessage.Error.UnknownCommand"));
                case PERMISSION_DENIED -> MessageDispatcher.getInstance().
                        sendMessage(this, e.sender(), getConfigManager().getYmlValue(YML_LANGUAGE,
                        "PlayerMessage.Error.PermissionDenied"));
            }
        });
    }

    @Override
    protected void registerPacketListener(PacketManager packetManager) {
        packetManager.registerListeners();
    }
}
