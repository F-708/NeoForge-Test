package net.f708.examplemod.block;

import net.f708.examplemod.ExampleMod;
import net.f708.examplemod.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ExampleMod.MODID);

    public static final DeferredBlock<Block> CARVEDDIAMONDORE = registerBlock("carved_diamond_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(2.5f, 1.5f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)));


    public static final DeferredBlock<Block> CARVEDDIAMONDORE2 = registerBlock("carved_diamond_ore2",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(2.0f, 1.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)));


    public static final DeferredBlock<Block> CARVEDEMERALDORE = registerBlock("carved_emerald_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(2.5f, 1.5f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)));


    public static final DeferredBlock<Block> CARVEDEMERALDORE2 = registerBlock("carved_emerald_ore2",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(2.0f, 1.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)));


    public static final DeferredBlock<Block> CARVEDEMERALDDEEPSLATE = registerBlock("carved_emerald_deepslate",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(4.0f, 2.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.DEEPSLATE)));


    public static final DeferredBlock<Block> CARVEDEMERALDDEEPSLATE2 = registerBlock("carved_emerald_deepslate2",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(3.5f, 2.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.DEEPSLATE)));


    public static final DeferredBlock<Block> CARVEDDIAMONDDEEPSLATE = registerBlock("carved_diamond_deepslate",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(4.0f, 2.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.DEEPSLATE)));


    public static final DeferredBlock<Block> CARVEDDIAMONDDEEPSLATE2 = registerBlock("carved_diamond_deepslate2",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(3.5f, 2.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.DEEPSLATE)));











    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block){
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, Supplier<T> block){
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));


    }
    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
}
