package net.anvian.record_days_survived;

import net.anvian.record_days_survived.components.ModComponents;
import net.anvian.record_days_survived.util.DaysData;
import net.anvian.record_days_survived.util.IEntityDataSaver;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.nbt.NbtCompound;
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

	private static final long TICKS_PER_DAY = 24000;
	private static long ticksPassed = 1200;
	private static final String command = "record_report";

	@Override
	public void onInitialize() {

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				dispatcher.register(CommandManager.literal(command).executes(context -> {
					ModComponents.DAY.maybeGet(context.getSource().getPlayer()).ifPresent(dayComponent -> {
						context.getSource().sendMessage(Text.translatable("title_report").fillStyle(Style.EMPTY.withBold(true)));

						int days = dayComponent.getDays();
						int recordDay = dayComponent.getRecordDay();

						Supplier<Text> daysText = () -> Text.of(I18n.translate("report_day", days));
						Supplier<Text> recordText = () -> Text.of(I18n.translate("report_record_day", recordDay));

						context.getSource().sendFeedback(daysText, false);
						context.getSource().sendFeedback(recordText, false);

					});
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
			IEntityDataSaver player = (IEntityDataSaver) newPlayer;

			int recordDay = player.getPersistentData().getInt("recordDay");

			newPlayer.sendMessage(Text.translatable("reset").fillStyle(Style.EMPTY.withBold(true)));
			newPlayer.sendMessage(Text.of(I18n.translate("report_record_day", recordDay)));
		});

		ServerTickEvents.START_SERVER_TICK.register((server->{
			long worldTime = server.getOverworld().getTime();

			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {

				//one minute has passed
				if (worldTime % 1200 == 0) {
					DaysData.addTicksPassed((IEntityDataSaver)player, worldTime);
					ticksPassed = ((IEntityDataSaver)player).getPersistentData().getLong("ticksPassed");
				}

				//one day has passed
				if (ticksPassed % TICKS_PER_DAY == 0) {
					DaysData.dayPassed((IEntityDataSaver)player, player);
					ticksPassed = 1200;
				}
			}
		}));

		EntitySleepEvents.STOP_SLEEPING.register((entity, sleepingPos) -> {
			if (entity instanceof ServerPlayerEntity) {
				DaysData.dayPassed((IEntityDataSaver)entity, entity);
			}
		});

		ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
			if (entity instanceof ServerPlayerEntity) {
				DaysData.resetDays((IEntityDataSaver)entity);
			}
		});

		ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
			NbtCompound oldNbt = ((IEntityDataSaver) oldPlayer).getPersistentData();
			NbtCompound newNbt = ((IEntityDataSaver) newPlayer).getPersistentData();

			newNbt.putInt("recordDay", oldNbt.getInt("recordDay"));
		});

		LOGGER.info("Record Days Survived mod initialized!");
	}
}