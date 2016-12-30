package br.edu.ifba.tcc.iot.winerfid.bean;

import java.util.ArrayList;

/**
 * Created by randler on 10/10/16.
 */
public class WineBean {

    private ArrayList<String> caixas_tag_id = new ArrayList<String>();
    private String palete_id;
    private String status_pedido_pallet;
    private boolean saida;
    private boolean search;

    public boolean isSaida() {
        return saida;
    }

    public void setSaida(boolean saida) {
        this.saida = saida;
    }

    public boolean isSearch() {
        return search;
    }

    public void setSearch(boolean search) {
        this.search = search;
    }

    public ArrayList<String> getTag_id() {
        return caixas_tag_id;
    }

    public void addTagId(String id){
        caixas_tag_id.add(id);
    }

    public ArrayList<String> getCaixas_tag_id() {
        return caixas_tag_id;
    }

    public void setCaixas_tag_id(ArrayList<String> caixas_tag_id) {
        this.caixas_tag_id = caixas_tag_id;
    }

    public void addCaixa(String caixa){
        caixas_tag_id.add(caixa);
    }

    public String getPalete_id() {
        return palete_id;
    }

    public void setPalete_id(String palete_id) {
        this.palete_id = palete_id;
    }

    public String getStatus_pedido_pallet() {
        return status_pedido_pallet;
    }

    public void setStatus_pedido_pallet(String status_pedido_pallet) {
        this.status_pedido_pallet = status_pedido_pallet;
    }
}
