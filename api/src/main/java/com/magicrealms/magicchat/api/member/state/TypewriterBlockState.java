package com.magicrealms.magicchat.api.member.state;

import com.magicrealms.magicchat.api.MagicChat;
import com.magicrealms.magicchat.api.member.Member;
import com.magicrealms.magicchat.api.message.ExclusiveMessage;
import com.magicrealms.magicchat.api.message.exclusive.SelectorMessage;
import com.magicrealms.magicchat.api.message.exclusive.TypewriterMessage;
import com.magicrealms.magiclib.bukkit.message.helper.TypewriterHelper;
import com.magicrealms.magiclib.core.dispatcher.NMSDispatcher;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

/**
 * @author Ryan-0916
 * @Desc 打印机阻塞
 * 发送打印机消息时将进入阻塞状态
 * 阻塞状态下发送专属消息，重组聊天框事件将由状态代理
 * @date 2025-04-25
 */
public class TypewriterBlockState implements BlockState {
    /* 打印机占用的 Task */
    private BukkitTask typewriterTask;
    /* 打印机内容 */
    private final TypewriterMessage message;

    public TypewriterBlockState(TypewriterMessage message) {
        this.message = message;
    }

    public void enter(Member member){
        /* 打印机处理器 */
        TypewriterHelper typewriterHelper = new TypewriterHelper(
                message.getPrintContent(),
                message.getPrefix(),
                message instanceof SelectorMessage selectorMessage ?
                        "\n".repeat(selectorMessage.getOptions().size())
                                + StringUtils.defaultString(message.getSuffix()) :
                        message.getSuffix()
        );
        typewriterTask = Bukkit.getScheduler()
                .runTaskTimer(MagicChat.getInstance(), () -> {
            Player player = Bukkit.getPlayer(member.getMemberId());
            if (player == null || !player.isOnline() || typewriterHelper.isPrinted()) {
                /* 结束打印机状态 */
                member.stopBlocking();
                if (message instanceof SelectorMessage selectorMessage) {
                    member.sendSelectorMessage(selectorMessage);
                } else {
                    member.addMessageHistory(message);
                }
                return;
            }
            String m = typewriterHelper.print();
            List<String> history = member.getMessageHistory(player);
            history.add(m);
            NMSDispatcher.getInstance().resetChatDialog(player, history);
        }, 0, message.getPrintTick() / (typewriterHelper.getRealContent().length() - 1));
    }

    public void exit(Member member){
        if (typewriterTask != null && !typewriterTask.isCancelled()) {
            typewriterTask.cancel();
            typewriterTask = null;
        }
    }

    @Override
    public void handleMessage(Member member, ExclusiveMessage message) {
        /* 打印机状态下消息只会添加到聊天记录中，不会重组聊天框 */
        if (!(message instanceof SelectorMessage)) {
            member.addMessageHistory(message);
        }
    }

}
