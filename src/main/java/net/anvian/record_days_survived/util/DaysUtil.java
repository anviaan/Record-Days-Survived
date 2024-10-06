package net.anvian.record_days_survived.util;

import net.anvian.record_days_survived.components.ModComponents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class DaysUtil {
    public static void dayPassed(LivingEntity entity) {
        int days, recordDay;

        var day = entity.getComponent(ModComponents.DAY);
        var record = entity.getComponent(ModComponents.RECORD_DAY);
        var tick = entity.getComponent(ModComponents.TICKS_PASSED);

        day.addDays(1);
        tick.resetTickPassed();

        days = day.getDays();
        recordDay = record.getRecordDay();

        if (days == 5 || days % 10 == 0) {
            entity.sendMessage(Text.translatable("record_notice").append(" ").append(String.valueOf(days)).formatted(Formatting.YELLOW));
        }

        if (days > recordDay) {
            entity.getComponent(ModComponents.RECORD_DAY).setRecordDay(days);
        }
    }
}
