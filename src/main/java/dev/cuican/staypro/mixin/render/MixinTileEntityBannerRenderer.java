package dev.cuican.staypro.mixin.render;


import dev.cuican.staypro.Stay;
import dev.cuican.staypro.event.events.render.RenderBannerEvent;
import net.minecraft.client.renderer.tileentity.TileEntityBannerRenderer;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {TileEntityBannerRenderer.class})
public class MixinTileEntityBannerRenderer {
    @Inject(method = {"render"}, at = {@At(value = "INVOKE")}, cancellable = true)
    private void renderBanner(TileEntityBanner te, double x, double y, double z, float partialTicks, int destroyStage, float alpha, CallbackInfo ci) {
        RenderBannerEvent event = new RenderBannerEvent();
        Stay.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }
}
