package net.anvian.record_days_survived.components;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.anvian.record_days_survived.RecordDaysSurvivedMod;
import net.minecraft.util.Identifier;

public class ModComponents implements EntityComponentInitializer {

    public static final ComponentKey<DayComponent> DAY = ComponentRegistry.getOrCreate(
            new Identifier(RecordDaysSurvivedMod.MOD_ID, "days"), DayComponent.class);
    public static final ComponentKey<RecordDayComponent> RECORD_DAY = ComponentRegistry.getOrCreate(
            new Identifier(RecordDaysSurvivedMod.MOD_ID, "record_day"), RecordDayComponent.class);
    public static final ComponentKey<TicksPassedComponent> TICKS_PASSED = ComponentRegistry.getOrCreate(
            new Identifier(RecordDaysSurvivedMod.MOD_ID, "ticks_passed"), TicksPassedComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(DAY, playerEntity -> new DayComponent(), RespawnCopyStrategy.LOSSLESS_ONLY);
        registry.registerForPlayers(RECORD_DAY, playerEntity -> new RecordDayComponent(), RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerForPlayers(TICKS_PASSED, playerEntity -> new TicksPassedComponent(), RespawnCopyStrategy.NEVER_COPY);
    }
}
