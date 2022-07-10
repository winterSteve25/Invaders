package wintersteve25.invaders.utils.helpers;

import net.minecraft.nbt.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class NBTHelper {
    public static <K, V> CompoundNBT serializeMap(Map<K, V> map, Function<K, INBT> keySerializer, Function<V, INBT> valueSerializer) {
        CompoundNBT tag = new CompoundNBT();

        ListNBT keys = new ListNBT();
        ListNBT values = new ListNBT();

        for (K key : map.keySet()) {
            keys.add(keySerializer.apply(key));
        }

        for (V value : map.values()) {
            values.add(valueSerializer.apply(value));
        }

        tag.put("keys", keys);
        tag.put("values", values);

        return tag;
    }
    
    public static <K, V> Map<K, V> deserializeMap(CompoundNBT nbt, Function<INBT, K> keyDeserializer, Function<INBT, V> valueDeserializer) {
        Map<K, V> map = new HashMap<>();
        
        INBT keysNBT = nbt.get("keys");
        INBT valuesNBT = nbt.get("values");

        if (keysNBT instanceof ListNBT && valuesNBT instanceof ListNBT) {
            ListNBT keys = (ListNBT) keysNBT;
            ListNBT values = (ListNBT) valuesNBT;

            for (int i = 0; i < keys.size(); i++) {
                map.put(keyDeserializer.apply(keys.get(i)), valueDeserializer.apply(values.get(i)));
            }
        }
        
        return map;
    }
}
