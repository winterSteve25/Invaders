package wintersteve25.invaders.contents.invasion.settings;

import net.minecraft.item.ItemStack;
import wintersteve25.invaders.settings.Settings;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class InvasionSettings extends Settings {
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
}
