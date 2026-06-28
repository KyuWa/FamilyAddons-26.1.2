package org.kyowa.familyaddons.features

import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.rendertype.LayeringTransform
import net.minecraft.client.renderer.rendertype.OutputTarget
import net.minecraft.client.renderer.rendertype.RenderSetup
import net.minecraft.client.renderer.rendertype.RenderType

object FamilyRenderTypes {

    val LINES: RenderType by lazy {
        RenderType.create(
            "familyaddons_lines",
            RenderSetup.builder(RenderPipelines.LINES)
                .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                .setOutputTarget(OutputTarget.MAIN_TARGET)
                .createRenderSetup()
        )
    }

    // 26.1 assembles the vanilla LINES pipeline from private shader snippets, so a
    // depth-test-disabled clone can't be declared without re-creating the whole
    // pipeline. LINES_DEPTH_BIAS biases depth toward the camera so the tracer draws
    // on top of world geometry in practice (the original used a true no-depth pipeline).
    val LINES_NO_DEPTH: RenderType by lazy {
        RenderType.create(
            "familyaddons_lines_no_depth",
            RenderSetup.builder(RenderPipelines.LINES_DEPTH_BIAS)
                .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                .setOutputTarget(OutputTarget.MAIN_TARGET)
                .createRenderSetup()
        )
    }

    // Solid-color translucent quad layer for beacon-beam columns. 26.1 dropped the
    // public textured beacon-beam RenderType factory, so the beam is drawn as
    // position+color quads (no texture) via the DEBUG_QUADS pipeline.
    val BEAM: RenderType by lazy {
        RenderType.create(
            "familyaddons_beam",
            RenderSetup.builder(RenderPipelines.DEBUG_QUADS)
                .setOutputTarget(OutputTarget.MAIN_TARGET)
                .createRenderSetup()
        )
    }
}
