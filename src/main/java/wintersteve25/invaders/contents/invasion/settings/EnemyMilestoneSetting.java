package wintersteve25.invaders.contents.invasion.settings;

import fictioncraft.wintersteve25.fclib.api.json.objects.providers.obj.SimpleObjProvider;

import java.util.List;

public class EnemyMilestoneSetting extends SimpleObjProvider {
    private final int difficulty;
    private final List<WaveBasedEnemy> entities;
    
    public EnemyMilestoneSetting(String name, int difficulty, List<WaveBasedEnemy> entities) {
        super(name, false, "EnemyMilestone");
        this.difficulty = difficulty;
        this.entities = entities;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public List<WaveBasedEnemy> getEntities() {
        return entities;
    }
}
