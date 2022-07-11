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
import com.argo.comodelivery.activity.activity.model.Produto;
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

public class NovoProdutoEmpresaActivity extends AppCompatActivity {

    //region VARIAVEIS
    private EditText editProdutoNome;
    private EditText editProdutoDescricao;
    private EditText editProdutoPreco;
    private CircleImageView imageProduto;
    private static  final int SELECAO_GALERIA = 200;
    private StorageReference storageReference;
    private String urlImagemSelecionada="";
    private String idUsuarioLogado;
    private DatabaseReference firebaseRef;

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_novo_produto_empresa);


        inicializarComponentes ();

        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();
       // firebaseRef = ConfiguracaoFirebase.getFirebase ();

        imageProduto.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                Intent i = new Intent (Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult (i, SELECAO_GALERIA);
            }
        });

        //region TOOBAR CONFIGURACAO
        Toolbar toolbar = findViewById (R.id.toolbar);
        toolbar.setTitle ("Novo produto"); //titulo
        setSupportActionBar (toolbar);
        getSupportActionBar ().setDisplayHomeAsUpEnabled (true); //seta voltar
        //endregion

       // recuperarDadosProduto ();
    }

    /**
     * VALIDACAO DOS CAMPOS DO PRODUTO*/
    public  void validarDadosProduto(View view){

        String nome = editProdutoNome.getText().toString();
        String descricao = editProdutoDescricao.getText().toString();
        String preco = editProdutoPreco.getText().toString();
        String imagem = imageProduto.toString ();


        if(!nome.isEmpty()){
            if(!descricao.isEmpty()){
                if(!preco.isEmpty()){
                    if(!imagem.isEmpty()){
                        Produto produto = new Produto ();
                        produto.setIdUsuario (idUsuarioLogado);
                        produto.setNomeProduto (nome);
                        produto.setDescricaoProduto (descricao);
                        produto.setPrecoProduto (Double.parseDouble (preco));
                        produto.setImagemProduto (urlImagemSelecionada);
                        produto.salvar ();
                        finish ();
                        exibirMensagem ("Produto salvo com sucesso!");

                    }else{
                        exibirMensagem("Selecione uma imagem para o produto!");
                    }
                }else{
                    exibirMensagem("Selecione o preço do produto!");
                }
            }else{
                exibirMensagem("Descreva o produto!");
            }
        }else{
            exibirMensagem("Defina o nome do produto!");
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
                    imageProduto.setImageBitmap (imagem);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream ();
                    imagem.compress (Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray ();

                    final StorageReference imagemRef = storageReference.child ("imagens").child ("produto").child (idUsuarioLogado +"jpeg");

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
                                Toast.makeText(NovoProdutoEmpresaActivity.this, "Sucesso ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(NovoProdutoEmpresaActivity.this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
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
     * LIGA VARIAVEIS COM OS COMPONENTES DA TELA*/
    private void inicializarComponentes(){
        imageProduto = findViewById(R.id.imageProduto);
        editProdutoNome = findViewById(R.id.editprodutoNome);
        editProdutoDescricao = findViewById(R.id.editProdutoDescricao);
        editProdutoPreco = findViewById(R.id.editProdutoPreco);
    }

}
