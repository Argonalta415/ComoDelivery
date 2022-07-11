package com.argo.comodelivery.activity.activity.model;

import android.provider.ContactsContract;

import com.argo.comodelivery.activity.activity.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.List;

public class Pedido {

    private String idUsuario;
    private String idEmpresa;
    private String idPedido;
    private String nome;
    private String numeroTel;
    private String pontoRef;
    private String numeroCasa;
    private String endereco;
    private String bairro;
    private List<ItensPedido> itens;
    private Double total;
    private String status = "Pendente";
    private int metodoPagamento;
    private String observacao;

    public Pedido() {
    }

    public void atualizarStatus(){
        HashMap<String, Object>status = new HashMap<> ();
        status.put ("status", getStatus ());
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase ();
        DatabaseReference pedidoRef = firebaseRef.child ("pedidos").child (getIdEmpresa ()).child ( getIdPedido ());
        pedidoRef.updateChildren (status);
    }

    public Pedido(String idUsuario, String idEmpresa) {
        setIdUsuario (idUsuario);
        setIdEmpresa (idEmpresa);

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase ();
        DatabaseReference pedidoRef = firebaseRef.child ("pedidos_usuario").child (idEmpresa).child (idUsuario);
        setIdPedido (pedidoRef.push ().getKey ());
    }


    public void removerPedido() {
        setIdUsuario (idUsuario);
        setIdEmpresa (idEmpresa);

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase ();
        DatabaseReference pedidoRef = firebaseRef.child ("pedidos_usuario").child (idEmpresa).child (idUsuario);
       pedidoRef.removeValue ();
    }


    public void salvar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase ();
        DatabaseReference pedidoRef = firebaseRef.child ("pedidos_usuario").child (getIdEmpresa ()).child (getIdUsuario ());
        pedidoRef.setValue (this);

    }

    public void confirmar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase ();
        DatabaseReference pedidoRef = firebaseRef.child ("pedidos").child (getIdEmpresa ()).child ( getIdPedido ());
        pedidoRef.setValue (this);

    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdEmpresa() {
        return idEmpresa;
    }

    public String getNumeroTel() {
        return numeroTel;
    }

    public void setNumeroTel(String numeroTel) {
        this.numeroTel = numeroTel;
    }

    public String getPontoRef() {
        return pontoRef;
    }

    public void setPontoRef(String pontoRef) {
        this.pontoRef = pontoRef;
    }

    public String getNumeroCasa() {
        return numeroCasa;
    }

    public void setNumeroCasa(String numeroCasa) {
        this.numeroCasa = numeroCasa;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public void setIdEmpresa(String idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String idPedido) {
        this.idPedido = idPedido;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public List<ItensPedido> getItens() {
        return itens;
    }

    public void setItens(List<ItensPedido> itens) {
        this.itens = itens;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(int metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
