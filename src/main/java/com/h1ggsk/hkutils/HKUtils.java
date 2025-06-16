package com.h1ggsk.hkutils;

import com.h1ggsk.hkutils.commands.H1ggsKPing;
import com.h1ggsk.hkutils.modules.AutoShearPlus;
import com.h1ggsk.hkutils.modules.MaceDMG;
import com.h1ggsk.hkutils.modules.WeatherChanger;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;

public class HKUtils extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category HKUtils = new Category("HKUtils");

    @Override
    public void onInitialize() {
        LOG.info("Initializing HKUtils Addon");

        // Commands
        Commands.add(new H1ggsKPing());

        // Modules
        Modules.get().add(new AutoShearPlus());
        Modules.get().add(new MaceDMG());
        Modules.get().add(new WeatherChanger());
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(HKUtils);
    }

    @Override
    public String getPackage() {
        return "com.h1ggsk.hkutils";
    }

    @Override
    public GithubRepo getRepo() {
        return new GithubRepo("H1ggsK", "HKUtils");
    }
}
