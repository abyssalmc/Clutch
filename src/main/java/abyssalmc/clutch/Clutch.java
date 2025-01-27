package abyssalmc.clutch;

import abyssalmc.clutch.sound.ModSounds;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abyssalmc.clutch.event.keyinputhandler;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static abyssalmc.clutch.event.keyinputhandler.*;
import static net.minecraft.entity.damage.DamageTypes.FALL;

public class Clutch implements ModInitializer {

	public static final String MOD_ID = "clutch";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
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


	private double lastTickTime = 0;
	public static List<Double> tickavg = new ArrayList<>(){};

	public static boolean isTas = false;

	public static List<Integer> cxcoords = new ArrayList<>();
	public static List<Integer> cycoords = new ArrayList<>();


	public static int guix = 0;
	public static int guiy = 0;
	public static int guitime = 0;
	public static int tempguitime = 0;

	public static boolean overplate = false;
	public static boolean lastoverplate = false;

	public static boolean closepass = false;

	public static boolean toggleshiftstate = false;
	public static boolean recursion = false;
	public static double currenty = 0;
    public static boolean slotclick = false;
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

	public static final Identifier CLOSE_GUI_PACKET_ID =  Identifier.of(MOD_ID, "close_gui");
	public static final Identifier SET_SNEAKING_PACKET_ID =  Identifier.of(MOD_ID, "set_sneaking");
	public static final Identifier SET_NOT_SNEAKING_PACKET_ID =  Identifier.of(MOD_ID, "set_not_sneaking");
	public static final Identifier PY_PACKET_ID =  Identifier.of(MOD_ID, "player_y");


