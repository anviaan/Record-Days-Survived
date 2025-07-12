package net.anvian.record_days_survived.components;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.ladysnake.cca.api.v3.component.Component;

public class RecordDayComponent implements Component {
    private final PlayerEntity player;
    private int recordDay;

    public RecordDayComponent(PlayerEntity player) {
        this.player = player;
        recordDay = 0;
    }

    @Override
    public void readData(ReadView readView) {
        recordDay = readView.getInt("recordDay", 0);
    }

    @Override
    public void writeData(WriteView writeView) {
        writeView.putInt("recordDay", recordDay);
    }

    public int getRecordDay() {
        return recordDay;
    }

    public void setRecordDay(int amount) {
        recordDay = amount;
    }
}
