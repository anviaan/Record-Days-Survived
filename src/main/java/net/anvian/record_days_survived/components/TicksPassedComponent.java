package net.anvian.record_days_survived.components;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.ladysnake.cca.api.v8.component.CardinalComponent;

public class TicksPassedComponent implements CardinalComponent {
    private final Player player;
    private long ticksPassed;

    public TicksPassedComponent(Player player) {
        this.player = player;
        this.ticksPassed = 1200;
    }

    @Override
    public void readData(ValueInput input) {
        ticksPassed = input.getLongOr("ticksPassed", 1200);
    }

    @Override
    public void writeData(ValueOutput output) {
        output.putLong("ticksPassed", ticksPassed);
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
