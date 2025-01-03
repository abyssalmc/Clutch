package abyssalmc.clutch;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record CraftItemPayload(BlockPos blockPos) implements CustomPayload {
    public static final CustomPayload.Id<CraftItemPayload> ID = new CustomPayload.Id<>(Clutch.UPDATE_SLOTS_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, CraftItemPayload> CODEC = PacketCodec.tuple(BlockPos.PACKET_CODEC, CraftItemPayload::blockPos, CraftItemPayload::new);


    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
