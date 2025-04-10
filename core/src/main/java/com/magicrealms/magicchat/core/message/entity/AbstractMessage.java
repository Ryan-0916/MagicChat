package com.magicrealms.magicchat.core.message.entity;

import com.magicrealms.magiclib.common.MagicRealmsPlugin;
import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * @author Ryan-0916
 * @Desc 抽象消息类
 * @date 2025-03-19
 */
@Getter
public abstract class AbstractMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 消息创建时间 */
    private final long createdTime;
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
        this.createdTime = System.currentTimeMillis();
        this.originalContent = content;
        this.content = handleMessage(sender, content);
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
