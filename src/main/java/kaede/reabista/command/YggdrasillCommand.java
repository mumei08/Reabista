package com.kaede.newabista.command;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.common.util.FakePlayerFactory;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.Vec2;
import net.minecraft.network.chat.Component;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.Direction;

@Mod.EventBusSubscriber
public class YggdrasillCommand {

    // -------------------------------
    //      コマンド登録
    // -------------------------------
    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {

        event.getDispatcher().register(
                Commands.literal("yggdrasill")
                        .requires(source -> source.hasPermission(0)) // OP2以上
                        .executes(ctx -> {

                            Level world = ctx.getSource().getLevel();
                            double x = ctx.getSource().getPosition().x();
                            double y = ctx.getSource().getPosition().y();
                            double z = ctx.getSource().getPosition().z();

                            Entity entity = ctx.getSource().getEntity();
                            if (entity == null && world instanceof ServerLevel sl)
                                entity = FakePlayerFactory.getMinecraft(sl);

                            Direction direction = entity != null ? entity.getDirection() : Direction.DOWN;

                            // ★ 統合した処理を呼び出す
                            runYggdrasillProcedure(world, x, y, z);

                            return 1;
                        })
        );
    }


    // --------------------------------------
    //     ここから下が手続き(Procedure)
    // --------------------------------------
    private static void runYggdrasillProcedure(LevelAccessor world, double x, double y, double z) {

        if (!(world instanceof ServerLevel originalLevel))
            return;

        LevelAccessor before = world;

        // Yggdrasil ディメンション取得
        ServerLevel yggLevel = originalLevel.getServer().getLevel(
                ResourceKey.create(Registries.DIMENSION, new ResourceLocation("reabista:yggdrasill"))
        );

        if (yggLevel == null) {
            return;
        }

        // -------------------------
        //   構造配置
        // -------------------------
        StructureTemplate template = yggLevel.getStructureManager().getOrCreate(
                new ResourceLocation("reabista", "yg_1")
        );

        BlockPos pos = new BlockPos(-50, -50, -50);

        template.placeInWorld(
                yggLevel,
                pos,
                pos,
                new StructurePlaceSettings()
                        .setRotation(Rotation.NONE)
                        .setMirror(Mirror.NONE)
                        .setIgnoreEntities(false),
                yggLevel.random,
                3
        );

        // -------------------------
        //     ポータル生成
        // -------------------------
        yggLevel.getServer().getCommands().performPrefixedCommand(
                new CommandSourceStack(
                        CommandSource.NULL,
                        new Vec3(x, y, z),
                        Vec2.ZERO,
                        yggLevel,
                        4,
                        "",
                        Component.literal(""),
                        yggLevel.getServer(),
                        null
                ).withSuppressedOutput(),
                "fill 31 7 14 29 5 14 minecraft:nether_portal"
        );
    }
}