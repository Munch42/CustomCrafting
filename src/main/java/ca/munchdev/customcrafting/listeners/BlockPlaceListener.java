package ca.munchdev.customcrafting.listeners;

import ca.munchdev.customcrafting.CustomCrafting;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitRunnable;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class BlockPlaceListener implements Listener {
    private CustomCrafting plugin;

    public BlockPlaceListener(CustomCrafting plugin){
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        //event.getPlayer().sendMessage(event.getBlockAgainst().toString()); This tells us what it is placed on.
        //event.getPlayer().sendMessage(event.getBlockPlaced().toString()); This and the .getBlock are the same
        //event.getPlayer().sendMessage(event.getBlock().toString());

        if(event.getBlockAgainst().getType().equals(Material.IRON_BLOCK) && event.getBlock().getType().equals(Material.CRAFTING_TABLE)){
            // We check if the block that it is placed against is one block below it so that it doesn't happen unless it is placed on top the block.
            if(event.getBlockAgainst().getLocation().getY() == (event.getBlock().getLocation().getY() - 1)) {
                // https://www.youtube.com/watch?v=XMeck_nTdVA
                // I ended up trying this one since the above one didn't really work:
                // https://www.youtube.com/watch?v=1IfimW0_1_M

                event.getPlayer().getWorld().playSound(event.getBlock().getLocation().add(0.5, 0, 0.5), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 8f, 1);

                new BukkitRunnable() {
                    double var = 0;
                    Location loc, first, second;
                    Block placedBlock = event.getBlock();

                    @Override
                    public void run() {
                        var += Math.PI / 16;

                        // https://christopherchudzicki.github.io/MathBox-Demos/parametric_curves_3D.html
                        // This ^ allows you to plug in the equations for the first and second variables and then you can see it before you
                        // run the game. Theoretically you could then use it to create new ones that you then put in to get cool new animations.
                        // Must add 0.5 to the x and z coords so that the location is the center of the block.
                        loc = placedBlock.getLocation().add(0.5, 0, 0.5);
                        first = loc.clone().add(Math.cos(var), Math.sin(var) + 1, Math.sin(var));
                        second = loc.clone().add(Math.cos(var + Math.PI), Math.sin(var) + 1, Math.sin(var + Math.PI));

                        placedBlock.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, first, 0);
                        placedBlock.getWorld().spawnParticle(Particle.FLAME, second, 0);

                        if (var > Math.PI * 4) {
                            this.cancel();
                        }
                    }
                }.runTaskTimer(plugin, 0, 1);
            }
        }
    }
}
