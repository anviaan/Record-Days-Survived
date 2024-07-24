package net.anvian.record_days_survived.components;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.Component;

public class TicksPassedComponent implements Component {
    private final PlayerEntity player;
    private long ticksPassed;

    public TicksPassedComponent(PlayerEntity player) {
        this.player = player;
        this.ticksPassed = 1200;
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        ticksPassed = tag.getLong("ticksPassed");
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putLong("ticksPassed", ticksPassed);
    }

    public long getTicksPassed() {
        return ticksPassed;
    }

    public void addTickPassed(long worldTime) {
        ticksPassed = worldTime;
    }

    public void resetTickPassed() {
        ticksPassed = 1200;
    }
}
