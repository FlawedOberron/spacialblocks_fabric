package net.flawedoberron.spacialblocks;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.flawedoberron.spacialblocks.block.customblocks.ModStairsBlock;
import net.flawedoberron.spacialblocks.block.customblocks.PanelBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.Material;
import net.minecraft.block.StairsBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
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
                "spruce",
                "warped_stem"
        };

        for (String nextWood : woodTypes)
        {
            GenerateConstructiveBlocks(nextWood,
                    true,
                    true,
                    FabricBlockSettings.of(Material.WOOD).strength(2f).sounds(BlockSoundGroup.WOOD).breakByHand(true),
                    true,
                    FabricBlockSettings.of(Material.WOOD).strength(1.5f).sounds(BlockSoundGroup.WOOD).breakByHand(true));
        }

        // Generate any additional types for stone blocks
        String[] stoneTypes = new String[]
        {
                "stone",
                "stone_bricks",
                "cobblestone",
                "andesite",
                "polished_andesite",
                "granite",
                "polished_granite",
                "diorite",
                "polished_diorite",
                "sandstone",
                "smooth_sandstone",
                "prismarine",
                "prismarine_bricks",
                "dark_prismarine"
        };

        for (String nextStone : stoneTypes)
        {
            GenerateConstructiveBlocks(nextStone,
                    false,
                    true,
                    FabricBlockSettings.of(Material.STONE).strength(1.5f).sounds(BlockSoundGroup.STONE).breakByHand(false).requiresTool(),
                    false,
                    FabricBlockSettings.of(Material.STONE).strength(1.5f).sounds(BlockSoundGroup.STONE).breakByHand(false).requiresTool());
        }

        // Register blocks and register any items as well...
        for (BlockRegister next : allBlocks)
        {
            RegisterBlock(next);
        }
    }

    private static void GenerateConstructiveBlocks(String type, Boolean incVert, Boolean incSlope, FabricBlockSettings baseBlockSettings, Boolean incPlanked, FabricBlockSettings plankedBlockSettings)
    {
        String[] regTags = new String[] {
                "wooden_fences",
                "fences"
        };

        var basePanel = new PanelBlock(baseBlockSettings);

        // Adds panels...
        allBlocks.add(new BlockRegister("panel_" + type.toLowerCase() + ((incVert || incPlanked) ? "_wh" : ""), new PanelBlock(baseBlockSettings), ItemGroup.BUILDING_BLOCKS, regTags));

        if (incVert)
        {
            allBlocks.add(new BlockRegister("panel_" + type.toLowerCase() + "_wv", new PanelBlock(baseBlockSettings), ItemGroup.BUILDING_BLOCKS, regTags));
        }

        if (incPlanked)
        {
            // Adds panels...
            allBlocks.add(new BlockRegister("panel_" + type.toLowerCase() + "_ph", new PanelBlock(plankedBlockSettings), ItemGroup.BUILDING_BLOCKS, regTags));

            if (incVert)
            {
                allBlocks.add(new BlockRegister("panel_" + type.toLowerCase() + "_pv", new PanelBlock(plankedBlockSettings), ItemGroup.BUILDING_BLOCKS, regTags));
            }
        }

        if (incSlope)
        {
            allBlocks.add(new BlockRegister("slope_" + type.toLowerCase(), new ModStairsBlock(basePanel.getDefaultState(), baseBlockSettings), ItemGroup.BUILDING_BLOCKS, regTags));

            if (incPlanked)
            {
                allBlocks.add(new BlockRegister("slope_" + type.toLowerCase() + "_p", new ModStairsBlock(basePanel.getDefaultState(), plankedBlockSettings), ItemGroup.BUILDING_BLOCKS, regTags));
            }
        }
    }

    private static void RegisterBlock(BlockRegister info)
    {
        Registry.register(Registry.BLOCK, new Identifier(MOD_ID, info.blockId), info.blockInfo);
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, info.blockId), new BlockItem(info.blockInfo, new FabricItemSettings().group(info.itemGroup)));
    }
}
