package namecorp.camera_lock_on.client.events;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;

import static namecorp.camera_lock_on.client.Camera_lock_onClient.*;

import namecorp.camera_lock_on.adapters.IEntityPickerAdapter;
import namecorp.camera_lock_on.util.LockOnUtil;
import namecorp.camera_lock_on.util.Rotation;

public class LockOnEvent implements WorldRenderEvents.Start {

    private IEntityPickerAdapter entityPickerAdapter;
    public LockOnEvent(IEntityPickerAdapter entityPickerAdapter) {
        this.entityPickerAdapter = entityPickerAdapter;
    }

    @Override
    public void onStart(WorldRenderContext worldRenderContext) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;

        if(player == null || client.world == null) { lockedEntity = null; return; }

        float tickDelta = worldRenderContext.tickCounter().getTickDelta(false);

        // Lock onto entity
        if(lockOnKeyBind.wasPressed()) {
            HitResult hit = entityPickerAdapter.pick(500.0f, tickDelta);
            if(lockedEntity != null) {
                lockedEntity = null;
            } else {
                if(hit instanceof EntityHitResult && ((EntityHitResult) hit).getEntity() instanceof LivingEntity) {
                    lockedEntity = (LivingEntity) ((EntityHitResult) hit).getEntity();
                } else {
                    float currYaw = player.getYaw(tickDelta);
                    float tempYaw;
                    float savedYaw = 999999;

                    LivingEntity tempEntity = null;

                    for(int i = -20; i < 20; i++) {
                        tempYaw = currYaw + Math.abs(i);
                        player.setYaw(currYaw+i);
                        HitResult hit1 = entityPickerAdapter.pick(500.0f, tickDelta);

                        if(hit1 instanceof EntityHitResult && ((EntityHitResult) hit1).getEntity() instanceof LivingEntity) {
                            LivingEntity newEntity = (LivingEntity) ((EntityHitResult) hit1).getEntity();
                            if(tempEntity == null) {
                                tempEntity = (LivingEntity) ((EntityHitResult) hit1).getEntity();
                            } else {
                                if(tempYaw < savedYaw) {
                                    tempEntity = newEntity;
                                    savedYaw = tempYaw;
                                }
                            }

                        }
                    }

                    if(tempEntity != null) {
                        lockedEntity = tempEntity;
                    }

                    player.setYaw(currYaw);
                }
            }
        }

        // Stops code if no entity is currently locked on
        if(lockedEntity == null) return;

        // Lose locked entity if the player cannot see it for more than 3 seconds
        if(!lockedEntity.isAlive()) {
            lockedEntity = null;
        } else {
            if(!player.canSee(lockedEntity) && !player.hasPermissionLevel(2)) {
                if(noSightTimer > 60) {
                    noSightTimer = 0;
                    lockedEntity = null;
                } else {
                    noSightTimer++;
                }
            } else {
                noSightTimer = 0;
            }
        }

        Rotation rotation = entityPickerAdapter.getRotation(player, lockedEntity, tickDelta);

        float newYaw = MathHelper.lerpAngleDegrees(tickDelta*cameraDelta, rotation.getCurrentYaw(), rotation.getTargetYaw());
        float newPitch = MathHelper.lerpAngleDegrees(tickDelta*cameraDelta, rotation.getCurrentPitch(), rotation.getTargetPitch());
        entityPickerAdapter.LookAt(newYaw, newPitch);
    }
}
