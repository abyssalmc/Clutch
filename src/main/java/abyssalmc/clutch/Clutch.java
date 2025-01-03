package abyssalmc.clutch;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.FurnaceScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abyssalmc.clutch.event.keyinputhandler;

import java.util.ArrayList;
import java.util.List;

import static abyssalmc.clutch.event.keyinputhandler.*;
import static net.minecraft.util.math.MathHelper.floor;

public class Clutch implements ModInitializer {

	public static final String MOD_ID = "clutch";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	//fuck
	int clock = 0;

	public static double resx = 0;
	public static int resy = 0;
	public static double resz = 0;

	public static double cursorx = 960;
	public static double cursory = 540;

	public static boolean offsetEnabled = false;

	public static boolean configured = false;

	public static boolean bladdercrouch = false;
	public static boolean boatcrouch = false;

	public static int automovementcountdown = 0;

	public static final Identifier PLATFORM_POS = Identifier.of(MOD_ID, "platform_pos");

	private double lastTickTime = 0;
	public static List<Double> tickavg = new ArrayList<>(){};

	public static boolean isTas = false;

	public static List<Integer> cxcoords = new ArrayList<>();
	public static List<Integer> cycoords = new ArrayList<>();

	public static List<Integer> ncxcoords = new ArrayList<>();
	public static List<Integer> ncycoords = new ArrayList<>();


	public static List<Integer> ocxcoords = new ArrayList<>();
	public static List<Integer> ocycoords = new ArrayList<>();

	public static boolean keypressed = false;
	public static boolean mousepressed = false;

	public static int lastmousex = 0;
	public static int lastmousey = 0;
	public static int formermousex = 0;
	public static int formermousey = 0;

	public static long ct = 0;
	public static long et = 0;

	public static int guix = 0;
	public static int guiy = 0;

	public static int hotbarcraft = 0;




	public static int getBlockPosPlayerIsLookingAt(PlayerEntity player, World world, double maxDistance) {
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
	}

	Identifier bladder = Identifier.of(Clutch.MOD_ID, "textures/bladder.png");
	Identifier bladder0 = Identifier.of(Clutch.MOD_ID, "textures/bladder0.png");
	Identifier bladder1 = Identifier.of(Clutch.MOD_ID, "textures/bladder1.png");
	Identifier bladder2 = Identifier.of(Clutch.MOD_ID, "textures/bladder2.png");
	Identifier bladder3 = Identifier.of(Clutch.MOD_ID, "textures/bladder3.png");
	Identifier bladder4 = Identifier.of(Clutch.MOD_ID, "textures/bladder4.png");

	Identifier boat = Identifier.of(Clutch.MOD_ID, "textures/boat.png");
	Identifier boat0 = Identifier.of(Clutch.MOD_ID, "textures/boat0.png");
	Identifier boat1 = Identifier.of(Clutch.MOD_ID, "textures/boat1.png");
	Identifier boat2 = Identifier.of(Clutch.MOD_ID, "textures/boat2.png");
	Identifier boat3 = Identifier.of(Clutch.MOD_ID, "textures/boat3.png");
	Identifier boat4 = Identifier.of(Clutch.MOD_ID, "textures/boat4.png");
	Identifier arrow = Identifier.of(Clutch.MOD_ID, "textures/arrow.png");

