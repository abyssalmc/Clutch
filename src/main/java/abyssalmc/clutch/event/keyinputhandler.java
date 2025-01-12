package abyssalmc.clutch.event;

import abyssalmc.clutch.GlobalDataHandler;
import abyssalmc.clutch.StateSaverAndLoader;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.Collection;
import java.util.List;
import static abyssalmc.clutch.Clutch.*;

import static abyssalmc.clutch.resetcommand.yaw;
import static java.lang.Math.abs;

public class keyinputhandler {
    public static final String KEY_CATEGORY_CLUTCH = "key.category.clutch.main";
    public static final String KEY_CATEGORY_SHOW = "key.category.clutch.show";
    public static final String KEY_CATEGORY_RESET = "key.category.clutch.reset";

    public static KeyBinding togglekey;
    public static KeyBinding resetkey;
    public static int boatv = 5;
    public static int bladderv = 5;
    public static int hayv = 5;
    public static boolean showclutch = false;
    public static boolean reset = true;
    public static boolean repeatcheck = true;

    public static void registerKeyInputs(){
        ClientTickEvents.END_CLIENT_TICK.register(client ->{


            if(togglekey.isPressed()) {
                if (reset){
                    reset = false;
                    PlayerEntity p = client.player;

                    if (showclutch) {
                        showclutch = false;
                    } else {
                        showclutch = true;
                    }
                }
            }
            else{
                reset = true;
            }
        });
    }

    public static void register(){
        togglekey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_CATEGORY_SHOW,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                KEY_CATEGORY_CLUTCH
        ));

        resetkey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_CATEGORY_RESET,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                KEY_CATEGORY_CLUTCH
        ));
        registerKeyInputs();
    }
}
