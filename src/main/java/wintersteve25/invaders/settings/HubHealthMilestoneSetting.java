package wintersteve25.invaders.settings;

import fictioncraft.wintersteve25.fclib.api.json.objects.providers.obj.SimpleObjProvider;

public class HubHealthMilestoneSetting extends SimpleObjProvider {
    
    private final int difficulty;
    private final float maxHealthScaleFactor;
    
    public HubHealthMilestoneSetting(String name, int difficulty, float maxHealthScaleFactor) {
        super(name, false, "HubHealthMilestone");
        this.difficulty = difficulty;
        this.maxHealthScaleFactor = maxHealthScaleFactor;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public float getMaxHealthScaleFactor() {
        return maxHealthScaleFactor;
    }
}
