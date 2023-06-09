package model;

import java.util.ArrayList;

import application.enums.CargaTipo;
import application.enums.SituacaoCarga;
import application.enums.SituacaoNavio;
import entity.Carga;
import entity.Frete;
import entity.Navio;
import entity.TipoCargaPerecivel;
import entity.Trajeto;

public class ListaFretes {
    private static ListaFretes fretes;
    private ArrayList<Frete> lista;
    int freteId = 1;

    /**
     * O constructor esta private pelo motivo que o objeto é um singleton
     */
    private ListaFretes() {
        lista = new ArrayList<Frete>();
    }

    /**
     * Cria o objeto se for a primeira vez
     * @return O objeto em si
     */
    public static ListaFretes listaFretes(){
        if(fretes == null){
            fretes = new ListaFretes();
        }
        return fretes;
    }

    /**
     * Insere (cadastra) um frete
     * @param idCarga
     * @throws Exception
     */
    public void insereFrete(int idCarga) throws Exception {
        ListaCargas cargas = ListaCargas.listaCargas();
        Carga carga = cargas.searchCarga(idCarga);
        if (carga.getSituacao() != SituacaoCarga.PENDENTE) {
            throw new Exception("Carga já se encontra atribuída a uma entrega.");
        }
        Navio navio = getNavioDisponivel(carga);
        Frete frete = new Frete(freteId, navio, carga);
        lista.add(frete);
        this.alteraSituacaoCarga(carga, "LOCADO");
        this.alteraSituacaoNavio(navio, SituacaoNavio.OCUPADO);
        this.freteId = this.freteId + 1;
    }

    /**
     * Valida o tempo do trajeto de acordo com as informações 
     * de validade (perecivel) e tempo máximo de entrega de uma carga
     * @param Trajeto trajeto
     * @param Navio navio
     * @param Carga carga
     * @return boolean
     */
    public boolean validaTempoTrajeto(Trajeto trajeto, Navio navio, Carga carga) {
        int tempoMax = carga.getTempoMaximo();
        // Tempo maximo será o menor valor entre a validade da carga e o tempo maximo de entrega, caso seja perecivel
        if (carga.getTipoCarga().getTipo() == CargaTipo.PERECIVEL) {
            tempoMax = tempoMax < ((TipoCargaPerecivel) carga.getTipoCarga()).getValidade() ? ((TipoCargaPerecivel) carga.getTipoCarga()).getValidade() : tempoMax;
        }
        // Conversao de segundos para dias
        return ((trajeto.getDistancia() / navio.getVelocidade()) / 86400) <= tempoMax;
    }

    public Frete searchFrete(int id) {
        if (lista.isEmpty()) {
            throw new IllegalArgumentException("Nao ha frete cadastrado.");
        }
        for (Frete f : lista) {
            if (f.getId() == id) {
                return f;
            }
        }
        throw new IllegalArgumentException("Não existe frete com este identificador.");
    }

    /**
     * Procura por um navio disponível, de acordo
     * com os critérios de entrega da carga
     * @param Carga carga
     * @return Navio
     * @throws Exception
     */
    public Navio getNavioDisponivel(Carga carga) throws Exception {
        ListaNavios navios = ListaNavios.listaNavios();
        ListaTrajetos trajetos = ListaTrajetos.getInstance();
        Trajeto trajeto = trajetos.searchTrajeto(carga.getOrigem(), carga.getDestino());
        for (Navio n : navios.getNavios()) {
            if (n.getSituacao() == SituacaoNavio.LIVRE && n.getAutonomia() <= trajeto.getDistancia() 
            && this.validaTempoTrajeto(trajeto, n, carga)) {
                return n;
            }
        }
        this.alteraSituacaoCarga(carga, "CANCELADO");
        throw new Exception("Nao existe navio disponivel para efetuar essa entrega.");
    }

    /**
     * Realiza entrega de uma carga
     * @param frete
     * @throws Exception
     */
    public void entregaCarga(Frete frete) throws Exception {
        this.alteraSituacaoCarga(frete.getCarga(), "FINALIZADO");
        this.alteraSituacaoNavio(frete.getNavio(), SituacaoNavio.LIVRE);
    }

    /**
     * Altera a situação de uma carga
     * @param carga
     * @param situacao
     * @throws Exception
     */
    private void alteraSituacaoCarga(Carga carga, String situacao) throws Exception {
        // Como regra de negócio, uma carga que se encontra cancelada ou finalizada não podem ter seu
        // status alterado
        if (carga.getSituacao().equals(SituacaoCarga.CANCELADO) || carga.getSituacao().equals(SituacaoCarga.FINALIZADO)) {
            throw new Exception("Situacao dessa carga não pode ser alterada pois se encontra "
                    + carga.getSituacao().getDescricao());
        }
        switch (situacao) {
            case "CANCELADO":
                carga.setSituacao(SituacaoCarga.CANCELADO);
                break;
            case "FINALIZADO":
                carga.setSituacao(SituacaoCarga.FINALIZADO);
                break;
            case "PENDENTE":
                carga.setSituacao(SituacaoCarga.PENDENTE);
                break;
            case "LOCADO":
                carga.setSituacao(SituacaoCarga.LOCADO);
                break;
        }
    }

    /**
     * Altera situação do navio (OCUPADO e LIVRE)
     * @param navio
     * @param situacao
     */
    private void alteraSituacaoNavio(Navio navio, SituacaoNavio situacao) {
        navio.setSituacao(situacao);
    }

    public ArrayList<Frete> getFretesList(){
        return lista;
    }
}
