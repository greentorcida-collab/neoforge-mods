package com.example.nodeath;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@Mod("nodeath")
public class NoDeath {
    public NoDeath(IEventBus modEventBus) {
        NeoForge.EVENT_BUS.addListener(NoDeath::onIncomingDamage);
    }

    private static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer)) {
            return;
        }
        DamageSource source = event.getSource();
        // Void dan perintah /kill tetap diizinkan
        if (source.is(DamageTypes.FELL_OUT_OF_WORLD) || source.is(DamageTypes.GENERIC_KILL)) {
            return;
        }
        event.setCanceled(true);
    }
}
