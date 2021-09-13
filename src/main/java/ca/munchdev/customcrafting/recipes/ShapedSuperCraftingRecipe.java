package ca.munchdev.customcrafting.recipes;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShapedSuperCraftingRecipe {
    private ItemStack output;
    private String[] rows;
    private Map<Character, ItemStack> ingredients = new HashMap<>();

    public ShapedSuperCraftingRecipe(ItemStack result) {
        this.output = result;

        // Since the blank slot will always be air we set it here.
        setIngredient(' ', new ItemStack(Material.AIR));
    }

    // This is from the ShapedRecipe.java file.
    public ShapedSuperCraftingRecipe shape(@NotNull final String... shape) {
        Validate.notNull(shape, "Must provide a shape");
        Validate.isTrue(shape.length > 0 && shape.length < 4, "Crafting recipes should be 1, 2 or 3 rows, not ", shape.length);

        int lastLen = -1;
        for (String row : shape) {
            Validate.notNull(row, "Shape cannot have null rows");
            Validate.isTrue(row.length() > 0 && row.length() < 4, "Crafting rows should be 1, 2, or 3 characters, not ", row.length());

            Validate.isTrue(lastLen == -1 || lastLen == row.length(), "Crafting recipes must be rectangular");
            lastLen = row.length();
        }
        this.rows = new String[shape.length];
        for (int i = 0; i < shape.length; i++) {
            this.rows[i] = shape[i];
        }

        // Remove character mappings for characters that no longer exist in the shape
        HashMap<Character, ItemStack> newIngredients = new HashMap<>();
        for (String row : shape) {
            for (Character c : row.toCharArray()) {
                newIngredients.put(c, ingredients.get(c));
            }
        }
        this.ingredients = newIngredients;

        return this;
    }

    public ShapedSuperCraftingRecipe setIngredient(char key, ItemStack ingredient) {
        ingredients.put(key, ingredient);

        return this;
    }

    // This is also from ShapedRecipe.java with some minor changes to accommodate materials instead of RecipeChoice
    public Map<Character, ItemStack> getIngredientMap() {
        HashMap<Character, ItemStack> result = new HashMap<Character, ItemStack>();
        for (Map.Entry<Character, ItemStack> ingredient : ingredients.entrySet()) {
            if (ingredient.getValue() == null) {
                result.put(ingredient.getKey(), null);
            } else {
                result.put(ingredient.getKey(), new ItemStack(ingredient.getValue()));
            }
        }
        return result;
    }

    public List<Material> getMaterialMap() {
        List<Material> recipe = new ArrayList<>();
        Map<Character, ItemStack> ingredients = getIngredientMap();

        // We loop through all the strings (each row in the crafting table) and then we loop through each character in each string.
        for (String x : getShape()){
            for (int y = 0; y < x.length(); y++){
                char i = x.charAt(y);

                ItemStack ingredient = ingredients.get(i);

                if(ingredient != null){
                    // Then we get the ingredient from the character key and we add that to the list of materials.
                    recipe.add(ingredient.getType());
                }
            }
        }

        return recipe;
    }

    public List<Character> getCharacterList() {
        // We create an arraylist with all the values of the ingredient maps keys.
        List<Character> allCharacters = new ArrayList<>(getIngredientMap().keySet());

        return allCharacters;
    }

    public List<ItemStack> getItemStackMap() {
        List<ItemStack> recipe = new ArrayList<>();
        Map<Character, ItemStack> ingredients = getIngredientMap();

        // We loop through all the strings (each row in the crafting table) and then we loop through each character in each string.
        for (String x : getShape()){
            for (int y = 0; y < x.length(); y++){
                char i = x.charAt(y);

                ItemStack ingredient = ingredients.get(i);

                if(ingredient != null){
                    // Then we get the ingredient from the character key and we add that to the list of materials.
                    recipe.add(ingredient);
                }
            }
        }

        return recipe;
    }

    public boolean compareItemStacks(ItemStack stack1, ItemStack stack2){
        if(stack1.getAmount() >= stack2.getAmount()) {
            ItemStack modifiedStack1 = stack1.clone();
            modifiedStack1.setAmount(stack2.getAmount());

            return modifiedStack1.equals(stack2);
        } else {
            return false;
        }
    }

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
                //Bukkit.getServer().getConsoleSender().sendMessage("No Item In Slot");
                //Bukkit.getServer().getConsoleSender().sendMessage(itemType.toString());
            }

            //System.out.println("Compared Recipe Item: " + recipeBeingCompared.get(recipeListLocation));

            //Bukkit.getServer().getConsoleSender().sendMessage(String.valueOf(recipeListLocation));
            //Bukkit.getServer().getConsoleSender().sendMessage(recipeBeingCompared.get(recipeListLocation).toString());
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

    public ItemStack getResult(){
        return output;
    }

    public String[] getShape(){
        return rows;
    }
}
