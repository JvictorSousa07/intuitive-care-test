package br.com.joao.ans.domain;

import java.util.Optional;

public enum UF {

    AC, AL, AP, AM,
    BA, CE, DF, ES, GO,
    MA, MT, MS, MG,
    PA, PB, PR, PE, PI,
    RJ, RN, RS, RO, RR,
    SC, SP, SE, TO,

    ND;

    public static Optional<UF> from(String valor) {
        if (valor == null || valor.isBlank()) {
            return Optional.empty();
        }

        try {
            return Optional.of(UF.valueOf(valor.trim().toUpperCase()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public static boolean isValida(String valor) {
        return from(valor).isPresent();
    }
}
