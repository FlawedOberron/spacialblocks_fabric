package net.flawedoberron.spacialblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemGroup;

public class BlockRegister
{
    public String blockId;
    public Block blockInfo;
    public ItemGroup itemGroup;
    public String[] regTags = new String[] { };

    public BlockRegister (String id, Block info, ItemGroup tab)
    {
        this.blockId = id;
        this.blockInfo = info;
        this.itemGroup = tab;
    }

    public BlockRegister (String id, Block info, ItemGroup tab, String[] tags)
    {
        this.blockId = id;
        this.blockInfo = info;
        this.itemGroup = tab;
        this.regTags = tags;
    }
}