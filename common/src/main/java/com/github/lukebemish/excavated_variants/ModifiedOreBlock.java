package com.github.lukebemish.excavated_variants;

import com.github.lukebemish.excavated_variants.data.BaseOre;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.OreBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModifiedOreBlock extends OreBlock {
    private final BaseOre ore;

    private Block target;

    public ModifiedOreBlock(Properties properties, BaseOre ore) {
        super(properties);
        this.ore = ore;
    }

    public void copyProperties() {
        this.target = RegistryUtil.getBlockById(ore.rl_block_id.get(0));
        if (this.target != null) {
            this.properties.strength(target.defaultDestroyTime()).color(target.defaultMaterialColor());
        }
    }

    @Override
    public MaterialColor defaultMaterialColor() {
        if (target != null) {
            return this.target.defaultMaterialColor();
        }
        return super.defaultMaterialColor();
    }

    @Override
    public float defaultDestroyTime() {
        if (target != null) {
            return this.target.defaultDestroyTime();
        }
        return super.defaultDestroyTime();
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        Block target = RegistryUtil.getBlockById(ore.rl_block_id.get(0));
        if (target != null) {
            state = target.defaultBlockState();
            ResourceLocation resourceLocation = target.getLootTable();
            if (resourceLocation == BuiltInLootTables.EMPTY) {
                return Collections.emptyList();
            }
            LootContext lootContext = builder.withParameter(LootContextParams.BLOCK_STATE, state).create(LootContextParamSets.BLOCK);
            ServerLevel serverLevel = lootContext.getLevel();
            LootTable lootTable = serverLevel.getServer().getLootTables().get(resourceLocation);
            List<ItemStack> items = lootTable.getRandomItems(lootContext);
            return items.stream().map((x) -> {
                if (x.is(target.asItem()) && this.asItem() != Items.AIR) {
                    int count = x.getCount();
                    ItemStack out = new ItemStack(this.asItem(), count);
                    return out;
                }
                return x;
            }).toList();
        }
        return new ArrayList<>();
    }

    @Override
    public void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack stack) {
        Block target = RegistryUtil.getBlockById(ore.rl_block_id.get(0));
        if (target != null) {
            target.spawnAfterBreak(state, level, pos, stack);
        } else {
            super.spawnAfterBreak(state, level, pos, stack);
        }
    }
}
