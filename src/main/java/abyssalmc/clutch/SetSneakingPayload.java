package abyssalmc.clutch;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record SetSneakingPayload(BlockPos blockPos) implements CustomPayload {
    public static final Id<SetSneakingPayload> ID = new Id<>(Clutch.SET_SNEAKING_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, SetSneakingPayload> CODEC = PacketCodec.tuple(BlockPos.PACKET_CODEC, SetSneakingPayload::blockPos, SetSneakingPayload::new);


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}