package namecorp.camera_lock_on.client;

import namecorp.camera_lock_on.client.events.HudRenderEvent;
import namecorp.camera_lock_on.client.events.HudTickDeltaEvent;
import namecorp.camera_lock_on.client.events.LockOnEvent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.LivingEntity;
import org.lwjgl.glfw.GLFW;

public class Camera_lock_onClient implements ClientModInitializer {
    public static LivingEntity lockedEntity = null;
    public static LivingEntity savedEntity = null;
    public static int hudTickDelta = 0;

    public static float cameraDelta = 0.25f;
    public static boolean showHUD = true;
    public static boolean showCrosshair = true;

    public static int noSightTimer = 0;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(new HudTickDeltaEvent());
        WorldRenderEvents.START.register(new LockOnEvent());
        HudRenderCallback.EVENT.register(new HudRenderEvent());
    }

    public static KeyBinding lockOnKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.lockon.lockon",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "category.lockon.lockon"
    ));
}
