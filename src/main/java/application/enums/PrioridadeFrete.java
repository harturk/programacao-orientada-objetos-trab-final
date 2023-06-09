package application.enums;

public enum PrioridadeFrete {
    BARATO(1),
    RAPIDO(2);

    private final int fator;

    private PrioridadeFrete(final int fator) {
        this.fator = fator;
    }

    public int getFator() {
        return this.fator;
    }

    public String getName() {
        if(fator==1){
            return "BARATO";
        }return "RAPIDO";
    }
}
