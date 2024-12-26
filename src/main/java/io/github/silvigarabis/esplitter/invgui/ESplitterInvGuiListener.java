package io.github.silvigarabis.esplitter.invgui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryView;


import java.util.HashMap;
import java.util.Map;

public final class ESplitterInvGuiListener implements Listener {

    static final Map<InventoryView, ESplitterInvGui> guiViews = new HashMap<>();

    /* pass click event to gui */
    @EventHandler(ignoreCancelled=true)
    public void inventoryClick(InventoryClickEvent event){
        var inventoryView = event.getView();
        var gui = guiViews.get(inventoryView);
        if (gui == null){
            return;
        }
        try {
            gui.viewInvOnClick(event);
        } catch (Exception ex){
            closeAll();
            throw ex;
        }
    }

    /* pass close event to gui */
    @EventHandler(ignoreCancelled=true)
    public void inventoryClose(InventoryCloseEvent event){
        var inventoryView = event.getView();
        var gui = guiViews.get(inventoryView);
        if (gui == null)
            return;
        
        guiViews.remove(inventoryView);
        try {
            gui.viewInvOnClose(event);
        } catch (Exception ex){
            closeAll();
            throw ex;
        }
    }

    /* pass drag event to gui */
    @EventHandler(ignoreCancelled=true)
    public void inventoryDrap(InventoryDragEvent event){
        var inventoryView = event.getView();
        var gui = guiViews.get(inventoryView);
        if (gui == null)
            return;

        try {
            gui.viewInvOnDrag(event);
        } catch (Exception ex){
            closeAll();
            throw ex;
        }
    }

    private static void closeAll(){
        for (Map.Entry<InventoryView, ESplitterInvGui> entry : guiViews.entrySet()){
            try {
                // entry.getValue().closeGui();
                entry.getKey().getPlayer().closeInventory();
                entry.getKey().getPlayer().sendMessage("[ESplitter] 出现未知错误");
            } catch (Throwable ignored){
                
            }
        }
        guiViews.clear();
    }
}
