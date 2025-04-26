package com.magicrealms.magicchat.core.message.option;

import com.magicrealms.magicchat.core.MagicChat;
import com.magicrealms.magicchat.core.member.Member;
import com.magicrealms.magiclib.bukkit.command.records.ExecutableCommand;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * CommandOption 类代表一种命令选项，包含命令文本、选择后的文本以及根据成员生成命令列表的功能。
 * 该类继承自 AbstractOption，并根据成员执行与命令相关的操作。
 * @author Ryan-0916
 * @date 2025-04-21
 */
public class CommandOption extends AbstractOption {

    /**
     * 用于生成与成员相关的命令列表的函数。
     * 该函数接受一个成员对象并返回一个命令列表。
     */
    private final Function<Member, @NotNull List<ExecutableCommand>> selectedFunction;

    /**
     * 构造一个新的 CommandOption 对象。
     * @param text 显示的文本，通常用于描述该选项
     * @param selectedText 选择后的文本，通常用于描述用户选择该选项后的行为
     * @param selectedFunction 用于生成与成员相关的命令列表的函数
     */
    public CommandOption(String text,
                         String selectedText,
                         Function<Member, List<ExecutableCommand>> selectedFunction) {
        super(text, selectedText);
        this.selectedFunction = selectedFunction;
    }

    /**
     * 当该选项被选择时，执行相应的命令。
     * @param member 被选中的成员对象，用于确定要执行哪些命令
     */
    @Override
    public void selected(Member member) {
        List<ExecutableCommand> commands = selectedFunction.apply(member);
        Optional.ofNullable(Bukkit.getPlayer(member.getMemberId())).ifPresent(
                player -> Bukkit.getScheduler().runTask(MagicChat.getInstance(), () -> commands.
                        forEach(command -> command.execute(player)))
        );
    }
}
