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

    public static void loadGlobalData() {
        try {
            if (Files.exists(GLOBAL_DATA_PATH)) {
                NbtCompound nbt = NbtIo.read(GLOBAL_DATA_PATH);
                pitch = nbt.getInt("gpitch");
                automov = nbt.getBoolean("automov");
                recipe = nbt.getInt("recipe");
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

            NbtIo.write(nbt, GLOBAL_DATA_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static int getPitch() { return pitch; }
    public static boolean getAutomov() { return automov; }
    public static int getRecipe() { return recipe; }

    public static void setPitch(int gpitch) { pitch = gpitch; }
    public static void setAutomov(boolean b) { automov = b; }
    public static void setRecipe(int rt) { recipe = rt; }
}