package net.mcreator.pruebamod.entity;

import javax.annotation.Nullable;

import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.GeoEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import net.minecraft.util.Mth;

import net.minecraft.tags.BlockTags;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.network.NetworkHooks;
import net.minecraft.server.level.ServerLevel;

import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.Level;

import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;

import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.LeavesBlock;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.phys.Vec3;

import net.mcreator.pruebamod.init.PruebaModModEntities;


/*
package net.mcreator.pruebamod.entity;

import net.minecraftforge.network.PlayMessages;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;

public class RedPandaEntity extends RedPandaEntityBehaviour{

	public RedPandaEntity(PlayMessages.SpawnEntity packet, Level world) {
		super(packet, world);
	}

	public RedPandaEntity(EntityType<RedPandaEntity> type, Level world) {
		super(type, world);
	}
}
*/

public class RedPandaGeckoEntity extends RedPandaEntityBehaviour implements GeoEntity{
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(RedPandaGeckoEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(RedPandaGeckoEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(RedPandaGeckoEntity.class, EntityDataSerializers.STRING);
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private boolean lastloop;
	public String animationprocedure = "empty";

	public RedPandaGeckoEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(PruebaModModEntities.RED_PANDA.get(), world);
	}

	public RedPandaGeckoEntity(EntityType<RedPandaEntity> type, Level world) {
		super(type, world);
		xpReward = 0;
		setNoAi(false);

		//FlyingAnimalConstructor();
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		// From GeoAnimatable

		return this.cache;
	}
	
	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar data) {
		// From GeoAnimatable

		data.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
		data.add(new AnimationController<>(this, "procedure", 4, this::procedurePredicate));
	}
	
	protected PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if (!this.isOnGround()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("Flight"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("Idle"));
		}
		return PlayState.STOP;
	}

	protected PlayState procedurePredicate(AnimationState event) {
		Entity entity = this;
		Level world = entity.level;
		boolean loop = false;
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		if (!loop && this.lastloop) {
			this.lastloop = false;
			event.getController().setAnimation(RawAnimation.begin().thenPlay(this.animationprocedure));
			event.getController().forceAnimationReset();
			return PlayState.STOP;
		}
		if (!this.animationprocedure.equals("empty") && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			if (!loop) {
				event.getController().setAnimation(RawAnimation.begin().thenPlay(this.animationprocedure));
				if (event.getController().getAnimationState() == AnimationController.State.STOPPED) {
					this.animationprocedure = "empty";
					event.getController().forceAnimationReset();
				}
			} else {
				event.getController().setAnimation(RawAnimation.begin().thenLoop(this.animationprocedure));
				this.lastloop = true;
			}
		}
		return PlayState.CONTINUE;
	}
	
	public String getTexture() {
		// Needed by RedPandaModel

		return this.entityData.get(TEXTURE);
	}
	
	public String getSyncedAnimation() {
		// Needed by EntityAnimationFactory

		return this.entityData.get(ANIMATION);
	}

	@Override
	protected void defineSynchedData() {
		// Needed by EntityAnimationFactory (at runtime)

		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "textura_panda_rojo");
	}

	public void setAnimation(String animation) {
		// Needed by EntityAnimationFactory

		this.entityData.set(ANIMATION, animation);
	}

	public static void init() {
		// Needed by PruebaModModEntities
		
		SpawnPlacements.register(PruebaModModEntities.RED_PANDA.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
				(entityType, world, reason, pos, random) -> (world.getBlockState(pos.below()).getMaterial() == Material.GRASS && world.getRawBrightness(pos, 0) > 8));
	}
}

