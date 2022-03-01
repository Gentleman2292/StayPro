package dev.cuican.staypro.module.modules.render;


import com.google.common.collect.Sets;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.concurrent.event.Listener;
import dev.cuican.staypro.event.events.render.RenderEvent;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.*;
import dev.cuican.staypro.utils.graphics.RenderUtils3D;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @reworked by 0b00101010 on 14/01/2021
 */

@ModuleInfo(name = "HoleESP", category = Category.RENDER)
public class HoleESP extends Module {

    public final Setting<Integer> range = setting("Range", 5, 1, 20);
    public final Setting<String> customHoles = setting("Show", "Single", listOf("Single", "Double", "Custom"));
    public final Setting<String> type = setting("Render", "Both", listOf("Outline", "Fill", "Both"));
    public final Setting<String> mode = setting("Mode", "Air", listOf("Air", "Ground", "Flat", "Slab", "Double"));
    public final Setting<Boolean> hideOwn = setting("Hide Own", false);
    public final Setting<Boolean> flatOwn = setting("Flat Own", false);
    public final Setting<Double> slabHeight = setting("Slab Height", 0.5, 0.1, 1.5);
    public final Setting<Integer> width = setting("Width", 1, 1, 10);
    public final Setting<String> mode2 = setting("Color", "Bedrock", listOf("Bedrock", "Obsidian", "Custom"));


    public final Setting<Integer> bedrockColorHue = setting("Hue", 0, 0, 255).whenAtMode(mode2,"Bedrock");
    public final Setting<Integer> bedrockColorSaturation = setting("Saturation", 0, 0, 255).whenAtMode(mode2,"Bedrock");
    public final Setting<Integer> bedrockColorBrightness = setting("Brightness", 0, 0, 255).whenAtMode(mode2,"Bedrock");

    public final Setting<Integer> customColorHue = setting("Hue", 0, 0, 255).whenAtMode(mode2,"Custom");
    public final Setting<Integer> customColorSaturation = setting("Saturation", 0, 0, 255).whenAtMode(mode2,"Custom");
    public final Setting<Integer> customColorBrightness = setting("Brightness", 0, 0, 255).whenAtMode(mode2,"Custom");

    public final Setting<Integer> ObsidianColorHue = setting("Hue", 0, 0, 255).whenAtMode(mode2,"Obsidian");
    public final Setting<Integer> ObsidianColorSaturation = setting("Saturation", 0, 0, 255).whenAtMode(mode2,"Obsidian");
    public final Setting<Integer> ObsidianColorBrightness = setting("Brightness", 0, 0, 255).whenAtMode(mode2,"Obsidian");


    public final Setting<Integer> ufoAlpha = setting("UFOAlpha", 255, 0, 255);

    private GSColor getbedrock(){
        return new GSColor(bedrockColorHue.getValue(),bedrockColorSaturation.getValue(),bedrockColorBrightness.getValue());
    }

    private GSColor getcustom(){
        return new GSColor(customColorHue.getValue(),customColorSaturation.getValue(),customColorBrightness.getValue());
    }

    private GSColor getObsidian(){
        return new GSColor(ObsidianColorHue.getValue(),ObsidianColorSaturation.getValue(),ObsidianColorBrightness.getValue());
    }


