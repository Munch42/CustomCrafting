package ca.munchdev.customcrafting.listeners;

import ca.munchdev.customcrafting.CustomCrafting;
import ca.munchdev.customcrafting.inventories.CraftingBaseGUI;
import ca.munchdev.customcrafting.inventories.SuperCraftingTableGUI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener {
    private CustomCrafting plugin;
    private SuperCraftingTableGUI superCraftingTable;

    public PlayerInteractListener(CustomCrafting plugin){
        this.plugin = plugin;

        superCraftingTable = new SuperCraftingTableGUI(plugin);

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        Block clickedBlock = event.getClickedBlock();

        if(clickedBlock == null){
            return;
        }

        if(clickedBlock.getType().equals(Material.CRAFTING_TABLE)){
            Location tableLocation = clickedBlock.getLocation();
            // We get the location of the crafting table and then subtract 1 from its y to get the block beneath it.
            Location blockBelowTableLocation = tableLocation;
            blockBelowTableLocation.setY(tableLocation.getY() - 1);
            Block blockUnderCraftingTable = blockBelowTableLocation.getBlock();

            // We can then check to see if the block is the block that makes it a special table and go from there.
            if(blockUnderCraftingTable.getType().equals(Material.IRON_BLOCK) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
                event.setCancelled(true);
                //plugin.getServer().getConsoleSender().sendMessage(plugin.getSuperCraftingTableSlots().toString());
                superCraftingTable.open(event.getPlayer());
            }
        }
    }
}
