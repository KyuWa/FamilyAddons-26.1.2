package org.kyowa.familyaddons.mixin;

import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import org.kyowa.familyaddons.features.PearlWaypoints;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Feeds Hypixel's grab progress text (`[prefix] XX%`) to PearlWaypaypoints.
 *
 * Hooks the three places the server can put it — title, subtitle and the
 * action bar (overlay message) — at the moment the packet sets them, rather
 * than polling a cached field per frame. Deduplicated per identical text so
 * the parser fires once per distinct message.
 */
@Mixin(Gui.class)
public class PearlInGameHudMixin {

    @Unique private String fa$lastText = "";

    @Unique
    private void fa$feed(Component c) {
        try {
            if (c == null) return;
            String raw = c.getString();
            if (raw == null || raw.isEmpty() || raw.equals(fa$lastText)) return;
            fa$lastText = raw;
            PearlWaypoints.INSTANCE.onTitle(raw);
        } catch (Throwable ignored) {
            // Never let this propagate — would break HUD rendering.
        }
    }

    @Inject(method = "setTitle", at = @At("HEAD"))
    private void familyaddons$onSetTitle(Component title, CallbackInfo ci) { fa$feed(title); }

    @Inject(method = "setSubtitle", at = @At("HEAD"))
    private void familyaddons$onSetSubtitle(Component subtitle, CallbackInfo ci) { fa$feed(subtitle); }

    @Inject(method = "setOverlayMessage", at = @At("HEAD"))
    private void familyaddons$onSetOverlay(Component message, boolean animateColor, CallbackInfo ci) { fa$feed(message); }
}
