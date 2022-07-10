package wintersteve25.invaders.contents.invasion.settings;

import fictioncraft.wintersteve25.fclib.api.json.objects.providers.obj.SimpleObjProvider;
import fictioncraft.wintersteve25.fclib.api.json.objects.providers.obj.templates.SimpleItemProvider;
import fictioncraft.wintersteve25.fclib.api.json.utils.JsonSerializer;
import fictioncraft.wintersteve25.fclib.common.helper.MiscHelper;
import net.minecraft.item.ItemStack;

import java.util.Random;

public class LootDrop extends SimpleObjProvider {
    
    private final SimpleItemProvider item;
    private final float weight;
    private final int minDrop;
    private final int maxDrop;
    
    public LootDrop(String name, SimpleItemProvider item, float weight, int minDrop, int maxDrop) {
        super(name, false, "Loot");
        this.item = item;
        this.weight = weight;
        this.minDrop = minDrop;
        this.maxDrop = maxDrop;
    }

    public SimpleItemProvider getItem() {
        return item;
    }

    public float getWeight() {
        return weight;
    }

    public int getMinDrop() {
        return minDrop;
    }

    public int getMaxDrop() {
        return maxDrop;
    }

    public ItemStack getStack() {
        if (!chance(getWeight())) {
            return ItemStack.EMPTY;
        }
        
        int rand = MiscHelper.randomInRange(getMinDrop(), getMaxDrop());
        ItemStack stack = JsonSerializer.ItemStackSerializer.getItemStackFromJsonItemStack(getItem());
        stack.setCount(rand);        
        
        return stack;
    }
    
    public static boolean chance(float chance) {
        Random rand = new Random();
        double randN = rand.nextFloat();
        return randN < chance;
    }
}
