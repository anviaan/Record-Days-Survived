package net.anvian.record_days_survived;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.anvian.record_days_survived.components.ModComponents;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Objects;
import java.util.function.Supplier;

public class RecordCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("record_day")
                        .then(CommandManager.literal("report")
                                .executes(RecordCommand::reportDay))
                        .then(CommandManager.literal("set").requires(source -> source.hasPermissionLevel(2))
                                .then(CommandManager.literal("day")
                                        .then(CommandManager.argument("value", IntegerArgumentType.integer())
                                                .executes(RecordCommand::setDay)
                                                .then(CommandManager.argument("target", EntityArgumentType.player())
                                                        .executes(RecordCommand::setDayWithTarget)))
                                )
                                .then(CommandManager.literal("record")
                                        .then(CommandManager.argument("value", IntegerArgumentType.integer())
                                                .executes(RecordCommand::setRecordDay)
                                                .then(CommandManager.argument("target", EntityArgumentType.player())
                                                        .executes(RecordCommand::setRecordDayWithTarget)))
                        )
        ));
    }

    private static int reportDay(CommandContext<ServerCommandSource> context) {
        var day = ModComponents.DAY.get(Objects.requireNonNull(context.getSource().getPlayer()));
        var record = ModComponents.RECORD_DAY.get(context.getSource().getPlayer());
        context.getSource().sendMessage(Text.translatable("title_report"));

        int days = day.getDays();
        int recordDay = record.getRecordDay();

        Supplier<Text> daysText = () -> Text.of(I18n.translate("report_day", days));
        Supplier<Text> recordText = () -> Text.of(I18n.translate("report_record_day", recordDay));

        context.getSource().sendFeedback(daysText, false);
        context.getSource().sendFeedback(recordText, false);

        return 1;
    }

    private static int setDay(CommandContext<ServerCommandSource> context) {
        int value = IntegerArgumentType.getInteger(context, "value");
        var day = ModComponents.DAY.get(Objects.requireNonNull(context.getSource().getPlayer()));
        var record = ModComponents.RECORD_DAY.get(context.getSource().getPlayer());

        day.setDays(value);

        int days = day.getDays();
        int recordDay = record.getRecordDay();
        if (days > recordDay){
            record.setRecordDay(days);
        }

        Supplier<Text> text = () -> Text.literal("The days were set to " + value);
        context.getSource().sendFeedback(text, true);

        return 1;
    }

    private static int setDayWithTarget(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        int value = IntegerArgumentType.getInteger(context, "value");
        PlayerEntity target = EntityArgumentType.getPlayer(context, "target");
        var day = ModComponents.DAY.get(target);
        var record = ModComponents.RECORD_DAY.get(Objects.requireNonNull(context.getSource().getPlayer()));

        day.setDays(value);

        int days = day.getDays();
        int recordDay = record.getRecordDay();
        if (days > recordDay){
            record.setRecordDay(days);
        }

        Supplier<Text> text = () -> Text.literal(target.getEntityName() + " days were set to " + value);
        context.getSource().sendFeedback(text, true);

        return 1;
    }

    private static int setRecordDay(CommandContext<ServerCommandSource> context) {
        int value = IntegerArgumentType.getInteger(context, "value");
        var record = ModComponents.RECORD_DAY.get(Objects.requireNonNull(context.getSource().getPlayer()));

        record.setRecordDay(value);

        Supplier<Text> text = () -> Text.literal("The record day were set to " + value);
        context.getSource().sendFeedback(text, true);

        return 1;
    }

    private static int setRecordDayWithTarget(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        int value = IntegerArgumentType.getInteger(context, "value");
        PlayerEntity target = EntityArgumentType.getPlayer(context, "target");

        var record = ModComponents.RECORD_DAY.get(target);
        record.setRecordDay(value);

        Supplier<Text> text = () -> Text.literal(target.getEntityName() + " record day  were set to " + value);
        context.getSource().sendFeedback(text, true);

        return 1;
    }
}
