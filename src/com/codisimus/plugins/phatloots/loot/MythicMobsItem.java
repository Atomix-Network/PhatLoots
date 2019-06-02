package com.codisimus.plugins.phatloots.loot;

import com.codisimus.plugins.phatloots.PhatLoot;
import com.codisimus.plugins.phatloots.PhatLoots;
import com.codisimus.plugins.phatloots.PhatLootsUtil;
import com.codisimus.plugins.phatloots.gui.Tool;
import io.lumine.xikage.mythicmobs.MythicMobs;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * A MythicMobsItem is an ItemStack generated by the plugin MythicMobs
 *
 * @author Codisimus
 */
@SerializableAs("MythicMobsItem")
public class MythicMobsItem extends Loot {
    public String itemId;
    public int amountLower = 1;
    public int amountUpper = 1;

    /**
     * Constructs a new Loot with the given tier
     *
     * @param itemId The name of the MythicMobs item
     */
    public MythicMobsItem(String itemId) {
        this.itemId = itemId;
    }

    /**
     * Constructs a new Loot with the given tier and amount/durability ranges
     *
     * @param itemId The name of the MythicMobs item
     * @param amountLower The lower bound of the amount range
     * @param amountUpper The upper bound of the amount range
     */
    public MythicMobsItem(String itemId, int amountLower, int amountUpper) {
        this.itemId = itemId;
        this.amountLower = amountLower;
        this.amountUpper = amountUpper;
    }

    /**
     * Constructs a new MythicDrops Item from a Configuration Serialized phase
     *
     * @param map The map of data values
     */
    public MythicMobsItem(Map<String, Object> map) {
        String currentLine = null; //The value that is about to be loaded (used for debugging)
        try {
            Object number = map.get(currentLine = "Probability");
            setProbability((number instanceof Double) ? (Double) number : (Integer) number);
            itemId = (String) map.get(currentLine = "ItemID");
            if (map.containsKey(currentLine = "Amount")) {
                amountLower = amountUpper = (Integer) map.get(currentLine);
            } else if (map.containsKey(currentLine = "AmountLower")) {
                amountLower = (Integer) map.get(currentLine);
                amountUpper = (Integer) map.get(currentLine = "AmountUpper");
            }
        } catch (Exception ex) {
            //Print debug messages
            PhatLoots.logger.severe("Failed to load MythicMobsItem line: " + currentLine);
            PhatLoots.logger.severe("of PhatLoot: " + (PhatLoot.current == null ? "unknown" : PhatLoot.current));
            PhatLoots.logger.severe("Last successfull load was...");
            PhatLoots.logger.severe("PhatLoot: " + (PhatLoot.last == null ? "unknown" : PhatLoot.last));
            PhatLoots.logger.severe("Loot: " + (Loot.last == null ? "unknown" : Loot.last.toString()));
        }
    }

    /**
     * Generates a MythicMobs item and adds it to the item list
     *
     * @param lootBundle The loot that has been rolled for
     * @param lootingBonus The increased chance of getting rarer loots
     */
    @Override
    public void getLoot(LootBundle lootBundle, double lootingBonus) {
        int amount = PhatLootsUtil.rollForInt(amountLower, amountUpper);
        ItemStack item = MythicMobs.inst().getItemManager().getItemStack(itemId).clone();
        item.setAmount(amount);
        lootBundle.addItem(item);
    }

    /**
     * Returns the information of the MythicDrops Item in the form of an ItemStack
     *
     * @return An ItemStack representation of the Loot
     */
    @Override
    public ItemStack getInfoStack() {
        //A MythicMobsItem is represented by an Enchantment Table
        ItemStack infoStack = new ItemStack(Material.ENCHANTING_TABLE);

        //Set the display name of the item
        ItemMeta info = Bukkit.getItemFactory().getItemMeta(infoStack.getType());
        info.setDisplayName("§2MythicMobs Item");

        //Add more specific details of the item
        List<String> details = new ArrayList<>();
        details.add("§qItem ID: §6" + itemId);
        details.add("§1Probability: §6" + getProbability());
        if (amountLower == amountUpper) {
            details.add("§1Amount: §6" + amountLower);
        } else {
            details.add("§1Amount: §6" + amountLower + '-' + amountUpper);
        }

        //Construct the ItemStack and return it
        info.setLore(details);
        infoStack.setItemMeta(info);
        return infoStack;
    }

    /**
     * Toggles a Loot setting depending on the type of Click
     *
     * @param click The type of Click (Only SHIFT_LEFT, SHIFT_RIGHT, and MIDDLE are used)
     * @return true if the Loot InfoStack should be refreshed
     */
    @Override
    public boolean onToggle(ClickType click) {
        return false;
    }

    /**
     * Toggles the MythicDrops Tier depending on the type of Click
     *
     * @param tool The Tool that was used to click
     * @param click The type of Click (Only LEFT, RIGHT, MIDDLE, SHIFT_LEFT, SHIFT_RIGHT, and DOUBLE_CLICK are used)
     * @return true if the Loot InfoStack should be refreshed
     */
    @Override
    public boolean onToolClick(Tool tool, ClickType click) {
        return false;
    }

    /**
     * Modifies the amount associated with the Loot
     *
     * @param amount The amount to modify by (may be negative)
     * @param both true if both lower and upper ranges should be modified, false for only the upper range
     * @return true if the Loot InfoStack should be refreshed
     */
    @Override
    public boolean modifyAmount(int amount, boolean both) {
        if (both) {
            amountLower += amount;
            if (amountLower < 0) {
                amountLower = 0;
            }
        }
        amountUpper += amount;
        //Upper bound cannot be less than lower bound
        if (amountUpper < amountLower) {
            amountUpper = amountLower;
        }
        return true;
    }

    /**
     * Resets the amount of Loot to 1
     *
     * @return true if the Loot InfoStack should be refreshed
     */
    @Override
    public boolean resetAmount() {
        amountLower = 1;
        amountUpper = 1;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(amountLower);
        if (amountLower != amountUpper) {
            sb.append('-');
            sb.append(amountUpper);
        }

        sb.append(" ");
        sb.append(itemId);
        sb.append(" MythicMobs item ");

        sb.append(" @ ");
        //Only display the decimal values if the probability is not a whole number
        sb.append(Math.floor(getProbability()) == getProbability() ? String.valueOf((int) getProbability()) : String.valueOf(getProbability()));

        sb.append("%");

        return sb.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof MythicMobsItem)) {
            return false;
        }

        MythicMobsItem loot = (MythicMobsItem) object;
        return loot.itemId.equals(itemId)
                && loot.amountLower == amountLower
                && loot.amountUpper == amountUpper;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(this.itemId);
        hash = 31 * hash + this.amountLower;
        hash = 31 * hash + this.amountUpper;
        return hash;
    }

    @Override
    public Map<String, Object> serialize() {
        Map map = new TreeMap();
        map.put("Probability", getProbability());
        map.put("ItemID", itemId);
        if (amountLower == amountUpper) {
            if (amountLower != 1) {
                map.put("Amount", amountLower);
            }
        } else {
            map.put("AmountLower", amountLower);
            map.put("AmountUpper", amountUpper);
        }
        return map;
    }
}
