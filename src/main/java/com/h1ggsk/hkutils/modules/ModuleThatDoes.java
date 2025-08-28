package com.h1ggsk.hkutils.modules;

import com.h1ggsk.hkutils.HKUtils;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;

public class ModuleThatDoes extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public ModuleThatDoes() {
        super(HKUtils.HKUtils, "       ", "A module that");
    }

    private final Setting<Boolean> boolSetting = sgGeneral.add(new BoolSetting.Builder()
        .name(" ")
        .description("Whether or not the module ")
        .defaultValue(true)
        .build()
    );

    @Override
    public void onActivate() {
        mc.getNarratorManager().destroy();
        this.toggle();
    }

    @Override
    public void onDeactivate() {
        // Cleanup if needed
    }
}
