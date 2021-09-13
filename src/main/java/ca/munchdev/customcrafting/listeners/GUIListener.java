package ca.munchdev.customcrafting.listeners;

import ca.munchdev.customcrafting.CustomCrafting;
import ca.munchdev.customcrafting.inventories.BaseGUI;
import ca.munchdev.customcrafting.inventories.CraftingBaseGUI;
import ca.munchdev.customcrafting.recipes.ShapedSuperCraftingRecipe;
import ca.munchdev.customcrafting.recipes.ShapedVanillaSuperRecipe;
import ca.munchdev.customcrafting.recipes.ShapelessSuperCraftingRecipe;
import ca.munchdev.customcrafting.utils.HelperFunctions;
import ca.munchdev.customcrafting.utils.HelperFunctions.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class GUIListener implements Listener {
    private CustomCrafting plugin;
    private boolean multipleIngredientsReady = false;
    private ShapedSuperCraftingRecipe lastShapedRecipe;
    private ShapelessSuperCraftingRecipe lastShapelessRecipe;

    public GUIListener(CustomCrafting plugin){
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event){
        if(!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        //event.getWhoClicked().sendMessage(String.valueOf(event.getRawSlot()));

        Player player = (Player) event.getWhoClicked();
        UUID playerUUID = player.getUniqueId();

        String invType = "Default";

        UUID inventoryUUID = BaseGUI.openInventories.get(playerUUID);

        if(inventoryUUID == null){
            inventoryUUID = CraftingBaseGUI.openCraftingInventories.get(playerUUID);
            invType = "Crafting";
        }

        if(inventoryUUID != null){
            if(invType.equalsIgnoreCase("Default")) {
                BaseGUI gui = BaseGUI.getInventoriesByUUID().get(inventoryUUID);
                BaseGUI.guiActions action = gui.getActions().get(event.getSlot());

                // This is where we cancel the clicking in the inventory.
                event.setCancelled(true);

                if(action != null) {
                    action.click(player);
                }
            } else if (invType.equalsIgnoreCase("Crafting")){
                CraftingBaseGUI gui = CraftingBaseGUI.getCraftingInventoriesByUUID().get(inventoryUUID);
                CraftingBaseGUI.guiActions action = gui.getActions().get(event.getSlot());

                // We only want to cancel the panes of glass in the GUI from being moved
                if(!gui.getOpenSlots().contains(event.getSlot())){
                    if (event.getRawSlot() <= (gui.getInvSize() - 1)) {
                        event.setCancelled(true);
                    }
                }

                ItemStack barrierItem = new ItemStack(Material.BARRIER);
                ItemMeta itemMeta = barrierItem.getItemMeta();
                itemMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "No Matching Recipe");
                barrierItem.setItemMeta(itemMeta);

                if(event.getCurrentItem() != null && event.getCurrentItem().equals(new ItemStack(barrierItem))){
                    event.setCancelled(true);
                }

                if(action != null) {
                    action.click(player);
                }
            }
        }
    }

    @EventHandler
    public void onClickSuperCrafting(InventoryClickEvent event){
        if(!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        UUID playerUUID = player.getUniqueId();

        String invType = "Default";

        UUID inventoryUUID = BaseGUI.openInventories.get(playerUUID);

        if(inventoryUUID == null){
            inventoryUUID = CraftingBaseGUI.openCraftingInventories.get(playerUUID);
            invType = "Crafting";
        }

        if(inventoryUUID != null) {
            if(invType.equalsIgnoreCase("Crafting")){
                CraftingBaseGUI gui = CraftingBaseGUI.getCraftingInventoriesByUUID().get(inventoryUUID);
                Inventory inv = event.getInventory();

                // This is the index number for the slot in the gui.getOpenSlots() list.
                int resultSlotIndex = gui.getOpenSlots().size() - 1;

                ItemStack barrierItem = new ItemStack(Material.BARRIER);
                ItemMeta itemMeta = barrierItem.getItemMeta();
                itemMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "No Matching Recipe");
                barrierItem.setItemMeta(itemMeta);

                if(event.getRawSlot() == gui.getOpenSlots().get(resultSlotIndex)){
                    if(!event.getCurrentItem().equals(new ItemStack(barrierItem))){
                        List<Integer> localCraftingSlots = new ArrayList<>(gui.getOpenSlots());
                        localCraftingSlots.remove(resultSlotIndex);

                        // We go through all the slots except the result slot and we set it to air/empty.
                        // But we need to check if there is multiple in the slot before doing so and we then just remove 1 copy of the recipe
                        // amount of each.
                        for (Integer slot : localCraftingSlots){
                            if(inv.getItem(slot) != null) {
                                if (inv.getItem(slot).getAmount() > 1) {
                                    //System.out.println("Item Looking For: " + inv.getItem(slot));
                                    if(lastShapedRecipe != null) {
                                        //System.out.println("After Amount > 1: " + lastShapedRecipe.getItemStackMap());
                                        ItemStack matchingRecipeIngredient = HelperFunctions.getIngredientAmountFromIngredientMap(lastShapedRecipe.getIngredientMap(), inv.getItem(slot));

                                        //System.out.println("After shaped recipe: " + matchingRecipeIngredient);
                                        //System.out.println("After shaped recipe Amount: " + matchingRecipeIngredient.getAmount());

                                        if (matchingRecipeIngredient == null) {
                                            // If it returned null, we just remove 1 as a fallback. The type doesn't matter here.
                                            matchingRecipeIngredient = new ItemStack(Material.AIR, 1);
                                        }

                                        inv.setItem(slot, new ItemStack(inv.getItem(slot).getType(), inv.getItem(slot).getAmount() - matchingRecipeIngredient.getAmount()));
                                    } else if(lastShapelessRecipe != null){
                                        //System.out.println("After Amount > 1 Pt 2: " + lastShapelessRecipe.getIngredientList());
                                        ItemStack matchingRecipeIngredient = HelperFunctions.getIngredientAmountFromIngredientList(lastShapelessRecipe.getIngredientList(), inv.getItem(slot));

                                        //System.out.println("After shapeless recipe: " + matchingRecipeIngredient);
                                        //System.out.println("After shapeless recipe Amount: " + matchingRecipeIngredient.getAmount());

                                        if (matchingRecipeIngredient == null) {
                                            // If it returned null, we just remove 1 as a fallback. The type doesn't matter here.
                                            matchingRecipeIngredient = new ItemStack(Material.AIR, 1);
                                        }

                                        inv.setItem(slot, new ItemStack(inv.getItem(slot).getType(), inv.getItem(slot).getAmount() - matchingRecipeIngredient.getAmount()));
                                    } else {
                                        if(inv.getItem(slot) != null){
                                            inv.setItem(slot, new ItemStack(inv.getItem(slot).getType(), inv.getItem(slot).getAmount() - 1));
                                        }
                                    }

                                    multipleIngredientsReady = true;
                                } else {
                                    inv.setItem(slot, new ItemStack(Material.AIR));
                                }
                            }
                        }

                        new BukkitRunnable(){
                            @Override
                            public void run() {
                                // We wait 1 tick so that the player has the item in their hand and then we set the result slot back to a barrier.
                                // But first we check to see if there are multiple items and if there are we just leave it.
                                if(!multipleIngredientsReady) {
                                    inv.setItem(gui.getOpenSlots().get(resultSlotIndex), new ItemStack(barrierItem));
                                } else {
                                    multipleIngredientsReady = false;
                                }
                            }
                        }.runTaskLater(plugin, 1L);
                    }
                }

                List<ShapedSuperCraftingRecipe> allRecipes = plugin.getShapedSuperCraftingRecipes();
                List<ShapelessSuperCraftingRecipe> allShapelessRecipes = plugin.getShapelessSuperCraftingRecipes();
                List<ShapedVanillaSuperRecipe> allVanillaShapedRecipes = plugin.getShapedVanillaSuperCraftingRecipes();

                // We need the bukkit runnable to delay it by 1 tick because otherwise the event doesn't recognize that the item
                // which was just clicked/placed is actually in the spot it is supposed to be in for the recipe.
                new BukkitRunnable(){
                    @Override
                    public void run(){
                        for(ShapedSuperCraftingRecipe recipe : allRecipes){
                            //player.sendMessage(String.valueOf(recipe.compareGridToRecipe(inv, gui.getOpenSlots())));
                            if(recipe.compareGridToRecipe(inv, gui.getOpenSlots())){
                                inv.setItem(gui.getOpenSlots().get(resultSlotIndex), recipe.getResult());
                                lastShapedRecipe = recipe;
                                break;
                            } else {
                                inv.setItem(gui.getOpenSlots().get(resultSlotIndex), new ItemStack(barrierItem));
                                lastShapedRecipe = null;
                            }
                        }

                        if(inv.getItem(gui.getOpenSlots().get(resultSlotIndex)).getType().equals(Material.BARRIER)){
                            for(ShapedVanillaSuperRecipe recipe : allVanillaShapedRecipes){
                                //player.sendMessage(String.valueOf(recipe.compareGridToRecipe(inv, gui.getOpenSlots())));
                                if(recipe.compareGridToRecipe(inv, gui.getOpenSlots())){
                                    inv.setItem(gui.getOpenSlots().get(resultSlotIndex), recipe.getResult());
                                    break;
                                } else {
                                    inv.setItem(gui.getOpenSlots().get(resultSlotIndex), new ItemStack(barrierItem));
                                }
                            }
                        }

                        // If after we have run through all of the shaped recipes, there is still no result, we try with the shapeless ones.
                        //System.out.println("Before Barrier Check");
                        if(inv.getItem(gui.getOpenSlots().get(resultSlotIndex)).getType().equals(Material.BARRIER)){
                            List<ItemStack> ingredientsInTable = new ArrayList<>();

                            //System.out.println("Result Slot: " + resultSlotIndex);

                            for(int slot : gui.getOpenSlots()){
                                //System.out.println("Slot Before: " + slot);
                                if(slot == gui.getOpenSlots().get(resultSlotIndex)){
                                    continue;
                                }

                                //System.out.println("Slot: " + slot);

                                if(inv.getItem(slot) != null) {
                                    ingredientsInTable.add(inv.getItem(slot));
                                }
                            }

                            //System.out.println("Table Ingredients: " + ingredientsInTable);

                            for(ShapelessSuperCraftingRecipe recipe: allShapelessRecipes){
                                boolean containsAllIngredients = true;
                                //System.out.println("Reset Does it Contain all: " + containsAllIngredients);

                                if(ingredientsInTable.size() <= 0){
                                    containsAllIngredients = false;
                                }

                                // We tally up all the different itemstack amounts in the table to compare them to the recipe.
                                Map<ItemStack, Integer> uniqueItemAmountsInTable = new HashMap<>();
                                Map<ItemStack, Integer> uniqueItemAmountsInRecipe = new HashMap<>();

                                // We loop through all the ingredients in the recipe and check if the crafting table has all of them.
                                // If it does, we set the result and apply the same logic as above.
                                for(ItemStack ingredient : recipe.getIngredientList()){
                                    //System.out.println("Ingredient To Check: " + ingredient);

                                    /*if (!ingredientsInTable.contains(ingredient)) {
                                        System.out.println("No Here!!");
                                        containsAllIngredients = false;
                                        //System.out.println("Doesn't Contain all: " + containsAllIngredients);
                                        break;
                                    }*/

                                    uniqueItemAmountsInRecipe.merge(ingredient, 1, Integer::sum);
                                }

                                if(containsAllIngredients){
                                    for(ItemStack item : ingredientsInTable) {
                                        if (!recipe.getIngredientTypeList().contains(item.getType())){
                                            containsAllIngredients = false;
                                            break;
                                        }

                                        ItemStack recipeTwinItem = HelperFunctions.getIngredientAmountFromIngredientAmountMap(uniqueItemAmountsInRecipe, item);
                                        // It shouldn't be null since the above if statement will break out of the loop if it is not in the recipe.
                                        ItemStack fixedItem = HelperFunctions.getLowestItemStackAmount(item, recipeTwinItem);

                                        // This is a cool snippet that the IDE recommended instead of looping through each item and increasing
                                        // its amount manually or setting it to 1 if it isn't already there.
                                        uniqueItemAmountsInTable.merge(fixedItem, 1, Integer::sum);
                                    }
                                }

                                if(containsAllIngredients){
                                    if(!(uniqueItemAmountsInTable.equals(uniqueItemAmountsInRecipe))){
                                        //System.out.println("Here!!");
                                        containsAllIngredients = false;
                                    }
                                }

                                // If it contains all the ingredients, we add the item as the result, and we break out of the recipe check loop.
                                if(containsAllIngredients){
                                    //System.out.println("Yay!");
                                    //System.out.println("Does Contain All: " + recipe.getResult());
                                    inv.setItem(gui.getOpenSlots().get(resultSlotIndex), recipe.getResult());
                                    lastShapelessRecipe = recipe;
                                    break;
                                } else {
                                    //System.out.println("Doesn't Contain All");
                                    inv.setItem(gui.getOpenSlots().get(resultSlotIndex), new ItemStack(barrierItem));
                                    lastShapelessRecipe = null;
                                }
                            }
                        }
                    }
                }.runTaskLater(plugin, 1L);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        //plugin.getServer().getConsoleSender().sendMessage(BaseGUI.openInventories.toString());
        //plugin.getServer().getConsoleSender().sendMessage(CraftingBaseGUI.openCraftingInventories.toString());
        BaseGUI.openInventories.remove(playerUUID);
        CraftingBaseGUI.openCraftingInventories.remove(playerUUID);
        //plugin.getServer().getConsoleSender().sendMessage(BaseGUI.openInventories.toString());
        //plugin.getServer().getConsoleSender().sendMessage(CraftingBaseGUI.openCraftingInventories.toString());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = (Player) event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        BaseGUI.openInventories.remove(playerUUID);
        CraftingBaseGUI.openCraftingInventories.remove(playerUUID);
    }

}
