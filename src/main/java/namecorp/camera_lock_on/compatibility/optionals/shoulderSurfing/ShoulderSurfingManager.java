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
    private final SingleCache<Vec3d, Vec3d> baseAngleCache = new SingleCache<>();
    private final SingleCachePair<Vec3d, Vec3d, Vec2f> offsetCache = new SingleCachePair<>();

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
        if (!isShoulderSurfingEnabled())
            return null;
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        PickContext pickContext = new PickContext.Builder(camera).build();
        return ShoulderSurfing.getInstance().getObjectPicker().pick(pickContext, interactionRange, tickDelta,
                MinecraftClient.getInstance().player);

    }

    public Vec2f getCameraAngleOffset(Entity target) {
        if (!isUsingCustomCamera() || mustIgnoreDisplacement()) return Vec2f.ZERO;

        Vec3d result = baseAngleCache.get(
            ShoulderSurfing.getInstance().getCamera().getTargetOffset(),
            offset -> {
                Vec3d xVec = VectorUtil.flatX(offset);
                return new Vec3d(
                    VectorUtil.angleXZ(offset, Vec3d.ZERO, xVec) * COMPENSATION_CONSTANT,
                    VectorUtil.angleYZ(offset, Vec3d.ZERO, xVec) * COMPENSATION_CONSTANT,
                    0
                );
            }
        );

        return offsetCache.get(
                result,
                MinecraftClient.getInstance().gameRenderer.getCamera().getPos(),
                cameraPosition -> {
                    Vec3d targetPos = target.getPos();
                    double distanceToTargetX = VectorUtil.squareDistanceXZ(cameraPosition, targetPos);
                    double distanceToTargetY = VectorUtil.squareDistanceYZ(cameraPosition, targetPos);
                    return new Vec2f(
                        (float) (-result.x / distanceToTargetX),
                        (float) (-result.y / distanceToTargetY)
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
        if (!isShoulderSurfingEnabled()) return false;
        IShoulderSurfingCamera camera = ShoulderSurfing.getInstance().getCamera();
        camera.setYRot(yaw);
        camera.setXRot(pitch);
        MinecraftClient.getInstance().player.setAngles(yaw, pitch);
        return true;
    }
}
