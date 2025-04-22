package namecorp.camera_lock_on.mixin;

import namecorp.camera_lock_on.client.Camera_lock_onClient;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @ModifyArg(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"), index = 3)
    private int preventCrosshairWhenLocked(int x) {
        if(Camera_lock_onClient.lockedEntity != null && Camera_lock_onClient.showCrosshair) {
            x = 0;
        }
        return x;
    }
}
