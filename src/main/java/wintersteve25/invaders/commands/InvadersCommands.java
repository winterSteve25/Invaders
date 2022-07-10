package wintersteve25.invaders.commands;

import com.google.gson.Gson;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;
import wintersteve25.invaders.Invaders;
import wintersteve25.invaders.utils.InvadersConstants;
import wintersteve25.invaders.utils.helpers.TranslationHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InvadersCommands {
    public static void registerCommands(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("invaders").then(ReRollSpawnCommand.register()));
        dispatcher.register(Commands.literal("invaders").then(Commands.literal("debug").then(Commands.literal("dumpForests").executes(InvadersCommands::dumpForests))));
    }

    private static int dumpForests(CommandContext<CommandSource> source) {
        List<String> biomes = new ArrayList<>();
        for (Biome biome : ForgeRegistries.BIOMES.getValues().stream().filter(biome -> biome.getBiomeCategory() == Biome.Category.FOREST).collect(Collectors.toList())) {
            if (biome.getRegistryName() == null) continue;
            biomes.add(biome.getRegistryName().toString());
        }
        source.getSource().sendSuccess(TranslationHelper.getCommandSuccessMessage(InvadersConstants.LangKeys.DUMP_FOREST), false);
        Invaders.LOGGER.info(new Gson().toJson(biomes));
        return 1;
    }
}
