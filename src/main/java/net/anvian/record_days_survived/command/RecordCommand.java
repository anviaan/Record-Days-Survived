package net.anvian.record_days_survived.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.anvian.record_days_survived.components.ModComponents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.player.Player;

public class RecordCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("record_day")
                        .then(Commands.literal("report").executes(RecordCommand::reportDay))
                        .then(Commands.literal("set").requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                                .then(Commands.literal("day")
                                        .then(Commands.argument("value", IntegerArgumentType.integer()).executes(RecordCommand::setDay)
                                                .then(Commands.argument("target", EntityArgument.player()).executes(RecordCommand::setDayWithTarget))))
                                .then(Commands.literal("record")
                                        .then(Commands.argument("value", IntegerArgumentType.integer()).executes(RecordCommand::setRecordDay)
                                                .then(Commands.argument("target", EntityArgument.player()).executes(RecordCommand::setRecordDayWithTarget))))));
    }

    private static int reportDay(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.translatable("title_report").append(": "));

        int days = context.getSource().getPlayer().getComponent(ModComponents.DAY).getDays();
        int recordDay = context.getSource().getPlayer().getComponent(ModComponents.RECORD_DAY).getRecordDay();

        context.getSource().sendSuccess(() -> Component.translatable("report_day").append(": ").append(String.valueOf(days)), false);
        context.getSource().sendSuccess(() -> Component.translatable("report_record_day").append(": ").append(String.valueOf(recordDay)), false);

        return 1;
    }

    private static int setDay(CommandContext<CommandSourceStack> context) {
        int value = IntegerArgumentType.getInteger(context, "value");
        var day = context.getSource().getPlayer().getComponent(ModComponents.DAY);
        var record = context.getSource().getPlayer().getComponent(ModComponents.RECORD_DAY);

        day.setDays(value);

        int days = day.getDays();
        int recordDay = record.getRecordDay();
        if (days > recordDay) {
            record.setRecordDay(days);
        }

        context.getSource().sendSuccess(() -> Component.translatable("set_day").append(" ").append(String.valueOf(value)), true);

        return 1;
    }

    private static int setDayWithTarget(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        int value = IntegerArgumentType.getInteger(context, "value");
        Player target = EntityArgument.getPlayer(context, "target");
        var day = target.getComponent(ModComponents.DAY);
        var record = context.getSource().getPlayer().getComponent(ModComponents.RECORD_DAY);

        day.setDays(value);

        int days = day.getDays();
        int recordDay = record.getRecordDay();
        if (days > recordDay) {
            record.setRecordDay(days);
        }

        context.getSource().sendSuccess(() -> Component.translatable("set_day_with_target").append(" ").append(target.getName()).append(" ").append(Component.translatable("were_set_to")).append(" ").append(String.valueOf(value)), true);

        return 1;
    }

    private static int setRecordDay(CommandContext<CommandSourceStack> context) {
        int value = IntegerArgumentType.getInteger(context, "value");
        var record = context.getSource().getPlayer().getComponent(ModComponents.RECORD_DAY);

        record.setRecordDay(value);

        context.getSource().sendSuccess(() -> Component.translatable("set_record_day").append(" ").append(String.valueOf(value)), true);

        return 1;
    }

    private static int setRecordDayWithTarget(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        int value = IntegerArgumentType.getInteger(context, "value");
        Player target = EntityArgument.getPlayer(context, "target");

        var record = target.getComponent(ModComponents.RECORD_DAY);
        record.setRecordDay(value);

        context.getSource().sendSuccess(() -> Component.translatable("set_record_day_with_target").append(" ").append(target.getName()).append(" ").append(Component.translatable("were_set_to")).append(" ").append(String.valueOf(value)), true);

        return 1;
    }
}
