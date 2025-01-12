package abyssalmc.clutch;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;


public class StateSaverAndLoader extends PersistentState {

    public String platformcoords = "unset";
    public String platformattempts = "";

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.putString("platformpos", platformcoords);
        nbt.putString("platformattempts", platformattempts);
        return nbt;
    }


    public static StateSaverAndLoader createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        StateSaverAndLoader state = new StateSaverAndLoader();
        state.platformcoords = tag.getString("platformpos");
        state.platformattempts = tag.getString("platformattempts");
        return state;
    }

    private static Type<StateSaverAndLoader> type = new Type<>(
            StateSaverAndLoader::new,
            StateSaverAndLoader::createFromNbt,
            null
    );

    public static StateSaverAndLoader getServerState(MinecraftServer server) {
        if (server != null){
            PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();

            StateSaverAndLoader state = persistentStateManager.getOrCreate(type, Clutch.MOD_ID);

            state.markDirty();
            return state;
        }
        return null;
    }
}