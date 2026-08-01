package abyssalmc.clutch;

import abyssalmc.clutch.setupblock.CustomStorageBlockEntity;
import abyssalmc.clutch.setupblock.StorageBlock;
import abyssalmc.clutch.sound.ModSounds;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import abyssalmc.clutch.setupblock.*;

public class Clutch implements ModInitializer {

	public static final String MOD_ID = "clutch";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static boolean configured = false;
	public static double currenty = 0;

	public static int getBlockPosPlayerIsLookingAt(PlayerEntity player, World world, double maxDistance) {
		if (player != null){


			Vec3d eyePosition = player.getCameraPosVec(1.0F);
			Vec3d lookVector = player.getRotationVec(1.0F);
			Vec3d endVec = eyePosition.add(lookVector.multiply(maxDistance));

			//raycast
			BlockHitResult hitResult = world.raycast(new RaycastContext(
					eyePosition,
					endVec,
					RaycastContext.ShapeType.OUTLINE,
					RaycastContext.FluidHandling.NONE,
					player
			));

			if (hitResult == null || hitResult.getType() != HitResult.Type.BLOCK) {
				return 99999;
			}

			return hitResult.getBlockPos().getY();
		} else {
			return 0;
		}
	}

	public static boolean canParseDouble(String input) {
		try {
			Double.parseDouble(input);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}


	public static Block CUSTOM_BLOCK;
	public static BlockEntityType<CustomStorageBlockEntity> CUSTOM_BLOCK_ENTITY;
	public static ScreenHandlerType<abyssalmc.clutch.setupblock.CustomStorageScreenHandler> CUSTOM_SCREEN_HANDLER;
	public static Item CUSTOM_BLOCK_ITEM;

	@Override
	public void onInitialize() {

		GlobalDataHandler.loadGlobalData();
		ServerLifecycleEvents.SERVER_STOPPED.register(server -> GlobalDataHandler.saveGlobalData());

		CommandRegistrationCallback.EVENT.register(ClutchCommand::register);

		ModSounds.registerSounds();

		configured = false;

		PayloadTypeRegistry.playC2S().register(CloseGUIPayload.ID, CloseGUIPayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(CloseGUIPayload.ID, (payload, context) -> {
			context.server().execute(() -> {
				if ((context.player().getVelocity().y < 0 && !context.player().isOnGround()) || ClutchClient.resetclose)
				{
					ClutchClient.closepass = true;
					ClutchClient.resetclose = false;
					if (context.player().currentScreenHandler != null) {
						context.player().closeHandledScreen();
					}
				}

			});
		});

		ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
			if (GlobalDataHandler.getStalls()){
				if (entity instanceof PlayerEntity && source.getType().toString().contains("deathMessageType=FALL_VARIANTS]")) {
					double servery = -9999;
					if (canParseDouble(new DecimalFormat("#.####").format(entity.getY()))){
						servery = Double.parseDouble(new DecimalFormat("#.####").format(entity.getY()));

					}

					if (((servery >= currenty && (servery == currenty || (servery - currenty) > 0.6)) || (servery < currenty && currenty-servery < 0.6))) {
						//entity.sendMessage(Text.literal("stall triggered. client height: " + currenty + ", server height: " + servery + "."));

						return false;

					}
				}
			}
			return true;
		});

		// setup block
		CUSTOM_BLOCK = Registry.register(
				Registries.BLOCK,
				Identifier.of(MOD_ID, "setup_block"),
				new StorageBlock(AbstractBlock.Settings.create())
		);
		CUSTOM_BLOCK_ENTITY = Registry.register(
				Registries.BLOCK_ENTITY_TYPE,
				Identifier.of(MOD_ID, "setup_be"),
				BlockEntityType.Builder.create(CustomStorageBlockEntity::new, CUSTOM_BLOCK).build()
		);
		CUSTOM_SCREEN_HANDLER = Registry.register(
				Registries.SCREEN_HANDLER,
				Identifier.of(MOD_ID, "custom_screen"),
				new ExtendedScreenHandlerType<>((syncId, playerInventory, pos) ->
						new CustomStorageScreenHandler(syncId, playerInventory, pos),
						BlockPos.PACKET_CODEC
				)
		);
		CUSTOM_BLOCK_ITEM = Registry.register(
				Registries.ITEM,
				Identifier.of(MOD_ID, "custom_storage_block"),
				new BlockItem(CUSTOM_BLOCK, new Item.Settings())
		);
	}
}