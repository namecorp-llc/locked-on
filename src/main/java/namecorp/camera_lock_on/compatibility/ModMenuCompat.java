package namecorp.camera_lock_on.compatibility;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import namecorp.camera_lock_on.client.Camera_lock_onClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.Map;

public class ModMenuCompat implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(MinecraftClient.getInstance().currentScreen)
                    .setTitle(Text.translatable("category.lockon.configtitle"));
            ConfigCategory general = builder.getOrCreateCategory(Text.translatable("category.lockon.config"));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            general.addEntry(entryBuilder.startFloatField(
                            Text.translatable("option.lockon.camdelta"), Camera_lock_onClient.cameraDelta)
                    .setDefaultValue(0.25f)
                    .setTooltip(Text.translatable("option.lockon.camdeltatooltip"))
                    .setSaveConsumer(newValue -> Camera_lock_onClient.cameraDelta = newValue)
                    .build());
            general.addEntry(entryBuilder.startBooleanToggle(
                    Text.translatable("option.lockon.showHUD"), Camera_lock_onClient.showHUD)
                    .setDefaultValue(true)
                    .setTooltip(Text.translatable("option.lockon.showHUDtooltip"))
                    .setSaveConsumer(newValue -> Camera_lock_onClient.showHUD = newValue)
                    .build()
            );

            general.addEntry(entryBuilder.startBooleanToggle(
                            Text.translatable("option.lockon.showCrosshair"), Camera_lock_onClient.showCrosshair)
                    .setDefaultValue(true)
                    .setTooltip(Text.translatable("option.lockon.showCrosshairtooltip"))
                    .setSaveConsumer(newValue -> Camera_lock_onClient.showCrosshair = newValue)
                    .build()
            );

            Screen screen = builder.build();

            return screen;
        };
    }

    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        return ModMenuApi.super.getProvidedConfigScreenFactories();
    }
}
