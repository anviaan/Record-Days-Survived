package net.anvian.record_days_survived;

import net.anvian.record_days_survived.util.DaysData;
import net.anvian.record_days_survived.util.IEntityDataSaver;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

public class RecordDaysSurvivedClient implements ClientModInitializer {
    private static final int TICKS_PER_DAY = 24000;
    private static long ticksPassed = 1200;
    @Override
    public void onInitializeClient() {

        ServerTickEvents.START_SERVER_TICK.register((server->{

            long worldTime = server.getOverworld().getTime();

            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {

                //one minute has passed
                if (worldTime % 1200 == 0) {
                    DaysData.addTicksPassed((IEntityDataSaver)player);
                    System.out.println(log((IEntityDataSaver) player));
                    ticksPassed = ((IEntityDataSaver)player).getPersistentData().getLong("ticksPassed");
                }

                if (ticksPassed % TICKS_PER_DAY == 0) {
                    DaysData.dayPassed((IEntityDataSaver)player);

                    System.out.println(log((IEntityDataSaver) player));
                }
            }
        }));

        EntitySleepEvents.STOP_SLEEPING.register((entity, sleepingPos) -> {
            if (entity instanceof ServerPlayerEntity) {
                DaysData.dayPassed((IEntityDataSaver)entity);
                System.out.println(log((IEntityDataSaver) entity));
            }
        });

        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            NbtCompound oldNbt = ((IEntityDataSaver) oldPlayer).getPersistentData();
            NbtCompound newNbt = ((IEntityDataSaver) newPlayer).getPersistentData();

            newNbt.putInt("recordDay", oldNbt.getInt("recordDay"));
        });

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (entity instanceof ServerPlayerEntity) {
                DaysData.resetDays((IEntityDataSaver)entity);
                System.out.println(log((IEntityDataSaver) entity));
            }
        });

    }
    public static String log(IEntityDataSaver entity){
        System.out.println(entity.getPersistentData().getInt("days") + " days");
        System.out.println(entity.getPersistentData().getInt("recordDay") + " record day");
        System.out.println(entity.getPersistentData().getInt("ticksPassed") + " ticks passed");
        return null;
    }
}
