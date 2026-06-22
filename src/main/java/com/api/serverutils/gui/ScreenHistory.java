package com.api.serverutils.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import com.api.serverutils.AlertHistoryManager;
import com.api.serverutils.AlertHistoryManager.AlertEntry;
import java.util.List;

public class ScreenHistory extends Screen {
    private final List<AlertEntry> historico;

    public ScreenHistory(List<AlertEntry> historico) {
        super(Component.literal("Histórico de Notificações Anti-Cheat"));
        this.historico = historico;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        int xCentro = this.width / 2;

        guiGraphics.drawCenteredString(this.font, "§lHISTÓRICO DE ALERTAS (ANTI-CHEAT)", xCentro, 20, 0xFFFFFF);
        guiGraphics.drawCenteredString(this.font, "§7Lista dos últimos trapaceiros detectados", xCentro, 32, 0xAAAAAA);

        int yInicial = 50;
        int espacamento = 16;

        if (historico.isEmpty()) {
            guiGraphics.drawCenteredString(this.font, "§aNenhum alerta registrado até o momento.", xCentro, yInicial + 20, 0x55FF55);
        } else {
            for (int i = 0; i < historico.size(); i++) {
                if (i >= 10) break; // Exibe os 10 primeiros
                AlertEntry entry = historico.get(i);
                String textoLinha = String.format("§8[%s] §c%s §7usou §e%s §f(%s)",
                        entry.timestamp, entry.playerName, entry.cheatType, entry.cheatName);
                guiGraphics.drawString(this.font, textoLinha, xCentro - 180, yInicial + (i * espacamento), 0xFFFFFF);
            }
        }
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}