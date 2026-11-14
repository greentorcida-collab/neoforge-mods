package com.example.trashslot;

import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod("trashslot")
public class TrashSlot {
    public TrashSlot(IEventBus modEventBus) {
        modEventBus.addListener(TrashSlot::registerPayloads);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(TrashPayload.TYPE, TrashPayload.STREAM_CODEC, TrashSlot::handle);
    }

    private static void handle(TrashPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> context.player().containerMenu.setCarried(ItemStack.EMPTY));
    }
}
