package net.anvian.record_days_survived.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.anvian.record_days_survived.components.ModComponents;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class RecordCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("record_day").then(CommandManager.literal("report").executes(RecordCommand::reportDay)).then(CommandManager.literal("set").requires(source -> source.hasPermissionLevel(2)).then(CommandManager.literal("day").then(CommandManager.argument("value", IntegerArgumentType.integer()).executes(RecordCommand::setDay).then(CommandManager.argument("target", EntityArgumentType.player()).executes(RecordCommand::setDayWithTarget)))).then(CommandManager.literal("record").then(CommandManager.argument("value", IntegerArgumentType.integer()).executes(RecordCommand::setRecordDay).then(CommandManager.argument("target", EntityArgumentType.player()).executes(RecordCommand::setRecordDayWithTarget))))));
    }

    private static int reportDay(CommandContext<ServerCommandSource> context) {
        context.getSource().sendMessage(Text.translatable("title_report").append(": "));

        int days = context.getSource().getPlayer().getComponent(ModComponents.DAY).getDays();
        int recordDay = context.getSource().getPlayer().getComponent(ModComponents.RECORD_DAY).getRecordDay();

        context.getSource().sendFeedback(() -> Text.translatable("report_day").append(": ").append(String.valueOf(days)), false);
        context.getSource().sendFeedback(() -> Text.translatable("report_record_day").append(": ").append(String.valueOf(recordDay)), false);

        return 1;
    }

    private static int setDay(CommandContext<ServerCommandSource> context) {
        int value = IntegerArgumentType.getInteger(context, "value");
        var day = context.getSource().getPlayer().getComponent(ModComponents.DAY);
        var record = context.getSource().getPlayer().getComponent(ModComponents.RECORD_DAY);

        day.setDays(value);

        int days = day.getDays();
        int recordDay = record.getRecordDay();
        if (days > recordDay) {
            record.setRecordDay(days);
        }

        context.getSource().sendFeedback(() -> Text.translatable("set_day").append(" ").append(String.valueOf(value)), true);

        return 1;
    }

    private static int setDayWithTarget(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        int value = IntegerArgumentType.getInteger(context, "value");
        PlayerEntity target = EntityArgumentType.getPlayer(context, "target");
        var day = target.getComponent(ModComponents.DAY);
        var record = context.getSource().getPlayer().getComponent(ModComponents.RECORD_DAY);

        day.setDays(value);

        int days = day.getDays();
        int recordDay = record.getRecordDay();
        if (days > recordDay) {
            record.setRecordDay(days);
        }

        context.getSource().sendFeedback(() -> Text.translatable("set_day_with_target").append(" ").append(target.getName()).append(" ").append("were_set_to").append(" ").append(String.valueOf(value)), true);

        return 1;
    }

    private static int setRecordDay(CommandContext<ServerCommandSource> context) {
        int value = IntegerArgumentType.getInteger(context, "value");
        var record = context.getSource().getPlayer().getComponent(ModComponents.RECORD_DAY);

        record.setRecordDay(value);

        context.getSource().sendFeedback(() -> Text.translatable("set_record_day").append(" ").append(String.valueOf(value)), true);

        return 1;
    }

    private static int setRecordDayWithTarget(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        int value = IntegerArgumentType.getInteger(context, "value");
        PlayerEntity target = EntityArgumentType.getPlayer(context, "target");

        var record = target.getComponent(ModComponents.RECORD_DAY);
        record.setRecordDay(value);

        context.getSource().sendFeedback(() -> Text.translatable("set_record_day_with_target").append(" ").append(target.getName()).append(" ").append("were_set_to").append(" ").append(String.valueOf(value)), true);

        return 1;
    }
}