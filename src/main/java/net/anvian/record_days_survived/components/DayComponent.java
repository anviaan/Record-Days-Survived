package net.anvian.record_days_survived.components;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.nbt.NbtCompound;

public class DayComponent implements Component {
    private int days;

    @Override
    public void readFromNbt(NbtCompound tag) {
        days = tag.getInt("days");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt("days", days);
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public void addDays(int amount) {
        days += amount;
    }
}
