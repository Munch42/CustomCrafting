package ca.munchdev.customcrafting.inventories;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BaseGUI {
    // https://www.spigotmc.org/threads/creating-guis-the-right-way.156378/

    private UUID uuid;
    private Inventory baseInventory;
    private Map<Integer, guiActions> actions;
    private String invType;
    private int invSize;

    public static Map<UUID, BaseGUI> inventoriesByUUID = new HashMap<>();
    //          layer UUID, GUI UUID
    public static Map<UUID, UUID> openInventories = new HashMap<>();

    public BaseGUI (int invSize, String invName) {
        uuid = UUID.randomUUID();
        baseInventory = Bukkit.createInventory(null, invSize, invName);
        actions = new HashMap<>();
        inventoriesByUUID.put(getUuid(), this);
        invType = "Default";
        this.invSize = invSize;
    }

    public Inventory getBaseInventory() {
        return baseInventory;
    }

    public void setItem(int slot, ItemStack stack, guiActions action){
        baseInventory.setItem(slot, stack);
        if (action != null) {
            actions.put(slot, action);
        }
    }

    public void setItem(int slot, ItemStack stack) {
        setItem(slot, stack, null);
    }

    public void open(Player p){
        p.openInventory(baseInventory);
        openInventories.put(p.getUniqueId(), getUuid());
    }

    public void delete(){
        for (Player p : Bukkit.getOnlinePlayers()) {
            UUID u = openInventories.get(p.getUniqueId());
            if (u.equals(getUuid())){
                p.closeInventory();
            }
        }

        inventoriesByUUID.remove(getUuid());
    }

    public UUID getUuid() {
        return uuid;
    }

    public static Map<UUID, BaseGUI> getInventoriesByUUID() {
        return inventoriesByUUID;
    }

    public static Map<UUID, UUID> getOpenInventories() {
        return openInventories;
    }

    public Map<Integer, guiActions> getActions() {
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
