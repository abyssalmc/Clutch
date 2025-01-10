package abyssalmc.clutch;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import static abyssalmc.clutch.Clutch.*;

public class resetcommand {

    public static double yaw;


    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment){
        dispatcher.register(CommandManager.literal("platform").executes(resetcommand::resetPointNoArgs));
        dispatcher.register(CommandManager.literal("platform").then(CommandManager.argument("coord offset", DoubleArgumentType.doubleArg()).executes(resetcommand::resetPoint)));
        dispatcher.register(CommandManager.literal("platformread").executes(resetcommand::getPlatform));

        dispatcher.register(CommandManager.literal("automov").then(CommandManager.literal("enable").executes(resetcommand::ae)));
        dispatcher.register(CommandManager.literal("automov").then(CommandManager.literal("disable").executes(resetcommand::ad)));


        dispatcher.register(CommandManager.literal("cursoroffset").then(CommandManager.argument("cursorX", IntegerArgumentType.integer()).then(CommandManager.argument("cursorY", IntegerArgumentType.integer()).executes(resetcommand::cursorOffset))));
        dispatcher.register(CommandManager.literal("disableoffset").executes(resetcommand::disableOffset));

        dispatcher.register(CommandManager.literal("pitch").then(CommandManager.argument("angle", IntegerArgumentType.integer()).executes(resetcommand::setpitch)));

        dispatcher.register(CommandManager.literal("recipebook").then(CommandManager.literal("default").executes(resetcommand::rbn)));
        dispatcher.register(CommandManager.literal("recipebook").then(CommandManager.literal("disable").executes(resetcommand::rbd)));
        dispatcher.register(CommandManager.literal("recipebook").then(CommandManager.literal("occlude").executes(resetcommand::rbo)));

        dispatcher.register(CommandManager.literal("inputloc").then(CommandManager.literal("off").executes(resetcommand::ilo)));
        dispatcher.register(CommandManager.literal("inputloc").then(CommandManager.literal("misses").executes(resetcommand::ilm)));
        dispatcher.register(CommandManager.literal("inputloc").then(CommandManager.literal("full").executes(resetcommand::ilf)));

        dispatcher.register(CommandManager.literal("inputlocstyle").then(CommandManager.literal("dot").executes(resetcommand::isd)));
        dispatcher.register(CommandManager.literal("inputlocstyle").then(CommandManager.literal("diamond").executes(resetcommand::iss)));
        dispatcher.register(CommandManager.literal("inputlocstyle").then(CommandManager.literal("plus").executes(resetcommand::isp)));

        dispatcher.register(CommandManager.literal("guitime").then(CommandManager.literal("set").then(CommandManager.argument("ticks", IntegerArgumentType.integer()).executes(resetcommand::customguitime))));
        dispatcher.register(CommandManager.literal("guitime").then(CommandManager.literal("default").executes(resetcommand::resetcustomguitime)));

        dispatcher.register(CommandManager.literal("guiinputsounds").then(CommandManager.literal("off").executes(resetcommand::csd)));
        dispatcher.register(CommandManager.literal("guiinputsounds").then(CommandManager.literal("osu").executes(resetcommand::cso)));
        dispatcher.register(CommandManager.literal("guiinputsounds").then(CommandManager.literal("basskick").executes(resetcommand::csb)));


    }


    private static int resetPointNoArgs(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity p = mc.player;
        Block b = mc.world.getBlockState(p.getBlockPos()).getBlock();

        if (b == Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE ||
                b == Blocks.ACACIA_PRESSURE_PLATE ||
                b == Blocks.BAMBOO_PRESSURE_PLATE ||
                b == Blocks.BIRCH_PRESSURE_PLATE ||
                b == Blocks.CHERRY_PRESSURE_PLATE ||
                b == Blocks.CRIMSON_PRESSURE_PLATE ||
                b == Blocks.JUNGLE_PRESSURE_PLATE ||
                b == Blocks.MANGROVE_PRESSURE_PLATE ||
                b == Blocks.DARK_OAK_PRESSURE_PLATE ||
                b == Blocks.OAK_PRESSURE_PLATE ||
                b == Blocks.SPRUCE_PRESSURE_PLATE ||
                b == Blocks.WARPED_PRESSURE_PLATE ||
                b == Blocks.STONE_PRESSURE_PLATE ||
                b == Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE ||
                b == Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE){
            BlockPos base = p.getBlockPos().down();

            int tables = 0;
            double xshift = 0;
            double zshift = 0;

            if (mc.world.getBlockState(base.south()).getBlock() == Blocks.CRAFTING_TABLE){yaw = 180;tables++;}
            if (mc.world.getBlockState(base.west()).getBlock() == Blocks.CRAFTING_TABLE){yaw = 270;tables++;}
            if (mc.world.getBlockState(base.north()).getBlock() == Blocks.CRAFTING_TABLE){yaw = 0;tables++;}
            if (mc.world.getBlockState(base.east()).getBlock() == Blocks.CRAFTING_TABLE){yaw = 90;tables++;}

            if (tables == 0){
                p.sendMessage(Text.literal("§cThere must be a crafting table adjacent to the standing block!"));
                configured = false;
            }
            if (tables >= 2){
                p.sendMessage(Text.literal("§cThere can only be one crafting table adjacent to the standing block!"));
                configured = false;
            }
            if (tables == 1){
                configured = true;
                resx = p.getBlockPos().getX()+xshift+0.5;
                resy = p.getBlockPos().getY();
                resz = p.getBlockPos().getZ()+zshift+0.5;

                StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(context.getSource().getServer());
                serverState.platformcoords = resx + " " + resy + " " + resz + " " + yaw + " ";


                PacketByteBuf data = PacketByteBufs.create();
                data.writeString(serverState.platformcoords);



                p.sendMessage(Text.literal("§aPlatform set at " + serverState.platformcoords + "with no offset."));
            }

        }
        else{
            p.sendMessage(Text.literal("§cYou must be on top of a pressure plate to set a platform!"));
            configured = false;
        }

        return 1;
    }

    private static int resetPoint(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity p = mc.player;
        Block b = mc.world.getBlockState(p.getBlockPos()).getBlock();

        double offset = DoubleArgumentType.getDouble(context, "coord offset");

        if (b == Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE ||
                b == Blocks.ACACIA_PRESSURE_PLATE ||
                b == Blocks.BAMBOO_PRESSURE_PLATE ||
                b == Blocks.BIRCH_PRESSURE_PLATE ||
                b == Blocks.CHERRY_PRESSURE_PLATE ||
                b == Blocks.CRIMSON_PRESSURE_PLATE ||
                b == Blocks.JUNGLE_PRESSURE_PLATE ||
                b == Blocks.MANGROVE_PRESSURE_PLATE ||
                b == Blocks.DARK_OAK_PRESSURE_PLATE ||
                b == Blocks.OAK_PRESSURE_PLATE ||
                b == Blocks.SPRUCE_PRESSURE_PLATE ||
                b == Blocks.WARPED_PRESSURE_PLATE ||
                b == Blocks.STONE_PRESSURE_PLATE ||
                b == Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE ||
                b == Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE){
            BlockPos base = p.getBlockPos().down();

            int tables = 0;
            double xshift = 0;
            double zshift = 0;

            if (mc.world.getBlockState(base.south()).getBlock() == Blocks.CRAFTING_TABLE){yaw = 180;tables++;zshift=offset;}
            if (mc.world.getBlockState(base.west()).getBlock() == Blocks.CRAFTING_TABLE){yaw = 270;tables++;xshift=-offset;}
            if (mc.world.getBlockState(base.north()).getBlock() == Blocks.CRAFTING_TABLE){yaw = 0;tables++;zshift=-offset;}
            if (mc.world.getBlockState(base.east()).getBlock() == Blocks.CRAFTING_TABLE){yaw = 90;tables++;xshift=offset;}

            if (tables == 0){
                p.sendMessage(Text.literal("§cThere must be a crafting table adjacent to the standing block!"));
                configured = false;
            }
            if (tables >= 2){
                p.sendMessage(Text.literal("§cThere can only be one crafting table adjacent to the standing block!"));
                configured = false;
            }
            if (tables == 1){
                configured = true;
                resx = p.getBlockPos().getX()+xshift+0.5;
                resy = p.getBlockPos().getY();
                resz = p.getBlockPos().getZ()+zshift+0.5;

                StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(context.getSource().getServer());
                serverState.platformcoords = resx + " " + resy + " " + resz + " " + yaw + " ";


                PacketByteBuf data = PacketByteBufs.create();
                data.writeString(serverState.platformcoords);

                p.sendMessage(Text.literal("§aPlatform set at " + serverState.platformcoords + "with an offset of " + offset + " blocks."));
            }
        }
        else{
            p.sendMessage(Text.literal("§cYou must be on top of a pressure plate to set a platform!"));
            configured = false;
        }

        return 1;
    }

    private static int getPlatform(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PlayerEntity p = context.getSource().getPlayer();

        StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(context.getSource().getServer());
        p.sendMessage(Text.literal(serverState.platformcoords));
        return 1;
    }

    private static int ae(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity p = mc.player;

        GlobalDataHandler.setAutomov(true);

        p.sendMessage(Text.literal("§aAutomatic reset movement enabled."));
        return 1;
    }
    private static int ad(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity p = mc.player;

        GlobalDataHandler.setAutomov(false);

        p.sendMessage(Text.literal("§aAutomatic reset movement disabled."));
        return 1;
    }

    private static int cursorOffset(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity p = mc.player;

        offsetEnabled = true;
        cursorx = IntegerArgumentType.getInteger(context, "cursorX");
        cursory = IntegerArgumentType.getInteger(context, "cursorY");

        p.sendMessage(Text.literal("§aThe cursor will now open at (" + cursorx + "," + cursory + ")."));
        return 1;
    }

    private static int disableOffset(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity p = mc.player;

        offsetEnabled = false;
        p.sendMessage(Text.literal("§aThe cursor will no longer offset."));
        return 1;
    }

    private static int setpitch(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PlayerEntity p = context.getSource().getPlayer();

        GlobalDataHandler.setPitch(IntegerArgumentType.getInteger(context, "angle"));
        p.sendMessage(Text.literal("Reset pitch updated to §a" + IntegerArgumentType.getInteger(context, "angle") + "§r."));

        return 1;
    }

    private static int rbn(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PlayerEntity p = context.getSource().getPlayer();

        GlobalDataHandler.setRecipe(0);
        p.sendMessage(Text.literal("§aRecipe book mode set to default."));

        return 1;
    }

    private static int rbd(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PlayerEntity p = context.getSource().getPlayer();

        GlobalDataHandler.setRecipe(1);
        p.sendMessage(Text.literal("§aRecipe book mode set to disabled."));

        return 1;
    }

    private static int rbo(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PlayerEntity p = context.getSource().getPlayer();

        GlobalDataHandler.setRecipe(2);
        p.sendMessage(Text.literal("§aRecipe book mode set to occluded."));

        return 1;
    }

    private static int ilo(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PlayerEntity p = context.getSource().getPlayer();

        GlobalDataHandler.setInputlocation(0);
        p.sendMessage(Text.literal("§aInput location utils will not be displayed."));

        return 1;
    }

    private static int ilm(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PlayerEntity p = context.getSource().getPlayer();

        GlobalDataHandler.setInputlocation(1);
        p.sendMessage(Text.literal("§aInput location utils will be displayed on misses."));

        return 1;
    }

    private static int ilf(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PlayerEntity p = context.getSource().getPlayer();

        GlobalDataHandler.setInputlocation(2);
        p.sendMessage(Text.literal("§aInput location utils will always be displayed."));

        return 1;
    }


    private static int isd(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PlayerEntity p = context.getSource().getPlayer();

        GlobalDataHandler.setInputlocator(0);
        p.sendMessage(Text.literal("§aInput locator style set to dot."));

        return 1;
    }

    private static int iss(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PlayerEntity p = context.getSource().getPlayer();

        GlobalDataHandler.setInputlocator(1);
        p.sendMessage(Text.literal("§aInput locator style set to diamond."));

        return 1;
    }

    private static int isp(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PlayerEntity p = context.getSource().getPlayer();

        GlobalDataHandler.setInputlocator(2);
        p.sendMessage(Text.literal("§aInput locator style set to plus."));

        return 1;
    }


    private static int customguitime(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PlayerEntity p = context.getSource().getPlayer();

        guitime = IntegerArgumentType.getInteger(context, "ticks");
        tempguitime = guitime;
        p.sendMessage(Text.literal("§aGUI time set to " + guitime + " ticks."));


        return 1;
    }

    private static int resetcustomguitime(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PlayerEntity p = context.getSource().getPlayer();

        guitime = 0;
        p.sendMessage(Text.literal("§aGUI time set to normal, closing the table after moving 8 blocks away."));


        return 1;
    }

    private static int csd(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PlayerEntity p = context.getSource().getPlayer();

        GlobalDataHandler.setCustomSounds(0);
        p.sendMessage(Text.literal("§aGUI input sounds disabled."));

        return 1;
    }

    private static int cso(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PlayerEntity p = context.getSource().getPlayer();

        GlobalDataHandler.setCustomSounds(1);
        p.sendMessage(Text.literal("§aGUI input sound set to osu."));

        return 1;
    }

    private static int csb(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PlayerEntity p = context.getSource().getPlayer();

        GlobalDataHandler.setCustomSounds(2);
        p.sendMessage(Text.literal("§aGUI input sound set to bass kick."));

        return 1;
    }

}
