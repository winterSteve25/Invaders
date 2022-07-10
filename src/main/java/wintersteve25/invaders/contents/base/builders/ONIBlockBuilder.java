package wintersteve25.invaders.contents.base.builders;

import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import wintersteve25.invaders.Invaders;
import wintersteve25.invaders.contents.base.*;
import wintersteve25.invaders.contents.base.functional.*;

import javax.annotation.Nonnull;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

public class ONIBlockBuilder<T extends ONIBaseBlock> {
    @Nonnull
    private final T block;
    @Nonnull
    private final ONIItemBuilder<ONIBaseItemBlock> blockItem;

    public ONIBlockBuilder(Supplier<T> block) {
        this(block, new Item.Properties().tab(Invaders.ITEM_GROUP), false);
    }

    public ONIBlockBuilder(Supplier<T> block, Item.Properties properties, boolean isAnimated) {
        this.block = block.get();
        this.blockItem = new ONIItemBuilder<>(() -> new ONIBaseItemBlock(this.block, properties));
    }

    public ONIBlockBuilder(Supplier<T> block, Supplier<Callable<ItemStackTileEntityRenderer>> ister, boolean isAnimated) {
        this(block, new Item.Properties().tab(Invaders.ITEM_GROUP).setISTER(ister), isAnimated);
    }

    public ONIBlockBuilder(Supplier<T> block, Item.Properties properties, Supplier<Callable<ItemStackTileEntityRenderer>> ister, boolean isAnimated) {
        this(block, properties.setISTER(ister), isAnimated);
    }

    public ONIBlockBuilder<T> placementCondition(IPlacementCondition condition) {
        this.blockItem.placementCondition(condition);
        return this;
    }

    public ONIBlockBuilder<T> shiftToolTip() {
        this.blockItem.shiftToolTip();
        return this;
    }

    public ONIBlockBuilder<T> tooltipCondition(Supplier<IToolTipCondition> condition) {
        this.blockItem.tooltipCondition(condition);
        return this;
    }

    public ONIBlockBuilder<T> tooltip(ITextComponent... tooltips) {
        this.blockItem.tooltip(tooltips);
        return this;
    }

    public ONIBlockBuilder<T> coloredName(Supplier<TextFormatting> color) {
        this.blockItem.coloredName(color);
        return this;
    }

    public ONIBlockBuilder<T> noModelGen() {
        this.block.setDoModelGen(false);
        this.blockItem.noModelGen();
        return this;
    }

    public ONIBlockBuilder<T> doStateGen() {
        this.block.setDoStateGen(true);
        return this;
    }

    public ONIBlockBuilder<T> noLangGen() {
        this.block.setDoLangGen(false);
        this.blockItem.noLangGen();
        return this;
    }

    public ONIBlockBuilder<T> noLootTableGen() {
        this.block.setDoLootTableGen(false);
        return this;
    }

    public ONIBlockBuilder<T> breakCondition(IBreakCondition condition) {
        this.block.setBreakCondition(condition);
        return this;
    }

    public ONIBlockBuilder<T> lightLevel(ILightValue lightLevel) {
        this.block.setLightLevel(lightLevel);
        return this;
    }

    public ONIBlockBuilder<T> setCategory(ONIIItem.ItemCategory category) {
        this.blockItem.setCategory(category);
        return this;
    }

    public ONIBlockBuilder<T> shape(IVoxelShapeProvider voxelShape) {
        this.block.setHitBox(voxelShape);
        return this;
    }

    public ONIBlockBuilder<T> allowVerticalPlacement() {
        isDirectional();
        ((ONIBaseDirectional) this.block).setAllowVertical(true);
        return this;
    }

    public ONIBlockBuilder<T> autoRotateShape() {
        isDirectional();
        ((ONIBaseDirectional) this.block).setAutoRotateShape(true);
        return this;
    }

    public ONIBlockBuilder<T> renderType(IRenderTypeProvider renderType) {
        this.block.setRenderType(renderType);
        return this;
    }

    public ONIBlockBuilder<T> tileEntity(ITETypeProvider teT, Class<? extends TileEntity> teClass) {
        this.block.setTileEntityType(teT);
        this.block.setTeClass(teClass);
        return this;
    }

    public ONIBlockBuilder<T> container(IGui gui) {
        isMachine();
        ((ONIBaseMachine) this.block).setGui(gui);
        return this;
    }

    public Tuple<T, ONIBaseItemBlock> build() {
        return new Tuple<>(this.block, this.blockItem.build());
    }

    private void isMachine() {
        if (!(this.block instanceof ONIBaseMachine))
            throw new IllegalStateException("Tried to create machine-only properties with a non-machine block");
    }

    private void isDirectional() {
        if (!(this.block instanceof ONIBaseDirectional))
            throw new IllegalStateException("Tried to create directional-only properties with a non-directional block");
    }
}