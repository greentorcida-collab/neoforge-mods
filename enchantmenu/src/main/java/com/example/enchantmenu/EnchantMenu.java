package com.example.enchantmenu;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod("enchantmenu")
public class EnchantMenu {
    public EnchantMenu(IEventBus modEventBus) {
        modEventBus.addListener(EnchantMenu::registerPayloads);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(ApplyPayload.TYPE, ApplyPayload.STREAM_CODEC, EnchantMenu::handle);
    }

    private static int parseLevel(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static void handle(ApplyPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            ItemStack stack = player.getMainHandItem();
            if (stack.isEmpty()) {
                return;
            }

            Registry<Enchantment> registry =
                player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
            ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);

            String data = payload.data();
            if (!data.isEmpty()) {
                for (String part : data.split(";")) {
                    String[] pair = part.split("=");
                    if (pair.length != 2) {
                        continue;
                    }
                    ResourceLocation id = ResourceLocation.tryParse(pair[0]);
                    int level = parseLevel(pair[1]);
                    if (id == null || level <= 0) {
                        continue;
                    }
                    registry.get(ResourceKey.create(Registries.ENCHANTMENT, id)).ifPresent(holder -> {
                        int capped = Math.min(level, holder.value().getMaxLevel());
                        mutable.set(holder, capped);
                    });
                }
            }
            stack.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());
        });
    }
}
