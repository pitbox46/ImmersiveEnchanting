package github.pitbox46.immersive_enchanting.enchantment_tree;

import com.google.gson.*;
import com.mojang.logging.LogUtils;
import github.pitbox46.immersive_enchanting.enchantment_tree.EnchantmentNode.ProtoNode;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import java.util.*;

public class EnchantmentTreeManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    protected static final Logger LOGGER = LogUtils.getLogger();

    public final EnchantmentNode rootNode = new EnchantmentNode(EnchantmentNode.ROOT, null, 0, null);

    public EnchantmentTreeManager() {
        super(GSON, "enchantment_tree");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        EnchantmentNode.SetMap<Pair<ResourceLocation, Integer>, ProtoNode> protoNodeMap = new EnchantmentNode.SetMap<>();
        map.forEach((rl, jsonElement) -> {
            try {
                JsonArray jsonArray = GsonHelper.convertToJsonArray(jsonElement, "enchantment");
                ProtoNode.fromJSON(rl, jsonArray, protoNodeMap);
            } catch (Exception exception) {
                LOGGER.error("Parsing error loading enchantment {}: {}", rl, exception.getMessage());
            }
        });

        buildTree(protoNodeMap);
    }

    /**
     * Builds the enchantment tree. The tree is accessible through the root node
     * @param protoNodeMap
     */
    protected void buildTree(EnchantmentNode.SetMap<Pair<ResourceLocation, Integer>, ProtoNode> protoNodeMap) {
        Deque<EnchantmentNode> pendingBranches = new ArrayDeque<>();
        pendingBranches.add(rootNode);

        while(!pendingBranches.isEmpty()) {
            EnchantmentNode currentNode = pendingBranches.pop();
            protoNodeMap.getOrDefault(Pair.of(currentNode.getId(), currentNode.getLevel()), Set.of()).forEach(node -> pendingBranches.add(node.createAndAttachNode(currentNode)));
        }
    }
}
