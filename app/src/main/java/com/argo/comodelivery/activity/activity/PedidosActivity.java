package com.argo.comodelivery.activity.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.argo.comodelivery.R;
import com.argo.comodelivery.activity.activity.adapter.AdapterPedido;
import com.argo.comodelivery.activity.activity.adapter.AdapterProduto;
import com.argo.comodelivery.activity.activity.helper.ConfiguracaoFirebase;
import com.argo.comodelivery.activity.activity.helper.UsuarioFirebase;
import com.argo.comodelivery.activity.activity.listener.RecyclerItemClickListener;
import com.argo.comodelivery.activity.activity.model.Pedido;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class PedidosActivity extends AppCompatActivity {

    private RecyclerView recyclerPedidos;
    private AdapterPedido adapterPedido;
    private List<Pedido> pedidos = new ArrayList<> ();
    private AlertDialog dialog;
    private DatabaseReference firebaseRef;
    private String idEmpresa;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_pedidos);

        
        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase ();
        idEmpresa = UsuarioFirebase.getIdUsuario ();
        
        //region TOOBAR CONFIGURACAO
        Toolbar toolbar = findViewById (R.id.toolbar);
        toolbar.setTitle ("Pedidos"); //titulo
        setSupportActionBar (toolbar);
        getSupportActionBar ().setDisplayHomeAsUpEnabled (true);
        //endregion

        //region CONFIGURAÇÃO DO RECYCLERVIEW
        recyclerPedidos.setLayoutManager (new LinearLayoutManager (this));
        recyclerPedidos.setHasFixedSize (true);
        adapterPedido = new AdapterPedido (pedidos);
        recyclerPedidos.setAdapter (adapterPedido);
        //endregion

        recuperarPedidos();

        recyclerPedidos.addOnItemTouchListener (new RecyclerItemClickListener (this, recyclerPedidos, new RecyclerItemClickListener.OnItemClickListener () {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onLongItemClick(View view, int position) {
                Pedido pedido = pedidos.get (position);
                pedido.setStatus ("Finalizado");
                pedido.atualizarStatus();
            }

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        }));

    }

    private void recuperarPedidos() {
      /*  dialog = new SpotsDialog.Builder ().setContext (this).setMessage ("Carregando...").setCancelable (false).build ();
        dialog.show ();*/

        DatabaseReference pedidoRef = firebaseRef.child ("pedidos").child (idEmpresa);
        Query pedidoPesquisa = pedidoRef.orderByChild ("status").equalTo ("Confirmado");
        pedidoPesquisa.addListenerForSingleValueEvent (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            pedidos.clear ();
            if (snapshot.getValue () != null){
                for (DataSnapshot ds: snapshot.getChildren ()   ){
                    Pedido pedido = ds.getValue (Pedido.class);
                    pedidos.add (pedido);
                }
                adapterPedido.notifyDataSetChanged ();
                //dialog.dismiss ();
            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void inicializarComponentes() {
        recyclerPedidos = findViewById (R.id.recyclerPedidos);
    }
}