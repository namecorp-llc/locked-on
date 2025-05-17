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

public class LockOnEvent implements WorldRenderEvents.Last {

    private IEntityPickerAdapter entityPickerAdapter;
    public LockOnEvent(IEntityPickerAdapter entityPickerAdapter) {
        this.entityPickerAdapter = entityPickerAdapter;
    }

    @Override
    public void onLast(WorldRenderContext last) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;

        if(player != null && client.world != null) {
            if(lockOnKeyBind.wasPressed()) {
                HitResult hit = entityPickerAdapter.pick(500.0f, last.tickCounter().getTickDelta(true));
                if(lockedEntity != null) {
                    lockedEntity = null;
                } else {
                    if(hit instanceof EntityHitResult && ((EntityHitResult) hit).getEntity() instanceof LivingEntity) {
                        lockedEntity = (LivingEntity) ((EntityHitResult) hit).getEntity();
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
                Rotation rotation = entityPickerAdapter.getRotation(player, lockedEntity, last);
                float yawDelta = cameraDelta;
                float pitchDelta = cameraDelta;

                if(player.input.jumping) {
                    pitchDelta = cameraDelta/5f;
                }

                float newYaw = MathHelper.lerp(yawDelta, rotation.getCurrentYaw(), rotation.getTargetYaw());
                float newPitch = MathHelper.lerp(pitchDelta, rotation.getCurrentPitch(), rotation.getTargetPitch());
                entityPickerAdapter.LookAt(newYaw, newPitch);
            }
        } else {
            lockedEntity = null;
        }
    }
}
