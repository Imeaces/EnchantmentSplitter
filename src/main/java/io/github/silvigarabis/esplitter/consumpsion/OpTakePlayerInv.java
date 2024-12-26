package io.github.silvigarabis.esplitter.consumpsion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.silvigarabis.esplitter.data.ESplitterConsumpsion;

public class OpTakePlayerInv {

    public static LinkedList<Runnable> genOpTakePlayerInv(Player player, ESplitterConsumpsion consumpsion){
        LinkedList<Runnable> opList = new LinkedList<>();
        final var item1 = consumpsion.getItem1();
        final var item2 = consumpsion.getItem2();
        final int item1Amount = consumpsion.getItem1Amount();
        final int item2Amount = consumpsion.getItem2Amount();
    
        final List<Pair<Material, Integer>> itemsToRemove = new ArrayList<>();
        if (item1 != null) {
            itemsToRemove.add(Pair.of(item1, item1Amount));
        }
        if (item2 != null) {
            itemsToRemove.add(Pair.of(item2, item2Amount));
        }
        if (itemsToRemove.isEmpty()) {
            throw new ESplitterConsumpsion.NoOpException();
        }
    
        Map<Integer, ItemStack> inventoryModified = new HashMap<>();
        int[] removalProgress = new int[itemsToRemove.size()];
    
        // 遍历每个物品和数量
        for (int i = 0; i < itemsToRemove.size(); i++) {
            var entry = itemsToRemove.get(i);
            Material material = entry.getKey();
            int targetAmount = entry.getValue();
    
            @SuppressWarnings("unchecked")
            Map<Integer, ItemStack> storedItems = (Map<Integer, ItemStack>) player.getInventory().all(material);
            storedItems.putAll(inventoryModified);
    
            for (Map.Entry<Integer, ? extends ItemStack> slotEntry : storedItems.entrySet()) {
                if (removalProgress[i] == targetAmount) {
                    break;
                }
    
                int slot = slotEntry.getKey();
                ItemStack itemStack = slotEntry.getValue();
                int amount = itemStack.getAmount();
    
                if (itemStack.getType() != material) {
                    continue;
                }
    
                int removedAmount = Math.min(amount, targetAmount - removalProgress[i]);
                itemStack.setAmount(amount - removedAmount);
                removalProgress[i] += removedAmount;
    
                // 如果物品用完，清除
                if (itemStack.getAmount() == 0) {
                    inventoryModified.put(slot, null);
                }
            }
    
            // 如果目标数量未完成，返回失败
            if (removalProgress[i] != targetAmount) {
                throw new ESplitterConsumpsion.OpCantExecuteException();
            }
        }
    
        // 提交所有变更
        opList.add(() -> inventoryModified.forEach(player.getInventory()::setItem));
    
        return opList;
    }
    
}
