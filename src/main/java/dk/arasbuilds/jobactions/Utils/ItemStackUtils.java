package dk.arasbuilds.jobactions.Utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.lang.reflect.Type;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

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
    }






    private static String itemStackToJson(ItemStack itemStack) {
        return gson.toJson(itemStack.serialize());
    }

    private static ItemStack jsonToItemStack(String json) {
        return ItemStack.deserialize(gson.fromJson(json, Map.class));
    }
}
