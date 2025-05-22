package namecorp.camera_lock_on;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import namecorp.camera_lock_on.compatibility.optionals.AdditionalMods;

public class Camera_lock_on implements ModInitializer {
    public static String MOD_ID = "lockon";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


    @Override
    public void onInitialize() {
        AdditionalMods.init();
    }
}
