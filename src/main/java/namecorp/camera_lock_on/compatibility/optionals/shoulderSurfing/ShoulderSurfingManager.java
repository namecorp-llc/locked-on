package namecorp.camera_lock_on.compatibility.optionals.shoulderSurfing;

import org.jetbrains.annotations.Nullable;

import com.github.exopandora.shouldersurfing.api.client.IObjectPicker;
import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.client.IShoulderSurfingCamera;
import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;
import com.github.exopandora.shouldersurfing.api.model.PickContext;
import namecorp.camera_lock_on.compatibility.optionals.ModManager;
import namecorp.camera_lock_on.util.Vec3dSingleCache;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class ShoulderSurfingManager extends ModManager {
    private static final int COMPENSATION_CONSTANT = 64;
    private static final Vec3d dimensionX = new Vec3d(1, 0, 1);
    private static final Vec3d dimensionY = new Vec3d(0, 1, 1);
    private final Vec3dSingleCache<Vec3d> baseAngleCache = new Vec3dSingleCache<>();
    private final Vec3dSingleCache<Vec3dSingleCache<Vec2f>> offsetCache = new Vec3dSingleCache<>();

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
            offset -> new Vec3d(
                getAngleA(offset, Vec3d.ZERO, offset.multiply(1, 0, 0), dimensionX) * COMPENSATION_CONSTANT,
                getAngleA(offset, Vec3d.ZERO, offset.multiply(1, 0, 0), dimensionY) * COMPENSATION_CONSTANT,
                0
            )
        );

        return offsetCache
            .get(result, x -> new Vec3dSingleCache<Vec2f>())
            .get(
                MinecraftClient.getInstance().gameRenderer.getCamera().getPos(),
                cameraPosition -> {
                    double distanceToTargetX = cameraPosition.multiply(dimensionX)
                            .squaredDistanceTo(target.getPos().multiply(dimensionX));
                    double distanceToTargetY = cameraPosition.multiply(dimensionX)
                            .squaredDistanceTo(target.getPos().multiply(dimensionY));
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

    private static double getAngleA(Vec3d a, Vec3d b, Vec3d c, Vec3d dimension) {
        Vec3d flattenedA = a.multiply(dimension);
        Vec3d flattenedB = b.multiply(dimension);
        Vec3d flattenedC = c.multiply(dimension);
        double bc2 = flattenedB.squaredDistanceTo(flattenedC);
        double ab2 = flattenedB.squaredDistanceTo(flattenedA);
        double ac2 = flattenedC.squaredDistanceTo(flattenedA);
        double cosCamera = (ab2 + ac2 - bc2) / (2 * Math.sqrt(ab2) * Math.sqrt(ac2));
        double cameraAngle = Math.acos(cosCamera);
        return Math.toDegrees(cameraAngle);
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
