package namecorp.camera_lock_on.compatibility.optionals.shoulderSurfing;

import static namecorp.camera_lock_on.client.Camera_lock_onClient.lockedEntity;
import com.github.exopandora.shouldersurfing.api.callback.ICameraCouplingCallback;
import com.github.exopandora.shouldersurfing.api.plugin.IShoulderSurfingPlugin;
import com.github.exopandora.shouldersurfing.api.plugin.IShoulderSurfingRegistrar;
import net.minecraft.client.MinecraftClient;

/**
 * Compatibility class for the "Should Surfing" mod
 */
public class ShoulderSurfingCoupledCameraPlugin implements ICameraCouplingCallback, IShoulderSurfingPlugin {

    @Override
    public boolean isForcingCameraCoupling(MinecraftClient mc) {
        return lockedEntity != null;
    }

    @Override
    public void register(IShoulderSurfingRegistrar registrar) {
        registrar.registerCameraCouplingCallback(this);
    }
}