package github.pitbox46.immersive_enchanting.enchantment_tree;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class EnchantmentBuilder {
    private final Map<Integer, EnchantmentLevelInfo> map = new HashMap<>();
    public final ResourceLocation id;

    public EnchantmentBuilder(ResourceLocation id) {
        this.id = id;
    }

    public EnchantmentBuilder putLevelInfo(int level, EnchantmentLevelInfo info) {
        map.put(level, info);
        return this;
    }

    public JsonArray buildToJson() {
        JsonArray returnArray = new JsonArray();
        map.forEach((level, info) -> {
            JsonObject levelEntry = new JsonObject();
            levelEntry.addProperty("level", level);
            levelEntry.addProperty("parent", info.parent.toString());
            levelEntry.addProperty("parent_level", info.parentLevel);
            returnArray.add(levelEntry);
        });
        return returnArray;
    }

    public static class EnchantmentLevelInfo {
        private ResourceLocation parent = null;
        private int parentLevel = 0;

        public EnchantmentLevelInfo setParent(ResourceLocation parent) {
            this.parent = parent;
            return this;
        }

        public EnchantmentLevelInfo setParentLevel(int level) {
            this.parentLevel = level;
            return this;
        }
    }
}
