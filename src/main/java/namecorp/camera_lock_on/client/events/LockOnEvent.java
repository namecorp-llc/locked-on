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

public class LockOnEvent implements WorldRenderEvents.Last {
    @Override
    public void onLast(WorldRenderContext last) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;

        float tickDelta = worldRenderContext.tickCounter().getTickDelta(false);
        
        if(player != null && client.world != null) {
            if(lockOnKeyBind.wasPressed()) {
                HitResult hit = raycastCrosshair(client.cameraEntity, 500.0f, tickDelta);
                if(lockedEntity != null) {
                    lockedEntity = null;
                } else {
                    if(hit instanceof EntityHitResult && ((EntityHitResult) hit).getEntity() instanceof LivingEntity) {
                        lockedEntity = (LivingEntity) ((EntityHitResult) hit).getEntity();
                    } else {
                        float currYaw = player.getYaw(tickDelta);
                        float tempYaw = currYaw;
                        float savedYaw = 999999;

                        LivingEntity temp = null;

                        for(int i = -20; i < 20; i++) {
                            tempYaw = currYaw + Math.abs(i);
                            player.setYaw(currYaw+i);
                            HitResult hit1 = raycastCrosshair(client.cameraEntity, 500.0f, tickDelta);

                            if(hit1 instanceof EntityHitResult && ((EntityHitResult) hit1).getEntity() instanceof LivingEntity) {
                                LivingEntity newEntity = (LivingEntity) ((EntityHitResult) hit1).getEntity();
                                if(temp == null) {
                                    temp = (LivingEntity) ((EntityHitResult) hit1).getEntity();
                                } else {
                                    if(tempYaw < savedYaw) {
                                        temp = newEntity;
                                        savedYaw = tempYaw;
                                    }
                                }

                            }
                        }

                        if(temp != null) {
                            lockedEntity = temp;
                        }

                        player.setYaw(currYaw);
                    }
                }
            }

            if(lockedEntity != null) {
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
            }

            if (lockedEntity != null) {
                Vec3d targetPos = lockedEntity.getPos();
                Vec3d playerPos = player.getPos();

                float lockedYaw;
                float lockedPitch;

                float currentYaw = player.getYaw(tickDelta);
                float currentPitch = player.getPitch(tickDelta);

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

                if (lockedYaw < currentYaw - 180f) {
                    lockedYaw += 360f;
                }

                if (lockedYaw > currentYaw + 180f) {
                    lockedYaw -= 360f;
                }

                float yawDelta = cameraDelta;
                float pitchDelta = cameraDelta;

                if(player.input.jumping) {
                    pitchDelta = cameraDelta/5f;
                }

                if(client.getCurrentFps() > 200) {
                    int i = client.getCurrentFps()/200;
                    for(int j = 0; j < i; j++) {
                        yawDelta = yawDelta/2;
                        pitchDelta = pitchDelta/2;
                    }
                }

                player.setYaw(MathHelper.lerp(yawDelta, currentYaw, lockedYaw));
                player.setPitch(MathHelper.lerp(pitchDelta, currentPitch, lockedPitch));
            }
        } else {
            lockedEntity = null;
        }
    }

    public static HitResult ensureTargetInRange(HitResult hitResult, Vec3d cameraPos, double interactionRange) {
        Vec3d vec3d = hitResult.getPos();
        if (!vec3d.isInRange(cameraPos, interactionRange)) {
            Vec3d vec3d2 = hitResult.getPos();
            Direction direction = Direction.getFacing(vec3d2.x - cameraPos.x, vec3d2.y - cameraPos.y, vec3d2.z - cameraPos.z);
            return BlockHitResult.createMissed(vec3d2, direction, BlockPos.ofFloored(vec3d2));
        } else {
            return hitResult;
        }
    }
    public static HitResult raycastCrosshair(Entity camera, double range, float tickDelta) {
        double d = range;
        double e = MathHelper.square(range);
        Vec3d vec3d = camera.getCameraPosVec(tickDelta);
        HitResult hitResult = camera.raycast(d, tickDelta, false);
        double f = hitResult.getPos().squaredDistanceTo(vec3d);
        if (hitResult.getType() != HitResult.Type.MISS) {
            e = f;
            d = Math.sqrt(f);
        }

        Vec3d vec3d2 = camera.getRotationVec(tickDelta);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
        float g = 1.0F;
        Box box = camera.getBoundingBox().stretch(vec3d2.multiply(d)).expand(4.0, 4.0, 4.0);
        EntityHitResult entityHitResult = ProjectileUtil.raycast(camera, vec3d, vec3d3, box, entity -> !entity.isSpectator() && entity.canHit(), e);
        return entityHitResult != null && entityHitResult.getPos().squaredDistanceTo(vec3d) < f
                ? ensureTargetInRange(entityHitResult, vec3d, range)
                : ensureTargetInRange(hitResult, vec3d, range);
    }
}
