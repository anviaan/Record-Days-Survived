package net.anvian.record_days_survived.mixin;

import net.anvian.record_days_survived.RecordDaysSurvived;
import net.anvian.record_days_survived.util.IEntityDataSaver;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class DataSaverMixin implements IEntityDataSaver {
    private NbtCompound persistentData;

    @Override
    public NbtCompound getPersistentData() {
        if (this.persistentData == null){
            this.persistentData = new NbtCompound();
        }
        return persistentData;
    }

    @Inject(method = "writeNbt", at = @At("HEAD"))
    protected void injectWriteMethod(NbtCompound nbt, CallbackInfoReturnable info) {
        if(persistentData != null) {
            nbt.put(RecordDaysSurvived.MOD_ID + ".days", persistentData);
            nbt.put(RecordDaysSurvived.MOD_ID + ".recordDay", persistentData);
            nbt.put(RecordDaysSurvived.MOD_ID + ".ticksPassed", persistentData);
        }

    }

    @Inject(method = "readNbt", at = @At("HEAD"))
    protected void injectReadMethod(NbtCompound nbt, CallbackInfo info) {
        if (nbt.contains(RecordDaysSurvived.MOD_ID + ".days", 10)) {
            persistentData = nbt.getCompound(RecordDaysSurvived.MOD_ID + ".days");
        }
        if (nbt.contains(RecordDaysSurvived.MOD_ID + ".recordDay", 10)) {
            persistentData = nbt.getCompound(RecordDaysSurvived.MOD_ID + ".recordDay");
        }
        if (nbt.contains(RecordDaysSurvived.MOD_ID + ".ticksPassed", 0)) {
            persistentData = nbt.getCompound(RecordDaysSurvived.MOD_ID + ".ticksPassed");
        }
    }
}
