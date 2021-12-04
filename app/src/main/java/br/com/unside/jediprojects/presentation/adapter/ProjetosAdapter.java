package br.com.unside.jediprojects.presentation.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.unside.jediprojects.R;
import br.com.unside.jediprojects.model.Projeto;
import br.com.unside.jediprojects.presentation.activities.Activity_Home;
import br.com.unside.jediprojects.presentation.activities.Activity_Project;

public class ProjetosAdapter extends RecyclerView.Adapter<ProjetosAdapter.ViewHolder> {

    private final List<Projeto> listProjetos;
    private Context contextAd;

    public ProjetosAdapter (Context context, List<Projeto> list){
        contextAd = context;
        listProjetos = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txt_nome_lista,txt_data_lista,txt_valor_lista,
                txt_risco_lista, txt_participantes_lista;
        private final Button btn_simular, btn_excluir, btn_editar;

        public ViewHolder(View view){
            super(view);
            txt_nome_lista = view.findViewById(R.id.txt_nome_lista);
            txt_data_lista = view.findViewById(R.id.txt_data_lista);
            txt_valor_lista = view.findViewById(R.id.txt_valor_lista);
            txt_risco_lista = view.findViewById(R.id.txt_risco_lista);
            txt_participantes_lista = view.findViewById(R.id.txt_participantes_lista);
            btn_simular = view.findViewById(R.id.btn_simular);
            btn_excluir = view.findViewById(R.id.btn_excluir);
            btn_editar = view.findViewById(R.id.btn_editar);
        }

        public TextView getTxt_nome_lista() {
            return txt_nome_lista;
        }

        public TextView getTxt_data_lista() {
            return txt_data_lista;
        }

        public TextView getTxt_valor_lista() {
            return txt_valor_lista;
        }

        public TextView getTxt_risco_lista() {
            return txt_risco_lista;
        }

        public TextView getTxt_participantes_lista() {
            return txt_participantes_lista;
        }

        public Button getBtn_simular() {
            return btn_simular;
        }

        public Button getBtn_excluir() {
            return btn_excluir;
        }

        public Button getBtn_editar() {
            return btn_editar;
        }
    }


    @NonNull
    @Override
    public ProjetosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(contextAd)
                .inflate(R.layout.produtos_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder,final int position) {
        viewHolder.getTxt_nome_lista().setText(listProjetos.get(position).getNomeProjeto());

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String dtInicio = format.format(listProjetos.get(position).getDataInicio());
        String dtTermino = format.format(listProjetos.get(position).getDataTermino());
        viewHolder.getTxt_data_lista().setText("DT.INICIAL: "+ dtInicio + "  -  DT.TERMINO: "+ dtTermino);

        viewHolder.getTxt_valor_lista().setText("VALOR: R$ "+ listProjetos.get(position).getValorProjeto());

        String risco = "";
        switch (listProjetos.get(position).getRiscoProjeto()){
            case 0:
                risco = "RISCO: 0 - BAIXO";
                break;
            case 1:
                risco = "RISCO: 1 - MÉDIO";
                break;
            case 2:
                risco = "RISCO: 2 - ALTO";
                break;
        }
        viewHolder.getTxt_risco_lista().setText(risco);

        viewHolder.getTxt_participantes_lista().setText("Participantes: "+listProjetos.get(position).getListaParticipante());

        viewHolder.getBtn_excluir().setOnClickListener(view -> excluir(position));
        viewHolder.getBtn_simular().setOnClickListener(view -> simularInvestimento(position));
        viewHolder.getBtn_editar().setOnClickListener(view -> editar(position));

    }

    @Override
    public int getItemCount() {
        return listProjetos != null ? listProjetos.size() : 0;
    }

    private void editar(int position){
        Intent intent = new Intent(contextAd, Activity_Project.class);
        intent.putExtra("isEdit",true);
        intent.putExtra("uuidProject",listProjetos.get(position).getUuidProjeto());
        contextAd.startActivity(intent);

    }
    private void excluir(int position){

        AlertDialog.Builder builder = new AlertDialog.Builder(contextAd);
        builder.setTitle("DELETAR PROJETO")
                .setMessage("Deseja DELETAR o projeto '"+listProjetos.get(position).getNomeProjeto()+"' ?");

        builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String uuidQuery = listProjetos.get(position).getUuidProjeto();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                Query query = databaseReference.child("projeto").orderByChild("uuidProjeto").equalTo(uuidQuery);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean isDelet = false;
                        for(DataSnapshot objeto : snapshot.getChildren()){
                            if(objeto.exists()){
                                objeto.getRef().removeValue();
                                isDelet = true;
                            }
                        }
                        if(isDelet){

                            /*Intent intent = new Intent(contextAd, Activity_Home.class);
                            contextAd.startActivity(intent);listProjetos.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, listProjetos.size());*/


                            listProjetos.clear();

                            Toast.makeText(contextAd,"PROJETO DELETADO COM SUCESSO!",Toast.LENGTH_LONG).show();
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(contextAd,"ERRO AO DELETAR! ESTOU ATUALIZANDO A PAGINA PARA VERIFICAR SE OUTRA PESSOAS JA DELETOU",Toast.LENGTH_LONG).show();

                    }
                });
            }
        });
        builder.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        AlertDialog dialog = builder.create();
        builder.show();

    }
    private void simularInvestimento(int position){

    }


}
