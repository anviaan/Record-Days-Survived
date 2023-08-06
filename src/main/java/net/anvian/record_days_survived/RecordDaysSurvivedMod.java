package net.anvian.record_days_survived;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import net.anvian.record_days_survived.util.DaysData;
import net.anvian.record_days_survived.util.IEntityDataSaver;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(RecordDaysSurvivedMod.MODID)
public class RecordDaysSurvivedMod {
    public static final String MODID = "record_days_survived";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final String command = "record_report";
    private static final long TICKS_PER_DAY = 24000;
    private static long ticksPassed = 1200;


    public RecordDaysSurvivedMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Record Days Survived mod initialized!");
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal(command).executes(context -> {
            IEntityDataSaver player = (IEntityDataSaver) context.getSource().getPlayerOrException();

            context.getSource().sendSystemMessage(Component.translatable("title_report").withStyle(style -> style.withBold(true)));

            int days = player.getPersistentData().getInt("days");
            int recordDay = player.getPersistentData().getInt("recordDay");

            context.getSource().sendSystemMessage(Component.nullToEmpty(I18n.get("report_day", days)));
            context.getSource().sendSystemMessage(Component.nullToEmpty(I18n.get("report_record_day", recordDay)));

            return 1;
        }));
    }

    @SubscribeEvent
    public void entityLoad(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof ServerPlayer) {
            CommandSourceStack source = entity.createCommandSourceStack().withEntity(entity);
            entity.getServer().getCommands().performPrefixedCommand(source, command);
        }
    }

    @SubscribeEvent
    public void afterRespawn(PlayerEvent.PlayerRespawnEvent event) {
        IEntityDataSaver player = (IEntityDataSaver) event.getEntity();
        int recordDay = player.getPersistentData().getInt("recordDay");

        event.getEntity().sendSystemMessage(Component.translatable("reset").withStyle(style -> style.withBold(true)));
        event.getEntity().sendSystemMessage(Component.nullToEmpty(I18n.get("report_record_day", recordDay)));
    }

    @SubscribeEvent
    public void startServerTick(TickEvent.ServerTickEvent event) {
        MinecraftServer server = event.getServer();
        long worldTime = server.overworld().dayTime();

        if (event.phase == TickEvent.Phase.START) {

            for (ServerPlayer player : server.getPlayerList().getPlayers()) {

                if (worldTime % 1200 == 0) {
                    DaysData.addTicksPassed((IEntityDataSaver) player, worldTime);
                    ticksPassed = ((IEntityDataSaver) player).getPersistentData().getLong("ticksPassed");
                }

                //aca surge el error
                if (ticksPassed % TICKS_PER_DAY == 0) {
                    DaysData.dayPassed((IEntityDataSaver) player, player);
                    ticksPassed = 1200;
                }

            }
        }
    }

    @SubscribeEvent
    public void stopSleeping(PlayerWakeUpEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof ServerPlayer) {
            DaysData.dayPassed((IEntityDataSaver) entity, entity);
        }
    }

    @SubscribeEvent
    public void afterDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            DaysData.resetDays((IEntityDataSaver) event.getEntity());
        }
    }

    @SubscribeEvent
    public void copyFrom(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            CompoundTag oldNbt = event.getOriginal().getPersistentData();
            CompoundTag newNbt = event.getEntity().getPersistentData();

            newNbt.putInt("recordDay", oldNbt.getInt("recordDay"));
        }
    }
}