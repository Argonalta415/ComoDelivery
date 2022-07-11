package com.argo.comodelivery.activity.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.argo.comodelivery.R;
import com.argo.comodelivery.activity.activity.helper.ConfiguracaoFirebase;
import com.argo.comodelivery.activity.activity.helper.UsuarioFirebase;
import com.argo.comodelivery.activity.activity.model.Empresa;
import com.argo.comodelivery.activity.activity.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ConfiguracoesUsuarioActivity extends AppCompatActivity {

   private EditText editNome;
   private EditText editContato;
   private EditText editBairro;
   private EditText editRua;
   private EditText editNumero;
   private EditText editPontoRef;

   private String id_Usuario;
   private DatabaseReference firebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_configuracoes_usuario);

        inicializarComponentes();

        id_Usuario = UsuarioFirebase.getIdUsuario ();
        firebaseRef = ConfiguracaoFirebase.getFirebase ();

        //region TOOBAR CONFIGURACAO
        Toolbar toolbar = findViewById (R.id.toolbar);
        toolbar.setTitle ("Configurações do usuário"); //titulo
        setSupportActionBar (toolbar);
        getSupportActionBar ().setDisplayHomeAsUpEnabled (true); //seta voltar
        //endregion

        recuperarDadosUsuario();


    }

    private void recuperarDadosUsuario(){
        DatabaseReference usuarioRef = firebaseRef.child ("usuario").child (id_Usuario);
        usuarioRef.addListenerForSingleValueEvent (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue ()!= null){
                    Usuario usuario = snapshot.getValue (Usuario.class);
                    editNome.setText (usuario.getNomeUsuario ());
                    editContato.setText (usuario.getContatoUsuario ());
                    editBairro.setText (usuario.getBairroUsuario ());
                    editRua.setText (usuario.getRuaUsuario ());
                    editNumero.setText (usuario.getNumeroUsuario ());
                    editPontoRef.setText (usuario.getPontoRefUsuario ());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * VALIDACAO DOS DADOS DA USUARIO*/
    public  void validarDadosUsuario(View view){

        String nome = editNome.getText().toString();
        String contato = editContato.getText ().toString ();
        String bairro = editBairro.getText ().toString ();
        String rua = editRua.getText ().toString ();
        String numero = editNumero.getText ().toString ();
        String pontoRef = editPontoRef.getText ().toString ();

        if (!nome.isEmpty ()){
            if(!contato.isEmpty ()){
                if (!bairro.isEmpty ()){
                    if (!rua.isEmpty ()){
                        if (!numero.isEmpty ()){
                            if (!pontoRef.isEmpty ()){
                                Usuario usuario = new Usuario ();
                                usuario.setId_Usuario (id_Usuario);
                                usuario.setNomeUsuario (nome);
                                usuario.setContatoUsuario (contato);
                                usuario.setBairroUsuario (bairro);
                                usuario.setRuaUsuario (rua);
                                usuario.setNumeroUsuario (numero);
                                usuario.setPontoRefUsuario (pontoRef);
                                usuario.salvar();
                                exibirMensagem ("Dados atualizados com sucesso!");
                                finish ();
                            }else{
                                exibirMensagem("Campo 'Ponto de referencia' e obrigatorio!");
                            }
                        }else{
                            exibirMensagem("Campo 'Numero da residencia' e obrigatorio!");
                        }
                    }else{
                        exibirMensagem("Campo 'Rua/Av' e obrigatorio!");
                    }
                }else{
                    exibirMensagem("Campo 'Bairro' e obrigatorio!");
                }
            }else{
                exibirMensagem("Campo 'Numero' e obrigatorio!");
            }
        }else{
            exibirMensagem("Campo 'Nome' e obrigatorio!");
        }

    }

    private void exibirMensagem(String texto){
        Toast.makeText(this, texto,Toast.LENGTH_SHORT).show();
    }

    private void inicializarComponentes(){
        editNome = findViewById (R.id.editNomeUsuario);
        editContato = findViewById (R.id.editNumeroUsuario);
        editBairro = findViewById (R.id.editBairro);
        editRua = findViewById (R.id.editRua);
        editNumero = findViewById (R.id.editNumeroResidencia);
        editPontoRef = findViewById (R.id.editPontoRef);

    }
}