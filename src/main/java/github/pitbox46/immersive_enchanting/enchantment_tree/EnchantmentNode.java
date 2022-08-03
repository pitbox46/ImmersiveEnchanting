package github.pitbox46.immersive_enchanting.enchantment_tree;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import github.pitbox46.immersive_enchanting.ImmersiveEnchanting;
import github.pitbox46.immersive_enchanting.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class EnchantmentNode {
    public static final ResourceLocation ROOT = new ResourceLocation(ImmersiveEnchanting.ID, "root");

    private final ResourceLocation id;
    private final Enchantment enchantment;
    private final int level;
    private EnchantmentNode parent;
    private Set<EnchantmentNode> children;


    public EnchantmentNode(ResourceLocation id, Enchantment enchantment, int level, EnchantmentNode parent) {
        this.id = id;
        this.enchantment = enchantment;
        this.level = level;
        this.children = new LinkedHashSet<>();
        this.parent = parent;
    }

    //Getters and setters section
    public ResourceLocation getId() {
        return id;
    }

    public Enchantment getEnchantment() {
        return enchantment;
    }

    public int getLevel() {
        return level;
    }

    public EnchantmentNode getParent() {
        return parent;
    }

    public void setParent(EnchantmentNode parent) {
        this.parent = parent;
    }

    public Set<EnchantmentNode> getChildren() {
        return children;
    }

    public void setChildren(Set<EnchantmentNode> children) {
        this.children = children;
    }

    public boolean addChild(EnchantmentNode node) {
        return this.children.add(node);
    }
    //End getters and setters section

    public ProtoNode toProto() {
        return new ProtoNode(id, enchantment, level, parent.id, parent.level);
    }

    public record ProtoNode(ResourceLocation id, Enchantment enchantment, int level, ResourceLocation parent, int parentLevel) {
        public static final ResourceLocation NULL = new ResourceLocation(ImmersiveEnchanting.ID, "null");

        public EnchantmentNode createAndAttachNode(EnchantmentNode parent) {
            EnchantmentNode node = new EnchantmentNode(id, enchantment, level, parent);
            parent.addChild(node);
            return node;
        }

        public boolean isNull() {
            return this.parent().equals(NULL);
        }

        public boolean isParentRoot() {
            return this.parent.equals(new ResourceLocation(ImmersiveEnchanting.ID, "root"));
        }

        public static ProtoNode createNullNode(ResourceLocation rl) {
            return new ProtoNode(rl, null, 0, NULL, 0);
        }

        /**
         *
         * @param rl Resource location
         * @param json The JSON object
         * @param protoNodeMap The map to be written to
         */
        public static void fromJSON(ResourceLocation rl, JsonArray json, Util.SetMap<Pair<ResourceLocation, Integer>, ProtoNode> protoNodeMap) {
            Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(rl);
            if(enchantment == null) {
                EnchantmentTreeManager.LOGGER.warn("Enchantment not found: {}", rl);
                protoNodeMap.putInSet(Pair.of(NULL, 0), createNullNode(rl));
            }
            else {
                for (JsonElement element : json) {
                    if(!element.isJsonObject()) {
                        EnchantmentTreeManager.LOGGER.warn("Enchantment doesn't have the correct format: {}", rl);
                        protoNodeMap.putInSet(Pair.of(NULL, 0), createNullNode(rl));
                        break;
                    }
                    try {
                        JsonObject jsonObject = element.getAsJsonObject();
                        int level = jsonObject.getAsJsonPrimitive("level").getAsInt();
                        ResourceLocation parent = ResourceLocation.tryParse(jsonObject.getAsJsonPrimitive("parent").getAsString());
                        int parentLevel = jsonObject.getAsJsonPrimitive("parent_level").getAsInt();

                        protoNodeMap.putInSet(Pair.of(parent, parentLevel), new ProtoNode(rl, enchantment, level, parent, parentLevel));
                    }
                    catch (IllegalStateException e) {
                        EnchantmentTreeManager.LOGGER.warn("Enchantment doesn't have the correct format: {}", rl);
                        protoNodeMap.putInSet(Pair.of(NULL, 0), new ProtoNode(rl, enchantment, 0, NULL, 0));
                    }
                }
            }
        }
    }

}
