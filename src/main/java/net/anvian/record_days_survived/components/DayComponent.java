package net.anvian.record_days_survived.components;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.nbt.NbtCompound;

public class DayComponent implements Component {
    private int days;
    private int recordDay;
    private long ticksPassed;
    @Override
    public void readFromNbt(NbtCompound tag) {
        days = tag.getInt("days");
        recordDay = tag.getInt("recordDay");
        ticksPassed = tag.getLong("ticksPassed");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt("days", days);
        tag.putInt("recordDay", recordDay);
        tag.putLong("ticksPassed", ticksPassed);
    }

    public int getDays() {
        return days;
    }

    public int getRecordDay() {
        return recordDay;
    }

    public void setDays(int days) {
        this.days = days;
    }
}
