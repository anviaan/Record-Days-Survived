package net.anvian.record_days_survived.util;

import net.anvian.record_days_survived.components.ModComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

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
            ((Player) entity).sendSystemMessage(Component.translatable("record_notice").append(" ").append(String.valueOf(days)).withStyle(ChatFormatting.YELLOW));
        }

        if (days > recordDay) {
            entity.getComponent(ModComponents.RECORD_DAY).setRecordDay(days);
        }
    }
}
