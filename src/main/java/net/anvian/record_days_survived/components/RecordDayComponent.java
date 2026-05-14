package net.anvian.record_days_survived.components;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.ladysnake.cca.api.v8.component.CardinalComponent;

public class RecordDayComponent implements CardinalComponent {
    private final Player player;
    private int recordDay;

    public RecordDayComponent(Player player) {
        this.player = player;
        recordDay = 0;
    }

    @Override
    public void readData(ValueInput input) {
        recordDay = input.getIntOr("recordDay", 0);
    }

    @Override
    public void writeData(ValueOutput output) {
        output.putInt("recordDay", recordDay);
    }

    public int getRecordDay() {
        return recordDay;
    }

    public void setRecordDay(int amount) {
        recordDay = amount;
    }
}
