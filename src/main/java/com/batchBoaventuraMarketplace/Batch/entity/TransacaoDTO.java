package com.batchBoaventuraMarketplace.Batch.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class TransacaoDTO {
    private Long id;
    private Timestamp dataPedido;
    private String nomeProduto;
    private String descricaoProduto;
    private String valorProduto;
    private String nomeNovoDono;
    private String nomeAntigoDono;

    // Getters e setters manuais
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getDataPedido() {
        return dataPedido;
    }

    public void setDataPedido(Timestamp dataPedido) {
        this.dataPedido = dataPedido;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public String getDescricaoProduto() {
        return descricaoProduto;
    }

    public void setDescricaoProduto(String descricaoProduto) {
        this.descricaoProduto = descricaoProduto;
    }

    public String getNomeNovoDono() {
        return nomeNovoDono;
    }

    public void setNomeNovoDono(String nomeNovoDono) {
        this.nomeNovoDono = nomeNovoDono;
    }

    public String getNomeAntigoDono() {
        return nomeAntigoDono;
    }

    public void setNomeAntigoDono(String nomeAntigoDono) {
        this.nomeAntigoDono = nomeAntigoDono;
    }

    public String getValorProduto() {
        return valorProduto;
    }

    public void setValorProduto(String valorProduto) {
        this.valorProduto = valorProduto;
    }
}

