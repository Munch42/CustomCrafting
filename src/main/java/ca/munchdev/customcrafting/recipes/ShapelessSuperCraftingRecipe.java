package ca.munchdev.customcrafting.recipes;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.ArrayList;
import java.util.List;

public class ShapelessSuperCraftingRecipe {
    private ItemStack output;
    private List<ItemStack> ingredients = new ArrayList<>();

    public ShapelessSuperCraftingRecipe(ItemStack result) {
        this.output = result;
    }

    public ShapelessSuperCraftingRecipe addIngredient(ItemStack ingredient){
        addIngredient(1, ingredient);

        return this;
    }

    public ShapelessSuperCraftingRecipe addIngredient(int count, ItemStack ingredient){
        // This is from the ShapelessRecipe.java file to validate that we don't have more ingredients than can fit in our table.
        Validate.isTrue(ingredients.size() + count <= 9, "Shapeless recipes cannot have more than 9 ingredients");

        // The count thing is so that we can for example have that you would need two different sugar pieces in the crafting table.
        for (int i = 0; i < count; i++) {
            ingredients.add(ingredient);
        }

        return this;
    }

    // This is basically from the ShapelessRecipe.java too
    public List<ItemStack> getIngredientList() {
        ArrayList<ItemStack> result = new ArrayList<ItemStack>(ingredients.size());
        for (ItemStack ingredient : ingredients) {
            result.add(ingredient.clone());
        }
        return result;
    }

    public List<Material> getIngredientTypeList() {
        ArrayList<Material> result = new ArrayList<Material>(ingredients.size());
        for (ItemStack ingredient : ingredients) {
            result.add(ingredient.clone().getType());
        }
        return result;
    }

    public ItemStack getResult() {
        return output;
    }
}
