package wintersteve25.invaders.contents.invasion.settings;

import fictioncraft.wintersteve25.fclib.api.json.objects.providers.obj.SimpleObjProvider;
import fictioncraft.wintersteve25.fclib.api.json.objects.providers.obj.templates.SimpleEntityProvider;
import fictioncraft.wintersteve25.fclib.api.json.utils.JsonSerializer;
import net.minecraft.entity.EntityType;

import javax.annotation.Nullable;

public class WaveBasedEnemy extends SimpleObjProvider {

    private final SimpleEntityProvider entity;
    private final int startSpawningFromWave;
    private final float spawnWeight;
    
    public WaveBasedEnemy(String name, SimpleEntityProvider entity, int startSpawningFromWave, float spawnWeight) {
        super(name, false, "WaveBasedEnemy");
        this.entity = entity;
        this.startSpawningFromWave = startSpawningFromWave;
        this.spawnWeight = spawnWeight;
    }

    public SimpleEntityProvider getEntity() {
        return entity;
    }

    public int getStartSpawningFromWave() {
        return startSpawningFromWave;
    }

    public float getSpawnWeight() {
        return spawnWeight;
    }

    @Nullable
    public EntityType<?> getEntityType() {
        return JsonSerializer.EntitySerialization.getEntityTypeFromJson(entity);
    }
}
