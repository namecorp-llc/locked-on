package namecorp.camera_lock_on.compatibility.optionals.shoulderSurfing;

import org.jetbrains.annotations.Nullable;

import namecorp.camera_lock_on.adapters.IEntityPickerAdapter;
import namecorp.camera_lock_on.compatibility.optionals.AdditionalMods;
import namecorp.camera_lock_on.util.Rotation;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;

public class ShoulderSurfingEntityPicker implements IEntityPickerAdapter {
    private IEntityPickerAdapter vanillaEntityPickerAdapter;

    public ShoulderSurfingEntityPicker(IEntityPickerAdapter vanillaEntityPickerAdapter) {
        this.vanillaEntityPickerAdapter = vanillaEntityPickerAdapter;
    }

    @Override
    public HitResult pick(double interactionRange, float tickDelta) {
        ShoulderSurfingManager shoulderSurfingManager = AdditionalMods.shoulderSurfing();
        @Nullable HitResult result = shoulderSurfingManager.pick(interactionRange, tickDelta);
        if (result == null) vanillaEntityPickerAdapter.pick(interactionRange, tickDelta);
        return result;
    }

    @Override
    public Rotation getRotation(ClientPlayerEntity player, Entity lockedEntity, float tickDelta) {
        Rotation rotation = vanillaEntityPickerAdapter.getRotation(player, lockedEntity, tickDelta);
        rotation.addOffset(AdditionalMods.shoulderSurfing().getCameraAngleOffset(lockedEntity));
        return rotation;
    }

    @Override
    public void LookAt(float yaw, float pitch) {
        if (AdditionalMods.shoulderSurfing().setCameraAngle(yaw, pitch)) return;
        vanillaEntityPickerAdapter.LookAt(yaw, pitch);
    }
    
}
