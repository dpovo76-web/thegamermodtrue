package com.gamersystem.zone;

import com.gamersystem.Config;
import com.gamersystem.data.GamerData;
import com.gamersystem.registry.ModAttachments;
import com.gamersystem.registry.ModBlockEntities;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ZoneCoreBlockEntity extends BlockEntity {
    public static final String TAG_PREFIX = "gamer_zone_";

    /** Zonas cargadas en este momento (para consultar rapido desde los eventos). */
    public static final Set<ZoneCoreBlockEntity> ACTIVE = ConcurrentHashMap.newKeySet();

    private boolean training = false;
    private int mobIndex = 0;

    public ZoneCoreBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ZONE_CORE.get(), pos, state);
    }

    // ---------- ciclo de vida ----------
    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide()) ACTIVE.add(this);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        ACTIVE.remove(this);
    }

    // ---------- guardado ----------
    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("training", training);
        output.putInt("mob", mobIndex);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        training = input.getBooleanOr("training", false);
        mobIndex = input.getIntOr("mob", 0);
    }

    // ---------- geometria ----------
    public String zoneTag() {
        return TAG_PREFIX + worldPosition.asLong();
    }

    private AABB area() {
        int r = Config.ZONE_RADIUS.get();
        return new AABB(worldPosition).inflate(r, r, r);
    }

    public boolean contains(Level other, Vec3 pos) {
        return other == level && area().contains(pos);
    }

    public static ZoneCoreBlockEntity zoneAt(Level level, Vec3 pos) {
        for (ZoneCoreBlockEntity z : ACTIVE) {
            if (z.contains(level, pos)) return z;
        }
        return null;
    }

    public boolean isTraining() {
        return training;
    }

    // ---------- interaccion ----------
    public void toggleMode(Player player) {
        training = !training;
        if (!training && level instanceof ServerLevel sl) clearTrainingMobs(sl);
        setChanged();
        player.displayClientMessage(Component.literal(training
                ? "[Zona] Modo ENTRENAMIENTO: " + currentMobId()
                : "[Zona] Modo PROTEGIDO"), true);
    }

    public void cycleMob(Player player) {
        List<? extends String> mobs = Config.TRAINING_MOBS.get();
        if (mobs.isEmpty()) return;
        mobIndex = Math.floorMod(mobIndex + 1, mobs.size());
        if (level instanceof ServerLevel sl) clearTrainingMobs(sl);
        setChanged();
        player.displayClientMessage(Component.literal("[Zona] Enemigo de entrenamiento: " + currentMobId()), true);
    }

    private String currentMobId() {
        List<? extends String> mobs = Config.TRAINING_MOBS.get();
        if (mobs.isEmpty()) return "(ninguno)";
        return mobs.get(Math.floorMod(mobIndex, mobs.size()));
    }

    // ---------- tick ----------
    public void serverTick() {
        if (!(level instanceof ServerLevel sl)) return;
        if (sl.getGameTime() % 40 != 0) return;

        AABB box = area();
        List<ServerPlayer> players = sl.getEntitiesOfClass(ServerPlayer.class, box);

        for (ServerPlayer p : players) {
            p.heal(2.0F);
            GamerData d = p.getData(ModAttachments.GAMER.get());
            if (d.awakened()) p.setData(ModAttachments.GAMER.get(), d.withMp(d.mp() + 3));
        }

        if (training && !players.isEmpty()) spawnTrainingMob(sl, box);
    }

    private void spawnTrainingMob(ServerLevel sl, AABB box) {
        List<? extends String> mobs = Config.TRAINING_MOBS.get();
        if (mobs.isEmpty()) return;

        String tag = zoneTag();
        int alive = sl.getEntitiesOfClass(Mob.class, box, m -> ZoneTags.hasTag(m, tag)).size();
        if (alive >= Config.MAX_TRAINING_MOBS.get()) return;

        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.parse(currentMobId())).orElse(null);
        if (type == null) return;

        RandomSource r = sl.getRandom();
        int rad = Config.ZONE_RADIUS.get();
        for (int attempt = 0; attempt < 10; attempt++) {
            int dx = r.nextInt(2 * rad + 1) - rad;
            int dz = r.nextInt(2 * rad + 1) - rad;
            for (int dy = 3; dy >= -3; dy--) {
                BlockPos p = worldPosition.offset(dx, dy, dz);
                BlockPos below = p.below();
                if (sl.getBlockState(p).isAir() && sl.getBlockState(p.above()).isAir()
                        && !sl.getBlockState(below).getCollisionShape(sl, below).isEmpty()) {
                    Entity e = type.create(sl, EntitySpawnReason.SPAWNER);
                    if (e instanceof Mob mob) {
                        mob.setPos(p.getX() + 0.5, p.getY(), p.getZ() + 0.5);
                        mob.addTag(tag);
                        mob.setPersistenceRequired();
                        sl.addFreshEntity(mob);
                    } else if (e != null) {
                        e.discard();
                    }
                    return;
                }
            }
        }
    }

    private void clearTrainingMobs(ServerLevel sl) {
        String tag = zoneTag();
        for (Mob m : sl.getEntitiesOfClass(Mob.class, area(), mob -> ZoneTags.hasTag(mob, tag))) {
            m.discard();
        }
    }
}
