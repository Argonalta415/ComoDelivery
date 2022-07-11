package com.argo.comodelivery.activity.activity.model;

import com.argo.comodelivery.activity.activity.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

public class Produto {

    private String idUsuario;

    private String idProduto;
    private String nomeProduto;
    private String descricaoProduto;
    private Double precoProduto;
    private String imagemProduto;

    public Produto() {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase ();
        DatabaseReference produtoRef = firebaseRef.child ("produtos");
        setIdProduto (produtoRef.push ().getKey ());
    }

    public void salvar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase ();
        DatabaseReference produtoRef = firebaseRef.child ("produtos").child (getIdUsuario ()).child (getIdProduto ());
        produtoRef.setValue (this);
        // firebaseRef.setValue (urlImagem.toString ());
    }

    public void remover(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase ();
        DatabaseReference produtoRef = firebaseRef.child ("produtos").child (getIdUsuario ()).child (getIdProduto ());
        produtoRef.removeValue ();
    }

    public String getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(String idProduto) {
        this.idProduto = idProduto;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
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

    public Double getPrecoProduto() {
        return precoProduto;
    }

    public void setPrecoProduto(Double precoProduto) {
        this.precoProduto = precoProduto;
    }

    public String getImagemProduto() {
        return imagemProduto;
    }

    public void setImagemProduto(String imagemProduto) {
        this.imagemProduto = imagemProduto;
    }
}
