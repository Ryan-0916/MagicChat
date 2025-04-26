package com.magicrealms.magicchat.core.message;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.util.UUID;

/**
 * @author Ryan-0916
 * @Desc 专属消息类
 * 该类用于表示只发送给特定接收者的消息。消息内容格式化后只能由指定的接收者查看。
 * 该类继承自抽象类 `AbstractMessage`，并实现了消息内容的具体处理方法。
 * @date 2025-03-25
 */
@Getter
public class ExclusiveMessage extends AbstractMessage {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 创建一条新的专属消息。
     * @param sender 消息发送者的 UUID，若为 NULL 则表示该消息由系统发送
     * @param content 消息的原始内容，插件需要传入未经过任何处理的文本
     */
    public ExclusiveMessage(@Nullable UUID sender, String content) {
        super(sender, content);
    }

    /**
     * 消息内容的具体处理方法
     * @param sender 消息发送者的 UUID，若为 NULL 则表示该消息由插件发送
     * @param content 消息的原始内容，未经过任何处理的文本
     * @return 处理后的消息内容
     */
    @Override
    protected String handleMessage(@Nullable UUID sender, String content) {
        return content;
    }

}
