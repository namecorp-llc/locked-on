package namecorp.camera_lock_on.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;

import static namecorp.camera_lock_on.client.Camera_lock_onClient.lockedEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {

    @Inject(method = "updateMouse", at = @At("HEAD"), cancellable = true)
    private void updateMouse(CallbackInfo ci) {
        if(lockedEntity != null && MinecraftClient.getInstance().currentScreen == null) {
            ci.cancel();
        }
    }
}
