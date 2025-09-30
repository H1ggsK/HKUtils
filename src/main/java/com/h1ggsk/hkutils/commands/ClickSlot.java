package com.h1ggsk.hkutils.commands;

import com.h1ggsk.hkutils.mixin.ComponentHasherNetworkHandlerAccessor;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.screen.sync.ComponentChangesHash;
import net.minecraft.screen.sync.ItemStackHash;

public class ClickSlot extends Command {

    public ClickSlot() {
        super("clickslot", "Clicks the given slot number using a ClickSlotC2SPacket");
    }

    public static int pickSlot = -1;

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("slot", IntegerArgumentType.integer(0)).executes(context -> {

            int slot = context.getArgument("slot", Integer.class);

            boolean ok = pickSwitch(slot);
            if (ok) ChatUtils.info("Clicked slot " + slot + " (packet sent).");
            else ChatUtils.error("Failed to click slot " + slot + " — check container / connection.");

            return SINGLE_SUCCESS;
        }));
    }

    /**
     * Sends a ClickSlotC2SPacket for the given slot index.
     * Returns true if we successfully built & sent the packet.
     */
    public static boolean pickSwitch(int slot) {
        if (slot < 0) return false;
        if (mc == null || mc.player == null || mc.getNetworkHandler() == null) return false;
        if (mc.player.currentScreenHandler == null) return false;

        pickSlot = slot;

        try {
            try {
                if (mc.player.currentScreenHandler.slots != null && slot >= mc.player.currentScreenHandler.slots.size()) {
                    ChatUtils.error("Slot index out of range for the current container.");
                    return false;
                }
            } catch (Exception ignored) {
            }

            int syncId = mc.player.currentScreenHandler.syncId;
            int revision = mc.player.currentScreenHandler.getRevision();

            int button = 0;
            SlotActionType actionType = SlotActionType.PICKUP;

            ItemStack clickedStack = mc.player.currentScreenHandler.getSlot(slot).getStack();
            ItemStack cursorStack = mc.player.currentScreenHandler.getCursorStack();

            Int2ObjectMap<ItemStackHash> modifiedStacks = new Int2ObjectOpenHashMap<>();

            ComponentChangesHash.ComponentHasher componentHasher =
                ((ComponentHasherNetworkHandlerAccessor) mc.getNetworkHandler()).getComponentHasher();

            ItemStackHash cursorHash = ItemStackHash.fromItemStack(cursorStack, componentHasher);

            ClickSlotC2SPacket packet = new ClickSlotC2SPacket(
                syncId,
                revision,
                (short) slot,
                (byte) button,
                actionType,
                modifiedStacks,
                cursorHash
            );

            mc.getNetworkHandler().sendPacket(packet);
            return true;
        } catch (IndexOutOfBoundsException e) {
            ChatUtils.error("Slot index out of bounds for current container.");
            return false;
        } catch (Exception e) {
            ChatUtils.error("Exception while sending ClickSlot packet: " + e.getMessage());
            return false;
        }
    }
}
