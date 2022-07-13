package wintersteve25.invaders.settings;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Settings {
    @Nullable
    protected <T> T getValue(Map<Integer, T> map, int milestone) {
        if (map.isEmpty()) return null;

        if (map.containsKey(milestone)) {
            return map.get(milestone);
        }

        int mapSize = map.size();
        List<T> values = new ArrayList<>(map.values());

        if (mapSize == 1) {
            return values.get(0);
        }

        List<Integer> keys = new ArrayList<>(map.keySet());

        for (int i = 0; i < mapSize; i++) {
            if (i == mapSize - 1) {
                return values.get(0);
            }

            if (milestone >= keys.get(i) && milestone < keys.get(i + 1)) {
                return values.get(i);
            }
        }

        return values.get(0);
    }
}