/*
public class RedPandaEntityBehaviour extends TamableAnimal implements FlyingAnimal, GeoEntity, ContainerListener, HasCustomInventoryScreen, OwnableEntity, PlayerRideableJumping, Saddleable{
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(RedPandaEntityBehaviour.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(RedPandaEntityBehaviour.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(RedPandaEntityBehaviour.class, EntityDataSerializers.STRING);
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";
	
	public float flap;
	public float flapSpeed;
	public float oFlapSpeed;
	public float oFlap;
	private float flapping = 1.0F;
	private float nextFlap = 1.0F;

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}
	
	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar data) {
		data.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
		data.add(new AnimationController<>(this, "procedure", 4, this::procedurePredicate));
	}

	public RedPandaEntityBehaviour(PlayMessages.SpawnEntity packet, Level world) {
		this(PruebaModModEntities.RED_PANDA.get(), world);
	}

	public RedPandaEntityBehaviour(EntityType<RedPandaEntity> type, Level world) {
		super(type, world);
		xpReward = 0;
		setNoAi(false);

		FlyingAnimalConstructor();
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "textura_panda_rojo");
	}

	public void setTexture(String texture) {
		this.entityData.set(TEXTURE, texture);
	}

	public String getTexture() {
		return this.entityData.get(TEXTURE);
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth();
			}
		});
      	this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D));
		//this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1));
      	this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
      	this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.0D, 5.0F, 1.0F, true));
		this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
		this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
		this.goalSelector.addGoal(5, new FloatGoal(this));
	}

	@Override
	public boolean isBaby() {
		return false;
	}

	@Override
	public boolean isFood(ItemStack p_29446_) {
		return false;
	}

	@Override
	public boolean canMate(Animal p_29381_) {
		return false;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	protected void doPush(Entity p_29367_) {
		super.doPush(p_29367_);
	}

	@Override
	public MobType getMobType() {
		return MobType.UNDEFINED;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.hurt"));
	}

	@Override
	public SoundEvent getDeathSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.death"));
	}

	@Override
	public void baseTick() {
		super.baseTick();
		this.refreshDimensions();
	}

	@Override
	public EntityDimensions getDimensions(Pose p_33597_) {
		return super.getDimensions(p_33597_).scale((float) 1);
	}

	public static void init() {
		SpawnPlacements.register(PruebaModModEntities.RED_PANDA.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
				(entityType, world, reason, pos, random) -> (world.getBlockState(pos.below()).getMaterial() == Material.GRASS && world.getRawBrightness(pos, 0) > 8));
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
		builder = builder.add(Attributes.MAX_HEALTH, 10);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 3);
		builder = builder.add(Attributes.FOLLOW_RANGE, 16);
		builder = builder.add(Attributes.FLYING_SPEED, (double)0.4F);

		return builder;
	}

	protected PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if (!this.isOnGround()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("Flight"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("Idle"));
		}
		return PlayState.STOP;
	}

	protected PlayState procedurePredicate(AnimationState event) {
		Entity entity = this;
		Level world = entity.level;
		boolean loop = false;
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		if (!loop && this.lastloop) {
			this.lastloop = false;
			event.getController().setAnimation(RawAnimation.begin().thenPlay(this.animationprocedure));
			event.getController().forceAnimationReset();
			return PlayState.STOP;
		}
		if (!this.animationprocedure.equals("empty") && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			if (!loop) {
				event.getController().setAnimation(RawAnimation.begin().thenPlay(this.animationprocedure));
				if (event.getController().getAnimationState() == AnimationController.State.STOPPED) {
					this.animationprocedure = "empty";
					event.getController().forceAnimationReset();
				}
			} else {
				event.getController().setAnimation(RawAnimation.begin().thenLoop(this.animationprocedure));
				this.lastloop = true;
			}
		}
		return PlayState.CONTINUE;
	}

	@Override
	protected void tickDeath() {
		++this.deathTime;
		if (this.deathTime == 20) {
			this.remove(RedPandaEntityBehaviour.RemovalReason.KILLED);
			this.dropExperience();
		}
	}

	public String getSyncedAnimation() {
		return this.entityData.get(ANIMATION);
	}

	public void setAnimation(String animation) {
		this.entityData.set(ANIMATION, animation);
	}
	
	public void aiStep() {
		// Animal.java
		// AgeableMob.java
		// Mob.java
		// LivingEntity.java

		super.aiStep();
		//this.calculateFlapping();
	}

	@Override
	protected PathNavigation createNavigation(Level p_29417_) {
		FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, p_29417_);
		flyingpathnavigation.setCanOpenDoors(false);
		flyingpathnavigation.setCanFloat(true);
		flyingpathnavigation.setCanPassDoors(false);
		return flyingpathnavigation;
	}

	@Override
	public boolean causeFallDamage(float l, float d, DamageSource source) {
		return false;
	}

	@Override
	protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
	}

	@Override
	public void setNoGravity(boolean ignored) {
		super.setNoGravity(false);
	}

	// interface FlyingAnimal
	private void FlyingAnimalConstructor()
	{
		int maxDegreesPerTick = 1;
		boolean hoversInPlace = false;
      	this.moveControl = new FlyingMoveControl(this, maxDegreesPerTick, hoversInPlace);
	}

	public boolean isFlying() {
		return !this.onGround;
	}

	private void calculateFlapping() {
		this.oFlap = this.flap;
		this.oFlapSpeed = this.flapSpeed;
		this.flapSpeed += (float)(!this.onGround && !this.isPassenger() ? 4 : -1) * 0.3F;
		this.flapSpeed = Mth.clamp(this.flapSpeed, 0.0F, 1.0F);
		if (!this.onGround && this.flapping < 1.0F) {
			this.flapping = 1.0F;
		}

		this.flapping *= 0.9F;
		Vec3 vec3 = this.getDeltaMovement();
		if (!this.onGround && vec3.y < 0.0D) {
			this.setDeltaMovement(vec3.multiply(1.0D, 0.6D, 1.0D));
		}

		this.flap += this.flapping * 2.0F;
	}

	@Override
	protected boolean isFlapping() {
		// called from entity.java
			
		return this.flyDist > this.nextFlap;
	}

	@Override
	protected void onFlap() {
		// called from entity.java

		this.playSound(SoundEvents.PARROT_FLY, 0.15F, 1.0F);
		this.nextFlap = this.flyDist + this.flapSpeed / 2.0F;
	}
	
	// interface FlyingAnimal
	@Nullable
	public AgeableMob getBreedOffspring(ServerLevel p_148993_, AgeableMob p_148994_) {
		return null;
	}

	static class RedPandaWanderGoal extends WaterAvoidingRandomFlyingGoal {
		public RedPandaWanderGoal(PathfinderMob p_186224_, double p_186225_) {
			super(p_186224_, p_186225_);
		}

		@Nullable
		protected Vec3 getPosition() {
			Vec3 vec3 = null;
			if (this.mob.isInWater()) {
				vec3 = LandRandomPos.getPos(this.mob, 15, 15);
			}

			return vec3 == null ? super.getPosition() : vec3;
		}
	}
}*/