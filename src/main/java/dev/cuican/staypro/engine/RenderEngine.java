package dev.cuican.staypro.engine;

import dev.cuican.staypro.concurrent.TaskManager;
import dev.cuican.staypro.concurrent.utils.ThreadUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;

public class RenderEngine {

    public static RenderEngine INSTANCE = new RenderEngine();
    private long lastUpdateTime = System.currentTimeMillis();

    public RenderEngine() {
        new Thread(() -> {
            while (true) {
                if (Minecraft.getMinecraft().player != null) onUpdate();
                ThreadUtil.delay();
            }
        }).start();
    }

    private final List<AsyncRenderer> subscribedAsyncRenderers = new ArrayList<>();

    public static void subscribe(AsyncRenderer asyncRenderer) {
        synchronized (INSTANCE.subscribedAsyncRenderers) {
            if (!INSTANCE.subscribedAsyncRenderers.contains(asyncRenderer))
                INSTANCE.subscribedAsyncRenderers.add(asyncRenderer);
        }
    }

    public static void unsubscribe(AsyncRenderer asyncRenderer) {
        synchronized (INSTANCE.subscribedAsyncRenderers) {
            INSTANCE.subscribedAsyncRenderers.remove(asyncRenderer);
        }
    }

    private void onUpdate() {
        if (System.currentTimeMillis() - lastUpdateTime >= 15) {
            lastUpdateTime = System.currentTimeMillis();
            ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
            int mouseX = Mouse.getX();
            int mouseY = Mouse.getY();
            synchronized (subscribedAsyncRenderers) {
                TaskManager.runBlocking(content -> subscribedAsyncRenderers.forEach(asyncRenderer -> content.launch(() -> asyncRenderer.onUpdate0(resolution, mouseX, mouseY))));
            }
        }
    }


}
