package net.anvian.record_days_survived.util;

import net.anvian.record_days_survived.RecordDaysSurvivedMod;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;

public class DaysUtil {
    public static void dayPassed(LivingEntity entity) {
        int days, recordDay;

        var day = entity.getComponent(RecordDaysSurvivedMod.DAY);
        var record = entity.getComponent(RecordDaysSurvivedMod.RECORD_DAY);
        var tick = entity.getComponent(RecordDaysSurvivedMod.TICKS_PASSED);

        day.addDays(1);
        tick.resetTickPassed();

        days = day.getDays();
        recordDay = record.getRecordDay();

        if (days == 5 || days % 10 == 0) {
            entity.sendMessage(Text.of(I18n.translate("record_notice", days)));
        }

        if (days > recordDay) {
            entity.getComponent(RecordDaysSurvivedMod.RECORD_DAY).setRecordDay(days);
        }
    }
}
