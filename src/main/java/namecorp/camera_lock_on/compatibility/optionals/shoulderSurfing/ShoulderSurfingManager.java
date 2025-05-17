package namecorp.camera_lock_on.compatibility.optionals.shoulderSurfing;

import static namecorp.camera_lock_on.client.Camera_lock_onClient.cameraDelta;

import org.jetbrains.annotations.Nullable;

import com.github.exopandora.shouldersurfing.api.client.IObjectPicker;
import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfingCamera;
import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.model.PickContext;

import namecorp.camera_lock_on.compatibility.optionals.AdditionalMods;
import namecorp.camera_lock_on.compatibility.optionals.ModManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

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

    public Vec2f getCameraAngleOffset() {
        if (!AdditionalMods.shouderSurfing().isUsingCustomCamera()) return Vec2f.ZERO;
        IShoulderSurfingCamera camera = ShoulderSurfing.getInstance().getCamera();
        Vec3d offset = camera.getOffset();
        double distance = Math.pow(camera.getCameraDistance(), 2);
        return new Vec2f(
            (float) -Math.toDegrees(Math.atan2(offset.z, distance)),
            (float) -Math.toDegrees(Math.atan2(offset.y, distance))
        );
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
