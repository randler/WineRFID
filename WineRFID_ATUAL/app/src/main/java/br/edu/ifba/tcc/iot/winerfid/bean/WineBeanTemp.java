package br.edu.ifba.tcc.iot.winerfid.bean;

/**
 * Created by randler on 10/10/16.
 */
public class WineBeanTemp {

    private String tag_id ;
    private String n_palete;
    private String data_cad;
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

    public String getData_cad() {
        return data_cad;
    }

    public void setData_cad(String data_cad) {
        this.data_cad = data_cad;
    }

    public String getTag_id() {
        return tag_id;
    }
    public void setTagId(String id){

        this.tag_id = id;
    }

    public String getN_palete() {
        return n_palete;
    }

    public void setN_palete(String n_palete) {
        this.n_palete = n_palete;
    }

}