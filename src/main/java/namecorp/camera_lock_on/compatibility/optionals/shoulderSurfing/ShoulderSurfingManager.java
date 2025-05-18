package namecorp.camera_lock_on.compatibility.optionals.shoulderSurfing;

import static namecorp.camera_lock_on.client.Camera_lock_onClient.cameraDelta;

import org.jetbrains.annotations.Nullable;

import com.github.exopandora.shouldersurfing.api.client.IObjectPicker;
import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfingCamera;
import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.model.PickContext;
import com.github.exopandora.shouldersurfing.config.Config;

import namecorp.camera_lock_on.compatibility.optionals.ModManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec2f;

public class ShoulderSurfingManager extends ModManager {
    public ShoulderSurfingManager() {
        super("shouldersurfing");
    }

    public boolean isShoulderSurfingEnabled() {
        return isInstalled();
    }

    public Boolean isUsingCustomCamera() {
        var shoulderSurfing = ShoulderSurfing.getInstance();
        return isInstalled()
                && !MinecraftClient.getInstance().options.getPerspective().isFirstPerson()
                && shoulderSurfing.isShoulderSurfing();
    }

    @Nullable
    public IObjectPicker getEntityPicker() {
        if (!isShoulderSurfingEnabled()) return null;
        return ShoulderSurfing.getInstance().getObjectPicker();
    }

    @Nullable
    public HitResult pick(double interactionRange, float tickDelta) {
        if (!isShoulderSurfingEnabled()) return null;
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
		PickContext pickContext = new PickContext.Builder(camera).build();
        return ShoulderSurfing.getInstance().getObjectPicker().pick(pickContext, interactionRange, tickDelta, MinecraftClient.getInstance().player);

    }

    public boolean setCameraAngle(float yaw, float pitch) {
        if (!isShoulderSurfingEnabled()) return false;
        IShoulderSurfingCamera camera = ShoulderSurfing.getInstance().getCamera();
        camera.setYRot(yaw);
        camera.setXRot(pitch);
        MinecraftClient.getInstance().player.setAngles(yaw, pitch);
        return true;
    }
}
