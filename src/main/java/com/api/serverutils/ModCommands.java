package com.api.serverutils;

import com.api.serverutils.network.HistoryRequestPacket;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ModCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("serverutils")
                .requires(source -> source.hasPermission(2)) // Apenas administradores (OP nível 2+) podem rodar
                .then(Commands.literal("alerts")
                        .executes(context -> {
                            if (context.getSource().getPlayer() != null) {
                                // Envia o pacote requisitando o histórico para o servidor
                                ModNetwork.CHANNEL.sendToServer(new HistoryRequestPacket());
                            }
                            return 1;
                        })
                )
        );
    }
}