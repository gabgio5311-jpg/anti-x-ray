package com.api.serverutils;

import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.repository.Pack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forgespi.language.IModInfo;

@Mod.EventBusSubscriber(modid = ServerUtilsCore.MOD_ID, value = Dist.CLIENT)
public class ClientCheatDetector {

    private static int contadorTicks = 0;
    private static boolean jaDetectou = false;

    // Esse evento roda 20 vezes por segundo no cliente de forma nativa e segura
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (jaDetectou) return; // Se já mandou o alerta pro server, para de checar

        contadorTicks++;
        // 100 ticks = 5 segundos. Ele vai checar o PC do jogador a cada 5 segundos.
        if (contadorTicks >= 100) {
            contadorTicks = 0;

            Minecraft mc = Minecraft.getInstance();

            // Se o jogador estiver no Singleplayer ou abriu pra LAN local, ignora e não checa nada
            if (mc.level == null || mc.isLocalServer()) {
                return;
            }

            // Se ele estiver em um servidor online de fato, roda a checagem
            verificarCliente();
        }
    }

    private static void verificarCliente() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getResourcePackRepository() == null) return;

        // 1. Checa as texturas ativas (se ele ativar o X-ray no meio da partida, pega aqui)
        for (Pack pack : mc.getResourcePackRepository().getSelectedPacks()) {
            String id = pack.getId().toLowerCase();
            String titulo = pack.getTitle().getString().toLowerCase();

            if (id.contains("xray") || id.contains("x-ray") || titulo.contains("xray") || titulo.contains("x-ray")) {
                ModNetwork.CHANNEL.sendToServer(new AlertPacket("Resource Pack", pack.getTitle().getString()));
                jaDetectou = true;
                return;
            }
        }

        // 2. Checa os mods carregados
        for (IModInfo mod : ModList.get().getMods()) {
            String modId = mod.getModId().toLowerCase();

            if (modId.contains("xray") || modId.contains("wurst") ||modId.contains("cheatutils")|| modId.contains("meteor") || modId.contains("inertiaclient")) {
                ModNetwork.CHANNEL.sendToServer(new AlertPacket("Mod Cheat", mod.getDisplayName()));
                jaDetectou = true;
                return;
            }
        }
    }
}