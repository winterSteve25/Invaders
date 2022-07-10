package wintersteve25.invaders.contents.invasion.settings;

import fictioncraft.wintersteve25.fclib.api.json.objects.providers.obj.SimpleObjProvider;

public class WavesMilestoneSetting extends SimpleObjProvider {
    private final int difficulty;
    private final int wavesCount;
    private final int minEnemyCount;
    private final int maxEnemyCount;
    
    public WavesMilestoneSetting(String name, int difficulty, int wavesCount, int minEnemyCount, int maxEnemyCount) {
        super(name, false, "WaveMilestone");
        this.difficulty = difficulty;
        this.wavesCount = wavesCount;
        this.minEnemyCount = minEnemyCount;
        this.maxEnemyCount = maxEnemyCount;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getWavesCount() {
        return wavesCount;
    }

    public int getMinEnemyCount() {
        return minEnemyCount;
    }

    public int getMaxEnemyCount() {
        return maxEnemyCount;
    }
}
