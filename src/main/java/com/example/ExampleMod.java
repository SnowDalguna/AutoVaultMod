package com.example;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

public class ExampleMod implements ModInitializer {
    private int cooldown = 0;

    @Override
    public void onInitialize() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;
            if (cooldown > 0) { cooldown--; return; }

            HitResult hit = client.crosshairTarget;
            if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHit = (BlockHitResult) hit;
                if (client.world.getBlockState(blockHit.getBlockPos()).isOf(Blocks.VAULT)) {
                    PlayerInventory inv = client.player.getInventory();
                    int keySlot = -1;
                    for (int i = 0; i < 9; i++) {
                        if (inv.getStack(i).isOf(Items.OMINOUS_TRIAL_KEY)) {
                            keySlot = i;
                            break;
                        }
                    }
                    if (keySlot != -1) {
                        int previousSlot = inv.selectedSlot;
                        inv.selectedSlot = keySlot;
                        client.interactionManager.interactBlock(client.player, Hand.MAIN_HAND, blockHit);
                        inv.selectedSlot = previousSlot;
                        cooldown = 20; 
                    }
                }
            }
        });
    }
}
