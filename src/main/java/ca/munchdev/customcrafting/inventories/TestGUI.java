package ca.munchdev.customcrafting.inventories;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TestGUI extends BaseGUI {
    public TestGUI() {
        super(9, "Munch's Test GUI");

        setItem(4, new ItemStack(Material.DEEPSLATE), player -> {
            player.sendMessage(ChatColor.BLACK + "This is one very cool building block!");
            player.getInventory().addItem(new ItemStack(Material.DEEPSLATE, 32));
        });
        
        setItem(5, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
    }
}
