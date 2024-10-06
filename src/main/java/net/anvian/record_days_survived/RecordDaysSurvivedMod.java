package net.anvian.record_days_survived;

import net.anvian.record_days_survived.command.RecordCommand;
import net.anvian.record_days_survived.components.DayComponent;
import net.anvian.record_days_survived.components.RecordDayComponent;
import net.anvian.record_days_survived.components.TicksPassedComponent;
import net.anvian.record_days_survived.util.DaysUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class RecordDaysSurvivedMod implements ModInitializer, EntityComponentInitializer {
    public static final String MOD_ID = "record_days_survived";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final ComponentKey<DayComponent> DAY = ComponentRegistry.getOrCreate(
            Identifier.of(RecordDaysSurvivedMod.MOD_ID, "days"), DayComponent.class);
    public static final ComponentKey<RecordDayComponent> RECORD_DAY = ComponentRegistry.getOrCreate(
            Identifier.of(RecordDaysSurvivedMod.MOD_ID, "record_day"), RecordDayComponent.class);
    public static final ComponentKey<TicksPassedComponent> TICKS_PASSED = ComponentRegistry.getOrCreate(
            Identifier.of(RecordDaysSurvivedMod.MOD_ID, "ticks_passed"), TicksPassedComponent.class);

    private static long ticksPassed = 1200;

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                RecordCommand.register(dispatcher));

        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof PlayerEntity) {
                ServerCommandSource source = entity.getCommandSource().withEntity(entity);

                Objects.requireNonNull(entity.getServer()).getCommandManager().executeWithPrefix(source, "record_day report");

            }
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            int recordDay = newPlayer.getComponent(RECORD_DAY).getRecordDay();

            newPlayer.sendMessage(Text.translatable("reset").fillStyle(Style.EMPTY.withBold(true)));
            newPlayer.sendMessage(Text.of(I18n.translate("report_record_day", recordDay)));
        });

        ServerTickEvents.START_SERVER_TICK.register((server -> {
            long worldTime = server.getOverworld().getTime();

            for (PlayerEntity player : server.getPlayerManager().getPlayerList()) {

                //one minute has passed
                if (worldTime % 1200 == 0) {
                    player.getComponent(TICKS_PASSED).addTickPassed(worldTime);
                    ticksPassed = player.getComponent(TICKS_PASSED).getTicksPassed();
                }

                //one day has passed
                if (ticksPassed % 24000 == 0) {
                    DaysUtil.dayPassed(player);
                    ticksPassed = 1200;
                }
            }
        }));

        EntitySleepEvents.STOP_SLEEPING.register((entity, sleepingPos) -> {
            if (entity instanceof PlayerEntity) {
                DaysUtil.dayPassed(entity);
            }
        });

        LOGGER.info("Record Days Survived mod initialized!");
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(DAY, DayComponent::new, RespawnCopyStrategy.LOSSLESS_ONLY);
        registry.registerForPlayers(RECORD_DAY, RecordDayComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerForPlayers(TICKS_PASSED, TicksPassedComponent::new, RespawnCopyStrategy.NEVER_COPY);
    }

}