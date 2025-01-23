package abyssalmc.clutch;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ToolItem;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.CommandBlockExecutor;

import java.util.List;

import static abyssalmc.clutch.Clutch.*;

public class ClutchCommand {

    public static double yaw;


    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment){
        dispatcher.register(CommandManager.literal("clutch").then(CommandManager.literal("platform").executes(ClutchCommand::resetPointNoArgs)));
        dispatcher.register(CommandManager.literal("clutch").then(CommandManager.literal("platform").then(CommandManager.argument("coord offset", DoubleArgumentType.doubleArg()).executes(ClutchCommand::resetPoint))));
        dispatcher.register(CommandManager.literal("clutch").then(CommandManager.literal("platformread").executes(ClutchCommand::getPlatform)));

        dispatcher.register(CommandManager.literal("clutch").then(CommandManager.literal("automov").then(CommandManager.literal("enable").executes(ClutchCommand::ae))));
        dispatcher.register(CommandManager.literal("clutch").then(CommandManager.literal("automov").then(CommandManager.literal("disable").executes(ClutchCommand::ad))));

        dispatcher.register(CommandManager.literal("clutch").then(CommandManager.literal("cursoroffset").then(CommandManager.argument("cursorX", IntegerArgumentType.integer()).then(CommandManager.argument("cursorY", IntegerArgumentType.integer()).executes(ClutchCommand::cursorOffset)))));
        dispatcher.register(CommandManager.literal("clutch").then(CommandManager.literal("disableoffset").executes(ClutchCommand::disableOffset)));

        dispatcher.register(CommandManager.literal("clutch").then(CommandManager.literal("pitch").then(CommandManager.argument("angle", IntegerArgumentType.integer()).executes(ClutchCommand::setpitch))));

        dispatcher.register(CommandManager.literal("clutch").then(CommandManager.literal("recipebook").then(CommandManager.literal("default").executes(ClutchCommand::rbn))));
        dispatcher.register(CommandManager.literal("clutch").then(CommandManager.literal("recipebook").then(CommandManager.literal("disable").executes(ClutchCommand::rbd))));
        dispatcher.register(CommandManager.literal("clutch").then(CommandManager.literal("recipebook").then(CommandManager.literal("occlude").executes(ClutchCommand::rbo))));

        dispatcher.register(CommandManager.literal("clutch").then(CommandManager.literal("inputloc").then(CommandManager.literal("off").executes(ClutchCommand::ilo))));
        dispatcher.register(CommandManager.literal("clutch").then(CommandManager.literal("inputloc").then(CommandManager.literal("misses").executes(ClutchCommand::ilm))));
        dispatcher.register(CommandManager.literal("clutch").then(CommandManager.literal("inputloc").then(CommandManager.literal("full").executes(ClutchCommand::ilf))));

        dispatcher.register(CommandManager.literal("clutch").then(CommandManager.literal("inputlocstyle").then(CommandManager.literal("dot").executes(ClutchCommand::isd))));
        dispatcher.register(CommandManager.literal("clutch").then(CommandManager.literal("inputlocstyle").then(CommandManager.literal("diamond").executes(ClutchCommand::iss))));
        dispatcher.register(CommandManager.literal("clutch").then(CommandManager.literal("inputlocstyle").then(CommandManager.literal("plus").executes(ClutchCommand::isp))));

        dispatcher.register(CommandManager.literal("clutch").then(CommandManager.literal("guitime").then(CommandManager.literal("set").then(CommandManager.argument("ticks", IntegerArgumentType.integer()).executes(ClutchCommand::customguitime)))));
        dispatcher.register(CommandManager.literal("clutch").then(CommandManager.literal("guitime").then(CommandManager.literal("default").executes(ClutchCommand::resetcustomguitime))));

        dispatcher.register(CommandManager.literal("clutch").then(CommandManager.literal("inputsounds").then(CommandManager.literal("off").executes(ClutchCommand::csd))));
        dispatcher.register(CommandManager.literal("clutch").then(CommandManager.literal("inputsounds").then(CommandManager.literal("osu").executes(ClutchCommand::cso))));
        dispatcher.register(CommandManager.literal("clutch").then(CommandManager.literal("inputsounds").then(CommandManager.literal("basskick").executes(ClutchCommand::csb))));

        dispatcher.register(CommandManager.literal("clutch").then(CommandManager.literal("attempts").then(CommandManager.literal("get").executes(ClutchCommand::attemptsget))));
        dispatcher.register(CommandManager.literal("clutch").then(CommandManager.literal("attempts").then(CommandManager.literal("reset").executes(ClutchCommand::attemptsreset))));
        dispatcher.register(CommandManager.literal("clutch").then(CommandManager.literal("attempts").then(CommandManager.literal("clear").executes(ClutchCommand::attemptsclear))));

        dispatcher.register(CommandManager.literal("clutch").then(CommandManager.literal("health").then(CommandManager.argument("health", FloatArgumentType.floatArg()).executes(ClutchCommand::sethealth))));
        dispatcher.register(CommandManager.literal("clutch").then(CommandManager.literal("hunger").then(CommandManager.argument("hunger", IntegerArgumentType.integer()).executes(ClutchCommand::sethunger))));
        dispatcher.register(CommandManager.literal("clutch").then(CommandManager.literal("saturation").then(CommandManager.argument("saturation", FloatArgumentType.floatArg()).executes(ClutchCommand::setsat))));

        dispatcher.register(CommandManager.literal("clutch").then(CommandManager.literal("toggleshift").then(CommandManager.literal("enable").executes(ClutchCommand::enabletoggleshift))));
        dispatcher.register(CommandManager.literal("clutch").then(CommandManager.literal("toggleshift").then(CommandManager.literal("disable").executes(ClutchCommand::disabletoggleshift))));

        dispatcher.register(CommandManager.literal("clutch").then(CommandManager.literal("projectilerng").then(CommandManager.literal("enable").executes(ClutchCommand::enableprojectilerng))));
        dispatcher.register(CommandManager.literal("clutch").then(CommandManager.literal("projectilerng").then(CommandManager.literal("disable").executes(ClutchCommand::disableprojectilerng))));

        dispatcher.register(CommandManager.literal("clutch").then(CommandManager.literal("setupgen").then(CommandManager.argument("setup id", StringArgumentType.string()).executes(ClutchCommand::setup))));

        dispatcher.register(CommandManager.literal("clutch").then(CommandManager.literal("stalls").then(CommandManager.literal("enable").executes(ClutchCommand::enablestalls))));
        dispatcher.register(CommandManager.literal("clutch").then(CommandManager.literal("stalls").then(CommandManager.literal("disable").executes(ClutchCommand::disablestalls))));

        dispatcher.register(CommandManager.literal("clutch").then(CommandManager.literal("extinguish").executes(ClutchCommand::extinguish)));

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

            if (mc.world.getBlockState(base.south()).createScreenHandlerFactory(mc.world, base.south()) != null){yaw = 180;tables++;}
            if (mc.world.getBlockState(base.west()).createScreenHandlerFactory(mc.world, base.west()) != null){yaw = 270;tables++;}
            if (mc.world.getBlockState(base.north()).createScreenHandlerFactory(mc.world, base.north()) != null){yaw = 0;tables++;}
            if (mc.world.getBlockState(base.east()).createScreenHandlerFactory(mc.world, base.east()) != null){yaw = 90;tables++;}

            if (tables == 0){
                p.sendMessage(Text.literal("§cThere must be a block entity adjacent to the standing block!"));
                configured = false;
            }
            if (tables >= 2){
                p.sendMessage(Text.literal("§cThere can only be one block entity adjacent to the standing block!"));
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

            if (mc.world.getBlockState(base.south()).createScreenHandlerFactory(mc.world, base.south()) != null){yaw = 180;tables++;zshift=offset;}
            if (mc.world.getBlockState(base.west()).createScreenHandlerFactory(mc.world, base.west()) != null){yaw = 270;tables++;xshift=-offset;}
            if (mc.world.getBlockState(base.north()).createScreenHandlerFactory(mc.world, base.north()) != null){yaw = 0;tables++;zshift=-offset;}
            if (mc.world.getBlockState(base.east()).createScreenHandlerFactory(mc.world, base.east()) != null){yaw = 90;tables++;xshift=offset;}

            if (tables == 0){
                p.sendMessage(Text.literal("§cThere must be a block entity adjacent to the standing block!"));
                configured = false;
            }
            if (tables >= 2){
                p.sendMessage(Text.literal("§cThere can only be one block entity adjacent to the standing block!"));
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

        if (context.getSource().isExecutedByPlayer() && context.getSource().getEntity() instanceof ServerPlayerEntity){
            p.sendMessage(Text.literal("§aThe cursor will now open at (" + cursorx + "," + cursory + ")."));
        }
       return 1;
    }

    private static int disableOffset(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity p = mc.player;

        offsetEnabled = false;
        if (context.getSource().isExecutedByPlayer()){
            p.sendMessage(Text.literal("§aThe cursor will no longer offset."));
        }
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

    private static int resetplatformthing(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(MinecraftClient.getInstance().getServer());
        serverState.platformattempts = "";
        return 1;
    }


    private static int attemptsget(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PlayerEntity p = context.getSource().getPlayer();

        if (MinecraftClient.getInstance().isIntegratedServerRunning() && MinecraftClient.getInstance().getServer() != null) {
            StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(MinecraftClient.getInstance().getServer());

            String pta = serverState.platformattempts;
            String[] allattempts = pta.split(" \\| ");

            BlockPos platepos = context.getSource().getPlayer().getBlockPos();
            int attemptcount = -1;
            for (String attempts : allattempts) {
                if (attempts.contains("(" + platepos.getX() + ", " + platepos.getY() + ", " + platepos.getZ())) {
                    attemptcount = Integer.parseInt(attempts.split(" ")[attempts.split(" ").length - 1]) + 1;
                }
            }
            if (attemptcount != -1) {
                p.sendMessage(Text.literal("§aThere have been " + (attemptcount-1) + " attempts on this platform."));
            } else {
                p.sendMessage(Text.literal("§cThere are no recorded attempts here. Make sure you are standing on the platform pressure plate!"));
            }
        }


        return 1;
    }

    private static int attemptsreset(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PlayerEntity p = context.getSource().getPlayer();

        StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(MinecraftClient.getInstance().getServer());

        String pta = serverState.platformattempts;
        String[] allattempts = pta.split(" \\| ");

        BlockPos platepos = context.getSource().getPlayer().getBlockPos();

        int removalcount = 0;
        String newattemptstring = "";
        for (String attempts : allattempts) {
            if (!attempts.contains("(" + platepos.getX() + ", " + platepos.getY() + ", " + platepos.getZ())) {
                newattemptstring = attempts + " | ";
            } else {
                removalcount++;
            }
        }
        if (removalcount == 0) {
            p.sendMessage(Text.literal("§cCould not remove attempts as none are recorded. Make sure to stand on the platform pressure plate!"));
        } else {
            p.sendMessage(Text.literal("§aPlatform attempts reset to 0."));
        }
        serverState.platformattempts = newattemptstring;
        return 1;
    }


    private static int attemptsclear(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PlayerEntity p = context.getSource().getPlayer();

        StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(MinecraftClient.getInstance().getServer());

        String pta = serverState.platformattempts;

        serverState.platformattempts = "";
        p.sendMessage(Text.literal("§aAll persistent platform attempts have been cleared."));

        return 1;
    }

    private static int sethealth(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PlayerEntity p = context.getSource().getPlayer();

        p.setHealth(FloatArgumentType.getFloat(context, "health"));

        return 1;
    }

    private static int sethunger(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PlayerEntity p = context.getSource().getPlayer();

        HungerManager hm = p.getHungerManager();
        hm.setFoodLevel(IntegerArgumentType.getInteger(context, "hunger"));

        return 1;
    }

    private static int setsat(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PlayerEntity p = context.getSource().getPlayer();

        HungerManager hm = p.getHungerManager();
        hm.setSaturationLevel(FloatArgumentType.getFloat(context, "saturation"));

        return 1;
    }

    private static int enabletoggleshift(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity p = mc.player;

        GlobalDataHandler.setToggleShift(true);

        p.sendMessage(Text.literal("§aToggle shift enabled (works in guis too). This only works if other toggle sneaks are disabled, including the vanilla one. There may also be issues using this on servers."));
        return 1;
    }
    private static int disabletoggleshift(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity p = mc.player;

        GlobalDataHandler.setToggleShift(false);

        p.sendMessage(Text.literal("§aToggle shift disabled."));
        return 1;
    }

    private static int enableprojectilerng(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity p = client.player;

        if (client.isIntegratedServerRunning() && client.getServer() != null) {
            StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(client.getServer());
            p.sendMessage(Text.literal("§aProjectiles will now have rng on this world."));
            serverState.projectilerng = true;
        } else {
            p.sendMessage(Text.literal("§cThis command can only be used in singleplayer."));
        }
        return 1;
    }
    private static int disableprojectilerng(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity p = client.player;

        if (client.isIntegratedServerRunning() && client.getServer() != null) {
            StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(client.getServer());
            p.sendMessage(Text.literal("§aProjectiles will no longer have rng on this world."));
            serverState.projectilerng = false;
        } else {
            p.sendMessage(Text.literal("§cThis command can only be used in singleplayer."));
        }
        return 1;
    }


    private static int setup(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String setupid = StringArgumentType.getString(context, "setup id");

        BlockPos platform = new BlockPos(context.getSource().getPlayer().getBlockPos().getX(), context.getSource().getPlayer().getBlockPos().getY(), context.getSource().getPlayer().getBlockPos().getZ());

        // ITEM READER
        String[] itemlist = setupid.split("S");
        BlockPos cmd = platform.add(0,-2,0);
        context.getSource().getServer().getCommandManager().getDispatcher().execute("gamerule sendCommandFeedback false", context.getSource());
        context.getSource().getServer().getCommandManager().getDispatcher().execute("gamerule commandBlockOutput false", context.getSource());
        context.getSource().getServer().getCommandManager().getDispatcher().execute("setblock " + cmd.getX() + " " +  cmd.getY() + " " + cmd.getZ() + " command_block[facing=down]{auto:0b,Command:\"clear @p\"}", context.getSource());

        for (String item : itemlist) {
            cmd = cmd.add(0,-1,0);
            String invslot = "";

            String itemname;
            int itemcount;
            if (Character.isDigit(item.charAt(0))) {
                String[] splitproc1 = item.split("(?<=\\d)(?=\\D)", 2);

                int itemslot = Integer.parseInt(splitproc1[0]);
                String[] splitproc2 = splitproc1[1].split("(?=\\d+$)", 2);
                itemname = splitproc2[0];

                invslot = "container." + itemslot;

                itemcount = Integer.parseInt(splitproc2[1]);
            }else{
                String itemslot = item.substring(0, 1);

                String splitproc1 = item.substring(1);
                String[] splitproc2 = splitproc1.split("(?=\\d+$)", 2);
                itemname = splitproc2[0];
                itemcount = Integer.parseInt(splitproc2[1]);

                if (itemslot.contains("h")) {
                    invslot = "armor.head";
                }
                if (itemslot.contains("c")) {
                    invslot = "armor.chest";
                }
                if (itemslot.contains("l")) {
                    invslot = "armor.legs";
                }
                if (itemslot.contains("f")) {
                    invslot = "armor.feet";
                }
                if (itemslot.contains("o")) {
                    invslot = "weapon.offhand";
                }
            }

            String itemclause = itemname;

            Identifier itemId = Identifier.tryParse("minecraft", itemname);

            Item regitem = Registries.ITEM.get(itemId);
            ItemStack itemstack = new ItemStack(regitem, 1);

            if (itemname.contains("E")) {
                String[] enchitem = itemname.split("E");
                itemname = enchitem[0];

                String[] itemench = enchitem[1].split("(?=\\d+$)", 2);
                String enchtype = itemench[0];
                String enchlevel = itemench[1];

                if (itemname.contains("enchanted_book")){
                    itemclause = itemname + "[stored_enchantments={levels:{\\\"minecraft:" + enchtype + "\\\":" + enchlevel + "}}]";
                } else {
                    itemclause = itemname + "[enchantments={levels:{\\\"minecraft:" + enchtype + "\\\":" + enchlevel + "}}]";
                }
            }

            context.getSource().getServer().getCommandManager().getDispatcher().execute("setblock " + cmd.getX() + " " +  cmd.getY() + " " + cmd.getZ() + " chain_command_block[facing=down]{auto:1b,Command:\"item replace entity @p " + invslot + " with " + itemclause + " " + itemcount + "\"}", context.getSource());

        }

        return 1;
    }

    private static int enablestalls(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity p = client.player;

        if (client.isIntegratedServerRunning() && client.getServer() != null) {
            p.sendMessage(Text.literal("§aStalls are now enabled."));
            GlobalDataHandler.setStalls(true);
        } else {
            p.sendMessage(Text.literal("§cThis command can only be used in singleplayer."));
        }
        return 1;
    }
    private static int disablestalls(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity p = client.player;

        if (client.isIntegratedServerRunning() && client.getServer() != null) {
            p.sendMessage(Text.literal("§aStalls are now disabled."));
            GlobalDataHandler.setStalls(false);
        } else {
            p.sendMessage(Text.literal("§cThis command can only be used in singleplayer."));
        }
        return 1;
    }

    private static int extinguish(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity p = client.player;

        p.setOnFire(false);
        return 1;
    }
}
