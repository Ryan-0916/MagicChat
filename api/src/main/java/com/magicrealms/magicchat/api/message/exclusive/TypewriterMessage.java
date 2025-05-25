package com.magicrealms.magicchat.api.message.exclusive;

import com.magicrealms.magicchat.api.message.ExclusiveMessage;
import com.magicrealms.magiclib.bukkit.message.helper.AdventureHelper;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @author Ryan-0916
 * @Desc 打字机效果消息类
 * 该类表示一种具有打字机效果的消息。消息内容将按字符逐个打印出来，模拟打字的过程。
 * 此类继承自 `ExclusiveMessage`，用于处理特定的独占性消息，通常适用于某个玩家或者特定的消息展示场景。
 * 打字机效果消息常用于消息内容的逐字显示，配合前缀（例如角色名或其他标识），提供更加生动和互动的聊天体验。
 * @date 2025-03-25
 */
@Getter
public class TypewriterMessage extends ExclusiveMessage {

    /* 打印时长 */
    private final int printTick;

    /* 消息前缀 */
    protected final String prefix;

    /* 消息尾缀 */
    protected final String suffix;

    /**
     * 创建一条具有打字机效果的独占消息。
     * @param sender 消息发送者的 UUID，若为 NULL 则表示该消息由插件发送
     * @param content 消息的内容，插件需要传入待显示的文本
     * @param prefix 消息前缀，通常用于标识消息的来源或附加信息
     * @param suffix 消息后缀，通常用于标识消息的结尾信息
     * @param printTick 每个字符之间的打印间隔（单位：tick，1 tick = 1/20 秒）
     */
    public TypewriterMessage(@Nullable UUID sender,
                             String content,
                             @Nullable String prefix,
                             @Nullable String suffix,
                             int printTick) {
        super(sender, content);
        this.prefix = prefix;
        this.suffix = suffix;
        this.printTick = printTick;
    }

    /**
     * 获取消息内容
     * 将消息内容转换为 MiniMessage 格式，通过 AdventureHelper 工具类进行处理。
     * 该方法用于将不同格式的文本转换为 MiniMessage 的格式，以支持 Minecraft 的文本样式。
     * @return 处理后的消息内容，转换为 MiniMessage 格式的文本
     */
    @Override
    public String getContent() {
        return AdventureHelper.serializeComponent(
                AdventureHelper.deserializeComponent(
                        AdventureHelper.legacyToMiniMessage(StringUtils.join(prefix, content, suffix))));
    }

    /**
     * 获取打字机打印部分的消息内容
     * 该方法用于获取 content 原文。
     * @return 打字机部分的打印内容
     */
    public String getPrintContent() {
        return content;
    }

}
