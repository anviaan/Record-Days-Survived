package net.anvian.record_days_survived;

import net.anvian.record_days_survived.components.ModComponents;
import net.anvian.record_days_survived.util.DaysData;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class RecordDaysSurvivedMod implements ModInitializer {
	public static final String MOD_ID = "record_days_survived";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static long ticksPassed = 1200;
	private static final String command = "record_report";

	@Override
	public void onInitialize() {

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				dispatcher.register(CommandManager.literal(command).executes(context -> {

					var day = ModComponents.DAY.get(context.getSource().getPlayer());
					var record = ModComponents.RECORD_DAY.get(context.getSource().getPlayer());
					context.getSource().sendMessage(Text.translatable("title_report").fillStyle(Style.EMPTY.withBold(true)));

					int days = day.getDays();
					int recordDay = record.getRecordDay();

					Supplier<Text> daysText = () -> Text.of(I18n.translate("report_day", days));
					Supplier<Text> recordText = () -> Text.of(I18n.translate("report_record_day", recordDay));

					context.getSource().sendFeedback(daysText, false);
					context.getSource().sendFeedback(recordText, false);

					return 1;
				})));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				dispatcher.register(CommandManager.literal("add_days").executes(context -> {
					ModComponents.DAY.maybeGet(context.getSource().getPlayer()).ifPresent(dayComponent -> {

						dayComponent.setDays(dayComponent.getDays() + 10);
					});

					return 1;
				})));

		ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			if (entity instanceof ServerPlayerEntity) {
				ServerCommandSource source = entity.getCommandSource().withEntity(entity);

				entity.getServer().getCommandManager().executeWithPrefix(source, command);

			}
		});

		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			int recordDay = ModComponents.RECORD_DAY.get(newPlayer).getRecordDay();

			newPlayer.sendMessage(Text.translatable("reset").fillStyle(Style.EMPTY.withBold(true)));
			newPlayer.sendMessage(Text.of(I18n.translate("report_record_day", recordDay)));
		});

		ServerTickEvents.START_SERVER_TICK.register((server->{
			long worldTime = server.getOverworld().getTime();

			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {

				//one minute has passed
				if (worldTime % 1200 == 0) {
					ModComponents.TICKS_PASSED.get(player).addTickPassed(worldTime);
					ticksPassed = ModComponents.TICKS_PASSED.get(player).getTicksPassed();
				}

				//one day has passed
				if (ticksPassed % 24000 == 0) {
					DaysData.dayPassed(player);
					ticksPassed = 1200;
				}
			}
		}));

		EntitySleepEvents.STOP_SLEEPING.register((entity, sleepingPos) -> {
			if (entity instanceof ServerPlayerEntity) {
				DaysData.dayPassed(entity);
			}
		});

		LOGGER.info("Record Days Survived mod initialized!");
	}
}