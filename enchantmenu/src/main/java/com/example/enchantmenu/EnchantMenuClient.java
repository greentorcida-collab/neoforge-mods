package com.example.enchantmenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = "enchantmenu", dist = Dist.CLIENT)
public class EnchantMenuClient {
    private static final int SIZE = 18;

    public EnchantMenuClient(IEventBus modEventBus) {
        NeoForge.EVENT_BUS.addListener(EnchantMenuClient::onRender);
        NeoForge.EVENT_BUS.addListener(EnchantMenuClient::onClick);
    }

    private static int buttonX(AbstractContainerScreen<?> screen) {
        int x = screen.getGuiLeft() + screen.getXSize() + 4;
        if (x + SIZE > screen.width) {
            x = screen.getGuiLeft() - SIZE - 4;
        }
        return x;
    }

    private static int buttonY(AbstractContainerScreen<?> screen) {
        return screen.getGuiTop() + 4;
    }

    private static boolean inside(AbstractContainerScreen<?> screen, double mx, double my) {
        int x = buttonX(screen);
        int y = buttonY(screen);
        return mx >= x && mx < x + SIZE && my >= y && my < y + SIZE;
    }

    private static void onRender(ScreenEvent.Render.Post event) {
        if (!(event.getScreen() instanceof InventoryScreen screen)) {
            return;
        }
        GuiGraphics graphics = event.getGuiGraphics();
        int x = buttonX(screen);
        int y = buttonY(screen);
        graphics.fill(x, y, x + SIZE, y + SIZE, 0xFF373737);
        graphics.fill(x + 1, y + 1, x + SIZE - 1, y + SIZE - 1, 0xFF7A3FBF);
        graphics.drawString(Minecraft.getInstance().font, "E", x + 6, y + 5, 0xFFFFFFFF);
    }

    private static void onClick(ScreenEvent.MouseButtonPressed.Pre event) {
        if (!(event.getScreen() instanceof InventoryScreen screen)) {
            return;
        }
        if (inside(screen, event.getMouseX(), event.getMouseY())) {
            Minecraft.getInstance().setScreen(new EnchantScreen());
            event.setCanceled(true);
        }
    }
}
