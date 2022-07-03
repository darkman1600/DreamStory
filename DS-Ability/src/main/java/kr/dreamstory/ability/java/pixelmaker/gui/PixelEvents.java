package kr.dreamstory.ability.java.pixelmaker.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class PixelEvents implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        PixelGUI gui = PixelGUI.getGUI((Player)e.getWhoClicked());
        if(gui != null) gui.clickInv(e);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        PixelGUI gui = PixelGUI.getGUI((Player)e.getPlayer());
        if(gui != null) {
            //PlayerSqlData psd = gui.getPsd();
            gui.closeInv();
        }
    }

}
