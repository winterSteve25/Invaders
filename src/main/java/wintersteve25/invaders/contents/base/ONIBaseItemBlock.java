package wintersteve25.invaders.contents.base;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import wintersteve25.invaders.contents.base.functional.IPlacementCondition;
import wintersteve25.invaders.contents.base.functional.IToolTipCondition;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class ONIBaseItemBlock extends BlockItem implements ONIIItem {

    private final String regName;

    // item builder properties
    private boolean doModelGen = true;
    private boolean doLangGen = true;
    private Supplier<TextFormatting> colorName;
    private Supplier<List<ITextComponent>> tooltips;
    private Supplier<IToolTipCondition> tooltipCondition = IToolTipCondition.DEFAULT;
    private IPlacementCondition placementCondition;
    private ItemCategory itemCategory = ItemCategory.GENERAL;

    public ONIBaseItemBlock(ONIBaseBlock blockIn, Properties builder) {
        super(blockIn, builder);
        this.regName = blockIn.getRegName();
    }

    @Override
    public boolean doModelGen() {
        return doModelGen;
    }

    @Override
    public boolean doStateGen() {
        return false;
    }

    @Override
    public boolean doLangGen() {
        return doLangGen;
    }

    @Override
    public boolean doLootTableGen() {
        return false;
    }

    @Override
    public Item get() {
        return this;
    }

    @Override
    public String getRegName() {
        return regName;
    }

    @Override
    public ITextComponent getName(ItemStack stack) {
        if (getColorName() != null && getColorName().get() != null) {
            return super.getName(stack).plainCopy().withStyle(getColorName().get());
        }
        return super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip(stack, worldIn, tooltip, flagIn);
    }

    @Override
    protected boolean canPlace(BlockItemUseContext context, BlockState state) {
        if (placementCondition != null) {
            return super.canPlace(context, state) && placementCondition.test(context, state);
        }
        return super.canPlace(context, state);
    }

    @Override
    public Supplier<TextFormatting> getColorName() {
        if (colorName != null) {
            return colorName;
        }

        return ()->itemCategory.getColor();
    }

    @Override
    public void setColorName(Supplier<TextFormatting> colorName) {
        this.colorName = colorName;
    }

    @Override
    public Supplier<List<ITextComponent>> getTooltips() {
        return tooltips;
    }

    @Override
    public void setTooltips(Supplier<List<ITextComponent>> tooltips) {
        this.tooltips = tooltips;
    }

    @Override
    public Supplier<IToolTipCondition> getTooltipCondition() {
        return tooltipCondition;
    }

    @Override
    public void setTooltipCondition(Supplier<IToolTipCondition> condition) {
        this.tooltipCondition = condition;
    }

    @Override
    public IPlacementCondition getPlacementCondition() {
        return placementCondition;
    }

    @Override
    public void setPlacementCondition(IPlacementCondition placementCondition) {
        this.placementCondition = placementCondition;
    }

    @Override
    public void setDoModelGen(boolean doModelGen) {
        this.doModelGen = doModelGen;
    }

    @Override
    public void setDoLangGen(boolean doLangGen) {
        this.doLangGen = doLangGen;
    }

    @Override
    public ItemCategory getModdedItemCategory() {
        return itemCategory;
    }

    @Override
    public void setModdedItemCategory(ItemCategory itemCategory) {
        this.itemCategory = itemCategory;
    }
}