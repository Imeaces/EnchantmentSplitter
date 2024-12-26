package io.github.silvigarabis.esplitter.invgui;

import static org.bukkit.Material.ENCHANTED_BOOK;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import io.github.silvigarabis.esplitter.Messages;
import io.github.silvigarabis.esplitter.data.ESplitterConsumpsion;

public class GuiItemBuilder {

    static final ItemStack updateSlotElement(ESplitterInvGui gui, int pagedElemenetIndex) {
        int elementIndex = gui.flagPageIndex * ESplitterInvGui.slotsElements.length + pagedElemenetIndex;
        var enchantSet = gui.listEnchantSet.get(elementIndex);
    
        var allEnchants = gui.linkController.getCurrentEnchants();
    
        var itemElement = new ItemStack(ENCHANTED_BOOK);
        var itemElementMeta = (EnchantmentStorageMeta) itemElement.getItemMeta();
    
        // 设置显示标题 (title)
        itemElementMeta.setDisplayName(Messages.invGuiElementTitle.getText(enchantSet.enchantSet.size()));

        // 设置动作消耗提示文本 (lores)
        String consumpsionTipText =
            ESplitterConsumpsion.generateConsumptionText(enchantSet.consumpsion, gui.getPlayer());
        itemElementMeta.setLore(Arrays.asList(consumpsionTipText.split("\\R")));
    
        // 添加附魔 (enchants)
        for (Enchantment ench : enchantSet.enchantSet) {
            int enchLevel = allEnchants.get(ench);
            itemElementMeta.addStoredEnchant(ench, enchLevel, true);
        }
    
        itemElement.setItemMeta(itemElementMeta);
    
        return itemElement;
    }

    static final void createLocalizedButton(int slot, Messages msg, Material material) {
        final ItemStack item0 = createItemLocalized(material, msg);
    
        ESplitterInvGui.slotItemsStatic[slot] = item0.clone();
    
        ESplitterInvGui.slotItemCreators[slot] = (player) -> {
            return createItemLocalized(material, msg, player);
        };
    }

    static final void initForBorderItem() {
        // 包围框物品
        final ItemStack itemBorder1 = createItem(Material.BLACK_STAINED_GLASS_PANE, "");
        final ItemStack itemBorder2 = createItem(Material.GRAY_STAINED_GLASS_PANE, "");
        final ItemStack itemBorder3 = createItem(Material.WHITE_STAINED_GLASS_PANE, "");

        for (int slot : ESplitterInvGui.slotsBorder1) {
            ESplitterInvGui.slotItemsStatic[slot] = itemBorder1;
        }
        for (int slot : ESplitterInvGui.slotsBorder2) {
            ESplitterInvGui.slotItemsStatic[slot] = itemBorder2;
        }
        for (int slot : ESplitterInvGui.slotsBorder3) {
            ESplitterInvGui.slotItemsStatic[slot] = itemBorder3;
        }
    }
    
    static final void ininForElementItem() {
        for (int elementIndex = 0; elementIndex < ESplitterInvGui.slotsElements.length; elementIndex++) {
            int slot = ESplitterInvGui.slotsElements[elementIndex];
            final int elementIndexFinalized = elementIndex;
            ESplitterInvGui.slotItemUpdaters[slot] = (gui, slotThatUpdating) -> {
                return GuiItemBuilder.updateSlotElement(gui, elementIndexFinalized);
            };
        }
    }

    /**
     * 创建一个本地化的按钮物品
     *
     * @param material   按钮的材质
     * @param message    本地化消息
     * @param player     当前玩家
     * @return 创建的按钮物品
     */
    public static ItemStack createItemLocalized(Material material, Messages message, Player player) {
        return GuiItemBuilder.createItem(material, message.getPlayerText(player));
    }

    /**
     * 创建一个本地化的按钮物品
     *
     * @param material   按钮的材质
     * @param message    本地化消息
     * @param player     当前玩家
     * @return 创建的按钮物品
     */
    public static ItemStack createItemLocalized(Material material, Messages message) {
        return GuiItemBuilder.createItem(material, message.getText());
    }

    /**
     * 创建一个带显示名称的按钮物品
     *
     * @param material    按钮的材质
     * @param displayName 按钮的显示名称
     * @return 创建的按钮物品
     */
    public static ItemStack createItem(Material material, String displayName) {
        var item = new ItemStack(material);
        var meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            item.setItemMeta(meta);
        }
        return item;
    }
}
