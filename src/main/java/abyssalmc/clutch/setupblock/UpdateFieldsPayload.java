package abyssalmc.clutch.setupblock;

import abyssalmc.clutch.Clutch;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record UpdateFieldsPayload(BlockPos pos, String slotText, String offsetText) implements CustomPayload {
    public static final CustomPayload.Id<UpdateFieldsPayload> ID = new CustomPayload.Id<>(Identifier.of(Clutch.MOD_ID, "update_fields"));

    public static final PacketCodec<RegistryByteBuf, UpdateFieldsPayload> CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, UpdateFieldsPayload::pos,
            PacketCodecs.STRING, UpdateFieldsPayload::slotText,
            PacketCodecs.STRING, UpdateFieldsPayload::offsetText,
            UpdateFieldsPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}