package namecorp.camera_lock_on.util;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;

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
