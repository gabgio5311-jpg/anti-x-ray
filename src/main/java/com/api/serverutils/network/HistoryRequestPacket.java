package com.api.serverutils.network;

import com.api.serverutils.ModNetwork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import com.api.serverutils.AlertHistoryManager;
import java.util.function.Supplier;

public class HistoryRequestPacket {
    public HistoryRequestPacket() {}
    public HistoryRequestPacket(FriendlyByteBuf buf) {}
    public void toBytes(FriendlyByteBuf buf) {}

    public static void handle(HistoryRequestPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (context.getSender() != null && context.getSender().hasPermissions(2)) {
                ModNetwork.CHANNEL.sendTo(
                        new HistoryResponsePacket(AlertHistoryManager.HISTORICO),
                        context.getSender().connection.connection,
                        net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT
                );
            }
        });
        context.setPacketHandled(true);
    }
}