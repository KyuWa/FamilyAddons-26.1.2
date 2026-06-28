package org.kyowa.familyaddons.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.kyowa.familyaddons.features.PearlWaypoints;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Reads Hypixel's progress title (`[some prefix] XX%`) by intercepting
 * {@code Gui.extractTitle} at HEAD and reading the cached {@code title} field
 * directly. Deduplicates per identical title text so the parse logic fires once
 * per unique title, not every render frame.
 *
 * 26.1 names:
 *   - Gui#title  (field — the cached title Component)
 *   - Gui#extractTitle(GuiGraphicsExtractor, DeltaTracker)  (the 26.1 extract-
 *     pipeline replacement for the old renderTitleAndSubtitle)
 */
@Mixin(Gui.class)
public class PearlInGameHudMixin {

    @Shadow @Nullable
    private Component title;

    @Unique private String fa$lastTitleText = "";

    @Inject(method = "extractTitle", at = @At("HEAD"))
    private void familyaddons$onRenderTitle(
            GuiGraphicsExtractor context,
            DeltaTracker tickCounter,
            CallbackInfo ci
    ) {
        try {
            Component t = this.title;
            if (t == null) return;
            String raw = t.getString();
            if (raw == null || raw.isEmpty()) return;
            if (raw.equals(fa$lastTitleText)) return;
            fa$lastTitleText = raw;
            PearlWaypoints.INSTANCE.onTitle(raw);
        } catch (Throwable ignored) {
            // Never let this propagate — would break HUD rendering.
        }
    }
}
