package kaede.reabista.command;

import kaede.reabista.registry.ModAttributes;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.RegisterCommandsEvent;

import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.EntityArgument;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;

import net.minecraftforge.common.util.FakePlayerFactory;

import java.util.Collection;

@Mod.EventBusSubscriber
public class AbilityTeleportCommand {

    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("abilityteleport")
                        .requires(src -> true) // 権限0

                        // /abilityteleport <targets> <pos>
                        .then(Commands.argument("targets", EntityArgument.entities())
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                        .executes(ctx -> {
                                            Entity caller = getCaller(ctx.getSource().getEntity(), ctx.getSource().getLevel());
                                            if (canUseAbility(caller)) return 0;

                                            Level world = ctx.getSource().getLevel();
                                            Collection<? extends Entity> targets = EntityArgument.getEntities(ctx, "targets");
                                            BlockPos pos = BlockPosArgument.getLoadedBlockPos(ctx, "pos");

                                            teleportEntities(world, targets, pos);
                                            return 1;
                                        })
                                )
                        )

                        // /abilityteleport <target>
                        .then(Commands.argument("target", EntityArgument.entity())
                                .executes(ctx -> {
                                    Entity caller = getCaller(ctx.getSource().getEntity(), ctx.getSource().getLevel());
                                    if (canUseAbility(caller)) return 0;

                                    Level world = ctx.getSource().getLevel();
                                    Entity target = EntityArgument.getEntity(ctx, "target");
                                    teleportEntity(world, target, caller.blockPosition());
                                    return 1;
                                })
                        )

                        // /abilityteleport <pos>
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes(ctx -> {
                                    Entity caller = getCaller(ctx.getSource().getEntity(), ctx.getSource().getLevel());
                                    if (canUseAbility(caller)) return 0;

                                    Level world = ctx.getSource().getLevel();
                                    BlockPos pos = BlockPosArgument.getLoadedBlockPos(ctx, "pos");
                                    teleportEntity(world, caller, pos);
                                    return 1;
                                })
                        )
        );
    }

    // ===============================
    //        能力条件チェック
    // ===============================
    private static boolean canUseAbility(Entity entity) {
        if (entity == null) return true;
        if (!(entity instanceof Player player)) return true;

        double attr = player.getAttributeBaseValue(ModAttributes.ABILITY.get());
        double AP = player.getAttributeBaseValue(ModAttributes.ABILITY_POINT.get());

        if (attr == 4){
            return false;
        } else if (attr == 1 & AP >= 100) {
            return false;
        }else if (attr == 2 & AP >= 25) {
            return false;
        }else{
            return true;
        }
    }

    // ===============================
    //      通常の TP ロジック
    // ===============================
    private static Entity getCaller(Entity e, Level world) {
        if (e != null) return e;
        if (world instanceof ServerLevel sl)
            return FakePlayerFactory.getMinecraft(sl);
        return null;
    }

    private static void teleportEntities(Level world, Collection<? extends Entity> list, BlockPos pos) {
        for (Entity e : list) teleportEntity(world, e, pos);
    }

    private static void teleportEntity(Level world, Entity entity, BlockPos pos) {
        if (!(world instanceof ServerLevel)) return;
        if (entity == null) return;

        entity.teleportTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
    }
}