    private ConcurrentHashMap<AxisAlignedBB, GSColor> holes;

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) {
            return;
        }

        if (holes == null) {
            holes = new ConcurrentHashMap<>();
        } else {
            holes.clear();
        }
        int range = (int) Math.ceil(this.range.getValue());
        HashSet<BlockPos> possibleHoles = Sets.newHashSet();
        List<BlockPos> blockPosList = EntityUtil.getSphere(PlayerUtil.getPlayerPos(), range, range, false, true, 0);

        for (BlockPos pos : blockPosList) {

            if (!mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR)) {
                continue;
            }

            if (mc.world.getBlockState(pos.add(0, -1, 0)).getBlock().equals(Blocks.AIR)) {
                continue;
            }
            if (!mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR)) {
                continue;
            }

            if (mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR)) {
                possibleHoles.add(pos);
            }
        }

        possibleHoles.forEach(pos -> {
            HoleUtil.HoleInfo holeInfo = HoleUtil.isHole(pos, false, false);
            HoleUtil.HoleType holeType = holeInfo.getType();
            if (holeType != HoleUtil.HoleType.NONE) {

                HoleUtil.BlockSafety holeSafety = holeInfo.getSafety();
                AxisAlignedBB centreBlocks = holeInfo.getCentre();

                if (centreBlocks == null)
                    return;

                GSColor colour;

                if (holeSafety == HoleUtil.BlockSafety.UNBREAKABLE) {

                    colour =  new GSColor(getbedrock(),255);
                } else {
                    colour = new GSColor(getObsidian(),255);
                }

                if (holeType == HoleUtil.HoleType.CUSTOM) {
                    colour = new GSColor(getcustom(),255);
                }

                String mode = customHoles.getValue();
                if (mode.equalsIgnoreCase("Custom") && (holeType == HoleUtil.HoleType.CUSTOM || holeType == HoleUtil.HoleType.DOUBLE)) {
                    holes.put(centreBlocks, colour);
                } else if (mode.equalsIgnoreCase("Double") && holeType == HoleUtil.HoleType.DOUBLE) {
                    holes.put(centreBlocks, colour);
                } else if (holeType == HoleUtil.HoleType.SINGLE) {
                    holes.put(centreBlocks, colour);
                }
            }
        });
    }


    @Listener
    public void onRenderWorld(RenderEvent event) {
        if (mc.player == null || mc.world == null || holes == null || holes.isEmpty()) {
            return;
        }

        holes.forEach(this::renderHoles);
    }

    private void renderHoles(AxisAlignedBB hole, GSColor color) {
        switch (type.getValue()) {
            case "Outline": {
                renderOutline(hole, color);
                break;
            }
            case "Fill": {
                renderFill(hole, color);
                break;
            }
            case "Both": {
                renderOutline(hole, color);
                renderFill(hole, color);
                break;
            }
        }
    }

    private void renderFill(AxisAlignedBB hole, GSColor color) {
        GSColor fillColor = new GSColor(color, 50);
        int ufoAlpha = (this.ufoAlpha.getValue() * 50) / 255;

        if (hideOwn.getValue() && hole.intersects(mc.player.getEntityBoundingBox())) return;

        switch (mode.getValue()) {
            case "Air": {
                if (flatOwn.getValue() && hole.intersects(mc.player.getEntityBoundingBox())) {
                    RenderUtil.drawBox(hole, true, 1, fillColor, ufoAlpha, GeometryMasks.Quad.DOWN);
                } else {
                    RenderUtil.drawBox(hole, true, 1, fillColor, ufoAlpha, GeometryMasks.Quad.ALL);
                }
                break;
            }
            case "Ground": {
                RenderUtil.drawBox(hole.offset(0, -1, 0), true, 1, new GSColor(fillColor, ufoAlpha), fillColor.getAlpha(), GeometryMasks.Quad.ALL);
                break;
            }
            case "Flat": {
                RenderUtil.drawBox(hole, true, 1, fillColor, ufoAlpha, GeometryMasks.Quad.DOWN);
                break;
            }
            case "Slab": {
                if (flatOwn.getValue() && hole.intersects(mc.player.getEntityBoundingBox())) {
                    RenderUtil.drawBox(hole, true, 1, fillColor, ufoAlpha, GeometryMasks.Quad.DOWN);
                } else {
                    RenderUtil.drawBox(hole, false, slabHeight.getValue(), fillColor, ufoAlpha, GeometryMasks.Quad.ALL);
                }
                break;
            }
            case "Double": {
                if (flatOwn.getValue() && hole.intersects(mc.player.getEntityBoundingBox())) {
                    RenderUtil.drawBox(hole, true, 1, fillColor, ufoAlpha, GeometryMasks.Quad.DOWN);
                } else {
                    RenderUtil.drawBox(hole.setMaxY(hole.maxY + 1), true, 2, fillColor, ufoAlpha, GeometryMasks.Quad.ALL);
                }
                break;
            }
        }
    }

    private void renderOutline(AxisAlignedBB hole, GSColor color) {
        GSColor outlineColor = new GSColor(color, 255);

        if (hideOwn.getValue() && hole.intersects(mc.player.getEntityBoundingBox())) return;

        switch (mode.getValue()) {
            case "Air": {
                if (flatOwn.getValue() && hole.intersects(mc.player.getEntityBoundingBox())) {
                    RenderUtil.drawBoundingBoxWithSides(hole, width.getValue(), outlineColor, ufoAlpha.getValue(), GeometryMasks.Quad.DOWN);
                } else {
                    RenderUtil.drawBoundingBox(hole, width.getValue(), outlineColor, ufoAlpha.getValue());
                }
                break;
            }
            case "Ground": {
                RenderUtil.drawBoundingBox(hole.offset(0, -1, 0), width.getValue(), new GSColor(outlineColor, ufoAlpha.getValue()), outlineColor.getAlpha());
                break;
            }
            case "Flat": {
                RenderUtil.drawBoundingBoxWithSides(hole, width.getValue(), outlineColor, ufoAlpha.getValue(), GeometryMasks.Quad.DOWN);
                break;
            }
            case "Slab": {
                if (this.flatOwn.getValue() && hole.intersects(mc.player.getEntityBoundingBox())) {
                    RenderUtil.drawBoundingBoxWithSides(hole, width.getValue(), outlineColor, ufoAlpha.getValue(), GeometryMasks.Quad.DOWN);
                } else {
                    RenderUtil.drawBoundingBox(hole.setMaxY(hole.minY + slabHeight.getValue()), width.getValue(), outlineColor, ufoAlpha.getValue());
                }
                break;
            }
            case "Double": {
                if (this.flatOwn.getValue() && hole.intersects(mc.player.getEntityBoundingBox())) {
                    RenderUtil.drawBoundingBoxWithSides(hole, width.getValue(), outlineColor, ufoAlpha.getValue(), GeometryMasks.Quad.DOWN);
                } else {
                    RenderUtil.drawBoundingBox(hole.setMaxY(hole.maxY + 1), width.getValue(), outlineColor, ufoAlpha.getValue());
                }
                break;
            }
        }
    }
}