package abyssalmc.clutch;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.minecraft.world.PersistentStateType;


public class StateSaverAndLoader extends PersistentState {

    public String platformcoords;
    public String platformdim;
    public String platformattempts;
    public Boolean projectilerng;

    public StateSaverAndLoader() {
        this.platformcoords = "unset";
        this.platformdim = "unset";
        this.platformattempts = "";
        this.projectilerng = true;
    }

    public StateSaverAndLoader(String platformcoords, String platformdim, String platformattempts, Boolean projectilerng) {
        this.platformcoords = platformcoords;
        this.platformdim = platformdim;
        this.platformattempts = platformattempts;
        this.projectilerng = projectilerng;
    }

    public static final Codec<StateSaverAndLoader> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("platformpos").forGetter(state -> state.platformcoords),
                    Codec.STRING.fieldOf("platformdim").forGetter(state -> state.platformdim),
                    Codec.STRING.fieldOf("platformattempts").forGetter(state -> state.platformattempts),
                    Codec.BOOL.fieldOf("projectilerng").forGetter(state -> state.projectilerng)
            ).apply(instance, StateSaverAndLoader::new)
    );

    private static final PersistentStateType<StateSaverAndLoader> type = new PersistentStateType<>(
            Clutch.MOD_ID,
            StateSaverAndLoader::new,
            StateSaverAndLoader.CODEC,
            null
    );

    public static StateSaverAndLoader getServerState(MinecraftServer server) {
        if (server != null){
            PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();

            StateSaverAndLoader state = persistentStateManager.getOrCreate(type);

            state.markDirty();
            return state;
        }
        return null;
    }
}