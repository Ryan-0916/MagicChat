package com.magicrealms.magicchat.core;

import com.magicrealms.magicchat.core.entity.Member;
import com.magicrealms.magicchat.core.store.MemberStorage;
import com.magicrealms.magiclib.common.MagicRealmsPlugin;
import com.magicrealms.magiclib.common.enums.ParseType;
import com.magicrealms.magiclib.common.manage.BungeeMessageManager;
import com.magicrealms.magiclib.common.manage.CommandManager;
import com.magicrealms.magiclib.common.manage.ConfigManager;
import com.magicrealms.magiclib.common.manage.PacketManager;
import com.magicrealms.magiclib.common.store.RedisStore;
import com.magicrealms.magiclib.paper.dispatcher.MessageDispatcher;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import java.util.Optional;

import static com.magicrealms.magicchat.core.MagicChatConstant.*;

/**
 * @author Ryan-0916
 * @Desc 聊天插件启动类
 * @date 2025-04-01
 */
public class MagicChat extends MagicRealmsPlugin implements Listener {

    @Getter
    private static MagicChat instance;

    @Getter
    private RedisStore redisStore;

    @Getter
    private BungeeMessageManager bungeeMessageManager;

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
            getServer().getPluginManager().registerEvents(this, this);
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Member member = MemberStorage.getInstance().retrieveMember(event.getPlayer());
        member.resetChatDialog();
    }

    private void setupRedisStore() {
        String host = getConfigManager().getYmlValue(YML_REDIS, "DataSource.Host"), password = getConfigManager().getYmlValue(YML_REDIS, "DataSource.Password");
        int port = getConfigManager().getYmlValue(YML_REDIS, "DataSource.Port", 6379, ParseType.INTEGER);
        boolean redisPasswordModel = getConfigManager().getYmlValue(YML_REDIS, "DataSource.PasswordModel", false, ParseType.BOOLEAN);
        this.redisStore = new RedisStore(this, host, port, redisPasswordModel ? password : null);
        this.unsubscribe();
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
        Optional.ofNullable(bungeeMessageManager).ifPresent(BungeeMessageManager::unsubscribe);
    }

    @Override
    protected void loadConfig(ConfigManager configManager) {
        configManager.loadConfig(YML_CONFIG,
                YML_LANGUAGE,
                YML_REDIS);
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
                                "ConsoleMessage.Error.NotConsole"));
                case UN_KNOWN_COMMAND -> MessageDispatcher.getInstance().
                        sendMessage(this, e.sender(), getConfigManager().getYmlValue(YML_LANGUAGE,
                                "ConsoleMessage.Error.UnknownCommand"));
                case PERMISSION_DENIED -> MessageDispatcher.getInstance().
                        sendMessage(this, e.sender(), getConfigManager().getYmlValue(YML_LANGUAGE,
                        "ConsoleMessage.Error.PermissionDenied"));
            }
        });
    }

    @Override
    protected void registerPacketListener(PacketManager packetManager) {
        packetManager.registerListeners();
    }
}
