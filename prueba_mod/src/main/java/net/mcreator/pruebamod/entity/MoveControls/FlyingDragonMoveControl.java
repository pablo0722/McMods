package net.mcreator.pruebamod.entity;

import java.util.Stack; 

import net.minecraft.world.entity.ai.control.*;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

public class FlyingDragonMoveControl extends MoveControl {
   private final int maxTurn;
   private final boolean alwaysFlying;
   private final double flyHeight;
   private final double flyThresholdStartDistance;
   private final double flyThresholdEndDistance;
   private final Level level;

   private double moveToPositionX;
   private double moveToPositionY;
   private double moveToPositionZ;
   private State state = State.IDLE;

   private static FlyingDragonMoveControl lastObj = null;
   
   public enum State
   {
      IDLE, WALKING, START_FLYING, FLYING, STOP_FLYING
   }

   public FlyingDragonMoveControl(Mob This, Level level, int maxDegreesPerTick, boolean alwaysFlying, double flyHeight, double flyThresholdStartDistance, double flyThresholdEndDistance) {
      super(This);
      this.level = level;
      this.maxTurn = maxDegreesPerTick;
      this.alwaysFlying = alwaysFlying;
      this.flyHeight = flyHeight;
      this.flyThresholdStartDistance = flyThresholdStartDistance;
      this.flyThresholdEndDistance = flyThresholdEndDistance;

      FlyingDragonMoveControl.lastObj = this;
   }
   
   @Override
   public void setWantedPosition(double p_24984_, double p_24985_, double p_24986_, double p_24987_) {
      System.out.println(System.identityHashCode(this) + " setWantedPosition");
      this.wantedX = p_24984_+3;
      this.wantedY = p_24985_;
      this.wantedZ = p_24986_+3;
      this.speedModifier = p_24987_;
      if (this.operation != MoveControl.Operation.JUMPING) {
         this.operation = MoveControl.Operation.MOVE_TO;
      }

   }

   protected double getGroundY(Vec3 p_186132_) {
      BlockPos blockpos = BlockPos.containing(p_186132_);
      return WalkNodeEvaluator.getFloorLevel(this.level, blockpos);
   }

   public void tick() {
      if (this.operation == MoveControl.Operation.MOVE_TO) {
         if(this == FlyingDragonMoveControl.lastObj)
            System.out.println(System.identityHashCode(this) + " MOVE TO START: " + this.state.name());
         this.operation = MoveControl.Operation.WAIT;

         if(this.wantedX == this.moveToPositionX && this.wantedY == this.moveToPositionY && this.wantedZ == this.moveToPositionZ) {
            return;
         }

         this.moveToPositionX = this.wantedX;
         this.moveToPositionY = this.wantedY;
         this.moveToPositionZ = this.wantedZ;

         double dx = this.moveToPositionX - this.mob.getX();
         double dy = this.moveToPositionY - this.mob.getY();
         double dz = this.moveToPositionZ - this.mob.getZ();
         double d2 = Math.sqrt(dx * dx + dz * dz);
         double d3 = Math.sqrt(dx * dx + dy * dy + dz * dz);
         //System.out.println("Y: " + Double.toString(this.mob.getY()));

         if (d2 < (double)1.0F) {
            this.mob.setZza(0.0F);
         }

         if (Math.abs(dy) < (double)1.0F) {
            this.mob.setNoGravity(false);
            this.mob.setYya(0.0F);

            if (d2 < (double)1.0F) {
               this.state = State.IDLE;
               return;
            }
         }
         
         switch(this.state) {
            case IDLE:
               if(d3 >= flyThresholdStartDistance) {
                  this.state = State.START_FLYING;
               } else {
                  this.state = State.WALKING;
               }
               break;
            case WALKING:
               if(d3 >= flyThresholdStartDistance) {
                  this.mob.setNoGravity(true);
                  this.state = State.START_FLYING;
               }
               break;
            case START_FLYING:
               if(d3 < flyThresholdEndDistance) {
                  this.state = State.STOP_FLYING;
               }
               break;
            case FLYING:
               if(d3 < flyThresholdEndDistance) {
                  this.state = State.STOP_FLYING;
               }
               break;
            case STOP_FLYING:
               if(d3 >= flyThresholdStartDistance) {
                  this.state = State.START_FLYING;
               }
               break;
            default:
               this.state = State.IDLE;
         }

         float f = (float)(Mth.atan2(dz, dx) * (double)(180F / (float)Math.PI)) - 90.0F;
         this.mob.setYRot(this.rotlerp(this.mob.getYRot(), f, 90.0F));

         float speed;
         if (this.state == State.IDLE || this.state == State.WALKING) {
            speed = (float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
         } else {
            speed = (float)(this.speedModifier * this.mob.getAttributeValue(Attributes.FLYING_SPEED));
         }
         this.mob.setSpeed(speed);
         if(this == FlyingDragonMoveControl.lastObj)
            System.out.println(System.identityHashCode(this) + " MOVE TO END: " + this.state.name());
      } else {
         if(this == FlyingDragonMoveControl.lastObj)
            System.out.println(System.identityHashCode(this) + " TICK START: " + this.state.name());
         if (this.state != State.IDLE) {
            double dx = this.moveToPositionX - this.mob.getX();
            double dy = this.moveToPositionY - this.mob.getY();
            double dz = this.moveToPositionZ - this.mob.getZ();
            double d2 = Math.sqrt(dx * dx + dz * dz);
            double d3 = Math.sqrt(dx * dx + dy * dy + dz * dz);

            if (d2 < (double)1.0E-2F) {
               this.mob.setZza(0.0F);
            }

            if (Math.abs(dy) < (double)1.0E-2F) {
               this.mob.setNoGravity(false);
               this.mob.setYya(0.0F);

               if (d2 < (double)1.0E-2F) {
                  this.state = State.IDLE;
                  return;
               }
            }

            float flySpeed = (float) this.mob.getAttributeValue(Attributes.FLYING_SPEED);
            switch(this.state) {
               case IDLE:
                  break;
               case WALKING:
                  break;
               case START_FLYING:
                  if (Math.abs(dy) > (double)1.0E-2F || Math.abs(d2) > (double)1.0E-2F) {
                     float f2 = (float)(-(Mth.atan2(dy, d2) * (double)(180F / (float)Math.PI)));
                     this.mob.setXRot(this.rotlerp(this.mob.getXRot(), f2, (float)this.maxTurn));
                     this.mob.setYya(dy > 0.0D ? flySpeed : -flySpeed);
                  }
                  if(d2 < flyThresholdEndDistance) {
                     this.state = State.STOP_FLYING;
                  }
                  break;
               case FLYING:
                  if(d2 < flyThresholdEndDistance) {
                     this.state = State.STOP_FLYING;
                  }
                  break;
               case STOP_FLYING:
                  if (Math.abs(dy) > (double)1.0E-2F || Math.abs(d2) > (double)1.0E-2F) {
                     float f2 = (float)(-(Mth.atan2(dy, d2) * (double)(180F / (float)Math.PI)));
                     this.mob.setXRot(this.rotlerp(this.mob.getXRot(), f2, (float)this.maxTurn));
                     this.mob.setYya(dy > 0.0D ? flySpeed : -flySpeed);
                  } else {
                     this.mob.setNoGravity(false);
                  }
                  break;
               default:
                  this.state = State.IDLE;
            }
         }
         if(this == FlyingDragonMoveControl.lastObj)
            System.out.println(System.identityHashCode(this) + " TICK END: " + this.state.name());
      }
   }
}