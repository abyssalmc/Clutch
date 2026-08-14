package abyssalmc.clutch.setupblock;

import abyssalmc.clutch.Clutch;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record PasteInventoryPayload(BlockPos pos) implements CustomPayload {
    public static final CustomPayload.Id<PasteInventoryPayload> ID = new CustomPayload.Id<>(Identifier.of(Clutch.MOD_ID, "paste_inventory"));

    public static final PacketCodec<RegistryByteBuf, PasteInventoryPayload> CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, PasteInventoryPayload::pos,
            PasteInventoryPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}