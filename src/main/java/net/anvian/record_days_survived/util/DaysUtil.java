package net.anvian.record_days_survived.util;

import net.anvian.record_days_survived.components.ModComponents;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;

public class DaysUtil {
    public static void dayPassed(LivingEntity entity) {
        int days, recordDay;

        var day = ModComponents.DAY.get(entity);
        var record = ModComponents.RECORD_DAY.get(entity);
        var tick = ModComponents.TICKS_PASSED.get(entity);

        day.addDays(1);
        tick.resetTickPassed();

        days = day.getDays();
        recordDay = record.getRecordDay();

        if (days == 5 || days % 10 == 0) {
            entity.sendMessage(Text.of(I18n.translate("record_notice", days)));
        }

        if (days > recordDay) {
            ModComponents.RECORD_DAY.get(entity).setRecordDay(days);
        }
    }
}
