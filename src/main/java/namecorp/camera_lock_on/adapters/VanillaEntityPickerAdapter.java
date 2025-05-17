package namecorp.camera_lock_on.adapters;

import static namecorp.camera_lock_on.client.Camera_lock_onClient.cameraDelta;

import namecorp.camera_lock_on.util.LockOnUtil;
import namecorp.camera_lock_on.util.Rotation;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;

public class VanillaEntityPickerAdapter implements IEntityPickerAdapter {
    @Override
    public HitResult pick(double interactionRange, float tickDelta) {
        Entity camera = MinecraftClient.getInstance().cameraEntity;
        HitResult hitResult = camera.raycast(interactionRange, tickDelta, false);
        double d = interactionRange;
        double e = MathHelper.square(interactionRange);
        Vec3d cameraPos = camera.getCameraPosVec(tickDelta);
        Vec3d pos = hitResult.getPos();
        double f = pos.squaredDistanceTo(cameraPos);
        if (hitResult.getType() != HitResult.Type.MISS) {
            e = f;
            d = Math.sqrt(f);
        }

        Vec3d vec3d2 = camera.getRotationVec(tickDelta);
        Vec3d vec3d3 = cameraPos.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
        Box box = camera.getBoundingBox().stretch(vec3d2.multiply(d)).expand(4.0, 4.0, 4.0);
        EntityHitResult entityHitResult = ProjectileUtil.raycast(camera, cameraPos, vec3d3, box, entity -> !entity.isSpectator() && entity.canHit(), e);
        if (entityHitResult != null && entityHitResult.getPos().squaredDistanceTo(cameraPos) < f) {
            hitResult = entityHitResult;
            pos = hitResult.getPos();
        }
        if (!pos.isInRange(cameraPos, interactionRange)) {
            Direction direction = Direction.getFacing(pos.x - cameraPos.x, pos.y - cameraPos.y, pos.z - cameraPos.z);
            return BlockHitResult.createMissed(pos, direction, BlockPos.ofFloored(pos));
        }
        return hitResult;
    }

    @Override
    public Rotation getRotation(ClientPlayerEntity player, Entity lockedEntity, WorldRenderContext last) {
        Vec3d targetPos = lockedEntity.getPos();
        Vec3d playerPos = player.getPos();

        float tickDelta = last.tickCounter().getTickDelta(true);
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

        float lockedYaw;
        float lockedPitch;
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

        return new Rotation(currentYaw, currentPitch, lockedYaw, lockedPitch);
    }

    @Override
    public void LookAt(float yaw, float pitch) {
        MinecraftClient mc = MinecraftClient.getInstance();
        mc.player.setAngles(yaw, pitch);
        mc.cameraEntity.setAngles(yaw, pitch);
    }
}
