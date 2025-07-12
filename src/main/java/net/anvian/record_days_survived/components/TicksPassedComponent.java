package net.anvian.record_days_survived.components;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.ladysnake.cca.api.v3.component.Component;

public class TicksPassedComponent implements Component {
    private final PlayerEntity player;
    private long ticksPassed;

    public TicksPassedComponent(PlayerEntity player) {
        this.player = player;
        this.ticksPassed = 1200;
    }

    @Override
    public void readData(ReadView readView) {
        ticksPassed = readView.getLong("ticksPassed", 1200);
    }

    @Override
    public void writeData(WriteView writeView) {
        writeView.putLong("ticksPassed", ticksPassed);
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
