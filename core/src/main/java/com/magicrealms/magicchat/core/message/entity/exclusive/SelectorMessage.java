package com.magicrealms.magicchat.core.message.entity.exclusive;

import com.magicrealms.magicchat.core.entity.Member;
import com.magicrealms.magicchat.core.message.entity.option.AbstractOption;
import com.magicrealms.magiclib.common.message.helper.AdventureHelper;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Ryan-0916
 * @Desc 选择器消息
 * 该类表示一种具有选择器效果的消息。
 * 消息问题将按字符逐个打印出来，模拟打字的过程。
 * 消息选项将在问题打印出来后直接显示在问题下方。
 * 玩家可以通过滚轮选择需要选择的选项并通过回车选择选项
 * 此类继承自 `TypewriterMessage`，用于处理特定的独占性消息，通常适用于某个玩家或者特定的消息展示场景。
 * 打字机效果消息常用于消息内容的逐字显示，配合前缀（例如角色名或其他标识），提供更加生动和互动的聊天体验。
 * @date 2025-04-21
 */
@Getter
public class SelectorMessage extends TypewriterMessage{


    /* 问题的选项 */
    private final List<AbstractOption> options;

    /* 选择的选项下标 */
    @Setter
    private int selectedIndex;

    /**
     * 创建一条具有选择器效果的打印机消息。
     * 该消息具有逐字显示的效果，并且玩家可以在显示的问题后选择一个选项。
     * @param sender    消息发送者的 UUID，若为 NULL 则表示该消息由插件发送
     * @param content   消息的内容，插件需要传入待显示的文本
     * @param prefix    消息前缀，通常用于标识消息的来源或附加信息
     * @param suffix    消息后缀，通常用于标识消息的结尾信息
     * @param printTick 每个字符之间的打印间隔（单位：tick，1 tick = 1/20 秒）
     * @param options   消息中的选项列表，玩家可以选择的多个选项
     * @throws RuntimeException 如果没有提供选项，则抛出异常
     */
    public SelectorMessage(@Nullable UUID sender,
                           String content,
                           @Nullable String prefix,
                           @Nullable String suffix,
                           int printTick,
                           List<AbstractOption> options) {
        super(sender, content, prefix, suffix, printTick);
        this.options = options;
        if (options.isEmpty()) { throw new RuntimeException("No options found"); }
        this.options.get(selectedIndex).setSelected(true);
    }

    public void selected(Member member) {
        member.stopBlocking();
        this.options.get(selectedIndex).selected(member);
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
                        AdventureHelper.legacyToMiniMessage(StringUtils.join(prefix, content,
                                "\n",
                                 options.stream().
                                         map(e -> e.isSelected() ?
                                                 e.getSelectedText() : e.getText())
                                         .collect(Collectors.joining("\n")),
                                suffix))));
    }

    public void selectOption(boolean isUp) {
        this.options.get(selectedIndex).setSelected(false);
        selectedIndex = isUp ? selectedIndex - 1 < 0 ? options.size() - 1 : selectedIndex -1
                : selectedIndex + 1 >= options.size() ? 0 : selectedIndex + 1;
        this.options.get(selectedIndex).setSelected(true);
    }
}
