package net.anvian.record_days_survived.util;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

public class DaysData {
    public static int addDays(IEntityDataSaver player, int amount) {
        CompoundTag nbt = player.getPersistentData();
        int days = nbt.getInt("days");
        days += amount;

        nbt.putInt("days", days);
        // sync the data
        return days;
    }
    public static int resetDays(IEntityDataSaver player) {
        CompoundTag nbt = player.getPersistentData();
        int days = nbt.getInt("days");
        days = 0;
        nbt.putInt("days", days);
        return days;
    }

    public static long addTicksPassed(IEntityDataSaver player, long worldTime) {
        CompoundTag nbt = player.getPersistentData();
        long setTicksPassed = nbt.getLong("ticksPassed");
        setTicksPassed = worldTime;

        nbt.putLong("ticksPassed", setTicksPassed);
        // sync the data
        return setTicksPassed;
    }

    public static int resetTicksPassed(IEntityDataSaver player) {
        CompoundTag nbt = player.getPersistentData();
        int setTicksPassed = nbt.getInt("ticksPassed");
        setTicksPassed = 1200;

        nbt.putInt("ticksPassed", setTicksPassed);
        // sync the data
        return setTicksPassed;
    }

    public static int setRecordDay(IEntityDataSaver player, int amount) {
        CompoundTag nbt = player.getPersistentData();
        int setRecordDay = nbt.getInt("recordDay");

        nbt.putLong("recordDay", amount);
        // sync the data
        return setRecordDay;
    }

    public static void dayPassed(IEntityDataSaver player, LivingEntity entity) {
        int days, recordDay;
        DaysData.addDays(player, 1);
        DaysData.resetTicksPassed(player);

        days = (player).getPersistentData().getInt("days");
        recordDay = (player).getPersistentData().getInt("recordDay");

        if (days == 5 || days % 10 == 0){
            entity.sendSystemMessage(Component.nullToEmpty(I18n.get("record_notice", days)));
        }

        if (days > recordDay ) {
            DaysData.setRecordDay(player, days);
        }
    }
}
