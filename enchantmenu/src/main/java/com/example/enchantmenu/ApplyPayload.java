package com.example.enchantmenu;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ApplyPayload(String data) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ApplyPayload> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("enchantmenu", "apply"));

    public static final StreamCodec<ByteBuf, ApplyPayload> STREAM_CODEC =
        ByteBufCodecs.STRING_UTF8.map(ApplyPayload::new, ApplyPayload::data);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
