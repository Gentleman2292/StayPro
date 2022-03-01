package dev.cuican.staypro.mixin.accessor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DestroyBlockProgress;
import net.minecraft.client.renderer.RenderGlobal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(RenderGlobal.class)
public interface AccessorIrendGlobal {
    @Accessor(value="damagedBlocks")
    public abstract Map<Integer, DestroyBlockProgress> getDamagedBlocks();
}
