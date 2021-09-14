package ca.munchdev.customcrafting.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class HeadHelpers {
    public static boolean arePlayerHeadsEqual(ItemStack head1, ItemStack head2){
        if (head1.getType() == Material.PLAYER_HEAD && head2.getType() == Material.PLAYER_HEAD) {
            if (head1.hasItemMeta() && head2.hasItemMeta()) {
                if (head1.getItemMeta() instanceof SkullMeta && head2.getItemMeta() instanceof SkullMeta) {
                    SkullMeta Meta1 = (SkullMeta) head1.getItemMeta();
                    SkullMeta Meta2 = (SkullMeta) head2.getItemMeta();
                    if (Meta1.hasOwner() && Meta2.hasOwner()) {
                        if (Meta1.getOwningPlayer().equals(Meta2.getOwningPlayer())) {
                            return true;
                        }
                    }
                }
            }
        }

        // If they are not equal, return false
        return false;
    }
}
