package wintersteve25.invaders.contents.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import wintersteve25.invaders.contents.blocks.core.hub.HubBE;

import java.util.ArrayList;

public class HubEntity extends LivingEntity {
    private BlockPos blockEntity;
    
    public HubEntity(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }
    
    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return new ArrayList<>();
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlotType pSlot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlotType pSlot, ItemStack pStack) {
    }

    @Override
    public HandSide getMainArm() {
        return HandSide.LEFT;
    }

    @Override
    public void baseTick() {
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void move(MoverType pType, Vector3d pPos) {
    }

    @Override
    public boolean canCollideWith(Entity pEntity) {
        return !(pEntity instanceof PlayerEntity);
    }

    @Override
    public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
        super.hurt(p_70097_1_, p_70097_2_);
        if (level.isClientSide()) return false;
        TileEntity te = level.getBlockEntity(blockEntity);
        if (!(te instanceof HubBE)) return false;
        HubBE hub = (HubBE) te;
        hub.addHealth(-p_70097_2_);
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
        super.addAdditionalSaveData(p_213281_1_);
        p_213281_1_.put("hubBlockEntity", NBTUtil.writeBlockPos(blockEntity));
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
        super.readAdditionalSaveData(p_70037_1_);
        blockEntity = NBTUtil.readBlockPos((CompoundNBT) p_70037_1_.get("hubBlockEntity"));
    }

    public BlockPos getBlockEntity() {
        return blockEntity;
    }

    public void setBlockEntity(BlockPos blockEntity) {
        this.blockEntity = blockEntity;
    }

    public static AttributeModifierMap.MutableAttribute createAttribute() {
        return LivingEntity.createLivingAttributes();
    }
}
