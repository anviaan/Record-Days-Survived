package net.anvian.record_days_survived.components;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.ladysnake.cca.api.v3.component.Component;

public class DayComponent implements Component {
    private final PlayerEntity player;
    private int days;

    public DayComponent(PlayerEntity player) {
        this.player = player;
        days = 0;
    }

    @Override
    public void readData(ReadView readView) {
        days = readView.getInt("days", 0);
    }

    @Override
    public void writeData(WriteView writeView) {
        writeView.putInt("days", days);
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
