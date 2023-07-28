package net.anvian.record_days_survived;

import net.anvian.record_days_survived.util.DaysData;
import net.anvian.record_days_survived.util.IEntityDataSaver;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public class RecordDaysSurvivedClient implements ClientModInitializer {
    private static final int TICKS_PER_DAY = 24000;
    @Override
    public void onInitializeClient() {

        ServerTickEvents.START_SERVER_TICK.register((server->{
            int days, recordDay;

            long worldTime = server.getOverworld().getTime();

            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {

//                if (worldTime % 1200 == 0) {
//                    //one minute has passed
//                    DaysData.setTicksPassed((IEntityDataSaver)player, worldTime);
//                    System.out.println(((IEntityDataSaver) player).getPersistentData().getLong("ticksPassed"));
//                }

                if (worldTime % 200 == 0) {
                    DaysData.setDays((IEntityDataSaver)player, 1);
                    DaysData.setTicksPassed((IEntityDataSaver)player);

                    days = ((IEntityDataSaver) player).getPersistentData().getInt("days");
                    recordDay = ((IEntityDataSaver) player).getPersistentData().getInt("recordDay");

                    if (days > recordDay ) {
                        DaysData.setRecordDay((IEntityDataSaver)player, days);
                    }

                    System.out.println(((IEntityDataSaver) player).getPersistentData().getInt("days") + " days");
                    System.out.println(((IEntityDataSaver) player).getPersistentData().getInt("recordDay") + " record day");
                    System.out.println(((IEntityDataSaver) player).getPersistentData().getInt("ticksPassed") + " ticks passed");
                }
            }
        }));

        ServerPlayerEvents.ALLOW_DEATH.register((player, damageSource, damageAmount) -> {
            DaysData.resetDays((IEntityDataSaver)player);
            System.out.println(((IEntityDataSaver) player).getPersistentData().getInt("recordDay"));
            return true;
        });

    }
}
