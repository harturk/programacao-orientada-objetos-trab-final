package entity;

import application.enums.CustoRegiao;
import application.enums.PrioridadeFrete;
import model.ListaTrajetos;

public class Frete {
    private int id;
    private Navio navio;
    private Carga carga;
    private CustoRegiao custoRegiao;

    public Frete(int id, Navio navio, Carga carga) {
        this.id = id;
        this.navio = navio;
        this.carga = carga;
        CustoRegiao regiao = CustoRegiao.NACIONAL;
        if (!carga.getDestino().getPais().equals("Brasil") || !carga.getDestino().getPais().equals("Brasil")) {
            regiao = CustoRegiao.INTERNACIONAL;
        }
        this.custoRegiao = regiao;
    }

    public int getId() {
        return this.id;
    }

    public Navio getNavio() {
        return this.navio;
    }

    public Carga getCarga() {
        return this.carga;
    }

    public CustoRegiao getCustoRegiao() {
        return this.custoRegiao;
    }

    public double calculafrete() {
        ListaTrajetos listaTrajetos = ListaTrajetos.getInstance();
        Trajeto trajeto = listaTrajetos.searchTrajeto(carga.getOrigem(), carga.getDestino());
        int fatorPrioridade = 1;
        if (this.carga.getPrioridade() == PrioridadeFrete.RAPIDO) {
            fatorPrioridade = 2;
        }
        return (fatorPrioridade * this.navio.getCustoPorMilhaBasico()) * trajeto.getDistancia() + carga.calculaPreco() + this.custoRegiao.getCusto();
    }

    public String toString() {
        String data = this.carga.toString();
        data += "<html><br>Custo Frete: " + this.calculafrete() + "</html>";
        return data;
    }
}


