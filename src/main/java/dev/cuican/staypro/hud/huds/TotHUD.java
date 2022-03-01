package dev.cuican.staypro.hud.huds;

import dev.cuican.staypro.client.FontManager;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.hud.HUDModule;
import dev.cuican.staypro.module.Category;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

@ModuleInfo(name = "TotemHUD", category = Category.HUD)
public class TotHUD extends HUDModule {




    @Override
    public void onHUDRender(ScaledResolution resolution) {
        renderTotemHUD();
    }



    private static final ItemStack totem = new ItemStack(Items.TOTEM_OF_UNDYING);



    public void renderTotemHUD() {
        int totems = mc.player.inventory.mainInventory.stream().filter(itemStack -> (itemStack.getItem() == Items.TOTEM_OF_UNDYING)).mapToInt(ItemStack::getCount).sum();
        if (mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
            totems += mc.player.getHeldItemOffhand().getCount();
        }
        if (totems > 0) {
            GlStateManager.enableTexture2D();
            GlStateManager.enableDepth();
            mc.getRenderItem().zLevel = 200.0F;
            mc.getRenderItem().renderItemAndEffectIntoGUI(totem, x, y);
            mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, totem, x, y, "");
            mc.getRenderItem().zLevel = 0.0F;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            FontManager.fontRenderer.drawStringWithShadow(totems + "", (x + 19 - 2 - mc.fontRenderer.getStringWidth(totems + "")), (y + 9), 16777215);
            GlStateManager.enableDepth();
            GlStateManager.disableLighting();
            width = 16;
            height = 16;

        }
    }



}
