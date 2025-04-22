package namecorp.camera_lock_on.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;

import java.util.HashMap;
import java.util.Map;

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
