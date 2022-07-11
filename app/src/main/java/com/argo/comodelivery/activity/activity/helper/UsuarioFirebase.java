package com.argo.comodelivery.activity.activity.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UsuarioFirebase {
    public static String getIdUsuario(){
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao ();
        return autenticacao.getCurrentUser ().getUid ();
    }
    /**recuperando usuario atual*/
    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAutenticacao ();
        return usuario.getCurrentUser ();
    }

    public static boolean atualizarTipoUsuaio (String tipo){
        try {

            FirebaseUser user =  getUsuarioAtual ();
            UserProfileChangeRequest profile  = new UserProfileChangeRequest.Builder ()
                    .setDisplayName (tipo).build ();
            user.updateProfile (profile);
            return true;
        }catch (Exception e){
            e.printStackTrace ();
            return false ;
        }
    }

}
