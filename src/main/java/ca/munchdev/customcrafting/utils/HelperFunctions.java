package ca.munchdev.customcrafting.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class HelperFunctions {
    public static ItemStack getIngredientAmountFromIngredientList (List<ItemStack> ingredients, ItemStack ingredientToFind){
        for(ItemStack ingredient : ingredients) {
            if(ingredientToFind.getAmount() >= ingredient.getAmount()) {
                ItemStack modifiedIngredientToFind = ingredientToFind.clone();
                modifiedIngredientToFind.setAmount(ingredient.getAmount());

                if (ingredient.equals(modifiedIngredientToFind)) {
                    return ingredient;
                }
            }
        }

        // Otherwise, we return null
        return null;
    }

    public static ItemStack getIngredientAmountFromIngredientMap (Map<Character, ItemStack> ingredients, ItemStack ingredientToFind){
        for(ItemStack ingredient : ingredients.values()) {
            if(ingredientToFind.getAmount() >= ingredient.getAmount()) {
                ItemStack modifiedIngredientToFind = ingredientToFind.clone();
                modifiedIngredientToFind.setAmount(ingredient.getAmount());

                if (ingredient.equals(modifiedIngredientToFind)) {
                    return ingredient;
                }
            }
        }

        return null;
    }

    public static ItemStack getIngredientAmountFromIngredientAmountMap (Map<ItemStack, Integer> ingredients, ItemStack ingredientToFind){
        for(ItemStack ingredient : ingredients.keySet()) {
            if(ingredientToFind.getAmount() >= ingredient.getAmount()) {
                ItemStack modifiedIngredientToFind = ingredientToFind.clone();
                modifiedIngredientToFind.setAmount(ingredient.getAmount());

                if (ingredient.equals(modifiedIngredientToFind)) {
                    return ingredient;
                }
            }
        }
        return null;
    }

    public static ItemStack getLowestItemStackAmount(ItemStack stack1, ItemStack stack2){
        if(stack1.getAmount() >= stack2.getAmount()) {
            ItemStack modifiedStack1 = stack1.clone();
            modifiedStack1.setAmount(stack2.getAmount());

            return modifiedStack1;
        } else {
            return null;
        }
    }
}
