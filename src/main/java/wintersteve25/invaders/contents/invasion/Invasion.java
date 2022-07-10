package wintersteve25.invaders.contents.invasion;

import dev.ftb.mods.ftbteams.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.data.Team;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.world.server.ServerWorld;
import wintersteve25.invaders.Invaders;
import wintersteve25.invaders.contents.blocks.core.hub.HubBE;
import wintersteve25.invaders.contents.entities.HubEntity;
import wintersteve25.invaders.data.worlddata.InvasionWorldData;
import wintersteve25.invaders.init.InvadersConfigs;
import wintersteve25.invaders.utils.InvadersConstants;
import wintersteve25.invaders.utils.helpers.TranslationHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Invasion {
    private final UUID targetedTeam;
    private final WaveSpawner waveSpawner;
    
    private int enemiesLeft;
    private int totalEnemyCount;
    private int glowTimer;
    private int waveCooldown;
    
    private boolean started;
    
    private Invasion(UUID targetedTeam, ServerWorld world) {
        this.targetedTeam = targetedTeam;
        this.waveSpawner = new WaveSpawner(targetedTeam, world);
        this.glowTimer = InvadersConfigs.Common.GLOW_INTERVALS.get();
        this.waveCooldown = InvadersConfigs.Common.WAVE_INTERVALS.get();
    }
    
    public Invasion(CompoundNBT nbt) {
        this.targetedTeam = nbt.getUUID("targetedTeam");
        this.waveSpawner = new WaveSpawner((CompoundNBT) nbt.get("waves"));
        this.enemiesLeft = nbt.getInt("enemiesLeft");
        this.totalEnemyCount = nbt.getInt("totalEnemyCount");
        this.waveCooldown = nbt.getInt("waveCooldown");
        this.glowTimer = InvadersConfigs.Common.GLOW_INTERVALS.get();
    }
    
    public void start(ServerWorld world) {
        waveSpawner.initializeBossbar(world);
        started = true;
    }
    
    public void resume(ServerWorld world) {
        waveSpawner.resumeBossbar(world);
        if (enemiesLeft != 0) {
            waveSpawner.getBossBar().setPercent(enemiesLeft / (float) totalEnemyCount);
        }
        started = true;
    }
    
    public void tick(ServerWorld world) {
        if (!started) return;
        
        if (enemiesLeft == 0) {
            if (!waveSpawner.hasNextWave()) {
                finished(world);
                return;
            }
            
            float total = InvadersConfigs.Common.WAVE_INTERVALS.get();
            waveSpawner.getBossBar().setPercent((total - waveCooldown) / total);

            waveCooldown--;
            if (waveCooldown <= 0) {
                totalEnemyCount = waveSpawner.spawnWave(world);
                enemiesLeft = totalEnemyCount;
                waveCooldown = InvadersConfigs.Common.WAVE_INTERVALS.get();
                waveStarted();
            }

            waveSpawner.resetBossbarName();
            return;
        }
        
        glowTimer--;
        if (glowTimer == 0) {
            for (Entity entity : waveSpawner.getEnemies()) {
                if (entity instanceof LivingEntity) {
                    LivingEntity livingEntity = (LivingEntity) entity;
                    livingEntity.addEffect(new EffectInstance(Effects.GLOWING, 400, 1));
                }
            }
            glowTimer = InvadersConfigs.Common.GLOW_INTERVALS.get();
        }
        
        HubBE hubBE = (HubBE) world.getBlockEntity(waveSpawner.getHubPos());
        
        for (Entity entity : waveSpawner.getEnemies()) {
            if (entity instanceof MobEntity) {
                MobEntity mob = (MobEntity) entity;
                if (!(mob.getTarget() instanceof HubEntity)) {
                    mob.setTarget((LivingEntity) world.getEntity(hubBE.getEntity()));
                }
            }
        }
    }

    public void onInvasionMobKilled(Entity entity) { 
        enemiesLeft--;
        waveSpawner.getEnemies().remove(entity);
        waveSpawner.getBossBar().setPercent(enemiesLeft / (float) totalEnemyCount);
    }

    public void finished(ServerWorld world) {
        List<ServerPlayerEntity> players = new ArrayList<>(waveSpawner.getBossBar().getPlayers());
        for (ServerPlayerEntity player : players) {
            player.connection.send(new STitlePacket(STitlePacket.Type.TITLE, InvadersConstants.LangKeys.INVASION_SUCCESS));
            player.playSound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
        }
        
        waveSpawner.getBossBar().removeAllPlayers();
        started = false;
        InvasionWorldData.get(world).removeInvasion(targetedTeam);
    }

    private void waveStarted() {
        List<ServerPlayerEntity> players = new ArrayList<>(waveSpawner.getBossBar().getPlayers());
        for (ServerPlayerEntity player : players) {
            player.connection.send(new STitlePacket(STitlePacket.Type.TITLE, InvadersConstants.LangKeys.INVASION_WAVE_STARTED));
            player.connection.send(new STitlePacket(STitlePacket.Type.SUBTITLE, TranslationHelper.titles(InvadersConstants.LangKeys.INVASION_WAVE_ENEMY_COUNT, totalEnemyCount)));
        }
    }
    
    public void pause() {
        waveSpawner.getBossBar().removeAllPlayers();
        started = false;
    }

    public boolean isCompleted() {
        return enemiesLeft == -1;
    }

    public UUID getTargetedTeam() {
        return targetedTeam;
    }

    @Nullable
    public static Invasion tryCreateInvasion(UUID targetedTeam, ServerWorld world) {
        Team team = FTBTeamsAPI.getPlayerTeam(targetedTeam);
        if (team == null) {
            Invaders.LOGGER.warn("Triggered invasion on a team that does not exist");
            return null;
        }

        if (team.getOnlineMembers().isEmpty()) {
            Invaders.LOGGER.warn("Triggered invasion on a team that does not have any online players");
            return null;
        }
        
        InvasionWorldData data = InvasionWorldData.get(world);

        if (data.getInvasion(targetedTeam) != null) {
            Invaders.LOGGER.warn("Triggered invasion on a team that already has an on going invasion");
            return null;
        }

        Invasion invasion = new Invasion(targetedTeam, world);
        data.addInvasion(targetedTeam, invasion);
        return invasion;
    }
    
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putUUID("targetedTeam", targetedTeam);
        nbt.put("waves", waveSpawner.serializeNBT());
        nbt.putInt("enemiesLeft", enemiesLeft);
        nbt.putInt("totalEnemyCount", totalEnemyCount);
        nbt.putBoolean("started", started);
        nbt.putInt("waveCooldown", waveCooldown);
        return nbt;
    }
}
