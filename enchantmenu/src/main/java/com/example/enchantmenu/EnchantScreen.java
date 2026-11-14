package com.example.enchantmenu;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.neoforge.network.PacketDistributor;

public class EnchantScreen extends Screen {
    private static final int PER_PAGE = 8;

    private final List<Holder<Enchantment>> all = new ArrayList<>();
    private final Map<String, Integer> levels = new LinkedHashMap<>();
    private int page = 0;

    public EnchantScreen() {
        super(Component.literal("Enchant"));
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            mc.level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                .listElements().forEach(all::add);
        }
        if (mc.player != null) {
            ItemStack stack = mc.player.getMainHandItem();
            if (!stack.isEmpty()) {
                ItemEnchantments current =
                    stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
                for (Holder<Enchantment> holder : current.keySet()) {
                    levels.put(idOf(holder), current.getLevel(holder));
                }
            }
        }
    }

    private static String idOf(Holder<Enchantment> holder) {
        return holder.unwrapKey().map(key -> key.location().toString()).orElse("");
    }

    private int pageCount() {
        return Math.max(1, (all.size() + PER_PAGE - 1) / PER_PAGE);
    }

    private Component label(Holder<Enchantment> holder) {
        int level = levels.getOrDefault(idOf(holder), 0);
        int max = holder.value().getMaxLevel();
        return holder.value().description().copy().append(Component.literal(": " + level + "/" + max));
    }

    private void changePage(int delta) {
        page = Math.floorMod(page + delta, pageCount());
        this.rebuildWidgets();
    }

    private void apply() {
        Minecraft mc = this.minecraft;
        if (mc == null || mc.player == null || mc.player.getMainHandItem().isEmpty()) {
            return;
        }
        StringBuilder data = new StringBuilder();
        for (Map.Entry<String, Integer> entry : levels.entrySet()) {
            if (entry.getValue() > 0 && !entry.getKey().isEmpty()) {
                if (data.length() > 0) {
                    data.append(';');
                }
                data.append(entry.getKey()).append('=').append(entry.getValue());
            }
        }
        PacketDistributor.sendToServer(new ApplyPayload(data.toString()));
        mc.setScreen(null);
    }

    @Override
    protected void init() {
        int colWidth = 150;
        int center = this.width / 2;
        int left = center - colWidth - 5;
        int right = center + 5;
        int start = page * PER_PAGE;

        for (int i = 0; i < PER_PAGE; i++) {
            int index = start + i;
            if (index >= all.size()) {
                break;
            }
            Holder<Enchantment> holder = all.get(index);
            int x = (i % 2 == 0) ? left : right;
            int y = 36 + (i / 2) * 24;
            this.addRenderableWidget(Button.builder(label(holder), button -> {
                int max = holder.value().getMaxLevel();
                int next = (levels.getOrDefault(idOf(holder), 0) + 1) % (max + 1);
                levels.put(idOf(holder), next);
                button.setMessage(label(holder));
            }).bounds(x, y, colWidth, 20).build());
        }

        int bottom = this.height - 28;
        this.addRenderableWidget(Button.builder(Component.literal("<"), button -> changePage(-1))
            .bounds(center - 155, bottom, 30, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal(">"), button -> changePage(1))
            .bounds(center - 120, bottom, 30, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("Terapkan"), button -> apply())
            .bounds(center - 80, bottom, 120, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("Tutup"),
            button -> this.minecraft.setScreen(null))
            .bounds(center + 45, bottom, 110, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        ItemStack held = this.minecraft != null && this.minecraft.player != null
            ? this.minecraft.player.getMainHandItem() : ItemStack.EMPTY;
        Component header = held.isEmpty()
            ? Component.literal("Pegang alat di tangan dulu") : held.getHoverName();
        graphics.drawCenteredString(this.font, header, this.width / 2, 10, 0xFFFFFFFF);
        graphics.drawCenteredString(this.font,
            Component.literal("Halaman " + (page + 1) + "/" + pageCount()), this.width / 2, 22, 0xFFAAAAAA);
    }
}
