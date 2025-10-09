package net.anvian.record_days_survived;

import net.anvian.anvianslib.util.LibUtil;
import net.anvian.record_days_survived.command.RecordCommand;
import net.anvian.record_days_survived.components.ModComponents;
import net.anvian.record_days_survived.util.DaysUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecordDaysSurvivedMod implements ModInitializer {
    public static final String MOD_ID = "record_days_survived";
    public static final String MOD_VERSION = "2.3";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static long ticksPassed = 1200;

    @Override
    public void onInitialize() {
        LOGGER.info("Record Days Survived mod initialized!");

        LibUtil.setupTelemetry(MOD_ID, MOD_VERSION);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> RecordCommand.register(dispatcher));

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            int recordDay = newPlayer.getComponent(ModComponents.RECORD_DAY).getRecordDay();
            int daysSurvived = newPlayer.getComponent(ModComponents.DAY).getDays();

            newPlayer.sendMessage(Text.translatable("reset").fillStyle(Style.EMPTY.withBold(true)));
            newPlayer.sendMessage(Text.translatable("report_record_day").append(": " + recordDay));
            newPlayer.sendMessage(Text.translatable("days_survived").append(": " + daysSurvived));
        });

        ServerTickEvents.START_SERVER_TICK.register((server -> {
            long worldTime = server.getOverworld().getTime();

            for (PlayerEntity player : server.getPlayerManager().getPlayerList()) {

                //one minute has passed
                if (worldTime % 1200 == 0) {
                    player.getComponent(ModComponents.TICKS_PASSED).addTickPassed(worldTime);
                    ticksPassed = player.getComponent(ModComponents.TICKS_PASSED).getTicksPassed();
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
    }
}