package com.codisimus.plugins.phatloots.loot;

import com.codisimus.plugins.phatloots.PhatLoot;
import com.codisimus.plugins.phatloots.PhatLoots;
import com.codisimus.plugins.phatloots.PhatLootsCommandSender;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * A CommandLoot is a Command which may be executed from the player or the console
 *
 * @author Cody
 */
@SerializableAs("Message")
public class Message extends Loot {
    private String msg;

    /**
     * Constructs a new Message for the given String
     *
     * @param command The given command
     */
    public Message(String msg) {
        this.msg = ChatColor.translateAlternateColorCodes('&', msg);
    }

    /**
     * Constructs a new Message from a Configuration Serialized phase
     *
     * @param map The map of data values
     */
    public Message(Map<String, Object> map) {
        String currentLine = null; //The value that is about to be loaded (used for debugging)
        try {
            probability = (Double) map.get(currentLine = "Probability");
            msg = (String) map.get(currentLine = "Message");
        } catch (Exception ex) {
            //Print debug messages
            PhatLoots.logger.severe("Failed to load Message line: " + currentLine);
            PhatLoots.logger.severe("of PhatLoot: " + (PhatLoot.current == null ? "unknown" : PhatLoot.current));
            PhatLoots.logger.severe("Last successfull load was...");
            PhatLoots.logger.severe("PhatLoot: " + (PhatLoot.last == null ? "unknown" : PhatLoot.last));
            PhatLoots.logger.severe("Loot: " + (Loot.last == null ? "unknown" : Loot.last.toString()));
        }
    }

    /**
     * Adds this message to the LootBundle
     *
     * @param lootBundle The loot that has been rolled for
     * @param lootingBonus The increased chance of getting rarer loots
     */
    @Override
    public void getLoot(LootBundle lootBundle, double lootingBonus) {
        lootBundle.addMessage(msg);
    }

    /**
     * Returns the information of the CommandLoot in the form of an ItemStack
     *
     * @return An ItemStack representation of the Loot
     */
    @Override
    public ItemStack getInfoStack() {
        //A CommandLoot is represented by a Command Block
        ItemStack infoStack = new ItemStack(Material.MAP);

        //Set the display name to the command
        ItemMeta info = Bukkit.getItemFactory().getItemMeta(infoStack.getType());
        info.setDisplayName("§2Message");

        //Construct the ItemStack and return it
        infoStack.setItemMeta(info);
        return infoStack;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(msg);
        sb.append(" @ ");
        //Only display the decimal values if the probability is not a whole number
        sb.append(String.valueOf(Math.floor(probability) == probability ? (int) probability : probability));
        sb.append("%");
        return sb.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Message) {
            Message loot = (Message) object;
            return loot.msg.equals(msg);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.msg != null ? this.msg.hashCode() : 0);
        return hash;
    }

    @Override
    public Map<String, Object> serialize() {
        Map map = new TreeMap();
        map.put("Probability", probability);
        map.put("Message", msg);
        return map;
    }
}