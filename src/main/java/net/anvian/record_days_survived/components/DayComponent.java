package net.anvian.record_days_survived.components;

import net.minecraft.nbt.NbtCompound;

public class DayComponent implements Mycomponents{
    private NbtCompound persistentData;
    @Override
    public void readFromNbt(NbtCompound tag) {
        if (tag.contains("perfume.data", 10)) {
            persistentData = tag.getCompound("perfume.data");
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        if(persistentData != null) {
            tag.put("perfume.data", persistentData);
        }
    }

    @Override
    public NbtCompound persistentData() {
        if (this.persistentData == null){
            this.persistentData = new NbtCompound();
        }
        return persistentData;
    }
}
