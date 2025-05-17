package namecorp.camera_lock_on.compatibility.optionals.shoulderSurfing;

import org.jetbrains.annotations.Nullable;

import namecorp.camera_lock_on.adapters.IEntityPickerAdapter;
import namecorp.camera_lock_on.compatibility.optionals.AdditionalMods;
import namecorp.camera_lock_on.util.Rotation;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
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
        ShoulderSurfingManager shoulderSurfingManager = AdditionalMods.shouderSurfing();
        @Nullable HitResult result = shoulderSurfingManager.pick(interactionRange, tickDelta);
        if (result == null) vanillaEntityPickerAdapter.pick(interactionRange, tickDelta);
        return result;
    }

    @Override
    public Rotation getRotation(ClientPlayerEntity player, Entity lockedEntity, WorldRenderContext last) {
        Rotation result = vanillaEntityPickerAdapter.getRotation(player, lockedEntity, last);
        result.addOffset(AdditionalMods.shouderSurfing().getCameraAngleOffset());
        
        return result;
    }

    @Override
    public void LookAt(float yaw, float pitch) {
        if (AdditionalMods.shouderSurfing().setCameraAngle(yaw, pitch)) return;
        vanillaEntityPickerAdapter.LookAt(yaw, pitch);
    }
    
}
