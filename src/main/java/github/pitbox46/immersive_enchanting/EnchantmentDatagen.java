package github.pitbox46.immersive_enchanting;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import github.pitbox46.immersive_enchanting.enchantment_tree.EnchantmentBuilder;
import github.pitbox46.immersive_enchanting.enchantment_tree.EnchantmentBuilder.EnchantmentLevelInfo;
import github.pitbox46.immersive_enchanting.enchantment_tree.EnchantmentNode;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class EnchantmentDatagen implements DataProvider {
    public static void onGatherDataEvent(GatherDataEvent event) {
        if(event.includeServer())
            event.getGenerator().addProvider(new EnchantmentDatagen(event.getGenerator()));
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger LOGGER = LogUtils.getLogger();
    protected final DataGenerator generator;

    public EnchantmentDatagen(DataGenerator generator) {
        this.generator = generator;
    }

    @Override
    public void run(HashCache cache) {
        Path folderPath = generator.getOutputFolder();

        Set<EnchantmentBuilder> set = getEnchantments();

        set.forEach(enchantment -> {
            Path filePath = createPath(folderPath, enchantment.id);
            try {
                DataProvider.save(GSON, cache, enchantment.buildToJson(), filePath);
            } catch (IOException ioexception) {
                LOGGER.error("Couldn't save enchantment {}", filePath, ioexception);
            }
        });

    }

    private static Set<EnchantmentBuilder> getEnchantments() {
        Set<EnchantmentBuilder> enchantments = new HashSet<>();
        enchantments.add(new EnchantmentBuilder(new ResourceLocation("protection"))
                .putLevelInfo(1, new EnchantmentLevelInfo().setParent(EnchantmentNode.ROOT))
                .putLevelInfo(2, new EnchantmentLevelInfo().setParent(new ResourceLocation("protection")).setParentLevel(1))
                .putLevelInfo(3, new EnchantmentLevelInfo().setParent(new ResourceLocation("protection")).setParentLevel(2))
                .putLevelInfo(4, new EnchantmentLevelInfo().setParent(new ResourceLocation("protection")).setParentLevel(2))

        );
        return enchantments;
    }

    @Override
    public String getName() {
        return "Enchantment Tree";
    }

    private static Path createPath(Path path, ResourceLocation rl) {
        return path.resolve("data/" + rl.getNamespace() + "/enchantment_tree/" + rl.getPath() + ".json");
    }
}
