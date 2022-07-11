package com.argo.comodelivery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.argo.comodelivery.activity.activity.helper.ConfiguracaoFirebase;
import com.argo.comodelivery.activity.activity.helper.UsuarioFirebase;
import com.argo.comodelivery.activity.activity.model.Empresa;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConfiguracoesEmpresaActivity extends AppCompatActivity {

    /*private EditText editEmpresaNome;
    private EditText editEmpresaCategoria;
    private EditText editEmpresaTempo;
    private EditText editTaxaEmpresa;
    private CircleImageView imagePerfilEmpresa;
    private static final int  SELECAO_GALERIA = 200;
    private StorageReference storageReference;
    private String idUsuarioLogado;
     */

    private EditText editEmpresaNome;
    private EditText editTaxaEmpresa;
    private EditText editEmpresaTempo;
    private EditText editEmpresaCategoria;
    private EditText editEndereco;
    private EditText editContato;
    private CircleImageView imagePerfilEmpresa;
    private static  final int SELECAO_GALERIA = 200;
    private StorageReference storageReference;
    private String idUsuarioLogado;
    private String urlImagemSelecionada="";

    private DatabaseReference firebaseRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_empresa);

        inicializarComponentes();


        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();
        firebaseRef = ConfiguracaoFirebase.getFirebase ();

        //configtoolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações");
        //toolbar.setBottom (Integer.parseInt ("salvar"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       /* imagePerfilEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(i.resolveActivity(getPackageManager())!= null){
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });*/

        imagePerfilEmpresa.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                Intent i = new Intent (Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult (i, SELECAO_GALERIA);
            }
        });
        recuperarDadosEmpresa();

    }

    /**
     * VALIDACAO DOS DADOS DA EMPRESA*/
    public  void validarDadosEmpresa(View view){

        String nome = editEmpresaNome.getText().toString();
        String taxa = editTaxaEmpresa.getText().toString();
        String categoria = editEmpresaCategoria.getText().toString();
        String tempo = editEmpresaTempo.getText().toString();
        String endereco = editEndereco.getText ().toString ();
        String contato = editContato.getText ().toString ();

        if(!nome.isEmpty()){
            if(!taxa.isEmpty()){
                if(!categoria.isEmpty()){
                    if(!tempo.isEmpty()){
                        if (!endereco.isEmpty ()) {
                            if (!contato.isEmpty ()) {
                                Empresa empresa = new Empresa ();
                                empresa.setIdUsuario (idUsuarioLogado);
                                empresa.setNome (nome);
                                empresa.setPrecoEntrega (Double.parseDouble (taxa));
                                empresa.setCategoria (categoria);
                                empresa.setTempo (tempo);
                                empresa.setUrlImagem (urlImagemSelecionada);
                                empresa.setEndereco (endereco);
                                empresa.setContato (contato);
                                empresa.salvar ();
                                finish ();
                            }else{
                                exibirMensagem("Informe o numero de contato da empresa!");
                            }
                        }else{
                            exibirMensagem("Informe o endereço da empresa!");
                        }
                    }else{
                        exibirMensagem("Escolha um tempo de entrega");
                    }
                }else{
                    exibirMensagem("Digite uma categoria");
                }
            }else{
                exibirMensagem("Coloque uma taxa de entrega");
            }
        }else{
            exibirMensagem("Digite um nome para empresa");
        }
    }

    private void exibirMensagem(String texto){
        Toast.makeText(this, texto,Toast.LENGTH_SHORT).show();
    }

    /**
     * SALVAR IMAGEM NO FIFREBASE*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            Bitmap imagem = null;

            try {
                switch (requestCode){
                    case SELECAO_GALERIA:
                        Uri localImagem = data.getData ();
                        imagem = MediaStore.Images.Media.getBitmap (getContentResolver (),localImagem);
                        break;
                }
                if (imagem !=null){
                    imagePerfilEmpresa.setImageBitmap (imagem);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream ();
                    imagem.compress (Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray ();

                    final StorageReference imagemRef = storageReference.child ("imagens").child ("empresas").child (idUsuarioLogado +"jpeg");

                    UploadTask uploadTask = imagemRef.putBytes (dadosImagem);

                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>> () {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()){
                                throw task.getException();
                            }
                            return imagemRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUrl = task.getResult();
                                urlImagemSelecionada = downloadUrl.toString();
                                Toast.makeText(ConfiguracoesEmpresaActivity.this, "Sucesso ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(ConfiguracoesEmpresaActivity.this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace ();

            }
        }
    }

    /**
     * RECUPERAR DADOS DO FIREBASE*/
    private void recuperarDadosEmpresa(){
        DatabaseReference empresaRef = firebaseRef.child ("empresas").child (idUsuarioLogado);
        empresaRef.addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue () != null) {

                    /**
                     * RECUPERANDO DADOS DO PARA TELA EMPRESA*/
                    Empresa empresa = snapshot.getValue (Empresa.class);
                    editEmpresaNome.setText (empresa.getNome ());
                    editEmpresaCategoria.setText (empresa.getCategoria ());
                    editEmpresaTempo.setText (empresa.getTempo ());
                    editTaxaEmpresa.setText (empresa.getPrecoEntrega ().toString ());
                    editEndereco.setText (empresa.getEndereco ().toString ());
                    editContato.setText (empresa.getContato ().toString ());


                    urlImagemSelecionada = empresa.getUrlImagem ();

                    if (urlImagemSelecionada.isEmpty ()) {
                        imagePerfilEmpresa.setImageResource (R.drawable.logo);
                    } else {
                        Picasso.get().load (empresa.getUrlImagem()).into (imagePerfilEmpresa);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * LIGA VARIAVEIS COM OS COMPONENTES DA TELA*/
    private void inicializarComponentes(){
        imagePerfilEmpresa = findViewById(R.id.imageEmpresa);
        editEmpresaTempo = findViewById(R.id.editEmpresaTempo);
        editEmpresaNome = findViewById(R.id.editprodutoNome);
        editEmpresaCategoria = findViewById(R.id.editProdutoDescricao);
        editTaxaEmpresa = findViewById(R.id.editEmpresaTaxa);
        editEndereco = findViewById (R.id.editProdutoPreco);
        editContato = findViewById (R.id.editEmpresaContato);
    }



}



