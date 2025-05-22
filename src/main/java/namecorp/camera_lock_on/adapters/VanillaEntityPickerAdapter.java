package namecorp.camera_lock_on.adapters;

import namecorp.camera_lock_on.util.LockOnUtil;
import namecorp.camera_lock_on.util.Rotation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;

public class VanillaEntityPickerAdapter implements IEntityPickerAdapter {
    @Override
    public HitResult pick(double interactionRange, float tickDelta) {
        Entity camera = MinecraftClient.getInstance().cameraEntity;
        return LockOnUtil.raycastCrosshair(camera, interactionRange, tickDelta);
    }

    @Override
    public Rotation getRotation(ClientPlayerEntity player, Entity lockedEntity, float tickDelta) {
        // Actual Math Here
        Vec3d targetPos = lockedEntity.getPos();
        Vec3d playerPos = player.getPos();

        float currentYaw = player.getYaw(tickDelta);
        float currentPitch = player.getPitch(tickDelta);

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

        return new Rotation(currentYaw, currentPitch, lockedYaw, lockedPitch);
    }

    @Override
    public void LookAt(float yaw, float pitch) {
        MinecraftClient mc = MinecraftClient.getInstance();
        mc.player.setAngles(yaw, pitch);
        mc.cameraEntity.setAngles(yaw, pitch);
    }
}
