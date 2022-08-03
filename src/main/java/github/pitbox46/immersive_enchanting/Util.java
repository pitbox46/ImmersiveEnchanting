package github.pitbox46.immersive_enchanting;

import org.spongepowered.asm.mixin.Overwrite;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Util {
    /**
     * A map that has multiple values for each key. For convenience.
     */
    public static class SetMap<K, V> extends HashMap<K, Set<V>> {
        /**
         * Automatically creates an entry in the map for the key if one does not exist. Then it appends the new value.
         * @param key
         * @param value
         * @return True if {@link Set#add(V value)} succeeded
         */
        public boolean putInSet(K key, V value) {
            return this.computeIfAbsent(key, a -> new HashSet<>()).add(value);
        }

        /**
         * Removes a
         * @param key
         * @param value
         * @return
         */
        public boolean removeFromSet(K key, V value) {
            return this.contains(key, value) && this.get(key).remove(value);
        }

        /**
         * Queries the map to see if the key contains the value.
         * @param key The key
         * @param value An object inside the associated set
         * @return True if the key exists and contains the value. False otherwise.
         */
        public boolean contains(K key, V value) {
            return this.containsKey(key) && this.get(key).contains(value);
        }

        public SetMap<K, V> createCopy() {
            return net.minecraft.Util.make(new SetMap<>(), map -> map.putAll(this));
        }
    }
}
