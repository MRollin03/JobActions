package dk.arasbuilds.jobactions.Utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.lang.reflect.Type;
import java.util.List;
import java.util.ArrayList;
import java.util.*;

public class ItemStackUtils {

    private static final Gson gson = new Gson();

    public static String serializeItemStacks(List<ItemStack> itemStacks) {
        List<String> itemStrings = new ArrayList<>();
        for (ItemStack itemStack : itemStacks) {
            itemStrings.add(itemStackToJson(itemStack));
        }
        return gson.toJson(itemStrings);
    }

    public static List<ItemStack> deserializeItemStacks(String json) {
        Type listType = new TypeToken<List<String>>() {}.getType();
        List<String> itemStrings = gson.fromJson(json, listType);
        List<ItemStack> itemStacks = new ArrayList<>();
        for (String itemString : itemStrings) {
            itemStacks.add(jsonToItemStack(itemString));
        }
        return itemStacks;
    }

    public static String serializeItemStack(ItemStack itemStack) {
        List<String> itemStrings = new ArrayList<>();
        itemStrings.add(itemStackToJson(itemStack));
        return gson.toJson(itemStrings);
    }



    public static ItemStack deserializeItemStack(String jsonString) {
        JsonElement jsonElement = JsonParser.parseString(jsonString);
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            // Your existing deserialization code
        } else {
            throw new IllegalStateException("Expected a JSON object");
        }
        return gson.fromJson(jsonString, ItemStack.class);
    }

    public static List<ItemStack> compressItemStacks(List<ItemStack> items) {
        Map<Material, Integer> materialCountMap = new HashMap<>();

        // Step 1: Count the total amount for each Material
        for (ItemStack item : items) {
            if (item == null || item.getAmount() <= 0) {
                continue; // Skip null or invalid stacks
            }

            Material material = item.getType();
            int amount = item.getAmount();

            // Add to the total count for this material
            materialCountMap.put(material, materialCountMap.getOrDefault(material, 0) + amount);
        }

        // Step 2: Compress the stacks into maximum stack size groups
        List<ItemStack> compressedStacks = new ArrayList<>();
        for (Map.Entry<Material, Integer> entry : materialCountMap.entrySet()) {
            Material material = entry.getKey();
            int totalAmount = entry.getValue();
            int maxStackSize = material.getMaxStackSize();

            // Create full stacks as much as possible
            while (totalAmount > maxStackSize) {
                compressedStacks.add(new ItemStack(material, maxStackSize));
                totalAmount -= maxStackSize;
            }

            // Add the remaining stack if there's any leftover
            if (totalAmount > 0) {
                compressedStacks.add(new ItemStack(material, totalAmount));
            }
        }

        return compressedStacks;
    }







    private static String itemStackToJson(ItemStack itemStack) {
        return gson.toJson(itemStack.serialize());
    }

    private static ItemStack jsonToItemStack(String json) {
        return ItemStack.deserialize(gson.fromJson(json, Map.class));
    }
}
