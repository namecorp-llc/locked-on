package namecorp.camera_lock_on.util;
import java.util.function.Function;

import net.minecraft.util.math.Vec3d;

public class Vec3dSingleCache<TValue> {
    private Vec3d key = null;
    public TValue value = null;

    public TValue get(Vec3d vec, Function<Vec3d, TValue> valueSupplier) {
        if (key != null && key.equals(vec)) return value;
        key = vec;
        value = valueSupplier.apply(vec);
        return value;
    }
}
