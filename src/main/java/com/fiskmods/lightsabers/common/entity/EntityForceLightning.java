package com.fiskmods.lightsabers.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import java.util.Optional;
import java.util.UUID;

public class EntityForceLightning extends Entity {

    private static final EntityDataAccessor<Optional<UUID>> CASTER_ID =
            SynchedEntityData.defineId(EntityForceLightning.class, EntityDataSerializers.OPTIONAL_UUID);

    private LivingEntity caster;

    public EntityForceLightning(EntityType<? extends EntityForceLightning> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public EntityForceLightning(Level level, LivingEntity caster) {
        this(ModEntities.FORCE_LIGHTNING.get(), level);
        this.caster = caster;
        this.entityData.set(CASTER_ID, Optional.of(caster.getUUID()));
        setPos(caster.getX(), caster.getEyeY() - 0.2F, caster.getZ());
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(CASTER_ID, Optional.empty());
    }

    public LivingEntity getCaster() {
        if (caster == null) {
            Optional<UUID> opt = entityData.get(CASTER_ID);
            if (opt.isPresent()) {
                Entity e = level().getEntity(opt.get());
                if (e instanceof LivingEntity living) {
                    caster = living;
                }
            }
        }
        return caster;
    }

    @Override
    public void tick() {
        super.tick();
        LivingEntity c = getCaster();
        if (c == null || !c.isAlive()) {
            discard();
            return;
        }

        setPos(c.getX(), c.getEyeY() - 0.2, c.getZ());

        if (tickCount > 2) {
            discard();
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.hasUUID("Caster")) {
            entityData.set(CASTER_ID, Optional.of(tag.getUUID("Caster")));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        entityData.get(CASTER_ID).ifPresent(uuid -> tag.putUUID("Caster", uuid));
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
