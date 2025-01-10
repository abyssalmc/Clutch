package abyssalmc.clutch.sound;

import abyssalmc.clutch.Clutch;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static SoundEvent OSU = registerSoundEvent("osu");
    public static SoundEvent BASSKICK = registerSoundEvent("basskick");


    private static SoundEvent registerSoundEvent(String name){
        Identifier id = Identifier.of(Clutch.MOD_ID, name);

        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }
    public static void registerSounds(){
        Clutch.LOGGER.info("Registering sounds for " + Clutch.MOD_ID);
    }

}
