package dev.cuican.staypro.module.modules.client;

import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.common.annotations.Parallel;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;

@Parallel
@ModuleInfo(name = "GUISetting", category = Category.CLIENT, description = "Settings of GUI")
public class GUISetting extends Module {

    public static GUISetting instance;

    public GUISetting() {
        instance = this;
    }

    public Setting<Boolean> rainbow = setting("Rainbow", false, "Rainbow color");
    public Setting<Float> rainbowSpeed = setting("Rainbow Speed", 1.0f, 0.0f, 30.0f, "Rainbow color change speed").whenTrue(rainbow);
    public Setting<Float> rainbowSaturation = setting("Saturation", 0.75f, 0.0f, 1.0f, "Rainbow color saturation").whenTrue(rainbow);
    public Setting<Float> rainbowBrightness = setting("Brightness", 0.8f, 0.0f, 1.0f, "Rainbow color brightness").whenTrue(rainbow);
    public Setting<Integer> red = setting("Red", 200, 0, 255, "Red").whenFalse(rainbow);
    public Setting<Integer> green = setting("Green", 120, 0, 255, "Green").whenFalse(rainbow);
    public Setting<Integer> blue = setting("Blue", 180, 0, 255, "Blue").whenFalse(rainbow);
    public Setting<Integer> transparency = setting("Transparency", 255, 0, 255, "The transparency(Alpha)");
    public Setting<Boolean> particle = setting("Particle", true, "Display particles on background");
    public Setting<String> background = setting("BG", "Blur", listOf("Shadow", "Blur", "Both", "None"), "Background effect");

    public Setting<Boolean> transparencys = setting("Intransparency", true, "transparency color");
    public Setting<Integer> redH = setting("backplaneRed", 200, 0, 255, "Red").whenTrue(transparencys);
    public Setting<Integer> greenH = setting("backplaneGreen", 120, 0, 255, "Green").whenTrue(transparencys);
    public Setting<Integer> blueH = setting("backplaneBlue", 180, 0, 255, "Blue").whenTrue(transparencys);
    public Setting<Integer> transparencyH= setting("backplaneTransparency", 1, 0, 10, "Blue").whenTrue(transparencys);

    @Override
    public void onEnable() {
        disable();
    }

}
