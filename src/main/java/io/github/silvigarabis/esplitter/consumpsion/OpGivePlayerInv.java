package io.github.silvigarabis.esplitter.consumpsion;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.silvigarabis.esplitter.data.ESplitterConsumpsion;

public class OpGivePlayerInv {

    public static LinkedList<Runnable> genOpGivePlayerInv(Player player, ESplitterConsumpsion consumpsion) {
        LinkedList<Runnable> opList = new LinkedList<>();
        final var backItem1 = consumpsion.getBackItem1();
        final var backItem2 = consumpsion.getBackItem2();
        final int backItem1Amount = consumpsion.getBackItem1Amount();
        final int backItem2Amount = consumpsion.getBackItem2Amount();
    
        List<ItemStack> itemsToGive = new ArrayList<>();
        if (backItem1 != null) {
            itemsToGive.add(new ItemStack(backItem1, backItem1Amount));
        }
        if (backItem2 != null) {
            itemsToGive.add(new ItemStack(backItem2, backItem2Amount));
        }
        if (itemsToGive.isEmpty()) {
            throw new ESplitterConsumpsion.NoOpException();
        }
        var dropLocation = player.getLocation();
        if (itemsToGive.size() > 0) {
            opList.add(() -> itemsToGive.forEach(item -> dropLocation.getWorld().dropItem(dropLocation, item)));
        }
    
        return opList;
    }
    
}
