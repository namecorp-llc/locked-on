package namecorp.camera_lock_on.client.events;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

import static namecorp.camera_lock_on.client.Camera_lock_onClient.hudTickDelta;
import static namecorp.camera_lock_on.client.Camera_lock_onClient.lockedEntity;

public class HudTickDeltaEvent implements ClientTickEvents.EndTick{
    @Override
    public void onEndTick(MinecraftClient minecraftClient) {
        if(lockedEntity != null) {
            if(hudTickDelta < 15) {
                hudTickDelta++;
            }
        } else {
            if(hudTickDelta > 0) {
                hudTickDelta--;
            }
        }
    }
}
