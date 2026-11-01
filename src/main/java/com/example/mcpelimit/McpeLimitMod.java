package com.example.mcpelimit;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class McpeLimitMod implements ModInitializer {
    public static final String MOD_ID = "mcpelimit";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Lista de itens liberados (baseada no MCPE 0.5.0, com IDs atualizados para 1.21.8)
    public static final Set<String> WHITELIST = Set.of(
        "minecraft:air", "minecraft:stone", "minecraft:grass_block", "minecraft:dirt",
        "minecraft:cobblestone", "minecraft:oak_planks", "minecraft:bedrock", "minecraft:sand",
        "minecraft:gravel", "minecraft:gold_ore", "minecraft:iron_ore", "minecraft:coal_ore",
        "minecraft:oak_log", "minecraft:oak_leaves", "minecraft:glass", "minecraft:lapis_ore",
        "minecraft:sandstone", "minecraft:yellow_wool", "minecraft:cyan_wool", "minecraft:magenta_wool",
        "minecraft:blue_wool", "minecraft:brown_wool", "minecraft:green_wool", "minecraft:red_wool",
        "minecraft:black_wool", "minecraft:white_wool", "minecraft:dandelion", "minecraft:poppy",
        "minecraft:brown_mushroom", "minecraft:red_mushroom", "minecraft:gold_block", "minecraft:iron_block",
        // 'minecraft:slab' não existe mais -> variantes atuais
        "minecraft:stone_slab", "minecraft:cobblestone_slab", "minecraft:sandstone_slab", "minecraft:oak_slab",
        "minecraft:brick_stairs", "minecraft:cobblestone_stairs", "minecraft:mossy_cobblestone",
        "minecraft:obsidian", "minecraft:torch", "minecraft:oak_stairs", "minecraft:diamond_ore",
        "minecraft:diamond_block", "minecraft:crafting_table", "minecraft:farmland", "minecraft:furnace",
        "minecraft:ladder", "minecraft:snow", "minecraft:ice", "minecraft:cactus",
        "minecraft:clay",
        "minecraft:sugar_cane",
        "minecraft:oak_fence", // 'minecraft:fence' -> 'oak_fence'
        "minecraft:glowstone",
        "minecraft:stone_bricks", "minecraft:glass_pane", "minecraft:melon", "minecraft:netherrack",
        "minecraft:soul_sand", "minecraft:stone_brick_stairs", "minecraft:nether_bricks", "minecraft:nether_brick_stairs",
        "minecraft:iron_shovel", "minecraft:iron_pickaxe", "minecraft:iron_axe", "minecraft:flint_and_steel",
        "minecraft:apple", "minecraft:bow", "minecraft:arrow", "minecraft:coal",
        "minecraft:diamond", "minecraft:iron_ingot", "minecraft:gold_ingot", "minecraft:wooden_sword",
        "minecraft:wooden_shovel", "minecraft:wooden_pickaxe", "minecraft:wooden_axe", "minecraft:stone_sword",
        "minecraft:stone_shovel", "minecraft:stone_pickaxe", "minecraft:stone_axe", "minecraft:diamond_sword",
        "minecraft:diamond_shovel", "minecraft:diamond_pickaxe", "minecraft:diamond_axe", "minecraft:stick",
        "minecraft:bowl", "minecraft:mushroom_stew", "minecraft:golden_sword", "minecraft:golden_shovel",
        "minecraft:golden_pickaxe", "minecraft:golden_axe", "minecraft:string", "minecraft:feather",
        "minecraft:gunpowder", "minecraft:wooden_hoe", "minecraft:stone_hoe", "minecraft:iron_hoe",
        "minecraft:diamond_hoe", "minecraft:golden_hoe", "minecraft:wheat_seeds", "minecraft:wheat",
        "minecraft:bread", "minecraft:leather_helmet", "minecraft:leather_chestplate", "minecraft:leather_leggings",
        "minecraft:leather_boots", "minecraft:chainmail_helmet", "minecraft:chainmail_chestplate", "minecraft:chainmail_leggings",
        "minecraft:chainmail_boots", "minecraft:iron_helmet", "minecraft:iron_chestplate", "minecraft:iron_leggings",
        "minecraft:iron_boots", "minecraft:diamond_helmet", "minecraft:diamond_chestplate", "minecraft:diamond_leggings",
        "minecraft:diamond_boots", "minecraft:golden_helmet", "minecraft:golden_chestplate", "minecraft:golden_leggings",
        "minecraft:golden_boots", "minecraft:flint", "minecraft:porkchop", "minecraft:cooked_porkchop",
        "minecraft:painting", "minecraft:golden_apple", "minecraft:oak_door", "minecraft:redstone",
        "minecraft:snowball", "minecraft:leather", "minecraft:brick", "minecraft:clay_ball",
        "minecraft:paper", "minecraft:book", "minecraft:slime_ball", "minecraft:egg",
        "minecraft:glowstone_dust", "minecraft:bone", "minecraft:sugar",
        "minecraft:red_bed", // 'minecraft:bed' -> 'red_bed' (cor padrão)
        "minecraft:cookie", "minecraft:melon_slice", "minecraft:melon_seeds", "minecraft:beef",
        "minecraft:cooked_beef", "minecraft:chicken", "minecraft:cooked_chicken", "minecraft:rotten_flesh"
    );

    @Override
    public void onInitialize() {
        // Verifica o inventário de todos os jogadores a cada tick (inclui armadura e mão secundária)
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                checkPlayer(player);
            }
        });
        LOGGER.info("McpeLimit carregado: {} itens na whitelist.", WHITELIST.size());
    }

    private static void checkPlayer(ServerPlayerEntity player) {
        var inv = player.getInventory();
        for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getStack(i);
            if (isBlocked(stack)) {
                notifyAndRemove(player, stack);
                inv.setStack(i, ItemStack.EMPTY);
            }
        }

        // Item que está no cursor (ex.: pego no inventário criativo)
        ItemStack cursor = player.currentScreenHandler.getCursorStack();
        if (isBlocked(cursor)) {
            notifyAndRemove(player, cursor);
            player.currentScreenHandler.setCursorStack(ItemStack.EMPTY);
        }
    }

    private static boolean isBlocked(ItemStack stack) {
        if (stack.isEmpty()) return false;
        String id = Registries.ITEM.getId(stack.getItem()).toString();
        return !WHITELIST.contains(id);
    }

    private static void notifyAndRemove(ServerPlayerEntity player, ItemStack stack) {
        String id = Registries.ITEM.getId(stack.getItem()).toString();
        player.sendMessage(Text.literal("O item " + id + " não existia no MCPE 0.5.0!"), true);
    }
}
