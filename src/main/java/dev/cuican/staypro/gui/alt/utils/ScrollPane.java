package dev.cuican.staypro.gui.alt.utils;


import dev.cuican.staypro.utils.graphics.RenderUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL41;

import java.awt.*;

public class ScrollPane
extends Pane {
    private static final double SCROLL_AMOUNT = 0.25;
    private int scrollOffset = 0;
    private boolean hovered = false;
    private int realHeight;

    public ScrollPane(ILayoutManager layoutManager) {
        super(layoutManager);
    }

    @Override
    public void updateLayout() {
        this.updateLayout(this.getWidth(), Integer.MAX_VALUE, true);
    }

    @Override
    protected void updateLayout(int width, int height, boolean changeHeight) {
        super.updateLayout(width, height, false);
        this.realHeight = this.layout.getMaxHeight();
        this.validateOffset();
    }

    @Override
    public void render() {
        GL41.glClearDepthf(1.0f);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glColorMask(false, false, false, false);
        GL11.glDepthFunc(513);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);

        drawRect(7, this.x, this.y, this.getWidth(), this.getHeight(), Color.WHITE.getRGB());

        GL11.glColorMask(true, true, true, true);
        GL11.glDepthMask(true);
        GL11.glDepthFunc(514);

        super.render();

        GL41.glClearDepthf(1.0f);
        GL11.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glClear(1280);
        GL11.glDisable(2929);
        GL11.glDepthFunc(515);
        GL11.glDepthMask(false);

        int maxY = this.realHeight - this.getHeight();
        if (maxY > 0) {
            int sliderHeight = (int)((double)this.getHeight() / (double)this.realHeight * (double)this.getHeight());
            int sliderWidth = 3;
            RenderUtils.drawRoundedRectangle(this.x + this.getWidth() - sliderWidth, (double)this.y + (double)(this.getHeight() - sliderHeight) * ((double)this.scrollOffset / (double)maxY), sliderWidth, sliderHeight, 1.0, Color.white);
        }
    }

    public static void drawRect(int mode, int x, int y, int width, int height, int color) {
        float f = (float)(color >> 16 & 0xFF) / 255.0f;
        float f1 = (float)(color >> 8 & 0xFF) / 255.0f;
        float f2 = (float)(color & 0xFF) / 255.0f;
        float f3 = (float)(color >> 24 & 0xFF) / 255.0f;
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3553);
        GL11.glColor4f((float)f, (float)f1, (float)f2, (float)f3);
        GL11.glBegin((int)mode);
        GL11.glVertex2d((double)(x + width), (double)y);
        GL11.glVertex2d((double)x, (double)y);
        GL11.glVertex2d((double)x, (double)(y + height));
        GL11.glVertex2d((double)(x + width), (double)(y + height));
        GL11.glEnd();
        GL11.glEnable((int)3553);
        GL11.glDisable((int)3042);
    }


    @Override
    protected void updateComponentLocation() {
        for (AbstractComponent component : this.components) {
            int[] ints = this.componentLocations.get(component);
            if (ints == null) {
                this.updateLayout();
                this.updateComponentLocation();
                return;
            }
            component.setX(this.x + ints[0]);
            component.setY(this.y + ints[1] - this.scrollOffset);
        }
    }

    private void updateHovered(int x, int y, boolean offscreen) {
        this.hovered = !offscreen && x >= this.x && y >= this.y && x <= this.x + this.getWidth() && y <= this.y + this.getHeight();
    }

    @Override
    public boolean mouseWheel(int change) {
        this.scrollOffset = (int)((double)this.scrollOffset - (double)change * 0.25);
        this.validateOffset();
        return super.mouseWheel(change);
    }

    private void validateOffset() {
        if (this.scrollOffset > this.realHeight - this.getHeight()) {
            this.scrollOffset = this.realHeight - this.getHeight();
        }
        if (this.scrollOffset < 0) {
            this.scrollOffset = 0;
        }
    }

    @Override
    public boolean mouseMove(int x, int y, boolean offscreen) {
        this.updateHovered(x, y, offscreen);
        return super.mouseMove(x, y, offscreen || x < this.x || y < this.y || x > this.x + this.getWidth() || y > this.y + this.getHeight());
    }

    @Override
    public boolean mousePressed(int button, int x, int y, boolean offscreen) {
        return super.mousePressed(button, x, y, offscreen || x < this.x || y < this.y || x > this.x + this.getWidth() || y > this.y + this.getHeight());
    }

    @Override
    public boolean mouseReleased(int button, int x, int y, boolean offscreen) {
        return super.mouseReleased(button, x, y, offscreen || x < this.x || y < this.y || x > this.x + this.getWidth() || y > this.y + this.getHeight());
    }

    @Override
    public void addComponent(AbstractComponent component) {
        super.addComponent(component);
    }
}

