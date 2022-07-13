package wintersteve25.invaders.settings;

import java.util.HashMap;
import java.util.Map;

public class InvadersSettings extends Settings {
    public static InvadersSettings JsonSettings;
    
    static {
        JsonSettings = new InvadersSettings();
    }
    
    public final Map<Integer, Float> hubHealthMilestone;

    public InvadersSettings() {
        this(new HashMap<>());
    }
    
    public InvadersSettings(Map<Integer, Float> hubHealthMilestone) {
        this.hubHealthMilestone = hubHealthMilestone;
    }

    public float getScaleFactor(int difficulty) {
        Float drops = getValue(hubHealthMilestone, difficulty);
        if (drops == null) return 1f;
        return drops;
    }
}