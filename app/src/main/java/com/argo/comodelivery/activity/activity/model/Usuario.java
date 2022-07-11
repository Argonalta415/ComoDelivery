package com.argo.comodelivery.activity.activity.model;

import static com.argo.comodelivery.activity.activity.helper.UsuarioFirebase.getIdUsuario;

import com.argo.comodelivery.activity.activity.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

public class Usuario {
    private String id_Usuario;
    private String nomeUsuario;
    private String contatoUsuario;
    private String bairroUsuario;
    private String ruaUsuario;
    private String numeroUsuario;
    private String pontoRefUsuario;

    public Usuario() {
    }

    public void salvar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase ();
        DatabaseReference usuarioRef = firebaseRef.child ("usuario").child (getIdUsuario ());
        usuarioRef.setValue (this);
        // firebaseRef.setValue (urlImagem.toString ());
    }

    public String getId_Usuario() {
        return id_Usuario;
    }

    public void setId_Usuario(String id_Usuario) {
        this.id_Usuario = id_Usuario;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getContatoUsuario() {
        return contatoUsuario;
    }

    public void setContatoUsuario(String contatoUsuario) {
        this.contatoUsuario = contatoUsuario;
    }

    public String getBairroUsuario() {
        return bairroUsuario;
    }

    public void setBairroUsuario(String bairroUsuario) {
        this.bairroUsuario = bairroUsuario;
    }

    public String getRuaUsuario() {
        return ruaUsuario;
    }

    public void setRuaUsuario(String ruaUsuario) {
        this.ruaUsuario = ruaUsuario;
    }

    public String getNumeroUsuario() {
        return numeroUsuario;
    }

    public void setNumeroUsuario(String numeroUsuario) {
        this.numeroUsuario = numeroUsuario;
    }

    public String getPontoRefUsuario() {
        return pontoRefUsuario;
    }

    public void setPontoRefUsuario(String pontoRefUsuario) {
        this.pontoRefUsuario = pontoRefUsuario;
    }
}
