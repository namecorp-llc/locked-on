package namecorp.camera_lock_on.compatibility.optionals;
import net.fabricmc.loader.api.FabricLoader;

public abstract class ModManager {
    private boolean installed = false;
    private final String modId;

    public ModManager(String modId) {
        this.modId = modId;
    }

    public void init() {
        installed = FabricLoader.getInstance().isModLoaded(modId);
    }

    public boolean isInstalled() {
        return installed;
    }

    public String getModID() {
        return modId;
    }
}
