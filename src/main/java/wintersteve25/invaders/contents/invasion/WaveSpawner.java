package wintersteve25.invaders.contents.invasion;

import dev.ftb.mods.ftbteams.FTBTeamsAPI;
import fictioncraft.wintersteve25.fclib.common.helper.MiscHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;
import wintersteve25.invaders.Invaders;
import wintersteve25.invaders.contents.blocks.core.hub.HubBE;
import wintersteve25.invaders.contents.invasion.settings.InvasionSettings;
import wintersteve25.invaders.contents.invasion.settings.WaveBasedEnemy;
import wintersteve25.invaders.contents.invasion.settings.WavesMilestoneSetting;
import wintersteve25.invaders.data.worlddata.InvadersWorldData;
import wintersteve25.invaders.data.worlddata.InvasionWorldData;
import wintersteve25.invaders.init.InvadersCapabilities;
import wintersteve25.invaders.utils.InvadersConstants;
import wintersteve25.invaders.utils.helpers.TranslationHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WaveSpawner {

    private final InvasionSettings settings;
    private final ChunkPos[] spawnChunks;
    private final int teamDifficulty;
    private final int totalWaves;
    private final UUID targetedTeam;
    private final ServerBossInfo bossBar;
    private final List<Entity> enemies;
    private final ListNBT enemiesUUIDStored;
    private final BlockPos hubPos;
    
    private int waveOn;

    public WaveSpawner(UUID targetedTeam, ServerWorld world) {
        settings = InvasionSettings.JsonSettings;
        enemies = new ArrayList<>();
        enemiesUUIDStored = null;

        this.teamDifficulty = InvasionWorldData.get(world).getTeamDifficulty(targetedTeam);
        this.totalWaves = settings.getWaveCount(teamDifficulty).getWavesCount();
        this.waveOn = 0;
        this.targetedTeam = targetedTeam;

        this.bossBar = new ServerBossInfo(StringTextComponent.EMPTY, BossInfo.Color.RED, BossInfo.Overlay.PROGRESS);

        InvadersWorldData data = InvadersWorldData.get(world);
        this.hubPos = data.getTeamHubPos(targetedTeam);
        HubBE hubBE = (HubBE) world.getBlockEntity(this.hubPos);
        if (hubBE == null) {
            throw new IllegalStateException("Unable to find hub block for team " + targetedTeam);
        }

        this.spawnChunks = new ChunkPos[4];

        spawnChunks[0] = hubBE.getNorthOceanChunk();
        spawnChunks[1] = hubBE.getWestOceanChunk();
        spawnChunks[2] = hubBE.getEastOceanChunk();
        spawnChunks[3] = hubBE.getSouthOceanChunk();
    }

    public WaveSpawner(CompoundNBT nbt) {
        settings = InvasionSettings.JsonSettings;

        teamDifficulty = nbt.getInt("teamDifficulty");
        totalWaves = nbt.getInt("totalWaves");
        waveOn = nbt.getInt("waveOn");
        targetedTeam = nbt.getUUID("targetedTeam");

        spawnChunks = new ChunkPos[4];

        INBT cs = nbt.get("spawnChunks");
        if (cs instanceof ListNBT) {
            ListNBT chunks = (ListNBT) cs;
            for (int i = 0; i < chunks.size(); i++) {
                spawnChunks[i] = new ChunkPos(((LongNBT) chunks.get(i)).getAsLong());
            }
        }

        enemiesUUIDStored = (ListNBT) nbt.get("enemies");

        hubPos = NBTUtil.readBlockPos((CompoundNBT) nbt.get("hubPos"));
        
        enemies = new ArrayList<>();
        bossBar = new ServerBossInfo(TranslationHelper.bossBar("invasion.starting"), BossInfo.Color.RED, BossInfo.Overlay.PROGRESS);
    }

    // TODO: why is the first wave no longer spawning things?
    public int spawnWave(ServerWorld world) {
        waveOn++;
        
        WavesMilestoneSetting wave = settings.getWaveCount(teamDifficulty);
        int expectedEnemySpawnCount = MiscHelper.randomInRange(wave.getMinEnemyCount(), wave.getMaxEnemyCount());
        List<WaveBasedEnemy> waveBasedEnemies = settings.getEnemies(waveOn, teamDifficulty);
        
        if (waveBasedEnemies.isEmpty()) {
            Invaders.LOGGER.warn("No enemies spawned on wave " + waveOn + " on difficulty " + teamDifficulty);
        }
        
        int enemySpawnCount = 0;
        enemies.clear();

        for (WaveBasedEnemy waveEnemy : waveBasedEnemies) {
            int countOfTypeToSpawn = (int) Math.ceil(expectedEnemySpawnCount * waveEnemy.getSpawnWeight());
            EntityType<?> entityType = waveEnemy.getEntityType();
            for (int i = 0; i < 4; i++) {
                ChunkPos chunkPos = spawnChunks[i];
                for (int j = 0; j < Math.round(countOfTypeToSpawn / 4f); j++) {
                    if (spawn(world, entityType, chunkPos)) {
                        enemySpawnCount++;
                    }
                }
            }
        }

        return enemySpawnCount;
    }

    private boolean spawn(ServerWorld world, EntityType<?> type, ChunkPos chunkPos) {
        if (type == null) return false;

        Entity entity = type.spawn(
                world,
                null,
                null,
                null,
                randomBlockPos(world.getChunk(chunkPos.x, chunkPos.z)),
                SpawnReason.MOB_SUMMONED,
                false,
                false
        );

        if (entity == null) {
            Invaders.LOGGER.error("Tried to spawn entity during invasion but resulted in an error");
            return false;
        }

        HubBE hubBE = (HubBE) world.getBlockEntity(hubPos);
        
        if (entity instanceof MobEntity) {
            MobEntity mobEntity = (MobEntity) entity;
            mobEntity.setTarget((LivingEntity) world.getEntity(hubBE.getEntity()));
        }

        entity.getCapability(InvadersCapabilities.INVASION_MOBS).ifPresent(cap -> {
            cap.setInvasionMob(true);
            cap.setWave(waveOn);
            cap.setDifficulty(teamDifficulty);
            cap.setTargetedTeam(targetedTeam);
        });

        enemies.add(entity);

        return true;
    }

    public void initializeBossbar(ServerWorld world) {
        bossBar.removeAllPlayers();

        bossBar.setCreateWorldFog(true);
        bossBar.setVisible(true);
        bossBar.setPercent(0);
        resetBossbarName();

        for (ServerPlayerEntity player : FTBTeamsAPI.getPlayerTeam(targetedTeam).getOnlineMembers()) {
            bossBar.addPlayer(player);
        }

        if (enemiesUUIDStored == null) return;
        for (INBT inbt : enemiesUUIDStored) {
            this.enemies.add(world.getEntity(NBTUtil.loadUUID(inbt)));
        }
    }
    
    public void resumeBossbar(ServerWorld world) {
        bossBar.removeAllPlayers();

        bossBar.setCreateWorldFog(true);
        bossBar.setVisible(true);
        resetBossbarName();

        for (ServerPlayerEntity player : FTBTeamsAPI.getPlayerTeam(targetedTeam).getOnlineMembers()) {
            bossBar.addPlayer(player);
        }

        if (enemiesUUIDStored == null) return;
        for (INBT inbt : enemiesUUIDStored) {
            this.enemies.add(world.getEntity(NBTUtil.loadUUID(inbt)));
        }
    }
    
    public boolean hasNextWave() {
        return waveOn < totalWaves;
    }

    public void resetBossbarName() {
        bossBar.setName(TranslationHelper.bossBar(InvadersConstants.LangKeys.INVASION, waveOn, totalWaves));
    }

    public ServerBossInfo getBossBar() {
        return bossBar;
    }

    public List<Entity> getEnemies() {
        return enemies;
    }

    public int getWaveOn() {
        return waveOn;
    }

    public BlockPos getHubPos() {
        return hubPos;
    }

    private BlockPos randomBlockPos(Chunk chunk) {
        ChunkPos pos = chunk.getPos();
        int x = MiscHelper.randomInRange(pos.getMinBlockX(), pos.getMaxBlockX());
        int z = MiscHelper.randomInRange(pos.getMinBlockZ(), pos.getMaxBlockZ());
        int y = chunk.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);
        return new BlockPos(x, y + 1, z);
    }

    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();

        nbt.putInt("teamDifficulty", teamDifficulty);
        nbt.putInt("totalWaves", totalWaves);
        nbt.putInt("waveOn", waveOn);
        nbt.putUUID("targetedTeam", targetedTeam);
        nbt.put("hubPos", NBTUtil.writeBlockPos(hubPos));
        
        ListNBT chunks = new ListNBT();
        for (ChunkPos chunkPos : spawnChunks) {
            chunks.add(LongNBT.valueOf(chunkPos.toLong()));
        }
        nbt.put("spawnChunks", chunks);

        ListNBT enemies = new ListNBT();
        for (Entity entity : this.enemies) {
            enemies.add(NBTUtil.createUUID(entity.getUUID()));
        }
        nbt.put("enemies", enemies);
        
        return nbt;
    }
}