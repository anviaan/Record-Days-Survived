package net.anvian.record_days_survived.components;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.Component;

public class RecordDayComponent implements Component {
    private final PlayerEntity player;
    private int recordDay;

    public RecordDayComponent(PlayerEntity player) {
        this.player = player;
        recordDay = 0;
    }

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