	private boolean isKeyPressed(int key) {
		return GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), key) == GLFW.GLFW_PRESS;
	}

	public static double calculateAverage(List<Double> doubleList) {
		if (doubleList.isEmpty()) {
			throw new IllegalArgumentException("List is empty, cannot calculate average.");
		}

		double sum = 0;
		for (double num : doubleList) {
			sum += num;
		}

		return sum / doubleList.size();
	}

	public static double thresholdTest(List<Double> doubleList) {
		if (doubleList.isEmpty()) {
			throw new IllegalArgumentException("List is empty, cannot calculate average.");
		}
		double flag = 0;
		for (double num : doubleList) {
			if (num > 50)
				flag++;
		}
		return flag;
	}

	public static final Identifier UPDATE_SLOTS_PACKET_ID =  Identifier.of(MOD_ID, "update_slots");


	@Override
	public void onInitialize() {
		GlobalDataHandler.loadGlobalData();
		ServerLifecycleEvents.SERVER_STOPPED.register(server -> GlobalDataHandler.saveGlobalData());

		CommandRegistrationCallback.EVENT.register(resetcommand::register);

		keyinputhandler.register();

		configured = false;
		MinecraftClient mc = MinecraftClient.getInstance();

		PayloadTypeRegistry.playC2S().register(CraftItemPayload.ID, CraftItemPayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(CraftItemPayload.ID, (payload, context) -> {
			context.server().execute(() -> {
				ServerPlayerEntity sp = context.server().getPlayerManager().getPlayer(mc.player.getUuid());
				sp.getInventory().setStack(hotbarcraft,sp.currentScreenHandler.slots.get(0).getStack());

				for (int i = 1; i <= 9; i++) {
					if (!sp.currentScreenHandler.slots.get(i).getStack().getName().getString().equals("Air")){
						ItemStack newStack = (sp.currentScreenHandler.slots.get(i).getStack().getCount() == 1) ? new ItemStack(Items.AIR,0) : new ItemStack(sp.currentScreenHandler.slots.get(i).getStack().getItem(),sp.currentScreenHandler.slots.get(i).getStack().getCount()-1);
						sp.currentScreenHandler.slots.get(i).setStack(newStack);
					}
				}

				sp.currentScreenHandler.sendContentUpdates();
				sp.playerScreenHandler.sendContentUpdates();

				sp.getInventory().markDirty();
			});
		});


		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			PlayerEntity p = mc.player;

			if (p != null) {

				//INDICATOR
				double currentTime = System.currentTimeMillis();
				if (lastTickTime != 0) {
					if (tickavg != null){
						double tickDuration = currentTime - lastTickTime;
						if (!(tickavg.size() < 10)) {
							tickavg.remove(0);
						}
						tickavg.add(tickDuration);
						if (calculateAverage(tickavg) >= 54 && thresholdTest(tickavg) > 4){
							isTas = true;
						} else {
							isTas = false;
						}
					}
				}
				lastTickTime = currentTime;

				//AUTO MOVEMENT
				if (client.isIntegratedServerRunning() && client.getServer() != null){
					StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(client.getServer());
					if (GlobalDataHandler.getAutomov() && !serverState.platformcoords.equals("unset")){
						if (automovementcountdown > 0){
							if (automovementcountdown < 15){
								if (automovementcountdown == 14){
									p.setVelocity(p.getVelocity().getX(),0.42,p.getVelocity().getZ());
									client.options.jumpKey.setPressed(true);
								}
								if (client.currentScreen == null) {client.options.backKey.setPressed(true);}
								if (automovementcountdown == 1){
									client.options.backKey.setPressed(false);
									client.options.jumpKey.setPressed(false);
								}
							}
							automovementcountdown--;
						}
					}
				}


				//INPUT LOC
				if (client.currentScreen == null){
					cxcoords = new ArrayList<>();
					cycoords = new ArrayList<>();
					ncxcoords = new ArrayList<>();
					ncycoords = new ArrayList<>();

					ocxcoords = new ArrayList<>();
					ocycoords = new ArrayList<>();
				}

				//PLATFORM
				if (client.isIntegratedServerRunning() && client.getServer() != null) {
					if (client.currentScreen != null) {
						// CLOSING WITH HOTKEY
						if (isKeyPressed((int) resetkey.getBoundKeyLocalizedText().getString().charAt(0)) && resetkey.getBoundKeyLocalizedText().getString().length() == 1) {
							client.currentScreen.close();

							automovementcountdown = 14;
							if (GlobalDataHandler.getAutomov()) {
								client.options.jumpKey.setPressed(false);
								client.options.backKey.setPressed(false);
							}

							StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(client.getServer());
							if (!serverState.platformcoords.equals("unset")) {
								String cmd = "tp @s " + serverState.platformcoords + GlobalDataHandler.getPitch();
								client.getNetworkHandler().sendChatCommand(cmd);
							} else {
								p.sendMessage(Text.literal("§cA platform must be set to use this! run /platform to get started."));
							}
						}
					}
				}
			}

			clock++;
			if (clock==7){
				clock=0;

				if (showclutch) {
					Integer groundY = Clutch.getBlockPosPlayerIsLookingAt(mc.player, mc.world, 300.0) + 1;

					double y = p.getY();
					if (Math.floor(y) - groundY > 4) {
						double v = p.getVelocity().getY();
						double ry = y - groundY; //relative y
						double py = 0; //peak y
						double sy = 0; //second last y
						double ly = 0; //last y

						while (ry > 3.23) {
							ry += v;
							v = 0.98 * (v - 0.08);
							py = ry;
							ly = py;
						}
						Integer t = 0;
						while (ry > 0) {
							if (ry > 0) {
								t += 1;
								sy = ly;
								ly = ry;
							}

							ry += v;
							v = 0.98 * (v - 0.08);
							if (Math.abs(v) < 0.003){v=0;}

						}

						boatv = 0;
						boatcrouch = false;
						if (t >= 3) {
							if (ly < 0.5625) {
								boatv = 3;
							} else {
								boatv = 2;
							}
						} else {
							if (t == 2) {
								if (ly < 0.5625) {
									boatv = 4;
								} else {
									boatv = 1;
								}
								if (py > 2.88 && boatv >= 1 && boatv <= 4){boatcrouch = true;}
								else{ boatcrouch = false;}
							}
						}

						bladderv = 4;
						bladdercrouch = false;
						if (t >= 2) {
							bladderv = 4;
							if (sy > 2.25) {
								bladderv = 3;
							}
							if (sy > 2.75) {
								bladderv = 2;
							}
							if (sy > 3) {
								bladderv = 1;
							}
							if (sy > 3.203430557) {
								bladderv = 0;
							}
							if (sy > 2.8532 && bladderv >= 1 && bladderv <= 4){
								bladdercrouch = true;
							} else{ bladdercrouch = false;}
						} else {
							bladderv = 0;
						}

						hayv=0;
						if (ly>0.9375){
							hayv=1;
							if (ly>1){
								hayv=2;
								if (py<2.88){
									hayv=3;
								}
							}
						}

					} else {
						boatv = 5;
						bladderv = 5;
						hayv = 4;
					}
				}
			}

		});


		ScreenEvents.AFTER_INIT.register((MinecraftClient client, Screen screen, int w, int h) -> {
			if ((screen instanceof CraftingScreen || (screen instanceof InventoryScreen && !client.player.isInCreativeMode())) && GlobalDataHandler.getRecipe() > 0){
				for (Element element : screen.children()) {
					if (GlobalDataHandler.getRecipe() > 1 && element instanceof ButtonWidget button){
						button.visible = false;
					}
					screen.children().remove(element);
				}
			}
		});

		HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
			if (showclutch){
				TextRenderer textRenderer = mc.textRenderer;
				drawContext.drawTexture(bladder, 4, mc.getWindow().getScaledHeight()-25, 0, 0, 24, 24, 24, 24);
				drawContext.drawTexture(boat, 31, mc.getWindow().getScaledHeight()-25, 0, 0, 24, 24, 24, 24);

				int bladdertot = 0;
				int boattot = 0;

				if (bladderv == 0){drawContext.drawTexture(bladder0, 4, mc.getWindow().getScaledHeight()-25, 0, 0, 24, 24, 24, 24);bladdertot++;}
				if (bladderv == 1){drawContext.drawTexture(bladder1, 4, mc.getWindow().getScaledHeight()-25, 0, 0, 24, 24, 24, 24);bladdertot++;}
				if (bladderv == 2){drawContext.drawTexture(bladder2, 4, mc.getWindow().getScaledHeight()-25, 0, 0, 24, 24, 24, 24);bladdertot++;}
				if (bladderv == 3){drawContext.drawTexture(bladder3, 4, mc.getWindow().getScaledHeight()-25, 0, 0, 24, 24, 24, 24);bladdertot++;}
				if (bladderv == 4){drawContext.drawTexture(bladder4, 4, mc.getWindow().getScaledHeight()-25, 0, 0, 24, 24, 24, 24);bladdertot++;}

				if (boatv == 0){drawContext.drawTexture(boat0, 31, mc.getWindow().getScaledHeight()-25, 0, 0, 24, 24, 24, 24);boattot++;}
				if (boatv == 1){drawContext.drawTexture(boat1, 31, mc.getWindow().getScaledHeight()-25, 0, 0, 24, 24, 24, 24);boattot++;}
				if (boatv == 2){drawContext.drawTexture(boat2, 31, mc.getWindow().getScaledHeight()-25, 0, 0, 24, 24, 24, 24);boattot++;}
				if (boatv == 3){drawContext.drawTexture(boat3, 31, mc.getWindow().getScaledHeight()-25, 0, 0, 24, 24, 24, 24);boattot++;}
				if (boatv == 4){drawContext.drawTexture(boat4, 31, mc.getWindow().getScaledHeight()-25, 0, 0, 24, 24, 24, 24);boattot++;}


				if (bladdercrouch && bladdertot>=1){drawContext.drawTexture(arrow, 10, mc.getWindow().getScaledHeight()-25, 0, 0, 24, 24, 24, 24);}
				if (boatcrouch && boattot>=1){drawContext.drawTexture(arrow, 40, mc.getWindow().getScaledHeight()-25, 0, 0, 24, 24, 24, 24);}
			}
		});

	}

}