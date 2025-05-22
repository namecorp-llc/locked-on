package namecorp.camera_lock_on.compatibility.optionals.shoulderSurfing;

import org.jetbrains.annotations.Nullable;

import com.github.exopandora.shouldersurfing.api.client.IObjectPicker;
import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfingCamera;
import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.model.PickContext;

import namecorp.camera_lock_on.compatibility.optionals.ModManager;
import namecorp.camera_lock_on.util.VectorUtil;
import namecorp.camera_lock_on.util.SingleCache;
import namecorp.camera_lock_on.util.SingleCachePair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class ShoulderSurfingManager extends ModManager {
    /*
     * Compensation constant for shoulder surfing offset.
     * There's no particular reason for this value other than
     * it being a multiple of the pixel height of a block.
     * It has been empirically determined that this value works well
     */
    private static final int COMPENSATION_CONSTANT = 64;
    private final SingleCache<Vec3d, Vec2f> baseAngleCache = new SingleCache<>();
    private final SingleCachePair<Vec2f, Vec3d, Vec2f> offsetCache = new SingleCachePair<>();

	public ShoulderSurfingManager() {
        super("shouldersurfing");
    }

    public Boolean isUsingCustomCamera() {
        return isInstalled()
            && !MinecraftClient.getInstance().options.getPerspective().isFirstPerson()
            && ShoulderSurfing.getInstance().isShoulderSurfing();
    }

    @Nullable
    public IObjectPicker getEntityPicker() {
        if (!isInstalled()) return null;
        return ShoulderSurfing.getInstance().getObjectPicker();
    }

    @Nullable
    public HitResult pick(double interactionRange, float tickDelta) {
        if (!isInstalled()) return null;
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        PickContext pickContext = new PickContext.Builder(camera).build();
        return ShoulderSurfing.getInstance().getObjectPicker().pick(pickContext, interactionRange, tickDelta,
                MinecraftClient.getInstance().player);

    }

    public Vec2f getCameraAngleOffset(Entity target) {
        if (!isUsingCustomCamera() || mustIgnoreDisplacement()) return Vec2f.ZERO;

        Vec2f result = baseAngleCache.get(
            ShoulderSurfing.getInstance().getCamera().getOffset(),
            offset -> {
                float xSign = offset.x > 0f ? -1f : 1f;
                return new Vec2f(
                    (float)(VectorUtil.angleXZ(offset, Vec3d.ZERO, VectorUtil.flatX(offset)) * COMPENSATION_CONSTANT * xSign),
                    0f
                );
            }
        );

        return offsetCache.get(
                result,
                MinecraftClient.getInstance().gameRenderer.getCamera().getPos(),
                cameraPosition -> {
                    Vec3d targetPos = target.getPos();
                    double distanceToTargetX = cameraPosition.squaredDistanceTo(VectorUtil.flatXZ(targetPos));
                    return new Vec2f(
                        (float) (-result.x / distanceToTargetX),
                        0f
                    );
                }
            );
    }

    public boolean mustIgnoreDisplacement() {
        IShoulderSurfing shoulderSurfing = ShoulderSurfing.getInstance();
        return Math.abs(shoulderSurfing.getCamera().getXRot()) > 45
            || shoulderSurfing.isAiming();
    }

    public boolean setCameraAngle(float yaw, float pitch) {
        if (!isInstalled()) return false;
        IShoulderSurfingCamera camera = ShoulderSurfing.getInstance().getCamera();
        camera.setYRot(yaw);
        camera.setXRot(pitch);
        MinecraftClient.getInstance().player.setAngles(yaw, pitch);
        return true;
    }
}
