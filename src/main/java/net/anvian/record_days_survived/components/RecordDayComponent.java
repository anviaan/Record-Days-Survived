package net.anvian.record_days_survived.components;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.nbt.NbtCompound;

public class RecordDayComponent implements Component {

    private int recordDay;

    @Override
    public void readFromNbt(NbtCompound tag) {
        recordDay = tag.getInt("recordDay");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt("recordDay", recordDay);
    }
}
