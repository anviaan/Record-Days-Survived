package net.anvian.record_days_survived.components;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.nbt.NbtCompound;

public class TicksPassedComponent implements Component {

    private long tickPassed;

    @Override
    public void readFromNbt(NbtCompound tag) {
        tickPassed = tag.getLong("ticksPassed");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putLong("ticksPassed", tickPassed);
    }
}
