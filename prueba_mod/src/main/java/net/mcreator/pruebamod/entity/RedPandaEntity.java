
package net.mcreator.pruebamod.entity;

import net.minecraftforge.network.PlayMessages;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;

public class RedPandaEntity extends RedPandaGeckoEntity{

	public RedPandaEntity(PlayMessages.SpawnEntity packet, Level world) {
		super(packet, world);
	}

	public RedPandaEntity(EntityType<RedPandaEntity> type, Level world) {
		super(type, world);
	}
}