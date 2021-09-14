package ca.munchdev.customcrafting.recipes;

import ca.munchdev.customcrafting.utils.HeadHelpers;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PlayerHeadSuperCraftingRecipe extends ShapedSuperCraftingRecipe {
    public PlayerHeadSuperCraftingRecipe(ItemStack result) {
        super(result);
    }

    @Override
    public boolean compareGridToRecipe(Inventory inv, List<Integer> craftingSlots){
        List<Integer> localCraftingSlots = new ArrayList<Integer>(craftingSlots);
        List<ItemStack> recipeBeingCompared = getItemStackMap();

        boolean isEqual = true;

        // We remove the last item as that will always be the result slot in the recipe.
        int lastIndex = localCraftingSlots.size() - 1;
        localCraftingSlots.remove(lastIndex);

        //Bukkit.getServer().getConsoleSender().sendMessage(recipeBeingCompared.toString());
        //Bukkit.getServer().getConsoleSender().sendMessage(craftingSlots.toString());
        //Bukkit.getServer().getConsoleSender().sendMessage(localCraftingSlots.toString());

        int recipeListLocation = 0;
        for (Integer localCraftingSlot : localCraftingSlots) {
            ItemStack itemAtSlot = inv.getItem(localCraftingSlot);

            if (itemAtSlot == null) {
                itemAtSlot = new ItemStack(Material.AIR, 1);
            }

            if(recipeBeingCompared.get(recipeListLocation).getType() == Material.PLAYER_HEAD && itemAtSlot.getType() == Material.PLAYER_HEAD){
                if(HeadHelpers.arePlayerHeadsEqual(itemAtSlot, recipeBeingCompared.get(recipeListLocation))){
                    // If it is a head, and it is equal, we want to add to the next slot and move on to the next slot not to compare
                    // the itemstacks below.
                    recipeListLocation++;
                    continue;
                } else {
                    isEqual = false;
                    break;
                }
            }

            // TODO: Make it so that we compare the itemstack to the item at the slot. We probably need to map the Materials to the location in the crafting table.
            if (compareItemStacks(itemAtSlot, recipeBeingCompared.get(recipeListLocation))) {
                recipeListLocation++;
            } else {
                isEqual = false;
                break;
            }
        }

        //System.out.println("Equal?: " + isEqual);
        return isEqual;
    }
}
