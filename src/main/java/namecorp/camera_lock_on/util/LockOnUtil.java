package namecorp.camera_lock_on.util;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.math.Vec3d;

public class LockOnUtil {
    public static ItemStack getSpawnEgg(Entity entity) {
        Item egg = SpawnEggItem.forEntity(entity.getType());
        if(egg != null) {
            return egg.getDefaultStack();
        } else {
            return null;
        }
    }
}
