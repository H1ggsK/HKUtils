package com.h1ggsk.hkutils.modules;

import com.h1ggsk.hkutils.HKUtils;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.Items;

public class AutoTotemLegit extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> always = sgGeneral.add(new BoolSetting.Builder()
        .name("always")
        .description("Always try to equip a totem when inventory is open.")
        .defaultValue(true)
        .build()
    );

    private int totems;
    private boolean needsTransfer;
    private boolean manuallyMoved;
    private Screen lastScreen;

    public AutoTotemLegit() {
        super(HKUtils.HKUtils, "auto-totem-legit", "Automatically equips a totem in your offhand when you open your inventory.");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;

        // Detect inventory close and reset manual override
        if (mc.currentScreen == null && lastScreen != null) manuallyMoved = false;
        lastScreen = mc.currentScreen;

        FindItemResult result = InvUtils.find(Items.TOTEM_OF_UNDYING);
        totems = result.count();

        if (totems <= 0) return;

        boolean offhandTotem = mc.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING);

        if (!offhandTotem && always.get() && !manuallyMoved) needsTransfer = true;

        if (needsTransfer && (mc.currentScreen != null && mc.currentScreen instanceof InventoryScreen)) {
            InvUtils.move().from(result.slot()).toOffhand();
            needsTransfer = false;
        }
    }

    public void notifyManualMove() {
        manuallyMoved = true;
    }

    @Override
    public String getInfoString() {
        return String.valueOf(totems);
    }
}
