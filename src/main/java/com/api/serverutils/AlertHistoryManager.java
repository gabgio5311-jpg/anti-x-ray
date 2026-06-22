package com.api.serverutils;

import java.util.ArrayList;
import java.util.List;

public class AlertHistoryManager {

    public static class AlertEntry {
        public String playerName;
        public String cheatType;
        public String cheatName;
        public String timestamp;

        public AlertEntry(String playerName, String cheatType, String cheatName, String timestamp) {
            this.playerName = playerName;
            this.cheatType = cheatType;
            this.cheatName = cheatName;
            this.timestamp = timestamp;
        }
    }

    // Lista em memória que armazena os alertas recebidos no servidor
    public static final List<AlertEntry> HISTORICO = new ArrayList<>();

    public static void adicionarAlerta(String jogador, String tipo, String nomeMod) {
        java.time.LocalDateTime agora = java.time.LocalDateTime.now();
        java.time.format.DateTimeFormatter formatador = java.time.format.DateTimeFormatter.ofPattern("dd/MM HH:mm:ss");
        String dataFormatada = agora.format(formatador);

        // Adiciona sempre na primeira posição para os mais recentes aparecerem no topo
        HISTORICO.add(0, new AlertEntry(jogador, tipo, nomeMod, dataFormatada));

        // Limita o histórico em memória a 100 registros para evitar uso desnecessário de RAM
        if (HISTORICO.size() > 100) {
            HISTORICO.remove(HISTORICO.size() - 1);
        }
    }
}