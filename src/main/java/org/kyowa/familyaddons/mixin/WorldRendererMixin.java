package org.kyowa.familyaddons.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.chunk.ChunkSectionsToRender;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.kyowa.familyaddons.features.CorpseESP;
import org.kyowa.familyaddons.features.EntityHighlight;
import org.kyowa.familyaddons.features.KuudraCrateWaypoints;
import org.kyowa.familyaddons.features.NpcLocations;
import org.kyowa.familyaddons.features.Parkour;
import org.kyowa.familyaddons.features.PearlWaypoints;
import org.kyowa.familyaddons.features.PileWaypoints;
import org.kyowa.familyaddons.features.SupplyWaypoints;
import org.kyowa.familyaddons.features.Waypoints;
import org.kyowa.familyaddons.features.WorldScanner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class WorldRendererMixin {

    private final PoseStack fa_matrices = new PoseStack();

    // 26.1: LevelRenderer.render -> LevelRenderer.renderLevel. The 5th argument
    // (Matrix4fc) is the model-view matrix the method multiplies into the
    // model-view stack, i.e. the old `positionMatrix`.
    @Inject(method = "renderLevel", at = @At("RETURN"))
    private void onRenderLevel(
            GraphicsResourceAllocator allocator,
            DeltaTracker deltaTracker,
            boolean renderBlockOutline,
            CameraRenderState cameraRenderState,
            Matrix4fc modelViewMatrix,
            GpuBufferSlice fogBuffer,
            Vector4f fogColor,
            boolean renderSky,
            ChunkSectionsToRender chunkSectionsToRender,
            CallbackInfo ci
    ) {
        if (!Waypoints.INSTANCE.hasWaypoints() &&
                !CorpseESP.INSTANCE.hasCachedCorpses() &&
                !NpcLocations.INSTANCE.hasActiveWaypoints() &&
                !Parkour.INSTANCE.hasRings() &&
                !EntityHighlight.INSTANCE.hasHighlighted() &&
                !WorldScanner.INSTANCE.hasWaypoints() &&
                !KuudraCrateWaypoints.INSTANCE.hasCrates() &&
                !PearlWaypoints.INSTANCE.hasWaypoints() &&
                !PileWaypoints.INSTANCE.hasBeams() &&
                !SupplyWaypoints.INSTANCE.hasBeams()) return;

        Minecraft client = Minecraft.getInstance();
        MultiBufferSource.BufferSource consumers = client.renderBuffers().bufferSource();
        Camera camera = client.gameRenderer.getMainCamera();
        Vec3 cam = camera.position();

        fa_matrices.setIdentity();
        fa_matrices.mulPose(new Matrix4f(modelViewMatrix));

        Waypoints.INSTANCE.onWorldRender(fa_matrices, consumers, cam);
        CorpseESP.INSTANCE.onWorldRender(fa_matrices, consumers, cam);
        NpcLocations.INSTANCE.onWorldRender(fa_matrices, consumers, cam);
        Parkour.INSTANCE.onWorldRender(fa_matrices, consumers, cam);
        EntityHighlight.INSTANCE.onWorldRender(fa_matrices, consumers, cam);
        WorldScanner.INSTANCE.onWorldRender(fa_matrices, consumers, cam);
        KuudraCrateWaypoints.INSTANCE.onWorldRender(fa_matrices, camera);
        PearlWaypoints.INSTANCE.onWorldRender(fa_matrices, camera);
        PileWaypoints.INSTANCE.onWorldRender(fa_matrices, camera);
        SupplyWaypoints.INSTANCE.onWorldRender(fa_matrices, camera);

        consumers.endBatch();
    }
}
