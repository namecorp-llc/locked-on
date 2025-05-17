package namecorp.camera_lock_on.util;

public class Rotation {
    private final float currentYaw;
    private final float currentPitch;
    private final float targetYaw;
    private final float targetPitch;
    public Rotation(float currentYaw, float currentPitch, float targetYaw, float targetPitch) {
        this.currentYaw = currentYaw;
        this.currentPitch = currentPitch;
        if (targetYaw < currentYaw - 180f) targetYaw += 360f;
        if (targetYaw > currentYaw + 180f) targetYaw -= 360f;
        this.targetYaw = targetYaw;
        this.targetPitch = targetPitch;

    }
    public float getCurrentYaw() {
        return currentYaw;
    }
    public float getCurrentPitch() {
        return currentPitch;
    }
    public float getTargetYaw() {
        return targetYaw;
    }
    public float getTargetPitch() {
        return targetPitch;
    }
}