package com.api.serverutils;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ServerUtilsCore.MOD_ID)
public class ServerUtilsCore {
    public static final String MOD_ID = "server_utils_api";

    public ServerUtilsCore() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModNetwork::register);
    }

    // ADICIONE ESTE BLOCO ABAIXO PARA O FORGE PEGAR O SEU COMANDO:
    @Mod.EventBusSubscriber(modid = ServerUtilsCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class CommandRegistryHandler {
        @SubscribeEvent
        public static void onRegisterCommands(RegisterCommandsEvent event) {
            ModCommands.register(event.getDispatcher());
        }
    }
}