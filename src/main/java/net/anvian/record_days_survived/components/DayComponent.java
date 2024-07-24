package net.anvian.record_days_survived.components;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.Component;

public class DayComponent implements Component {
    private final PlayerEntity player;
    private int days;

    public DayComponent(PlayerEntity player) {
        this.player = player;
        days = 0;
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        days = tag.getInt("days");
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
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
