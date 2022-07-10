package wintersteve25.invaders.contents.invasion.settings;

import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class InvasionSettings {
    public static InvasionSettings JsonSettings;

    static {
        JsonSettings = new InvasionSettings();
    }

    public final Map<Integer, List<WaveBasedEnemy>> enemyTypeMilestones;
    public final Map<Integer, List<LootDrop>> lootTypeMilestones;
    public final Map<Integer, WavesMilestoneSetting> wavesMilestones;

    public InvasionSettings() {
        this(new HashMap<>(), new HashMap<>(), new HashMap<>());
    }

    public InvasionSettings(Map<Integer, List<WaveBasedEnemy>> enemyTypeMilestones, Map<Integer, List<LootDrop>> lootTypeMilestones, Map<Integer, WavesMilestoneSetting> wavesMilestone) {
         this.enemyTypeMilestones = enemyTypeMilestones;
         this.lootTypeMilestones = lootTypeMilestones;
         this.wavesMilestones = wavesMilestone;
    }
    
    public List<WaveBasedEnemy> getEnemies(int wave, int difficulty) {
        List<WaveBasedEnemy> enemies = getValue(enemyTypeMilestones, difficulty);
        if (enemies == null) return new ArrayList<>();
        return enemies.stream().filter(waveBasedEnemy -> wave >= waveBasedEnemy.getStartSpawningFromWave()).collect(Collectors.toList());
    }
    
    public List<ItemStack> getLoot(int difficulty) {
        List<LootDrop> drops = getValue(lootTypeMilestones, difficulty);
        if (drops == null) return new ArrayList<>();
        return drops.stream().map(LootDrop::getStack).collect(Collectors.toList());
    }
    
    public WavesMilestoneSetting getWaveCount(int difficulty) {
        return getValue(wavesMilestones, difficulty);
    }
    
    @Nullable
    private <T> T getValue(Map<Integer, T> map, int milestone) {
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
