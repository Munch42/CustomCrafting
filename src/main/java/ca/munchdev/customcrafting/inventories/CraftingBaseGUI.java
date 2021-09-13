package ca.munchdev.customcrafting.inventories;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CraftingBaseGUI {
    private List<Integer> openSlots;
    private UUID uuid;
    private Inventory baseCraftingInventory;
    private Map<Integer, CraftingBaseGUI.guiActions> actions;
    private String invType;
    private int invSize;

    // If we want the items/data in the menu to not be saved we also have to clear it from here. Not just the open
    // ones.
    public static Map<UUID, CraftingBaseGUI> craftingInventoriesByUUID = new HashMap<>();
    //         Player UUID, GUI UUID
    public static Map<UUID, UUID> openCraftingInventories = new HashMap<>();

    public Inventory getBaseCraftingInventory() {
        return baseCraftingInventory;
    }

    public CraftingBaseGUI(int invSize, String invName, List<Integer> openSlots, ItemStack backgroundItem) {
        this.openSlots = openSlots;

        uuid = UUID.randomUUID();
        baseCraftingInventory = Bukkit.createInventory(null, invSize, invName);
        actions = new HashMap<>();
        craftingInventoriesByUUID.put(getUuid(), this);
        invType = "Crafting";
        this.invSize = invSize;

        for (int i = 0; i < invSize; i++){
            // If the slot we are on in the inventory is not an open slot, we add a glass pane
            if(!openSlots.contains(i)){
                setItem(i, backgroundItem);
            }
        }
    }

    public List<Integer> getOpenSlots() {
        return openSlots;
    }

    public void setItem(int slot, ItemStack stack, CraftingBaseGUI.guiActions action){
        baseCraftingInventory.setItem(slot, stack);
        if (action != null) {
            actions.put(slot, action);
        }
    }

    public void setItem(int slot, ItemStack stack) {
        setItem(slot, stack, null);
    }

    public void open(Player p){
        p.openInventory(baseCraftingInventory);
        openCraftingInventories.put(p.getUniqueId(), getUuid());
    }

    public void delete(){
        for (Player p : Bukkit.getOnlinePlayers()) {
            UUID u = openCraftingInventories.get(p.getUniqueId());
            if (u.equals(getUuid())){
                p.closeInventory();
            }
        }

        craftingInventoriesByUUID.remove(getUuid());
    }

    public UUID getUuid() {
        return uuid;
    }

    public static Map<UUID, CraftingBaseGUI> getCraftingInventoriesByUUID() {
        return craftingInventoriesByUUID;
    }

    public static Map<UUID, UUID> getOpenCraftingInventories() {
        return openCraftingInventories;
    }

    public Map<Integer, CraftingBaseGUI.guiActions> getActions() {
        return actions;
    }

    public String getInvType() {
        return invType;
    }

    public int getInvSize() {
        return invSize;
    }

    public interface guiActions {
        void click(Player player);
    }
}
