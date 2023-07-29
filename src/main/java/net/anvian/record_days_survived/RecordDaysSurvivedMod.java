package net.anvian.record_days_survived;

import net.anvian.record_days_survived.util.DaysData;
import net.anvian.record_days_survived.util.IEntityDataSaver;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecordDaysSurvivedMod implements ModInitializer {
	public static final String MOD_ID = "record_days_survived";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final long TICKS_PER_DAY = 24000;
	private static long ticksPassed = 1200;

	@Override
	public void onInitialize() {

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				dispatcher.register(CommandManager.literal("record_day").executes(context -> {
					IEntityDataSaver player = (IEntityDataSaver) context.getSource().getPlayer();

					context.getSource().sendFeedback(Text.literal("Days: " + player.getPersistentData().getInt("days")), false);
					context.getSource().sendFeedback(Text.literal("Record Day: " + player.getPersistentData().getInt("recordDay")), false);
					//context.getSource().sendFeedback(Text.literal("Ticks Passed: " + player.getPersistentData().getInt("ticksPassed")), false);

					return 1;
				})));

		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			IEntityDataSaver player = (IEntityDataSaver) newPlayer;

			//newPlayer.sendMessage(Text.literal("Days: " + player.getPersistentData().getInt("days")));
			newPlayer.sendMessage(Text.literal("Record Day: " + player.getPersistentData().getInt("recordDay")));
			//newPlayer.sendMessage(Text.literal("Ticks Passed: " + player.getPersistentData().getInt("ticksPassed")));
		});

		ServerTickEvents.START_SERVER_TICK.register((server->{
			long worldTime = server.getOverworld().getTime();

			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {

				//one minute has passed
				if (worldTime % 1200 == 0) {
					DaysData.addTicksPassed((IEntityDataSaver)player);
					ticksPassed = ((IEntityDataSaver)player).getPersistentData().getLong("ticksPassed");
				}

				//one day has passed
				if (ticksPassed % TICKS_PER_DAY == 0) {
					DaysData.dayPassed((IEntityDataSaver)player);
				}
			}
		}));

		EntitySleepEvents.STOP_SLEEPING.register((entity, sleepingPos) -> {
			if (entity instanceof ServerPlayerEntity) {
				DaysData.dayPassed((IEntityDataSaver)entity);
				DaysData.resetTicksPassed((IEntityDataSaver)entity);
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