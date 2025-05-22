package namecorp.camera_lock_on.client.events;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;

import static namecorp.camera_lock_on.client.Camera_lock_onClient.*;

public class LockOnEvent implements WorldRenderEvents.Start {
    @Override
    public void onStart(WorldRenderContext worldRenderContext) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;

        if(player == null || client.world == null) { lockedEntity = null; return; }

        float tickDelta = worldRenderContext.tickCounter().getTickDelta(false);

        // Lock onto entity
        if(lockOnKeyBind.wasPressed()) {
            HitResult hit = raycastCrosshair(client.cameraEntity, 500.0f, tickDelta);
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
                        HitResult hit1 = raycastCrosshair(client.cameraEntity, 500.0f, tickDelta);

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

        // Actual Math Here
        Vec3d targetPos = lockedEntity.getPos();
        Vec3d playerPos = player.getPos();

        float lockedYaw;
        float lockedPitch;

        double deltaX = Math.abs(targetPos.x - playerPos.x);
        double deltaY = targetPos.y - playerPos.y;
        double deltaZ = Math.abs(targetPos.z - playerPos.z);


        if(lockedEntity.getHeight() < 1.5f) {
            deltaY--;
        } else if (lockedEntity.getHeight() < 2) {
            deltaY -= 0.3f;
        }


        double thetaRadiansXY = Math.atan2(Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaZ, 2)), deltaY);
        double thetaDegreesXY = Math.toDegrees(thetaRadiansXY);

        if(targetPos.z >= playerPos.z) {
            double thetaRadiansXZ = Math.atan2(deltaX, deltaZ);
            double thetaDegreesXZ = Math.toDegrees(thetaRadiansXZ);
            if(targetPos.x >= playerPos.x) {
                lockedYaw = (float) -thetaDegreesXZ;
            } else {
                lockedYaw = (float) thetaDegreesXZ;
            }
        } else {
            double thetaRadiansXZ = Math.atan2(deltaZ, deltaX);
            double thetaDegreesXZ = Math.toDegrees(thetaRadiansXZ);
            if(targetPos.x >= playerPos.x) {
                lockedYaw = (float) -thetaDegreesXZ - 90;
            } else {
                lockedYaw = (float) thetaDegreesXZ + 90;
            }
        }


        lockedPitch = (float) thetaDegreesXY - 90;

        player.setYaw(MathHelper.lerpAngleDegrees(tickDelta*cameraDelta, player.prevYaw, lockedYaw));
        player.setPitch(MathHelper.lerpAngleDegrees(tickDelta*cameraDelta, player.prevPitch, lockedPitch));
    }
}
