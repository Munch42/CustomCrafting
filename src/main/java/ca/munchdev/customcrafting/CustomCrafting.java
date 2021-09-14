package ca.munchdev.customcrafting;

import ca.munchdev.customcrafting.inventories.CraftingBaseGUI;
import ca.munchdev.customcrafting.inventories.TestGUI;
import ca.munchdev.customcrafting.listeners.BlockPlaceListener;
import ca.munchdev.customcrafting.listeners.GUIListener;
import ca.munchdev.customcrafting.listeners.PlayerInteractListener;
import ca.munchdev.customcrafting.listeners.PlayerJoinListener;
import ca.munchdev.customcrafting.recipes.PlayerHeadSuperCraftingRecipe;
import ca.munchdev.customcrafting.recipes.ShapedSuperCraftingRecipe;
import ca.munchdev.customcrafting.recipes.ShapedVanillaSuperRecipe;
import ca.munchdev.customcrafting.recipes.ShapelessSuperCraftingRecipe;
import ca.munchdev.customcrafting.utils.HeadHelpers;
import ca.munchdev.customcrafting.utils.SkullCreator;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public final class CustomCrafting extends JavaPlugin implements Listener {

    private static CustomCrafting plugin;
    //private TestGUI testGUI;
    //private CraftingBaseGUI craftingGUI;
    private List<Integer> superCraftingTableSlots = new ArrayList<>();
    private ProtocolManager protocolManager;
    private List<ShapedSuperCraftingRecipe> shapedSuperCraftingRecipes = new ArrayList<>();
    private List<ShapedVanillaSuperRecipe> shapedVanillaSuperCraftingRecipes = new ArrayList<>();
    private List<PlayerHeadSuperCraftingRecipe> shapedHeadSuperCraftingRecipes = new ArrayList<>();
    private List<ShapelessSuperCraftingRecipe> shapelessSuperCraftingRecipes = new ArrayList<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;

        calculateOpenSlots();
        setCustomShapedSuperCraftingRecipes();
        addVanillaRecipesToSuperCraftingTable();
        protocolManager = ProtocolLibrary.getProtocolManager();

        //testGUI = new TestGUI();
        new GUIListener(this);
        new PlayerInteractListener(this);
        new BlockPlaceListener(this);
        new PlayerJoinListener(this);
        //Bukkit.getPluginManager().registerEvents(this, plugin);

        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "CustomCrafting Version " + getDescription().getVersion() + " is enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        getServer().getConsoleSender().sendMessage(ChatColor.RED + "CustomCrafting Version " + getDescription().getVersion() + " is disabled!");
    }

    private void calculateOpenSlots(){
        // Super Crafting Table Slots
        int slot = 10;
        for (int x = 0; x < 10; x++) {
            //getServer().getConsoleSender().sendMessage(String.valueOf(slot));
            superCraftingTableSlots.add(slot);
            switch (slot) {
                case 12:
                    slot = 19;
                    break;
                case 21:
                    slot = 28;
                    break;
                case 30:
                    slot = 24;
                    break;
                default:
                    slot++;
            }
        }
    }

    private void setCustomShapedSuperCraftingRecipes() {
        ShapedSuperCraftingRecipe recipeTest = new ShapedSuperCraftingRecipe(new ItemStack(Material.DIAMOND));
        recipeTest.shape(" D ", " D ", " C ");
        recipeTest.setIngredient('D', new ItemStack(Material.DIRT));
        recipeTest.setIngredient('C', new ItemStack(Material.COBBLESTONE));

        shapedSuperCraftingRecipes.add(recipeTest);

        ShapedSuperCraftingRecipe recipeMultipleItemTest = new ShapedSuperCraftingRecipe(new ItemStack(Material.NETHERITE_INGOT));
        recipeMultipleItemTest.shape(" D ", " D ", " D ");
        recipeMultipleItemTest.setIngredient('D', new ItemStack(Material.DIRT, 2));

        shapedSuperCraftingRecipes.add(recipeMultipleItemTest);

        ShapelessSuperCraftingRecipe shapelessTest = new ShapelessSuperCraftingRecipe(new ItemStack(Material.DIRT));
        shapelessTest.addIngredient(3, new ItemStack(Material.DIAMOND));

        shapelessSuperCraftingRecipes.add(shapelessTest);

        PlayerHeadSuperCraftingRecipe headRecipeTest = new PlayerHeadSuperCraftingRecipe(new ItemStack(Material.ACACIA_BOAT));
        headRecipeTest.shape(" H ", "   ", "   ");
        headRecipeTest.setIngredient('H', new ItemStack(Material.DIRT));

        shapedHeadSuperCraftingRecipes.add(headRecipeTest);

        //getServer().getConsoleSender().sendMessage(recipeTest.getResult().toString());
        //getServer().getConsoleSender().sendMessage(Arrays.toString(recipeTest.getShape()));
        //getServer().getConsoleSender().sendMessage(recipeTest.getIngredientMap().toString());
    }

    private String[] filterShape(String[] vanillaShape){
        // We know that it has to be a rectangle, so it can't be for example: ab, cde
        // It would have to be abc, def with the c being air. Therefore, we can check to see the properties of the first one,
        // and we can mirror the fixes that we do to that to the second one.
        // Our goal is to make it into a 3x3 shape for our crafting system.

        // This lets us know how wide and tall it is.
        int firstRowLength = vanillaShape[0].length();
        int totalRows = vanillaShape.length;
        String[] filteredShape = new String[3];
        boolean alteredOne = false;
        boolean alteredTwo = false;
        boolean alteredThree = false;

        //System.out.println("Before Check 1: " + firstRowLength);

        if(firstRowLength < 3) {
            int difference = 3 - firstRowLength;
            //System.out.println("In Check 1: " + difference);

            int rowNum = 0;
            for(String row : vanillaShape){
                //System.out.println("In loop 1: " + rowNum);
                StringBuilder rowBuilder = new StringBuilder(row);
                for (int i = 0; i < difference; i++) {
                    rowBuilder.append("z");
                    //System.out.println("In loop 2: " + rowBuilder.toString());
                }

                row = rowBuilder.toString();

                filteredShape[rowNum] = row;

                switch(rowNum){
                    case 0:
                        alteredOne = true;
                        break;
                    case 1:
                        alteredTwo = true;
                        break;
                    case 2:
                        alteredThree = true;
                        break;
                }

                rowNum++;
            }
        }

        if(!alteredOne){
            filteredShape[0] = vanillaShape[0];
        }

        if(totalRows <= 1){
            filteredShape[1] = "zzz";
        } else if(!alteredTwo){
            filteredShape[1] = vanillaShape[1];
        }

        if(totalRows <= 2){
            filteredShape[2] = "zzz";
        } else if(!alteredThree){
            filteredShape[2] = vanillaShape[2];
        }

        //System.out.println(Arrays.toString(filteredShape));

        return filteredShape;
    }

    private void addVanillaRecipesToSuperCraftingTable(){
        // https://www.spigotmc.org/threads/gui-custom-crafting-table.392739/

        Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
        while(recipeIterator.hasNext()) {
            Recipe recipe = recipeIterator.next();
            if(recipe instanceof ShapelessRecipe) {
                //handle shapeless recipe
                ShapelessSuperCraftingRecipe vanillaShapelessRecipe = new ShapelessSuperCraftingRecipe(recipe.getResult());

                // We loop through all the ingredients and add them to the table.
                for(ItemStack ingredient : ((ShapelessRecipe) recipe).getIngredientList()){
                    vanillaShapelessRecipe.addIngredient(ingredient);
                }

                shapelessSuperCraftingRecipes.add(vanillaShapelessRecipe);

                //System.out.println(vanillaShapelessRecipe.getResult());
                //System.out.println(vanillaShapelessRecipe.getIngredientList());
            } else if(recipe instanceof ShapedRecipe) {
                //handle shaped recipes
                ShapedVanillaSuperRecipe vanillaRecipe = new ShapedVanillaSuperRecipe(recipe.getResult());

                //System.out.println(recipe.getResult().toString());
                //System.out.println(Arrays.toString(((ShapedRecipe) recipe).getShape()));
                //System.out.println(((ShapedRecipe) recipe).getIngredientMap());

                vanillaRecipe.shape(filterShape(((ShapedRecipe) recipe).getShape()));

                // We loop through all the ingredients and get their type (Their material) and add that to the vanillaRecipe recipe.
                Map<Character, ItemStack> ingredientMap = ((ShapedRecipe) recipe).getIngredientMap();
                //System.out.println(recipe.getResult());
                //System.out.println(ingredientMap);
                //System.out.println(Arrays.toString(((ShapedRecipe) recipe).getShape()));

                for (Map.Entry<Character, ItemStack> entry : ingredientMap.entrySet()) {
                    Character key = entry.getKey();
                    ItemStack item = entry.getValue();

                    //System.out.println("Key: " + key);
                    //System.out.println("Item: " + item);

                    if(item == null) {
                        item = new ItemStack(Material.AIR);
                    }

                    vanillaRecipe.setIngredient(key, item.getType());
                }

                // We always set z to air because this is the placeholder we add to the filtered shapes since I believe no recipe uses it.
                vanillaRecipe.setIngredient('z', Material.AIR);

                shapedVanillaSuperCraftingRecipes.add(vanillaRecipe);
            }
        }
    }

    public List<Integer> getSuperCraftingTableSlots() {
        return superCraftingTableSlots;
    }

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    public List<ShapedSuperCraftingRecipe> getShapedSuperCraftingRecipes(){
        return shapedSuperCraftingRecipes;
    }

    public List<ShapedVanillaSuperRecipe> getShapedVanillaSuperCraftingRecipes() { return shapedVanillaSuperCraftingRecipes; }

    public List<ShapelessSuperCraftingRecipe> getShapelessSuperCraftingRecipes() {
        return shapelessSuperCraftingRecipes;
    }

    // This is a temporary listener just to test the GUIs
    /*@EventHandler
    public void onJoin(PlayerJoinEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                craftingGUI.open(event.getPlayer());
            }
        }.runTaskLater(this, 1);
    }*/
}
