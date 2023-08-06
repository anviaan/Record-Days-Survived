package net.anvian.record_days_survived.mixin;

import net.anvian.record_days_survived.util.IEntityDataSaver;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class DataSaverMixin implements IEntityDataSaver {
    private CompoundTag persistentData;

    @Override
    public CompoundTag getPersistentData() {
        if (this.persistentData == null){
            this.persistentData = new CompoundTag();
        }
        return persistentData;
    }

    @Inject(method = "saveWithoutId", at = @At("HEAD"))
    protected void injectPersistentData(CompoundTag nbt, CallbackInfoReturnable info){
        if(this.persistentData != null){
            nbt.put("perfume.data", this.persistentData);
        }
    }

    @Inject(method = "load", at = @At("HEAD"))
    protected void injectReadMethod(CompoundTag nbt, CallbackInfo info) {
        if (nbt.contains("perfume.data", 10)) {
            persistentData = nbt.getCompound("perfume.data");
        }
    }
}