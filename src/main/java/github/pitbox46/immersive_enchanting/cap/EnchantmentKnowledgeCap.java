package github.pitbox46.immersive_enchanting.cap;

import github.pitbox46.immersive_enchanting.ImmersiveEnchanting;
import github.pitbox46.immersive_enchanting.Util;
import net.minecraft.core.Direction;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public interface EnchantmentKnowledgeCap extends INBTSerializable<ListTag> {
    ResourceLocation RL = new ResourceLocation(ImmersiveEnchanting.ID, "enchantment_knowledge");

    static ICapabilityProvider createProvider() {
        return new CapProvider();
    }

    /**
     * Queries to see if the player knows the enchantment
     */
    boolean knows(Enchantment enchantment, int level);

    /**
     * Add an enchantment to a player's known enchantments
     */
    void learn(Enchantment enchantment, int level);

    /**
     * Deletes the enchantment.
     * @return True if the enchantment was present and was deleted. False otherwise.
     */
    boolean forget(Enchantment enchantment, int level);

    /**
     * Returns a copy of all known enchantments
     */
    Util.SetMap<Enchantment, Integer> getAllKnown();

    class Impl implements EnchantmentKnowledgeCap {
        Util.SetMap<Enchantment, Integer> knownEnchantments = new Util.SetMap<>();

        @Override
        public boolean knows(Enchantment enchantment, int level) {
            return knownEnchantments.contains(enchantment, level);
        }

        @Override
        public void learn(Enchantment enchantment, int level) {
            knownEnchantments.putInSet(enchantment, level);
        }

        @Override
        public boolean forget(Enchantment enchantment, int level) {
            return knownEnchantments.removeFromSet(enchantment, level);
        }

        @Override
        public Util.SetMap<Enchantment, Integer> getAllKnown() {
            return knownEnchantments.createCopy();
        }

        @Override
        public ListTag serializeNBT() {
            ListTag list = new ListTag();
            knownEnchantments.forEach((enchantment, integers) -> {
                CompoundTag entry = new CompoundTag();
                entry.putString("id", String.valueOf(ForgeRegistries.ENCHANTMENTS.getKey(enchantment)));
                entry.putIntArray("levels", new ArrayList<>(integers));
                list.add(entry);
            });
            return list;
        }

        @Override
        public void deserializeNBT(ListTag list) {
            for(Tag tag : list) {
                if(tag instanceof CompoundTag compoundTag) {
                    Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(ResourceLocation.tryParse(compoundTag.getString("id")));
                    Set<Integer> set = knownEnchantments.computeIfAbsent(enchantment, k -> new HashSet<>());
                    set.addAll(Arrays.stream(compoundTag.getIntArray("levels")).collect(HashSet::new, HashSet::add, AbstractCollection::addAll));
                }
            }
        }
    }

    class CapProvider implements ICapabilityProvider, INBTSerializable<ListTag> {
        private final Lazy<Capability<EnchantmentKnowledgeCap>> lazy;
        private final LazyOptional<EnchantmentKnowledgeCap> lazyOptional;

        CapProvider() {
            lazy = Lazy.of(() -> Events.ENCHANTMENT_KNOWLEDGE);
            lazyOptional = LazyOptional.of(Impl::new);
        }

        @NotNull
        @Override
        public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return lazy.get().orEmpty(cap, lazyOptional);
        }

        public ListTag serializeNBT() {
            return lazyOptional.map(INBTSerializable::serializeNBT).orElse(new ListTag());
        }

        @Override
        public void deserializeNBT(ListTag nbt) {
            lazyOptional.ifPresent(c -> c.deserializeNBT(nbt));
        }
    }
}
