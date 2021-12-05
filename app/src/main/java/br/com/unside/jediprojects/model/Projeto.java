package br.com.unside.jediprojects.model;


import java.util.Date;

public class Projeto {

    private String uuidProjeto;
    private String nomeProjeto;
    private Date dataInicio;
    private Date dataTermino;
    private int riscoProjeto;
    private double valorProjeto;
    private String listaParticipante;


    public Projeto() {
    }

    public String getUuidProjeto() {
        return uuidProjeto;
    }

    public void setUuidProjeto(String uuidProjeto) {
        this.uuidProjeto = uuidProjeto;
    }

    public String getNomeProjeto() {
        return nomeProjeto;
    }

    public void setNomeProjeto(String nomeProjeto) {
        this.nomeProjeto = nomeProjeto;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataTermino() {
        return dataTermino;
    }

    public void setDataTermino(Date dataTermino) {
        this.dataTermino = dataTermino;
    }

    public int getRiscoProjeto() {
        return riscoProjeto;
    }

    public void setRiscoProjeto(int riscoProjeto) {
        this.riscoProjeto = riscoProjeto;
    }

    public double getValorProjeto() {
        return valorProjeto;
    }

    public void setValorProjeto(double valorProjeto) {
        this.valorProjeto = valorProjeto;
    }

    public String getListaParticipante() {
        return listaParticipante;
    }

    public void setListaParticipante(String listaParticipante) {
        this.listaParticipante = listaParticipante;
    }
}
