package ca.munchdev.customcrafting.inventories;

import ca.munchdev.customcrafting.CustomCrafting;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SuperCraftingTableGUI extends CraftingBaseGUI {
    private CustomCrafting plugin;

    public SuperCraftingTableGUI(CustomCrafting plugin) {
        super((5*9), "Super Crafting Table", plugin.getSuperCraftingTableSlots(), new ItemStack(Material.PURPLE_STAINED_GLASS_PANE));

        // This is where we set the result slot to have a barrier
        ItemStack barrierItem = new ItemStack(Material.BARRIER);
        ItemMeta itemMeta = barrierItem.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "No Matching Recipe");
        barrierItem.setItemMeta(itemMeta);

        this.setItem(plugin.getSuperCraftingTableSlots().get(plugin.getSuperCraftingTableSlots().size() - 1), new ItemStack(barrierItem));

        this.plugin = plugin;
    }
}
