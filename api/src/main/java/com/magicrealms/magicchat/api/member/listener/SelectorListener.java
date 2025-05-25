package com.magicrealms.magicchat.api.member.listener;

import com.magicrealms.magicchat.api.member.Member;
import com.magicrealms.magicchat.api.message.exclusive.SelectorMessage;
import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

/**
 * @author Ryan-0916
 * @Desc 选择器消息监听器
 * @date 2025-04-23
 */
public class SelectorListener implements Listener {

    private final Member member;

    @Getter
    private final SelectorMessage selector;

    public SelectorListener(Member member, SelectorMessage selector) {
        this.member = member;
        this.selector = selector;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerSwapHandItemsEvent (PlayerSwapHandItemsEvent e) {
        if (member.getMemberId().equals(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
            selector.selected(member);
            HandlerList.unregisterAll(this);
            member.resetChatDialog();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuitEvent(PlayerQuitEvent e) {
        if (member.getMemberId().equals(e.getPlayer().getUniqueId())) {
            member.stopBlocking();
            unregister();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSlotChange(PlayerItemHeldEvent e) {
        int currentSlot = e.getNewSlot();
        int lastSlot = e.getPreviousSlot();
        if (member.getMemberId().equals(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
            selector.selectOption((currentSlot < lastSlot) ||
                    (lastSlot == 0 && currentSlot == 8));
            member.resetChatDialog();
        }
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

}
