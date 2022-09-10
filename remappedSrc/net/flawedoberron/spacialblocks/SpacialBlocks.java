package net.flawedoberron.spacialblocks;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.Material;
import net.minecraft.block.StairsBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

// The value here should match an entry in the META-INF/mods.toml file
public class SpacialBlocks implements ModInitializer
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "spacialblocks";

    private static List<BlockRegister> allBlocks = new ArrayList<> ();

    @Override
    public void onInitialize()
    {
        // Register all wood constructor blocks...
        String[] woodTypes = new String[]
        {
                "acacia",
                "birch",
                "crimson_stem",
                "dark_oak",
                "jungle",
                "mangrove",
                "oak",
                "warped_stem"
        };

        for (String nextWood : woodTypes)
        {
            GenerateConstructiveBlocks(nextWood,
                    true,
                    true,
                    FabricBlockSettings.of(Material.WOOD).strength(2f).sounds(BlockSoundGroup.WOOD),
                    true,
                    FabricBlockSettings.of(Material.WOOD).strength(1.5f).sounds(BlockSoundGroup.WOOD));
        }

        // Register blocks and register any items as well...
        for (BlockRegister next : allBlocks)
        {
            RegisterBlock(next);
        }
    }

    private static void GenerateConstructiveBlocks(String type, Boolean incVert, Boolean incSlope, FabricBlockSettings baseBlockSettings, Boolean incPlanked, FabricBlockSettings plankedBlockSettings)
    {
        // Adds panels...
        allBlocks.add(new BlockRegister("panel_" + type.toLowerCase() + "_wh", new FenceBlock(baseBlockSettings), ItemGroup.BUILDING_BLOCKS));

        if (incVert)
        {
            allBlocks.add(new BlockRegister("panel_" + type.toLowerCase() + "_wv", new FenceBlock(baseBlockSettings), ItemGroup.BUILDING_BLOCKS));
        }

        if (incPlanked)
        {
            // Adds panels...
            allBlocks.add(new BlockRegister("panel_" + type.toLowerCase() + "_ph", new FenceBlock(plankedBlockSettings), ItemGroup.BUILDING_BLOCKS));

            if (incVert)
            {
                allBlocks.add(new BlockRegister("panel_" + type.toLowerCase() + "_pv", new FenceBlock(plankedBlockSettings), ItemGroup.BUILDING_BLOCKS));
            }
        }

        if (incSlope)
        {

        }
    }

    private static void RegisterBlock(BlockRegister info)
    {
        Registry.register(Registry.BLOCK, new Identifier(MOD_ID, info.blockId), info.blockInfo);
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, info.blockId), new BlockItem(info.blockInfo, new FabricItemSettings().group(info.itemGroup)));
    }
}
