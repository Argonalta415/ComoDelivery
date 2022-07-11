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
import com.argo.comodelivery.R;
import com.argo.comodelivery.activity.activity.adapter.AdapterEmpresa;
import com.argo.comodelivery.activity.activity.adapter.AdapterProduto;
import com.argo.comodelivery.activity.activity.helper.ConfiguracaoFirebase;
import com.argo.comodelivery.activity.activity.listener.RecyclerItemClickListener;
import com.argo.comodelivery.activity.activity.model.Empresa;
import com.argo.comodelivery.activity.activity.model.Produto;
import com.argo.comodelivery.activity.activity.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    //region VARIAVEIS
    private FirebaseAuth autenticacao;
    private MaterialSearchView searchView;
    private RecyclerView recyclerViewEmpresa;
    private List<Empresa> empresas = new ArrayList<> ();
    private DatabaseReference firebaseRef;
    private AdapterEmpresa adapterEmpresa;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_home);

        incializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase ();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao ();
        Usuario usuario = new Usuario ();

        //region TOOBAR CONFIGURACAO

        Toolbar toolbar = findViewById (R.id.toolbar);
        toolbar.setTitle (usuario.getNomeUsuario ()); //titulo
        setSupportActionBar (toolbar);
        //endregion

        //region CONFIGURAÇÃO DO RECYCLERVIEW
        recyclerViewEmpresa.setLayoutManager (new LinearLayoutManager (this));
        recyclerViewEmpresa.setHasFixedSize (true);
        adapterEmpresa = new AdapterEmpresa (empresas);
        recyclerViewEmpresa.setAdapter (adapterEmpresa);
        //endregion

        //recuperar empresas


        recuperarEmpresas();

        //region CONFIGURANDO PESQUISA
        searchView.setHint ("Pesquisar restaurantes");
        searchView.setOnQueryTextListener (new MaterialSearchView.OnQueryTextListener () {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                pesquisarEmpresas(newText);
                return true;
            }
        });
        //endregion

        //region CONFIGURANDO EVENTO DE SELEÇÃO EMPRESA
        recyclerViewEmpresa.addOnItemTouchListener (new RecyclerItemClickListener (this, recyclerViewEmpresa, new RecyclerItemClickListener.OnItemClickListener () {
            @Override
            public void onItemClick(View view, int position) {
                Empresa empresaSelecionada = empresas.get (position);
                Intent  i = new Intent (HomeActivity.this, CardapioActivity.class);
                i.putExtra ("empresa", empresaSelecionada);
                startActivity (i);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        }));

        //endregion
    }

    private void pesquisarEmpresas(String pesquisa){
        DatabaseReference empresaRef = firebaseRef.child ("empresas");
        Query query = empresaRef.orderByChild ("nome").startAt (pesquisa).endAt (pesquisa + "\uf8ff");
        query.addListenerForSingleValueEvent (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                empresas.clear ();
                for (DataSnapshot ds: snapshot.getChildren ()){
                    empresas.add (ds.getValue (Empresa.class));

                }
                adapterEmpresa.notifyDataSetChanged ();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void recuperarEmpresas(){
        DatabaseReference empresaRef = firebaseRef.child ("empresas");
        empresaRef.addValueEventListener (new ValueEventListener () {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                empresas.clear ();
                for (DataSnapshot ds: snapshot.getChildren ()){
                    empresas.add (ds.getValue (Empresa.class));

                }
                adapterEmpresa.notifyDataSetChanged ();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater ();

        inflater.inflate (R.menu.menu_usuario, menu);

        /**
             * CONFIGURACAO BOTAO PESQUISA*/
        MenuItem item = menu.findItem (R.id.menuPesquisa);
        searchView.setMenuItem (item);

        return super.onCreateOptionsMenu (menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId ()){
            case R.id.menuSair:deslogarUsuario();
                break;
            case R.id.menuConfiguracoes:abrirConfiguracoes();
                break;

        }
        return super.onOptionsItemSelected (item);
    }

    private void incializarComponentes(){
        searchView = findViewById (R.id.materialSearchView);
        recyclerViewEmpresa = findViewById (R.id.recycleEmpresas);
    }

    private void deslogarUsuario(){
        try {
            autenticacao.signOut ();
            finish ();
        }catch (Exception e){
            e.printStackTrace ();
        }
    }

    private void abrirConfiguracoes(){
        startActivity ( new Intent (HomeActivity.this, ConfiguracoesUsuarioActivity.class ));
    }

}