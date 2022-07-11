package com.argo.comodelivery.activity.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.argo.comodelivery.R;
import com.argo.comodelivery.activity.activity.adapter.AdapterProduto;
import com.argo.comodelivery.activity.activity.helper.ConfiguracaoFirebase;
import com.argo.comodelivery.activity.activity.helper.UsuarioFirebase;
import com.argo.comodelivery.activity.activity.listener.RecyclerItemClickListener;
import com.argo.comodelivery.activity.activity.model.Empresa;
import com.argo.comodelivery.activity.activity.model.ItensPedido;
import com.argo.comodelivery.activity.activity.model.Pedido;
import com.argo.comodelivery.activity.activity.model.Produto;
import com.argo.comodelivery.activity.activity.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class CardapioActivity extends AppCompatActivity {

    private RecyclerView recyclerProdutosCardapio;
    private ImageView imageEmpresaCardapio;
    private TextView textNomeEmpresaCardapio;
    private Empresa empresaSelecionada;

    private AdapterProduto adapterProduto;
    private List<Produto> produtos = new ArrayList<> ();
    private DatabaseReference firebaseRef;
    private String idEmpresa;
    private String idUsuarioLogado;
    private Usuario usuario;
    private Pedido pedidoRecuperado;
    private int qtdItensCarrinho;
    private Double totalCarrinho;

    private TextView textCarrinhoqtd;
    private TextView textCarrinhoTotal;

    private int metodoPagamento;


    //arrey pedidos
    private List<ItensPedido> itensCarrinho = new ArrayList<> ();

    //Dialog carregar
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_cardapio);

        inicializarComponentes();

        firebaseRef = ConfiguracaoFirebase.getFirebase ();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario ();


        //region CONFIGURANDO EMPRESA SELECIONADA
        Bundle bundle = getIntent ().getExtras ();
        if (bundle != null){
            empresaSelecionada = (Empresa) bundle.getSerializable ("empresa");
            idEmpresa = empresaSelecionada.getIdUsuario ();
            textNomeEmpresaCardapio.setText ( empresaSelecionada.getNome ());
            String url = empresaSelecionada.getUrlImagem ();
            Picasso.get ().load (url).into (imageEmpresaCardapio);
        }
        //endregion

        //region TOOBAR CONFIGURACAO
        Toolbar toolbar = findViewById (R.id.toolbar);
        toolbar.setTitle ("Cardápio"); //titulo
        setSupportActionBar (toolbar);
        getSupportActionBar ().setDisplayHomeAsUpEnabled (true); //seta voltar
        //endregion

        //region CONFIGURAÇÃO DO RECYCLERVIEW
        recyclerProdutosCardapio.setLayoutManager (new LinearLayoutManager (this));
        recyclerProdutosCardapio.setHasFixedSize (true);
        adapterProduto = new AdapterProduto (produtos, this);
        recyclerProdutosCardapio.setAdapter (adapterProduto);
        //endregion

        //region SELECIONANDO QUANTIDADE DO PRODUTO
        recyclerProdutosCardapio.addOnItemTouchListener (new RecyclerItemClickListener (this, recyclerProdutosCardapio, new RecyclerItemClickListener.OnItemClickListener () {
            @Override
            public void onItemClick(View view, int position) {
                confirmarQuantidaed(position);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        }));
        //endregion

        recuperarProdutos();
        recuperarDadosUsuario();
    }

    private void confirmarQuantidaed(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder (this);
        builder.setTitle ("Quantidade");
        builder.setMessage ("Digite a quantidade");

        EditText editQuantidade = new EditText (this);
        editQuantidade.setText ("1");

        builder.setView (editQuantidade);

        builder.setPositiveButton ("Confimar", new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String quantidade = editQuantidade.getText ().toString ();
                Produto produtoSelecionado = produtos.get (position);
                ItensPedido itensPedido = new ItensPedido ();
                itensPedido.setIdProduto (produtoSelecionado.getIdProduto ());
                itensPedido.setNomeProduto (produtoSelecionado.getNomeProduto ());
                itensPedido.setDescricaoProduto (produtoSelecionado.getDescricaoProduto ());
                itensPedido.setPreco (produtoSelecionado.getPrecoProduto ());
                itensPedido.setQuantidade (Integer.parseInt (quantidade));
                itensCarrinho.add (itensPedido);
                //verificando pedido recuperado
                if (pedidoRecuperado == null){
                    pedidoRecuperado = new Pedido (idUsuarioLogado,idEmpresa);

                }

                pedidoRecuperado.setNome (usuario.getNomeUsuario ());
                pedidoRecuperado.setEndereco (usuario.getRuaUsuario ());
                pedidoRecuperado.setNumeroTel (usuario.getContatoUsuario ());
                pedidoRecuperado.setNumeroCasa (usuario.getNumeroUsuario ());
                pedidoRecuperado.setBairro (usuario.getBairroUsuario ());
                pedidoRecuperado.setPontoRef (usuario.getPontoRefUsuario ());
                pedidoRecuperado.setItens (itensCarrinho);
                pedidoRecuperado.salvar();
            }
        });
        builder.setNegativeButton ("Cancelar", new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.create ();
        dialog.show ();
    }

    private void recuperarDadosUsuario() {
        dialog = new SpotsDialog.Builder ().setContext (this).setMessage ("Carregando...").setCancelable (false).build ();
        dialog.show ();
        DatabaseReference usuariosRef =  firebaseRef.child ("usuario").child (idUsuarioLogado);
        usuariosRef.addListenerForSingleValueEvent (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ( snapshot.getValue () != null){
                        usuario = snapshot.getValue (Usuario.class);
                }
                recuperarPedido();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void recuperarPedido() {
        DatabaseReference pedidoRef = firebaseRef.child ("pedidos_usuario").child (idEmpresa).child (idUsuarioLogado);
        pedidoRef.addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                qtdItensCarrinho = 0;
                totalCarrinho = 0.0;
                itensCarrinho = new ArrayList<> ();
                if (snapshot.getValue ()!=null){

                    pedidoRecuperado =snapshot.getValue (Pedido.class);
                    itensCarrinho = pedidoRecuperado.getItens ();

                    for (ItensPedido itensPedido: itensCarrinho){
                        int qtde = itensPedido.getQuantidade ();
                        Double preco = itensPedido.getPreco ();
                        totalCarrinho += (qtde * preco);
                        qtdItensCarrinho += qtde;

                    }
                }

                DecimalFormat df = new DecimalFormat ("0.00");

                textCarrinhoqtd.setText ("qtde: " + String.valueOf (qtdItensCarrinho));
                textCarrinhoTotal.setText ("R$: " + df.format (totalCarrinho));
                dialog.dismiss ();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater ();
        inflater.inflate (R.menu.menu_cardapio, menu);
        return super.onCreateOptionsMenu (menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId ()){
            case R.id.menuPedido:
                confirmarPedido();
                break;
        }
        return super.onOptionsItemSelected (item);
    }

    private void confirmarPedido() {
        AlertDialog.Builder builder = new AlertDialog.Builder (this);
        builder.setTitle ("Selecione um método de pagamento!");

        CharSequence[] itens = new CharSequence[]{
          "Dinheiro", "Cartão"
        };
        builder.setSingleChoiceItems (itens, 0, new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                metodoPagamento = which;
            }
        });
        EditText editObservacao = new EditText (this);
        editObservacao.setHint ("Digite uma observação");
        builder.setView (editObservacao);

        builder.setPositiveButton ("Confirmar", new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String observacao =editObservacao.getText ().toString ();
                pedidoRecuperado.setMetodoPagamento (metodoPagamento);
                pedidoRecuperado.setObservacao (observacao);
                pedidoRecuperado.setStatus ("Confirmado");
                pedidoRecuperado.confirmar();
                pedidoRecuperado.removerPedido ();
                pedidoRecuperado = null;
            }
        });

        builder.setNegativeButton ("Cancelar", new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog dialog = builder.create ();
        dialog.show ();
    }

    private void inicializarComponentes(){
        recyclerProdutosCardapio = findViewById (R.id.recycleProdutoCardapio);
        imageEmpresaCardapio = findViewById (R.id.imageEmpresaCardapio);
        textNomeEmpresaCardapio = findViewById (R.id.textNomeEmpresaCardapio);

        textCarrinhoqtd = findViewById (R.id.textCarrinhoQtde);
        textCarrinhoTotal = findViewById (R.id.textCarrinhoTot);
    }


    /**
     * RECUPERA O PRODUTOS SALVO NO BD*/
    private void recuperarProdutos(){
        DatabaseReference produtosRef = firebaseRef.child ("produtos").child (idEmpresa);
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