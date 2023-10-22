package net.mcreator.pruebamod.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.mcreator.pruebamod.entity.RedPandaEntity;

public class RedPandaModel extends GeoModel<RedPandaEntity> {
	@Override
	public ResourceLocation getAnimationResource(RedPandaEntity entity) {
		return new ResourceLocation("prueba_mod", "animations/prueba_redpanda.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(RedPandaEntity entity) {
		return new ResourceLocation("prueba_mod", "geo/prueba_redpanda.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(RedPandaEntity entity) {
		return new ResourceLocation("prueba_mod", "textures/entities/" + entity.getTexture() + ".png");
	}

}
