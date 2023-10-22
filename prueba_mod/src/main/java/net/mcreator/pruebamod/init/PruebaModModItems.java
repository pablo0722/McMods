
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.pruebamod.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.common.ForgeSpawnEggItem;

import net.minecraft.world.item.Item;

import net.mcreator.pruebamod.PruebaModMod;

public class PruebaModModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, PruebaModMod.MODID);
	public static final RegistryObject<Item> RED_PANDA_SPAWN_EGG = REGISTRY.register("red_panda_spawn_egg", () -> new ForgeSpawnEggItem(PruebaModModEntities.RED_PANDA, -26368, -39424, new Item.Properties()));
}
