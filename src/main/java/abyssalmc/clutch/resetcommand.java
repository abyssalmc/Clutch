package abyssalmc.clutch;

import com.mojang.brigadier.CommandDispatcher;
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
        dispatcher.register(CommandManager.literal("platform").executes(resetcommand::resetPoint));
        dispatcher.register(CommandManager.literal("platformread").executes(resetcommand::getPlatform));

        dispatcher.register(CommandManager.literal("automov").then(CommandManager.literal("enable").executes(resetcommand::ae)));
        dispatcher.register(CommandManager.literal("automov").then(CommandManager.literal("disable").executes(resetcommand::ad)));


        dispatcher.register(CommandManager.literal("cursoroffset").then(CommandManager.argument("cursorX", IntegerArgumentType.integer()).then(CommandManager.argument("cursorY", IntegerArgumentType.integer()).executes(resetcommand::cursorOffset))));
        dispatcher.register(CommandManager.literal("disableoffset").executes(resetcommand::disableOffset));

        dispatcher.register(CommandManager.literal("pitch").then(CommandManager.argument("angle", IntegerArgumentType.integer()).executes(resetcommand::setpitch)));

        dispatcher.register(CommandManager.literal("recipebook").then(CommandManager.literal("default").executes(resetcommand::rbn)));
        dispatcher.register(CommandManager.literal("recipebook").then(CommandManager.literal("disable").executes(resetcommand::rbd)));
        dispatcher.register(CommandManager.literal("recipebook").then(CommandManager.literal("occlude").executes(resetcommand::rbo)));
    }


    private static int resetPoint(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
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

            if (mc.world.getBlockState(base.south()).getBlock() == Blocks.CRAFTING_TABLE){yaw = 180;tables++;zshift=0.5;}
            if (mc.world.getBlockState(base.west()).getBlock() == Blocks.CRAFTING_TABLE){yaw = 270;tables++;xshift=-0.5;}
            if (mc.world.getBlockState(base.north()).getBlock() == Blocks.CRAFTING_TABLE){yaw = 0;tables++;zshift=-0.5;}
            if (mc.world.getBlockState(base.east()).getBlock() == Blocks.CRAFTING_TABLE){yaw = 90;tables++;xshift=0.5;}

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



                p.sendMessage(Text.literal("§aPlatform set at (" + serverState.platformcoords + ")."));
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


}
