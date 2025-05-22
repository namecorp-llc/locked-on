package namecorp.camera_lock_on.compatibility.optionals;

import java.util.Arrays;
import java.util.function.Supplier;

import namecorp.camera_lock_on.compatibility.optionals.shoulderSurfing.ShoulderSurfingManager;

public enum AdditionalMods {
    SHOULDER_SURFING(ShoulderSurfingManager::new);
    private final ModManager manager;

    AdditionalMods(Supplier<ModManager> supplier) {
        manager = supplier.get();
    }

    public ModManager get() {
        return manager;
    }

    public static void init() {
        Arrays.stream(values()).map(AdditionalMods::get).forEach(ModManager::init);
    }

    public static ShoulderSurfingManager shoulderSurfing() {
        return (ShoulderSurfingManager) SHOULDER_SURFING.get();
    }
}
