package namecorp.camera_lock_on.adapters;

import namecorp.camera_lock_on.util.Rotation;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;

public interface IEntityPickerAdapter {
    HitResult pick(double interactionRange, float tickDelta);
    Rotation getRotation(ClientPlayerEntity player, Entity lockedEntity, WorldRenderContext last);
    void LookAt(float yaw, float pitch);
}
