package com.argo.comodelivery.activity.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.argo.comodelivery.ConfiguracoesEmpresaActivity;
import com.argo.comodelivery.NovoProdutoEmpresaActivity;
import com.argo.comodelivery.R;
import com.argo.comodelivery.activity.activity.adapter.AdapterProduto;
import com.argo.comodelivery.activity.activity.helper.ConfiguracaoFirebase;
import com.argo.comodelivery.activity.activity.helper.UsuarioFirebase;
import com.argo.comodelivery.activity.activity.listener.RecyclerItemClickListener;
import com.argo.comodelivery.activity.activity.model.Produto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EmpresaActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private RecyclerView recyclerViewProdutos;
    private AdapterProduto adapterProduto;
    private List<Produto> produtos = new ArrayList<> ();
    private DatabaseReference firebaseRef;
    private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_empresa);

        //region CONFIGURAÇÕES INICIAIS
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao ();
        firebaseRef = ConfiguracaoFirebase.getFirebase ();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario ();
        //endregion

        inicializarComponentes();

        //region TOOBAR CONFIGURACAO
        Toolbar toolbar = findViewById (R.id.toolbar);
        toolbar.setTitle ("Empresa"); //titulo
        setSupportActionBar (toolbar);
        //endregion

        //region CONFIGURAÇÃO DO RECYCLERVIEW
        recyclerViewProdutos.setLayoutManager (new LinearLayoutManager (this));
        recyclerViewProdutos.setHasFixedSize (true);
        adapterProduto = new AdapterProduto (produtos, this);
        recyclerViewProdutos.setAdapter (adapterProduto);
        //endregion

        recuperarProdutos();

        //adicionar evento de click no reciclerview
        recyclerViewProdutos.addOnItemTouchListener (new RecyclerItemClickListener (this, recyclerViewProdutos, new RecyclerItemClickListener.OnItemClickListener () {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onLongItemClick(View view, int position) {
                Produto produtoSelecionado = produtos.get (position);
                produtoSelecionado.remover();
                Toast.makeText (EmpresaActivity.this,"Produto excluído com sucesso!", Toast.LENGTH_SHORT).show ();

            }

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater ();
        inflater.inflate (R.menu.menu_empresa, menu);
        return super.onCreateOptionsMenu (menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         switch (item.getItemId ()){
             case R.id.menuSair:deslogarUsuario();
             break;
             case R.id.menuConfiguracoes:abrirConfiguracoes();
             break;
             case R.id.menuNovoProduto:abrirNovoProduto();
             break;
             case R.id.menuPedidos:abrirPedidos ();
                 break;
         }
        return super.onOptionsItemSelected (item);
    }

    /**
     * DESLOGA USUARIO */
    private void deslogarUsuario(){
        try {
            autenticacao.signOut ();
            finish ();
        }catch (Exception e){
            e.printStackTrace ();
        }
    }

    /**
     * ABRE O FORMULARIO CONFIGURAÇÕES DA EMPRESA*/
    private void abrirConfiguracoes(){
            startActivity ( new Intent (EmpresaActivity.this, ConfiguracoesEmpresaActivity.class ));
    }

    /**
     * ABRE FORMULARIO DE CADASTRO DO PRODUTO*/
    private void abrirNovoProduto( ){
            startActivity (new Intent (EmpresaActivity.this, NovoProdutoEmpresaActivity.class));
    }

    private void abrirPedidos( ){
        startActivity (new Intent (EmpresaActivity.this, PedidosActivity.class));
    }


    /**
     * LINKA AS VARIAVEIS COM A TELA*/
    private void inicializarComponentes(){
        recyclerViewProdutos = findViewById (R.id.recyclerPedidos);

    }

    /**
     * RECUPERA O PRODUTOS SALVO NO BD*/
    private void recuperarProdutos(){
        DatabaseReference produtosRef = firebaseRef.child ("produtos").child (idUsuarioLogado);
        produtosRef.addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                produtos.clear ();
                for (DataSnapshot ds: snapshot.getChildren ()){
                    produtos.add (ds.getValue (Produto.class));

                }
                adapterProduto.notifyDataSetChanged ();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    

}