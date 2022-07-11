package com.argo.comodelivery.activity.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.argo.comodelivery.R;
import com.argo.comodelivery.activity.activity.helper.ConfiguracaoFirebase;
import com.argo.comodelivery.activity.activity.helper.UsuarioFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class AutencicacaoActivity extends AppCompatActivity {

    //region VARIAVEIS COMPONENTES DA TELA

    private EditText campoEmail;
    private EditText campoSenha;
    private Switch tipoAcesso;
    private Switch tipoUsuario;
    private Button btnAcessar;

    private LinearLayout linearLayoutTipoUsuario;

    //endregion

    //region VARIAVEIS AUTENTICAÇÃO
    private FirebaseAuth autenticacao;
    //endregion


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_autencicacao);


        inicializaComponentes();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao ();

        //verificar usuario logado
        verificarUsuarioLogado();

        btnAcessar.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                String email = campoEmail.getText ().toString ();
                String senha = campoSenha.getText ().toString ();

                //testando se o email e senha foram preenchidos
                if (!email.isEmpty ()){
                    if (!senha.isEmpty ()){

                        //verificar swict
                        if (tipoAcesso.isChecked ()){//cadastro
                        autenticacao.createUserWithEmailAndPassword (email, senha).addOnCompleteListener (new OnCompleteListener<AuthResult> () {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful ()){
                                    Toast.makeText (AutencicacaoActivity.this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show ();
                                    String tipoUsuario  = getTipoUsuario ();
                                    UsuarioFirebase.atualizarTipoUsuaio (tipoUsuario);

                                    abrirTelaPrincipal(tipoUsuario);
                                }else {
                                    String erroExcecao = "";
                                  try {
                                      throw task.getException ();

                                  }catch (FirebaseAuthWeakPasswordException e){
                                      erroExcecao = "Digite uma senha mais forte!";
                                  }
                                  catch (FirebaseAuthInvalidCredentialsException e){
                                      erroExcecao= "Por favor digite um e-mail válido";
                                  }
                                  catch (FirebaseAuthUserCollisionException e){
                                      erroExcecao = "Esta conta já foi cadastrada";
                                  }
                                  catch (Exception e){
                                      erroExcecao = "ao cadastrar usuario: " + e.getMessage ();
                                      e.printStackTrace ();
                                  }
                                  Toast.makeText (AutencicacaoActivity.this, "Deu ruim: " + erroExcecao, Toast.LENGTH_LONG).show ();
                                }
                            }
                        });
                        }else{//login
                            autenticacao.signInWithEmailAndPassword (email, senha).addOnCompleteListener (new OnCompleteListener<AuthResult> () {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful ()){
                                        Toast.makeText (AutencicacaoActivity.this, "Logado com sucesso!", Toast.LENGTH_SHORT).show ();

                                        abrirTelaPrincipal (task.getResult ().getUser ().getDisplayName ());
                                    }else {
                                        Toast.makeText (AutencicacaoActivity.this, "Erro em fazer login", Toast.LENGTH_SHORT).show ();

                                    }
                                }
                            });
                        }

                    }else{
                        Toast.makeText (AutencicacaoActivity.this, "Esqueceu de colocar a senha poh!", Toast.LENGTH_SHORT).show ();
                    }

                }else{
                    Toast.makeText (AutencicacaoActivity.this, "Preencha o E-mail meu consagrado!", Toast.LENGTH_SHORT).show ();
                }


            }
        });

        /**
         * EVENTO QUE DEIXA VISIVEL O TIPO DE USUARIO
         */
        tipoAcesso.setOnCheckedChangeListener (new CompoundButton.OnCheckedChangeListener () {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (tipoAcesso.isChecked ()){ // empresa
                    linearLayoutTipoUsuario.setVisibility (View.VISIBLE);
                }else{//usuario
                    linearLayoutTipoUsuario.setVisibility (View.GONE);
                }
            }
        });


    }

    /**
     * Método PADRÃO, PARA LIGAR OS COMPONENTES DA TELA COM AS VARIAVEIS
     */
    private void inicializaComponentes(){
        campoEmail = findViewById (R.id.editprodutoNome);
        campoSenha = findViewById (R.id.editProdutoDescricao);
        tipoAcesso = findViewById (R.id.switchAcesso);
        btnAcessar= findViewById (R.id.buttonAcesso);
        tipoUsuario = findViewById (R.id.switchAcessocad);
        linearLayoutTipoUsuario = findViewById (R.id.linearLayoutTipoUsuario);
    }

    /**
     * ABRIR TELA PRINCIPAL APOS USUARIO LOGADO*/
    private void abrirTelaPrincipal(String tipoUsuario){
     if (tipoUsuario.equals ("E")){
         startActivity ( new Intent (getApplicationContext (), EmpresaActivity.class ));
     }else if (tipoUsuario.equals ("U")){
         startActivity ( new Intent (getApplicationContext (), HomeActivity.class ));
     }
    }

    private String getTipoUsuario(){
        return tipoUsuario.isChecked () ? "E" : "U";
    }

    private void verificarUsuarioLogado(){
        FirebaseUser usuarioAtual = autenticacao.getCurrentUser ();

        if (usuarioAtual != null){
            abrirTelaPrincipal (usuarioAtual.getDisplayName ());
        }
    }
}