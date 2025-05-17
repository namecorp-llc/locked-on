package namecorp.camera_lock_on.client.events;

import com.mojang.blaze3d.systems.RenderSystem;
import namecorp.camera_lock_on.Camera_lock_on;
import namecorp.camera_lock_on.compatibility.optionals.AdditionalMods;
import namecorp.camera_lock_on.util.LockOnUtil;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

import static namecorp.camera_lock_on.client.Camera_lock_onClient.*;

public class HudRenderEvent implements HudRenderCallback {
    @Override
    public void onHudRender(DrawContext context, RenderTickCounter renderTickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;

        boolean bl1 = client.options.getPerspective().isFirstPerson() || AdditionalMods.shouderSurfing().isUsingCustomCamera();
        boolean bl2 = client.interactionManager.getCurrentGameMode() != GameMode.SPECTATOR;
        boolean bl3 = !client.inGameHud.getDebugHud().shouldShowDebugHud();
        boolean bl4 = showHUD;
        boolean bl5 = showCrosshair;

        if(bl1 && bl2 && bl3) {
            if(hudTickDelta != 0) {
                if(lockedEntity != null) {
                    savedEntity = lockedEntity;
                }

                if(bl4) {
                    context.drawText(client.textRenderer, "Current Target" , (int) ((32f*hudTickDelta)/3f)-115, 7, 0xffff55, true);
                    context.drawText(client.textRenderer, "Distance : " + Math.round(savedEntity.distanceTo(player)) + "m" , (int) ((32f*hudTickDelta)/3f)-115, 16, 0xffffff, true);

                    context.drawGuiTexture(Identifier.ofVanilla("toast/advancement"), (int) ((32f*hudTickDelta)/3f)-160, 0, 160, 32);
                    if(LockOnUtil.getSpawnEgg(savedEntity) != null) {
                        context.drawGuiTexture(Identifier.ofVanilla("hud/hotbar_offhand_left"), (int) ((32f*hudTickDelta)/3f)-146, 4, 29, 24);
                        context.drawItem(LockOnUtil.getSpawnEgg(savedEntity), (int) ((32f*hudTickDelta)/3f)-143, 8);
                    } else if (savedEntity instanceof PlayerEntity) {
                        context.drawTexture(
                                Identifier.of(Camera_lock_on.MOD_ID, "textures/hud/player_face.png"),
                                (int) ((32f*hudTickDelta)/3f)-143,
                                8,
                                0,
                                0,
                                16,
                                16,
                                16,
                                16
                        );
                    }
                }

                RenderSystem.disableDepthTest();
                RenderSystem.depthMask(false);
                RenderSystem.enableBlend();

                if(lockedEntity != null && bl5) {
                    context.drawTexture(
                            Identifier.of(Camera_lock_on.MOD_ID, "textures/hud/lock_on_left.png"),
                            (int) ((context.getScaledWindowWidth()/2) - 7 - (savedEntity.distanceTo(player)/5)),
                            (context.getScaledWindowHeight()/2) - 8,
                            0.0f,
                            0.0f,
                            16,
                            16,
                            16,
                            16
                    );

                    context.drawTexture(
                            Identifier.of(Camera_lock_on.MOD_ID, "textures/hud/lock_on_right.png"),
                            (int) ((context.getScaledWindowWidth()/2) - 8 + (savedEntity.distanceTo(player)/5)),
                            (context.getScaledWindowHeight()/2) - 8,
                            0.0f,
                            0.0f,
                            16,
                            16,
                            16,
                            16
                    );

                    context.drawTexture(
                            Identifier.of(Camera_lock_on.MOD_ID, "textures/hud/lock_on_center.png"),
                            (context.getScaledWindowWidth()/2) - 8,
                            (context.getScaledWindowHeight()/2) - 8,
                            0.0f,
                            0.0f,
                            16,
                            16,
                            16,
                            16
                    );
                }

                RenderSystem.disableBlend();
                RenderSystem.depthMask(true);
                RenderSystem.enableDepthTest();
            }
        }
    }
}