	@Override
	public void onInitialize() {

		GlobalDataHandler.loadGlobalData();
		ServerLifecycleEvents.SERVER_STOPPED.register(server -> GlobalDataHandler.saveGlobalData());

		CommandRegistrationCallback.EVENT.register(ClutchCommand::register);

		keyinputhandler.register();

		ModSounds.registerSounds();

		configured = false;
		MinecraftClient mc = MinecraftClient.getInstance();

		PayloadTypeRegistry.playC2S().register(CloseGUIPayload.ID, CloseGUIPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(SetSneakingPayload.ID, SetSneakingPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(SetNotSneakingPayload.ID, SetNotSneakingPayload.CODEC);


		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			PlayerEntity p = mc.player;

			ServerPlayNetworking.registerGlobalReceiver(CloseGUIPayload.ID, (payload, context) -> {
				context.server().execute(() -> {
					closepass = true;
					context.player().closeHandledScreen();
				});
			});
			ServerPlayNetworking.registerGlobalReceiver(SetSneakingPayload.ID, (payload, context) -> {
				context.server().execute(() -> {
					context.player().setSneaking(true);
				});
			});
			ServerPlayNetworking.registerGlobalReceiver(SetNotSneakingPayload.ID, (payload, context) -> {
				context.server().execute(() -> {
					context.player().setSneaking(false);
				});
			});
			if (p != null && MinecraftClient.getInstance().isIntegratedServerRunning() && MinecraftClient.getInstance().getServer() != null) {
				p.sendMessage(Text.literal(client.mouse.getX()+""));

				//STALL
				if (canParseDouble(new DecimalFormat("#.####").format(p.getY()))){
					currenty = Double.parseDouble(new DecimalFormat("#.####").format(p.getY()));
				}

				//ATTEMPT COUNTER
				overplate = false;
				BlockPos platepos = new BlockPos(0,0,0);
				for (int x = 0; x < 1; x++){
					for (int z = 0; z < 1; z++){
						double xcoord = p.getX()+0.6*x - 0.3 + (2*x-1)*0.0625;
						double zcoord = p.getZ()+0.6*x - 0.3 + (2*z-1)*0.0625;
						BlockPos hitboxpos = new BlockPos((int)Math.floor(xcoord), (int)Math.floor(p.getY()), (int)Math.floor(zcoord));
						if (mc.world.getBlockState(hitboxpos).getBlock().getName().toString().contains("pressure_plate")){
							if (mc.world.getBlockState(hitboxpos.down().north()).getBlock().getName().toString().contains("crafting_table") ||
									mc.world.getBlockState(hitboxpos.down().east()).getBlock().getName().toString().contains("crafting_table") ||
									mc.world.getBlockState(hitboxpos.down().south()).getBlock().getName().toString().contains("crafting_table") ||
									mc.world.getBlockState(hitboxpos.down().west()).getBlock().getName().toString().contains("crafting_table")){
								overplate = true;
								platepos = new BlockPos((int)Math.floor(xcoord), (int)Math.floor(p.getY()), (int)Math.floor(zcoord));
							}
						}
					}
				}
				if (lastoverplate != overplate && overplate){
					if (client.isIntegratedServerRunning() && client.getServer() != null) {
						StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(client.getServer());
						String pta = serverState.platformattempts;
						if (pta == ""){
							serverState.platformattempts = "(" + platepos.getX() + ", " + platepos.getY() + ", " + platepos.getZ() + ") 1 | ";
						} else if (pta.contains("(" + platepos.getX() + ", " + platepos.getY() + ", " + platepos.getZ() + ") ")){
							String[] allattempts = pta.split(" \\| ");
							int index = 0;
							String newplatformattempts = "";
							for (String attempts : allattempts){
								if (attempts.contains("(" + platepos.getX() + ", " + platepos.getY() + ", " + platepos.getZ())){
									int attemptcount = Integer.parseInt(attempts.split(" ")[attempts.split(" ").length-1]) + 1;
									String newString = "";
									int index2 = 0;
									for (String str : attempts.split(" ")){
										if (index2 != attempts.split(" ").length-1){
											newString += str + " ";
										}
										index2++;
									}
									newString += attemptcount + " | ";
									newplatformattempts = newplatformattempts + newString;

								} else {
									newplatformattempts = newplatformattempts + attempts + " | ";
								}

							}
							serverState.platformattempts = newplatformattempts;
						} else {
							serverState.platformattempts = pta + "(" + platepos.getX() + ", " + platepos.getY() + ", " + platepos.getZ() + ") 1 | ";
						}
					}
				}
				lastoverplate = overplate;


				//GUI TIME
				if (MinecraftClient.getInstance().currentScreen instanceof CraftingScreen){
					if (tempguitime > 0){
						tempguitime -= 1;
					}
					if (guitime != 0 && tempguitime == 0 && client.currentScreen instanceof CraftingScreen){

						ClientPlayNetworking.send(new CloseGUIPayload(new BlockPos(0,0,0)));
					}
					if (isKeyPressed(InputUtil.fromTranslationKey(client.options.inventoryKey.getBoundKeyTranslationKey()).getCode()) || isKeyPressed(GLFW.GLFW_KEY_ESCAPE)){
						if (guitime != 0){
							ClientPlayNetworking.send(new CloseGUIPayload(new BlockPos(0,0,0)));
							tempguitime = guitime;
						}
					}
				}

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
							if (automovementcountdown <= 11){
								if (automovementcountdown == 11){
									client.player.setVelocity(0,0,0);
									String cmd = "tp @s " + serverState.platformcoords + GlobalDataHandler.getPitch();
									client.getNetworkHandler().sendChatCommand(cmd);
									client.options.jumpKey.setPressed(false);
								}
								if (client.currentScreen == null) {
									client.options.backKey.setPressed(true);
								}
								if (automovementcountdown == 2){
									client.options.backKey.setPressed(false);
									client.options.jumpKey.setPressed(true);
								}
								if (automovementcountdown == 1) {
									client.options.jumpKey.setPressed(false);
									client.options.backKey.setPressed(false);
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

		ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
			if (MinecraftClient.getInstance().isIntegratedServerRunning() && MinecraftClient.getInstance().getServer() != null) {
				if (GlobalDataHandler.getStalls()){
					if (entity instanceof PlayerEntity && source.getType().toString().contains("deathMessageType=FALL_VARIANTS]")) {
						double servery = -9999;
						if (canParseDouble(new DecimalFormat("#.####").format(entity.getY()))){
							servery = Double.parseDouble(new DecimalFormat("#.####").format(entity.getY()));

						}

						if (((servery >= currenty && (servery == currenty || (servery - currenty) > 0.6)) || (servery < currenty && currenty-servery < 0.6)) && MinecraftClient.getInstance().options.jumpKey.isPressed()) {
							//entity.sendMessage(Text.literal("stall triggered. client height: " + currenty + ", server height: " + servery + "."));

							return false;

						}
					}
				}
			}
			return true;
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

	public boolean canParseDouble(String input) {
		try {
			Double.parseDouble(input);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}