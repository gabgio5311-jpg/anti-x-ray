package com.api.serverutils;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.repository.Pack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forgespi.language.IModInfo;
import com.api.serverutils.network.HistoryRequestPacket;
import com.mojang.blaze3d.platform.InputConstants;

public class ClientCheatDetector {

    private static int contadorTicks = 0;
    private static boolean jaDetectou = false;

    // 1. CRIANDO A TECLA 'H'
    public static final KeyMapping ABRIR_HISTORICO_KEY = new KeyMapping(
            "Abrir Histórico Anti-Cheat",
            InputConstants.KEY_H,
            "key.categories.misc"
    );

    // Evento que escuta o Tick do Cliente (Barramento FORGE padrão)
    @Mod.EventBusSubscriber(modid = ServerUtilsCore.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeClientEvents {

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;

            // --- ESCUTA O CLIQUE DA TECLA H (Fica antes da trava do cheat!) ---
            while (ABRIR_HISTORICO_KEY.consumeClick()) {
                if (Minecraft.getInstance().player != null) {
                    // Pede o histórico para o servidor dedicado
                    ModNetwork.CHANNEL.sendToServer(new HistoryRequestPacket());
                }
            }

            if (jaDetectou) return; // Trava do cheat só afeta o loop abaixo

            contadorTicks++;
            if (contadorTicks >= 100) { // A cada 5 segundos
                contadorTicks = 0;

                Minecraft mc = Minecraft.getInstance();
                if (mc.level == null || mc.isLocalServer()) {
                    return;
                }
                verificarCliente();
            }
        }
    }

    // 2. REGISTRANDO A TECLA NO EVENTO DE INICIALIZAÇÃO (Barramento MOD essencial)
    @Mod.EventBusSubscriber(modid = ServerUtilsCore.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModClientEvents {
        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(ABRIR_HISTORICO_KEY);
        }
    }

    private static void verificarCliente() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getResourcePackRepository() == null) return;

        // Checa as texturas ativas
        for (Pack pack : mc.getResourcePackRepository().getSelectedPacks()) {
            String id = pack.getId().toLowerCase();
            String titulo = pack.getTitle().getString().toLowerCase();

            if (id.contains("xray") || id.contains("x-ray") || titulo.contains("xray") || titulo.contains("x-ray")) {
                ModNetwork.CHANNEL.sendToServer(new AlertPacket("Resource Pack", pack.getTitle().getString()));
                jaDetectou = true;
                return;
            }
        }

        // Checa os mods carregados
        for (IModInfo mod : ModList.get().getMods()) {
            String modId = mod.getModId().toLowerCase();

            if (modId.contains("xray") || modId.contains("wurst") || modId.contains("cheatutils") || modId.contains("meteor") || modId.contains("inertiaclient")) {
                ModNetwork.CHANNEL.sendToServer(new AlertPacket("Mod Cheat", mod.getDisplayName()));
                jaDetectou = true;
                return;
            }
        }
    }
}