package net.anvian.record_days_survived;

import net.anvian.record_days_survived.command.RecordCommand;
import net.anvian.record_days_survived.components.ModComponents;
import net.anvian.record_days_survived.util.DaysUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class RecordDaysSurvivedMod implements ModInitializer {
    public static final String MOD_ID = "record_days_survived";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static long ticksPassed = 1200;

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                RecordCommand.register(dispatcher));

        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof ServerPlayerEntity) {
                ServerCommandSource source = entity.getCommandSource().withEntity(entity);

                Objects.requireNonNull(entity.getServer()).getCommandManager().executeWithPrefix(source, "record_day report");

            }
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            int recordDay = ModComponents.RECORD_DAY.get(newPlayer).getRecordDay();

            newPlayer.sendMessage(Text.translatable("reset").fillStyle(Style.EMPTY.withBold(true)));
            newPlayer.sendMessage(Text.of(I18n.translate("report_record_day", recordDay)));
        });

        ServerTickEvents.START_SERVER_TICK.register((server -> {
            long worldTime = server.getOverworld().getTime();

            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {

                //one minute has passed
                if (worldTime % 1200 == 0) {
                    ModComponents.TICKS_PASSED.get(player).addTickPassed(worldTime);
                    ticksPassed = ModComponents.TICKS_PASSED.get(player).getTicksPassed();
                }

                //one day has passed
                if (ticksPassed % 24000 == 0) {
                    DaysUtil.dayPassed(player);
                    ticksPassed = 1200;
                }
            }
        }));

        EntitySleepEvents.STOP_SLEEPING.register((entity, sleepingPos) -> {
            if (entity instanceof ServerPlayerEntity) {
                DaysUtil.dayPassed(entity);
            }
        });

        LOGGER.info("Record Days Survived mod initialized!");
    }
}