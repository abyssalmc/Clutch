package abyssalmc.clutch.setupblock;

import abyssalmc.clutch.Clutch;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record TextStoragePayload(BlockPos pos, String slotText, String offsetText) implements CustomPayload {
    public static final CustomPayload.Id<TextStoragePayload> ID = new CustomPayload.Id<>(Identifier.of(Clutch.MOD_ID, "storage_data"));

    public static final PacketCodec<RegistryByteBuf, TextStoragePayload> CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, TextStoragePayload::pos,
            PacketCodecs.STRING, TextStoragePayload::slotText,
            PacketCodecs.STRING, TextStoragePayload::offsetText,
            TextStoragePayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}