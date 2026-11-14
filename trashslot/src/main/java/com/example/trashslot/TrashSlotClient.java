package com.example.trashslot;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;

@Mod(value = "trashslot", dist = Dist.CLIENT)
public class TrashSlotClient {
    private static final int SIZE = 18;

    public TrashSlotClient(IEventBus modEventBus) {
        NeoForge.EVENT_BUS.addListener(TrashSlotClient::onRender);
        NeoForge.EVENT_BUS.addListener(TrashSlotClient::onClick);
    }

    private static int slotX(AbstractContainerScreen<?> screen) {
        int x = screen.getGuiLeft() + screen.getXSize() + 4;
        if (x + SIZE > screen.width) {
            x = screen.getGuiLeft() - SIZE - 4;
        }
        return x;
    }

    private static int slotY(AbstractContainerScreen<?> screen) {
        return screen.getGuiTop() + screen.getYSize() - 24;
    }

    private static boolean inside(AbstractContainerScreen<?> screen, double mx, double my) {
        int x = slotX(screen);
        int y = slotY(screen);
        return mx >= x && mx < x + SIZE && my >= y && my < y + SIZE;
    }

    private static void onRender(ScreenEvent.Render.Post event) {
        if (!(event.getScreen() instanceof AbstractContainerScreen<?> screen)
            || screen instanceof CreativeModeInventoryScreen) {
            return;
        }
        GuiGraphics graphics = event.getGuiGraphics();
        int x = slotX(screen);
        int y = slotY(screen);
        graphics.fill(x, y, x + SIZE, y + SIZE, 0xFF373737);
        graphics.fill(x + 1, y + 1, x + SIZE - 1, y + SIZE - 1, 0xFF8B8B8B);
        graphics.drawString(Minecraft.getInstance().font, "X", x + 6, y + 5, 0xFFFF5555);
    }

    private static void onClick(ScreenEvent.MouseButtonPressed.Pre event) {
        if (!(event.getScreen() instanceof AbstractContainerScreen<?> screen)
            || screen instanceof CreativeModeInventoryScreen) {
            return;
        }
        if (!inside(screen, event.getMouseX(), event.getMouseY())) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && !mc.player.containerMenu.getCarried().isEmpty()) {
            mc.player.containerMenu.setCarried(ItemStack.EMPTY);
            PacketDistributor.sendToServer(new TrashPayload());
        }
        event.setCanceled(true);
    }
}
