package wintersteve25.invaders.utils.helpers;

import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import wintersteve25.invaders.Invaders;
import wintersteve25.invaders.init.Registration;

import java.util.function.Supplier;

public class RegistryHelper {
    public static <I extends Block> RegistryObject<I> register(String name, Supplier<? extends I> block) {
        RegistryObject<I> registryObject = Registration.BLOCKS.register(name, block);
        Registration.ITEMS.register(name, () -> new BlockItem(registryObject.get(), new Item.Properties().tab(Invaders.ITEM_GROUP)));
        return registryObject;
    }

    public static <B extends Block, I extends Item> RegistryObject<B> register(String name, Supplier<? extends B> block, I blockItem) {
        RegistryObject<B> registryObject = Registration.BLOCKS.register(name, block);
        Registration.ITEMS.register(name, () -> blockItem);
        return registryObject;
    }

    public static <I extends Item> RegistryObject<I> registerItem(String name, I item) {
        return Registration.ITEMS.register(name, () -> item);
    }

    public static <I extends TileEntityType<?>> RegistryObject<I> registerTE(String name, Supplier<? extends I> te) {
        return Registration.TE.register(name, te);
    }

    public static <I extends ContainerType<?>> RegistryObject<I> registerContainer(String name, Supplier<? extends I> container) {
        return Registration.CONTAINER.register(name, container);
    }

    public static <I extends SoundEvent> RegistryObject<I> registerSounds(String name, Supplier<? extends I> sound) {
        return Registration.SOUND.register(name, sound);
    }

    public static <I extends SoundEvent> RegistryObject<SoundEvent> registerSounds(String name) {
        return Registration.SOUND.register(name, () -> createSound(name));
    }

    public static SoundEvent createSound(String name) {
        return new SoundEvent(new ResourceLocation(Invaders.MODID, name));
    }
}
