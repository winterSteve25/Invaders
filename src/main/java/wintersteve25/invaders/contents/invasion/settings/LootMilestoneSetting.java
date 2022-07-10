package wintersteve25.invaders.contents.invasion.settings;

import fictioncraft.wintersteve25.fclib.api.json.objects.providers.obj.SimpleObjProvider;

import java.util.List;

public class LootMilestoneSetting extends SimpleObjProvider {
    private final int difficulty;
    private final List<LootDrop> loot;
    
    public LootMilestoneSetting(String name, int difficulty, List<LootDrop> loot) {
        super(name, false, "LootMilestone");
        this.difficulty = difficulty;
        this.loot = loot;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public List<LootDrop> getLoot() {
        return loot;
    }
}
