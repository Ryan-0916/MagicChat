package com.magicrealms.magicchat.api.message;

import com.magicrealms.magiclib.bukkit.message.helper.AdventureHelper;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * @author Ryan-0916
 * @Desc 抽象消息类
 * 该类作为所有消息类的基类，定义了消息的基本属性和通用方法。
 * 主要用于构建和处理不同类型的消息，提供基本的时间戳、内容以及发送者的信息。
 * @date 2025-03-19
 */
@Getter
public abstract class AbstractMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID messageId;

    @Setter
    private boolean retracted;

    @Setter
    private UUID retractedBy;

    /** 消息创建时间 */
    private final long createdTime;
    /** 消息发送时间 */
    @Setter
    private long sentTime;
    /** 消息内容 */
    protected String content;
    /** 原始内容 */
    protected String originalContent;
    /** 消息的发送者如若为 null，则代表此插件并非玩家所发 */
    @Nullable
    protected UUID sender;

    /**
     * 创建一条消息
     * @param sender 消息发送者的 UUID，如为 NULL 则表示该消息由 PLUGIN 发送
     * @param content 消息的原始内容
     */
    public AbstractMessage(@Nullable UUID sender, String content) {
        this.messageId = UUID.randomUUID();
        this.createdTime = System.currentTimeMillis();
        this.sentTime = System.currentTimeMillis();
        this.originalContent = content;
        this.content = handleMessage(sender, content);
    }

    /**
     * 获取消息内容
     * 将消息内容转换为 MiniMessage 格式，通过 AdventureHelper 工具类进行处理。
     * 该方法用于将不同格式的文本转换为 MiniMessage 的格式，以支持 Minecraft 的文本样式。
     * @return 处理后的消息内容，转换为 MiniMessage 格式的文本
     */
    public String getContent() {
        return AdventureHelper.serializeComponent(
                AdventureHelper.deserializeComponent(
                        AdventureHelper.legacyToMiniMessage(content)));
    }

    /**
     * 消息的具体处理方法
     * 根据不同的消息类型处理成不同的格式化消息
     * @param sender 消息发送者的 UUID，如为 NULL 则表示该消息由 PLUGIN 发送
     * @param content 消息的原始内容
     * @return 处理后的消息内容，它必须是 MiniMessage {@link MiniMessage} 格式的内容文本
     */
    protected abstract String handleMessage(@Nullable UUID sender, String content);

}
