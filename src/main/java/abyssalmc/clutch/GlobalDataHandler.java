package abyssalmc.clutch;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GlobalDataHandler {
    private static final String DATA_KEY = "customData";
    private static final Path GLOBAL_DATA_PATH = Path.of("clutch_data/cache/global_data.nbt");
    private static int pitch = 77;
    private static boolean automov = false;
    private static int recipe = 0;
    private static int inputlocation = 0;
    private static int inputlocator = 0;
    private static int customsounds = 0;
    private static boolean toggleshift = false;
    private static boolean stalls = false;

    public static void loadGlobalData() {
        try {
            if (Files.exists(GLOBAL_DATA_PATH)) {
                NbtCompound nbt = NbtIo.read(GLOBAL_DATA_PATH);
                pitch = nbt.getInt("gpitch");
                automov = nbt.getBoolean("automov");
                recipe = nbt.getInt("recipe");
                inputlocation = nbt.getInt("inputlocation");
                inputlocator = nbt.getInt("inputlocator");
                customsounds = nbt.getInt("customsounds");
                toggleshift = nbt.getBoolean("toggleshift");
                stalls = nbt.getBoolean("stalls");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveGlobalData() {
        try {
            Files.createDirectories(GLOBAL_DATA_PATH.getParent()); // Ensure directory exists

            NbtCompound nbt = new NbtCompound();
            nbt.putInt("gpitch", pitch);
            nbt.putBoolean("automov", automov);
            nbt.putInt("recipe", recipe);
            nbt.putInt("inputlocation", inputlocation);
            nbt.putInt("inputlocator", inputlocator);
            nbt.putInt("customsounds", customsounds);
            nbt.putBoolean("toggleshift", toggleshift);
            nbt.putBoolean("stalls", stalls);

            NbtIo.write(nbt, GLOBAL_DATA_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static int getPitch() { return pitch; }
    public static boolean getAutomov() { return automov; }
    public static int getRecipe() { return recipe; }
    public static int getInputLocation() { return inputlocation; }
    public static int getInputLocator() { return inputlocator; }
    public static int getCustomSounds() { return customsounds; }
    public static boolean getToggleShift() { return toggleshift; }
    public static boolean getStalls() { return stalls; }

    public static void setPitch(int gpitch) { pitch = gpitch; }
    public static void setAutomov(boolean b) { automov = b; }
    public static void setRecipe(int rt) { recipe = rt; }
    public static void setInputlocation(int ipl) { inputlocation = ipl; }
    public static void setInputlocator(int ipl) { inputlocator = ipl; }
    public static void setCustomSounds(int sound) { customsounds = sound; }
    public static void setToggleShift(boolean b) { toggleshift = b; }
    public static void setStalls(boolean b) { stalls = b; }
}