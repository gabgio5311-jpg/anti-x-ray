package com.api.serverutils.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import com.api.serverutils.AlertHistoryManager.AlertEntry;
import com.api.serverutils.gui.ScreenHistory;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class HistoryResponsePacket {
    private List<AlertEntry> lista;

    public HistoryResponsePacket(List<AlertEntry> lista) {
        this.lista = lista;
    }

    public HistoryResponsePacket(FriendlyByteBuf buf) {
        int tamanho = buf.readInt();
        this.lista = new ArrayList<>();
        for (int i = 0; i < tamanho; i++) {
            lista.add(new AlertEntry(buf.readUtf(), buf.readUtf(), buf.readUtf(), buf.readUtf()));
        }
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(lista.size());
        for (AlertEntry entry : lista) {
            buf.writeUtf(entry.playerName);
            buf.writeUtf(entry.cheatType);
            buf.writeUtf(entry.cheatName);
            buf.writeUtf(entry.timestamp);
        }
    }

    public static void handle(HistoryResponsePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            net.minecraft.client.Minecraft.getInstance().setScreen(new ScreenHistory(packet.lista));
        });
        context.setPacketHandled(true);
    }
}