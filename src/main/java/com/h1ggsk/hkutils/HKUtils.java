package com.h1ggsk.hkutils;

import com.h1ggsk.hkutils.commands.H1ggsKPing;
import com.h1ggsk.hkutils.commands.LifeAdvice;
import com.h1ggsk.hkutils.commands.Troll1;
import com.h1ggsk.hkutils.modules.*;
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
        Commands.add(new LifeAdvice());
        Commands.add(new Troll1());

        // Modules
        Modules.get().add(new AutoShearPlus());
        Modules.get().add(new AutoTotemLegit());
        Modules.get().add(new ForwardChat());
        Modules.get().add(new FPSLimiter());
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
