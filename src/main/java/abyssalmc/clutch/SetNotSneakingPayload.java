package abyssalmc.clutch;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record SetNotSneakingPayload(BlockPos blockPos) implements CustomPayload {
    public static final Id<SetNotSneakingPayload> ID = new Id<>(Clutch.SET_NOT_SNEAKING_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, SetNotSneakingPayload> CODEC = PacketCodec.tuple(BlockPos.PACKET_CODEC, SetNotSneakingPayload::blockPos, SetNotSneakingPayload::new);


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}