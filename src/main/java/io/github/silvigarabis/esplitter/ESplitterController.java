package io.github.silvigarabis.esplitter;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.jetbrains.annotations.UnmodifiableView;

import io.github.silvigarabis.esplitter.data.ESplitterConsumpsion;
import io.github.silvigarabis.esplitter.data.ESplitterEvaluatedEnchantSet;

import static org.bukkit.Material.ENCHANTED_BOOK;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 控制一次会话。
 */
public final class ESplitterController {

    private Player player;
    public Player getPlayer() {
        return player;
    }

    private ItemStack selectedItem;
    private Map<Enchantment, Integer> currentEnchants;
    private List<ESplitterEvaluatedEnchantSet> evaluatedEnchantGroupList;
    private Set<ESplitterEvaluatedEnchantSet> removedEnchangGroupList = new HashSet<>();
    private ControllerConfig config;

    public ItemStack getSelectedItem() {
        return selectedItem.clone();
    }
    public @UnmodifiableView Map<Enchantment, Integer> getCurrentEnchants() {
        return Collections.unmodifiableMap(currentEnchants);
    }

    public @UnmodifiableView List<ESplitterEvaluatedEnchantSet> getEvaluatedEnchantGroupList() {
        return Collections.unmodifiableList(evaluatedEnchantGroupList);
    }

    public static class ControllerConfig {
        public int maxEnchantSetSize = 1;
    }

    public ESplitterController(Player player){
        this.config = new ControllerConfig();
        this.player = player;
    }

    public static boolean isItemAcceptable(ItemStack item){
        //附魔书暂时不支持（软限制）
        return !item.getType().equals(ENCHANTED_BOOK);
    }

    public boolean isItemAcceptable(){
        return itemAccepted;
    }
    
    private boolean itemAccepted = false;

    public void selectItem(ItemStack item){
        if (item != null) {
            this.selectedItem = item.clone();
        } else {
            this.selectedItem = null;
        }
        this.updateEnchants();
        this.evalEnchantsSet();
    }

    /**
     * 更新已知的附魔列表
     */
    private void updateEnchants() {
        Map<Enchantment, Integer> currentEnchants;
        if (this.selectedItem.getType().equals(ENCHANTED_BOOK)){
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) this.selectedItem.getItemMeta();
            currentEnchants = new LinkedHashMap<>(meta.getStoredEnchants());
        } else {
            currentEnchants = new LinkedHashMap<>(this.selectedItem.getEnchantments());
        }
        this.currentEnchants = currentEnchants;
    }

    /**
     * Evaluates the current enchantments set.
     */
    private void evalEnchantsSet(){
        int maxEnchantSetSize = this.config.maxEnchantSetSize;
        List<ESplitterEvaluatedEnchantSet> evaluatedEnchantGroupList = new LinkedList<>();

        // 这里做了一个以 maxEnchantSetSize 为单位的分组
        ESplitterEvaluatedEnchantSet evaluatedResult = null;
        for (var ench : this.currentEnchants.keySet()){
            if (evaluatedResult == null || evaluatedResult.enchantSet.size() >= maxEnchantSetSize) {
                evaluatedResult = new ESplitterEvaluatedEnchantSet();
                evaluatedResult.enchantSet = new LinkedHashSet<>();
                // not implements: 未实现：消耗内容分析
                evaluatedResult.consumpsion = new ESplitterConsumpsion();
                evaluatedEnchantGroupList.add(evaluatedResult);
            }
            // 更新附魔容器
            evaluatedResult.enchantSet.add(ench);
        }

        this.evaluatedEnchantGroupList = evaluatedEnchantGroupList;
        this.removedEnchangGroupList.clear();
    }

    public boolean removeEnchants(int enchantSetIndex){
        var evalResult = this.evaluatedEnchantGroupList.get(enchantSetIndex);
        if (removedEnchangGroupList.contains(evalResult)) {
            return false;
        } else {
            removedEnchangGroupList.add(evalResult);
        }
        for (var ench : evalResult.enchantSet){
            this.currentEnchants.remove(ench);
        }
        this.updateItem();
        return true;
    }

    /**
     * 在物品上移除已经被移除的附魔
     * 
     * @return 返回物品堆栈的副本
     */
    public ItemStack updateItem() {
        var item = this.selectedItem;
        if (item == null) {
            return null;
        }

        if (item.getType().equals(ENCHANTED_BOOK)){
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();

            Set<Enchantment> enchantsToRemove = new HashSet<>(meta.getStoredEnchants().keySet());
            enchantsToRemove.removeAll(this.currentEnchants.keySet());
            for (var ench : enchantsToRemove) {
                meta.removeStoredEnchant(ench);
            }

            item.setItemMeta(meta);
        } else {
            Set<Enchantment> enchantsToRemove = new HashSet<>(item.getEnchantments().keySet());
            enchantsToRemove.removeAll(this.currentEnchants.keySet());
            for (var ench : enchantsToRemove) {
                item.removeEnchantment(ench);
            }
        }
        return item.clone();
    }


    public boolean splitEnchantSet(int enchantSetIndex){
        throw new RuntimeException("method not implements");
    }
    
    public boolean grindEnchantSet(int enchantSetIndex){
        throw new RuntimeException("method not implements");
    }

    // public boolean removeEnchantment(EnchantmentSet enchantSet){
    //     if (selectedItem == null || currentEnchants.size() == 0){
    //         return false;
    //     }
        
    //     var playerInv = player.getInventory();
    //     if (!playerInv.contains(selectedItem)){
    //         return false;
    //     }

    //     var slot = playerInv.first(selectedItem);

    //     for (var ench : enchantSet){
    //         selectedItem.removeEnchantment(ench);
    //         currentEnchants.remove(ench);
    //     }

    //     evalEnchantGroupList.removeIf((e) -> e == enchantSet);

    //     playerInv.setItem(slot, selectedItem);
        
    //     return true;
    // }
}
