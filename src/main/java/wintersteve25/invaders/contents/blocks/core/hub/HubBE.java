package wintersteve25.invaders.contents.blocks.core.hub;

import dev.ftb.mods.ftbchunks.data.ClaimedChunk;
import dev.ftb.mods.ftbchunks.data.ClaimedChunkManager;
import dev.ftb.mods.ftbchunks.data.FTBChunksAPI;
import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import dev.ftb.mods.ftbteams.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.data.Team;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.FakePlayer;
import wintersteve25.invaders.Invaders;
import wintersteve25.invaders.contents.base.ONIBaseInvTE;
import wintersteve25.invaders.contents.base.ONIBaseMachine;
import wintersteve25.invaders.contents.base.ONIIItem;
import wintersteve25.invaders.contents.base.bounding.ONIBoundingBlock;
import wintersteve25.invaders.contents.base.bounding.ONIIBoundingBlock;
import wintersteve25.invaders.contents.base.builders.ONIBlockBuilder;
import wintersteve25.invaders.contents.base.builders.ONIContainerBuilder;
import wintersteve25.invaders.contents.base.functional.IGui;
import wintersteve25.invaders.contents.entities.HubEntity;
import wintersteve25.invaders.data.worlddata.InvadersWorldData;
import wintersteve25.invaders.data.worlddata.InvasionWorldData;
import wintersteve25.invaders.init.InvadersBlocks;
import wintersteve25.invaders.init.InvadersCapabilities;
import wintersteve25.invaders.init.InvadersConfigs;
import wintersteve25.invaders.init.InvadersEntities;
import wintersteve25.invaders.utils.InvadersConstants;
import wintersteve25.invaders.utils.helpers.TranslationHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HubBE extends ONIBaseInvTE implements ONIIBoundingBlock {
    private static final VoxelShape SHAPE = VoxelShapes.join(VoxelShapes.join(Block.box(1, 0, 2, 15, 16, 15), VoxelShapes.join(Block.box(1, 0, 1, 3, 16, 2), VoxelShapes.join(Block.box(7, 1, 1, 9, 15, 2), VoxelShapes.join(Block.box(3, 0, 1, 13, 1, 2), VoxelShapes.join(Block.box(3, 15, 1, 13, 16, 2), VoxelShapes.join(Block.box(9, 12, 1, 13, 13, 2), VoxelShapes.join(Block.box(3, 12, 1, 7, 13, 2), VoxelShapes.join(Block.box(9, 9, 1, 13, 10, 2), VoxelShapes.join(Block.box(3, 9, 1, 7, 10, 2), VoxelShapes.join(Block.box(9, 6, 1, 13, 7, 2), VoxelShapes.join(Block.box(3, 6, 1, 7, 7, 2), VoxelShapes.join(Block.box(3, 3, 1, 7, 4, 2), VoxelShapes.join(Block.box(9, 3, 1, 13, 4, 2), Block.box(13, 0, 1, 15, 16, 2), IBooleanFunction.OR), IBooleanFunction.OR), IBooleanFunction.OR), IBooleanFunction.OR), IBooleanFunction.OR), IBooleanFunction.OR), IBooleanFunction.OR), IBooleanFunction.OR), IBooleanFunction.OR), IBooleanFunction.OR), IBooleanFunction.OR), IBooleanFunction.OR), IBooleanFunction.OR), VoxelShapes.join(Block.box(6, 16, 6, 10, 21, 9), VoxelShapes.join(Block.box(6, 21, 7, 10, 22, 9), Block.box(6, 22, 8, 10, 23, 9), IBooleanFunction.OR), IBooleanFunction.OR), IBooleanFunction.OR);

    private UUID owner;
    private UUID entity;
    private ChunkPos northOceanChunk;
    private ChunkPos westOceanChunk;
    private ChunkPos eastOceanChunk;
    private ChunkPos southOceanChunk;
    
    private float health;
    private float maxHealth;

    public HubBE() {
        super(InvadersBlocks.Core.HUB_BE.get());
    }

    @Override
    public int getInvSize() {
        return 9;
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        tag.putUUID("owner_uuid", owner);
        tag.putUUID("associated_entity", entity);
        tag.putLong("northOceanChunk", northOceanChunk.toLong());
        tag.putLong("westOceanChunk", westOceanChunk.toLong());
        tag.putLong("eastOceanChunk", eastOceanChunk.toLong());
        tag.putLong("southOceanChunk", southOceanChunk.toLong());
        tag.putFloat("health", health);
        tag.putFloat("maxHealth", maxHealth);
        return super.save(tag);
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        owner = tag.getUUID("owner_uuid");
        entity = tag.getUUID("associated_entity");
        northOceanChunk = new ChunkPos(tag.getLong("northOceanChunk"));
        westOceanChunk = new ChunkPos(tag.getLong("westOceanChunk"));
        eastOceanChunk = new ChunkPos(tag.getLong("eastOceanChunk"));
        southOceanChunk = new ChunkPos(tag.getLong("southOceanChunk"));
        health = tag.getFloat("health");
        maxHealth =  tag.getFloat("maxHealth");
        super.load(state, tag);
    }

    @Override
    public void onPlace() {
        ONIBoundingBlock.makeBoundingBlock(this.getLevel(), this.getBlockPos().above(), this.getBlockPos());
    }

    @Override
    public void onBreak(BlockState oldState) {
        if (this.level != null) {
            this.level.removeBlock(this.getBlockPos().above(), false);
        }
    }

    public UUID getOwner() {
        return owner;
    }

    public boolean canAccess(UUID uuid) {
        return getOwner().equals(uuid) || FTBTeamsAPI.arePlayersInSameTeam(getOwner(), uuid);
    }

    public void unclaimClaimedChunks() {
        ClaimedChunkManager chunkManager = FTBChunksAPI.getManager();
        Team team = FTBTeamsAPI.getPlayerTeam(getOwner());
        World world = getLevel();
        if (team == null) return;
        if (world == null) return;
        MinecraftServer server = world.getServer();
        if (server == null) return;
        CommandSource source = server.createCommandSourceStack();

        for (ClaimedChunk chunk : new ArrayList<>(chunkManager.getData(team).getClaimedChunks())) {
            chunkManager.getData(team).unclaim(source, chunk.pos, false);
        }
    }

    public List<ChunkPos> getClaimableChunks(BlockPos center) {
        List<ChunkPos> posList = new ArrayList<>();
        ChunkPos thisChunk = new ChunkPos(center);

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                posList.add(new ChunkPos(thisChunk.x + i, thisChunk.z + j));
            }
        }

        return posList;
    }

    public List<ChunkPos> getClaimableChunks() {
        return getClaimableChunks(getBlockPos());
    }

    public ChunkPos getNorthOceanChunk() {
        return northOceanChunk;
    }

    public ChunkPos getWestOceanChunk() {
        return westOceanChunk;
    }

    public ChunkPos getEastOceanChunk() {
        return eastOceanChunk;
    }

    public ChunkPos getSouthOceanChunk() {
        return southOceanChunk;
    }

    public UUID getEntity() {
        return entity;
    }
    
    public void addHealth(float health) {
        this.health += health;
        
        if (this.health < 0) {
            this.health = 0;
            if (isServer()) {
                InvasionWorldData.get(level).getInvasion(FTBTeamsAPI.getPlayerTeamID(owner)).failed((ServerWorld) level);
            }
        }
        
        updateBlock();
    }

    public float getHealth() {
        return health;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    @Override
    public void onPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onPlacedBy(worldIn, pos, state, placer, stack);

        if (placer == null) {
            Invaders.LOGGER.warn("A non player just placed a hub at {}, destroying now", pos);
            worldIn.removeBlock(pos, false);
            return;
        }
        
        maxHealth = InvadersConfigs.Common.BASE_HUB_HEALTH.get();
        health = maxHealth;

        owner = placer.getUUID();

        if (!worldIn.isClientSide()) {
            Invaders.LOGGER.debug("Player {} just placed hub at {}", placer.getStringUUID(), pos);

            Entity e = InvadersEntities.HUB.get().spawn((ServerWorld) worldIn, null, null, getBlockPos(), SpawnReason.STRUCTURE, false, false);
            if (!(e instanceof HubEntity)) {
                throw new IllegalStateException("Hub Entity Spawned is invalid");
            }
            HubEntity hubEntity = (HubEntity) e;
            hubEntity.setBlockEntity(getBlockPos());
            
            entity = e.getUUID();
        }

        placer.getCapability(InvadersCapabilities.PLAYER_DATA).ifPresent(cap -> {
            cap.setHubPos(getBlockPos());
            if (isServer()) {
                Team team = FTBTeamsAPI.getPlayerTeam(placer.getUUID());
                if (team != null) {
                    InvadersWorldData worldData = InvadersWorldData.get(worldIn);
                    worldData.setTeamHubPos(team.getId(), cap.getHubPos());
                    ChunkPos origin = new ChunkPos(pos);
                    northOceanChunk = findBiomeInDirection(worldIn, origin, 0, -1);
                    westOceanChunk = findBiomeInDirection(worldIn, origin, -1, 0);
                    eastOceanChunk = findBiomeInDirection(worldIn, origin, 1, 0);
                    southOceanChunk = findBiomeInDirection(worldIn, origin, 0, 1);
                    InvadersWorldData.refreshClient((ServerPlayerEntity) placer);
                }
            }
        });

        updateBlock();
    }

    @Override
    public void onHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onHarvested(world, pos, state, player);
        
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) world;
            serverWorld.despawn(serverWorld.getEntity(entity));
        }
        
        player.getCapability(InvadersCapabilities.PLAYER_DATA).ifPresent(cap -> {
            cap.setHubPos(null);
            if (isServer()) {
                Team team = FTBTeamsAPI.getPlayerTeam(player.getUUID());
                if (team != null) {
                    InvadersWorldData worldData = InvadersWorldData.get(world);
                    worldData.removeTeamHubPos(team.getId());
                    InvadersWorldData.refreshClient((ServerPlayerEntity) player);
                }
            }
        });
        unclaimClaimedChunks();
    }

    private ChunkPos findBiomeInDirection(World world, ChunkPos origin, int x, int z) {
        ChunkPos posToLook = origin;
        int trial = 0;

        while (world.getBiome(posToLook.getWorldPosition()).getBiomeCategory() != Biome.Category.OCEAN) {
            if (trial > 6) {
                break;
            }
            posToLook = new ChunkPos(posToLook.x + x, posToLook.z + z);
            trial++;
        }

        return posToLook;
    }

    public static ONIBlockBuilder<ONIBaseMachine> createBlock() {
        return new ONIBlockBuilder<>(() -> new ONIBaseMachine("Hub Terminal", AbstractBlock.Properties.of(Material.METAL).harvestTool(ToolType.PICKAXE).strength(-1.0F, 3600000.0F).requiresCorrectToolForDrops().noOcclusion()))
                .placementCondition((ctx, state) -> {
                    BlockPos pos = ctx.getClickedPos().above();
                    World world = ctx.getLevel();
                    // only placable in the overworld
                    if (!world.dimension().location().equals(new ResourceLocation("minecraft", "overworld")))
                        return false;
                    PlayerEntity player = ctx.getPlayer();
                    if (player == null || player instanceof FakePlayer) return false;
                    if (!player.isShiftKeyDown()) {
                        if (ctx.getLevel().isClientSide())
                            player.sendMessage(InvadersConstants.LangKeys.PLACE_HUB, player.getUUID());
                        return false;
                    }

                    return World.isInWorldBounds(pos) && world.getBlockState(pos).getMaterial().isReplaceable();
                })
                .breakCondition((state, player, blockReader, pos) -> {
                    if (player.getCommandSenderWorld().isClientSide())
                        player.sendMessage(InvadersConstants.LangKeys.DESTROY_HUB, player.getUUID());
                    return 0f;
                })
                .autoRotateShape()
                .shape((state, world, pos, ctx) -> SHAPE)
                .tileEntity((state, world) -> InvadersBlocks.Core.HUB_BE.get(), HubBE.class)
                .container(new IGui() {
                    @Override
                    public Container container(int i, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                        return InvadersBlocks.Core.HUB_CONTAINER_BUILDER.buildNewInstance(i, world, pos, playerInventory, playerEntity);
                    }

                    @Override
                    public String machineName() {
                        return TranslationHelper.guiTitle(InvadersConstants.LangKeys.HUB).getKey();
                    }

                    @Override
                    public boolean canOpen(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult, TileEntity te) {
                        return ((HubBE) te).canAccess(player.getUUID());
                    }
                })
                .noModelGen()
                .setCategory(ONIIItem.ItemCategory.CORE)
                .tooltip(TranslationHelper.itemTooltip(InvadersConstants.LangKeys.HUB))
                .shiftToolTip()
                .lightLevel((state, world, pos) -> 16);
    }

    public static ONIContainerBuilder createContainer() {
        return new ONIContainerBuilder(InvadersConstants.LangKeys.HUB)
                .disablePlayerSlots();
    }
}
