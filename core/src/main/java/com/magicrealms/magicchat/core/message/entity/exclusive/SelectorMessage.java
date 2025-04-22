package com.magicrealms.magicchat.core.message.entity.exclusive;

import com.magicrealms.magicchat.core.message.entity.answer.AbstractOption;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.UUID;

/**
 * @author Ryan-0916
 * @Desc 说明
 * @date 2025-04-21
 */
@Getter
public class SelectorMessage extends TypewriterMessage{


    private final ArrayList<AbstractOption> options;

    /**
     * 创建一条具有打字机效果的独占消息。
     * @param sender    消息发送者的 UUID，若为 NULL 则表示该消息由插件发送
     * @param content   消息的内容，插件需要传入待显示的文本
     * @param prefix    消息前缀，通常用于标识消息的来源或附加信息
     * @param suffix 消息后缀，通常用于标识消息的结尾信息
     * @param printTick 每个字符之间的打印间隔（单位：tick，1 tick = 1/20 秒）
     */
    public SelectorMessage(@Nullable UUID sender,
                           String content,
                           @Nullable String prefix,
                           @Nullable String suffix,
                           int printTick,
                           ArrayList<AbstractOption> options) {
        super(sender, content, prefix, suffix, printTick);
        this.options = options;
    }

}
