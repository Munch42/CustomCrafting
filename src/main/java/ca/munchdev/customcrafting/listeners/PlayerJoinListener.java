package ca.munchdev.customcrafting.listeners;

import ca.munchdev.customcrafting.CustomCrafting;
import ca.munchdev.customcrafting.utils.HeadHelpers;
import ca.munchdev.customcrafting.utils.SkullCreator;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerJoinListener implements Listener {
    private CustomCrafting plugin;

    public PlayerJoinListener(CustomCrafting plugin){
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        //ItemStack myHead = HeadHelpers.getHead("munch42");
        //ItemStack mangoHead = SkullCreator.itemFromUrl("https://textures.minecraft.net/texture/e4895aa67247c3eb406fb905d3f6d35acd660c6f14a85bcf7bfb898b82646e70");

        //event.getPlayer().getInventory().addItem(myHead);
        //event.getPlayer().getInventory().addItem(mangoHead);
    }
}
