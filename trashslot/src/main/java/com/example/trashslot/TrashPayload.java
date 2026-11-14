package com.example.trashslot;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record TrashPayload() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<TrashPayload> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("trashslot", "trash"));

    public static final StreamCodec<ByteBuf, TrashPayload> STREAM_CODEC =
        StreamCodec.unit(new TrashPayload());

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
