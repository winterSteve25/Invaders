package wintersteve25.invaders.contents.base;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import wintersteve25.invaders.contents.base.functional.IPlacementCondition;
import wintersteve25.invaders.contents.base.functional.IToolTipCondition;
import wintersteve25.invaders.utils.InvadersConstants;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public interface ONIIItem extends ONIIRegistryObject<Item> {
    default Supplier<TextFormatting> getColorName() {
        return null;
    }

    default void setColorName(Supplier<TextFormatting> colorName) {

    }

    default Supplier<List<ITextComponent>> getTooltips() {
        return null;
    }

    default void setTooltips(Supplier<List<ITextComponent>> tooltips) {

    }

    default Supplier<IToolTipCondition> getTooltipCondition() {
        return IToolTipCondition.DEFAULT;
    }

    default void setTooltipCondition(Supplier<IToolTipCondition> condition) {

    }

    default IPlacementCondition getPlacementCondition() {
        return null;
    }

    default void setPlacementCondition(IPlacementCondition placementCondition) {
    }

    default void setDoModelGen(boolean doModelGen) {

    }

    default void setDoLangGen(boolean doLangGen) {

    }

    default ItemCategory getModdedItemCategory() {
        return ItemCategory.GENERAL;
    }

    default void setModdedItemCategory(ItemCategory itemCategory) {
    }

    default void tooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (getTooltips() != null && getTooltips().get() != null && !getTooltips().get().isEmpty()) {
            IToolTipCondition condition = getTooltipCondition().get();
            if (condition == null) {
                tooltip.addAll(getTooltips().get());
            } else {
                if (condition == IToolTipCondition.DEFAULT.get()) {
                    if (condition.canShow(stack, worldIn, tooltip, flagIn)) {
                        tooltip.addAll(getTooltips().get());
                    } else {
                        tooltip.add(InvadersConstants.LangKeys.HOLD_SHIFT);
                    }
                } else {
                    if (condition.canShow(stack, worldIn, tooltip, flagIn)) {
                        tooltip.addAll(getTooltips().get());
                    } else {
                        tooltip.add(condition.textWhenNotShown());
                    }
                }
            }
        }
    }
    enum ItemCategory {
        GENERAL("", null),
        CORE("core", TextFormatting.AQUA);

        private final String pathName;
        private final TextFormatting color;

        ItemCategory(String pathName, TextFormatting color) {
            this.pathName = pathName;
            this.color = color;
        }

        public String getPathName() {
            return pathName;
        }

        public TextFormatting getColor() {
            return color;
        }
    }
}
