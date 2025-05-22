package namecorp.camera_lock_on.compatibility.optionals.shoulderSurfing;

import static namecorp.camera_lock_on.client.Camera_lock_onClient.lockedEntity;
import com.github.exopandora.shouldersurfing.api.callback.ICameraCouplingCallback;
import com.github.exopandora.shouldersurfing.api.callback.ITargetCameraOffsetCallback;
import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.plugin.IShoulderSurfingPlugin;
import com.github.exopandora.shouldersurfing.api.plugin.IShoulderSurfingRegistrar;

import namecorp.camera_lock_on.compatibility.optionals.AdditionalMods;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

/**
 * Compatibility class for the "Should Surfing" mod
 */
public class ShoulderSurfingPlugin implements ICameraCouplingCallback, IShoulderSurfingPlugin, ITargetCameraOffsetCallback {
    private static final double supportedX = 0.75;
    private static final double supportedY = 0;

    @Override
    public boolean isForcingCameraCoupling(MinecraftClient mc) {
        return lockedEntity != null;
    }

    @Override
    public void register(IShoulderSurfingRegistrar registrar) {
        registrar
            .registerCameraCouplingCallback(this)
            .registerTargetCameraOffsetCallback(this);
    }

    @Override
    public Vec3d post(IShoulderSurfing instance, Vec3d targetOffset, Vec3d defaultOffset) {
        if (lockedEntity == null) return defaultOffset;
        ShoulderSurfingManager shoulderSurfing = AdditionalMods.shoulderSurfing();
        if (shoulderSurfing.mustIgnoreDisplacement()) return defaultOffset.multiply(0, 0, 1);
        return new Vec3d(defaultOffset.x > 0 ? supportedX : -supportedX, supportedY, defaultOffset.z);
    }
}