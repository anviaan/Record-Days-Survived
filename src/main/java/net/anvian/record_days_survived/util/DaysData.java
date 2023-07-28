package net.anvian.record_days_survived.util;

import net.minecraft.nbt.NbtCompound;

public class DaysData {
    public static int addDays(IEntityDataSaver player, int amount) {
        NbtCompound nbt = player.getPersistentData();
        int days = nbt.getInt("days");
        days += amount;

        nbt.putInt("days", days);
        // sync the data
        return days;
    }
    public static int resetDays(IEntityDataSaver player) {
        NbtCompound nbt = player.getPersistentData();
        int days = nbt.getInt("days");
        days = 0;
        nbt.putInt("days", days);
        return days;
    }

    public static int addTicksPassed(IEntityDataSaver player) {
        NbtCompound nbt = player.getPersistentData();
        int setTicksPassed = nbt.getInt("ticksPassed");
        setTicksPassed += 1200;

        nbt.putInt("ticksPassed", setTicksPassed);
        // sync the data
        return setTicksPassed;
    }

    public static int resetTicksPassed(IEntityDataSaver player) {
        NbtCompound nbt = player.getPersistentData();
        int setTicksPassed = nbt.getInt("ticksPassed");
        setTicksPassed = 0;

        nbt.putInt("ticksPassed", setTicksPassed);
        // sync the data
        return setTicksPassed;
    }

    public static int setRecordDay(IEntityDataSaver player, int amount) {
        NbtCompound nbt = player.getPersistentData();
        int setRecordDay = nbt.getInt("recordDay");

        nbt.putLong("recordDay", amount);
        // sync the data
        return setRecordDay;
    }

    public static void dayPassed(IEntityDataSaver player) {
        int days, recordDay;
        DaysData.addDays(player, 1);
        DaysData.addTicksPassed(player);

        days = (player).getPersistentData().getInt("days");
        recordDay = (player).getPersistentData().getInt("recordDay");

        if (days > recordDay ) {
            DaysData.setRecordDay(player, days);
        }
    }
}
