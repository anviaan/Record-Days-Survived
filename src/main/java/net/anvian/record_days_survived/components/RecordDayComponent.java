package net.anvian.record_days_survived.components;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.Component;

public class RecordDayComponent implements Component {
    private int recordDay;

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        recordDay = tag.getInt("recordDay");
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putInt("recordDay", recordDay);
    }

    public int getRecordDay() {
        return recordDay;
    }

    public void setRecordDay(int amount) {
        recordDay = amount;
    }
}
