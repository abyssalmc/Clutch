package abyssalmc.clutch;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record CloseGUIPayload(BlockPos blockPos) implements CustomPayload {
    public static final Id<CloseGUIPayload> ID = new Id<>(Clutch.CLOSE_GUI_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, CloseGUIPayload> CODEC = PacketCodec.tuple(BlockPos.PACKET_CODEC, CloseGUIPayload::blockPos, CloseGUIPayload::new);


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}