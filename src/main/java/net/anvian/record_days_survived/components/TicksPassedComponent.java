package net.anvian.record_days_survived.components;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.Component;

public class TicksPassedComponent implements Component {
    private long tickPassed;

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tickPassed = tag.getLong("ticksPassed");
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putLong("ticksPassed", tickPassed);
    }

    public long getTicksPassed() {
        return tickPassed;
    }

    public void addTickPassed(long worldTime) {
        tickPassed = worldTime;
    }

    public void resetTickPassed(){
        tickPassed = 1200;
    }
}
