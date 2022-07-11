package com.argo.comodelivery.activity.activity.adapter;

import android.content.Context;

import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.argo.comodelivery.R;
import com.argo.comodelivery.activity.activity.model.Empresa;
import com.argo.comodelivery.activity.activity.model.Produto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Jamilton
 */

public class AdapterProduto extends RecyclerView.Adapter<AdapterProduto.MyViewHolder>{

    private List<Produto> produtos;
    private Context context;

    private DatabaseReference firebaseRef;
    private String idProdutos;
    private String urlImagemSelecionada="";
    private CircleImageView imageProduto;


    public AdapterProduto(List<Produto> produtos, Context context) {
        this.produtos = produtos;
        this.context = context;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_produto, parent, false);
       // recuperarDadosImagem();
        return new MyViewHolder(itemLista);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Produto produto = produtos.get(i);

        holder.nome.setText(produto.getNomeProduto ());
        holder.descricao.setText(produto.getDescricaoProduto ());
        holder.valor.setText("R$ " + produto.getPrecoProduto ());
        if (produto.getImagemProduto () !=  null){
            Uri uri = Uri.parse (produto.getImagemProduto ()); //convertendo imagem string para imagem uri
            Picasso.get().load (uri).into (holder.foto);
        }else{
            holder.foto.setImageResource (R.drawable.lanche);
        }

    }

    /*private void recuperarDadosImagem(){
        DatabaseReference empresaRef = firebaseRef.child ("produtos").child (idProdutos);
        empresaRef.addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue () != null) {


                    Empresa empresa = snapshot.getValue (Empresa.class);


                    Produto produto = new Produto ();
                    urlImagemSelecionada = produto.getImagemProduto ();

                    if (urlImagemSelecionada.isEmpty ()) {
                        imageProduto.setImageResource (R.drawable.logo);
                    } else {
                        Picasso.get().load (produto.getImagemProduto ()).into (imageProduto);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/

    @Override
    public int getItemCount() {
        return produtos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView foto;

        TextView nome;
        TextView descricao;
        TextView valor;



        public MyViewHolder(View itemView) {
            super(itemView);
           foto = itemView.findViewById (R.id.imageProduto);
            nome = itemView.findViewById(R.id.textNomeRefeicao);
            descricao = itemView.findViewById(R.id.textDescricaoRefeicao);
            valor = itemView.findViewById(R.id.textPreco);
        }
    }
}
