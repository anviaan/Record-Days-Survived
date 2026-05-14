package net.anvian.record_days_survived.components;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.ladysnake.cca.api.v8.component.CardinalComponent;

public class DayComponent implements CardinalComponent {
    private final Player player;
    private int days;

    public DayComponent(Player player) {
        this.player = player;
        days = 0;
    }

    @Override
    public void readData(ValueInput input) {
        days = input.getIntOr("days", 0);
    }

    @Override
    public void writeData(ValueOutput output) {
        output.putInt("days", days);
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
