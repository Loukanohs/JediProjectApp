package br.com.unside.jediprojects.presentation.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
import android.widget.RelativeLayout.LayoutParams;

import br.com.unside.jediprojects.R;
import br.com.unside.jediprojects.model.Projeto;
import br.com.unside.jediprojects.presentation.activities.Activity_Home;
import br.com.unside.jediprojects.presentation.activities.Activity_Project;

public class ProjetosAdapter extends RecyclerView.Adapter<ProjetosAdapter.ViewHolder> {

    private final List<Projeto> listProjetos;
    private Context contextAd;

/**O Adapter é responsável por fazer uma Exibição para cada item no conjunto de resultados.
 * O Adaptador é responsável por criar visualizações filhas usadas para representar cada
 * item e fornecer acesso aos dados.
 * */

/**Recebo no metodo contrutor a lista de projetos da tela home, e o contexto, no caso a Activity_Home*/
    public ProjetosAdapter (Context context, List<Projeto> list){
        contextAd = context;
        listProjetos = list;
    }

    /**Essa classe é um ViewHolder, como o nome já diz,
     * ela segura uma view, dessa forma podemos referenciala com mais facilidade
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        //resgatar todos os campus do layout que vou referenciar
        private final TextView txt_nome_lista,txt_data_lista,txt_valor_lista,
                txt_risco_lista, txt_participantes_lista;
        private final Button btn_simular, btn_excluir, btn_editar;

        public ViewHolder(View view){
            super(view);
            //referencio eles
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

    /**Aqui eu indico o layout base que vou usar e falo a tela em quem vou usar esse lay
     */
    @NonNull
    @Override
    public ProjetosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(contextAd)
                .inflate(R.layout.produtos_item, parent, false);
        return new ViewHolder(view);
    }

    /** Aqui é onde preencho os dados equivalantes na textview.
     *Com a posicao de cada item, nada fica fora do lugar.
     * Tambem está sendo dado uma acao aos botes e referenciando eles ao obj mostrado
     */
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

    /**Retorno o tamanho da lista, caso nula, retorno 0 para n causar exceções*/
    @Override
    public int getItemCount() {
        return listProjetos != null ? listProjetos.size() : 0;
    }

    /**Caso selecione EDITAR é enviado para a tela Activity_Project onde ocorre o cadastro e
     * edicacao. Tambem é enviado o uuid do objeto e eavisado que é modo de edicao
     */
    private void editar(int position){
        Intent intent = new Intent(contextAd, Activity_Project.class);
        intent.putExtra("isEdit",true);
        intent.putExtra("uuidProject",listProjetos.get(position).getUuidProjeto());
        contextAd.startActivity(intent);
    }

    /**Caso selecione EXCLUIR aparece uma tela de dialogo perguntando se deseja excluir aquele projeto.
     * Se sim, o objeto é removido da lista e do banco de dados
     */
    private void excluir(int position){

        //criacao do dialogo
        AlertDialog.Builder builder = new AlertDialog.Builder(contextAd);
        builder.setTitle("DELETAR PROJETO")
                .setMessage("Deseja DELETAR o projeto '"+listProjetos.get(position).getNomeProjeto()+"' ?");

        builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                //procura o objeto selecionado e caso encontrado, o objeto é apagado
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
                // fecha o dialogo
            }
        });
        builder.show();
        notifyDataSetChanged();

    }

    /**Caso selecione SIMULAR INVESTIMENTO, abre um dialogo pedindo para colocar o valor do investimento.
     * O valor de investimento nao pode ser menor que o do projeto.
     * O cálculo de retorno do investimento deve levar em consideração o risco e valor que será investido.
     * (RISCO BAIXO - 0: 5%DO VALOR DE INVESTIMENTO)(RISCO MEDIO - 1: 10% DO VALOR DE INVESTIMENTO)(RISCO ALTO - 2: 20% DO VALOR DE INVESTIMENTO)
     */
    private void simularInvestimento(int position){
        AlertDialog.Builder simularDialog = new AlertDialog.Builder(contextAd);
        simularDialog.setTitle("SIMULAR INVESTIMENTO")
                .setMessage("Simular Investimento no projeto '"+listProjetos.get(position).getNomeProjeto()
                        +"'. Insira um valor maior ou igual a R$ "+listProjetos.get(position).getValorProjeto()+" no campo abaixo!");

        //CRIACAO DO EDITTEXT DO DIALOGO
        final EditText edt_invest = new EditText(contextAd);
        edt_invest.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        edt_invest.setPadding(50,20,50,20);
        simularDialog.setView(edt_invest);
        edt_invest.requestFocus();

        simularDialog.setPositiveButton("CONFIRMAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Double retornoInvest = 0.0, percent = 0.0,
                        investimento = 0.0,
                        valorProjeto = listProjetos.get(position).getValorProjeto();

                try{
                    investimento = Double.parseDouble(edt_invest.getText().toString());
                    if(investimento >= valorProjeto){
                        switch (listProjetos.get(position).getRiscoProjeto()){
                            case 0:
                                //RISCO: 0 - BAIXO
                                percent = 0.05;
                                break;
                            case 1:
                                //RISCO: 1 - MÉDIO
                                percent = 0.1;
                                break;
                            case 2:
                                //RISCO: 2 - ALTO
                                percent = 0.2;
                                break;
                        }
                        retornoInvest = investimento * percent;
                        AlertDialog.Builder retornoDialog = new AlertDialog.Builder(contextAd);
                        retornoDialog.setTitle("INVESTIMENTO SIMULADO")
                                .setMessage("O retorno do projeto '"+listProjetos.get(position).getNomeProjeto()
                                        +"' é de R$ "+retornoInvest+"!! Cerca de "+percent*100+"% do valor investido!");
                        retornoDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(contextAd, "SIMULAÇÃO CONCLUIDA!", Toast.LENGTH_LONG).show();
                            }
                        });
                        retornoDialog.show();


                    }else{
                        Toast.makeText(contextAd, "VALOR DE INVESTIMENTO ABAIXO DO LIMITE!", Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(contextAd, "VALOR INVÁLIDO (USE . EM VEZ DE ,)", Toast.LENGTH_LONG).show();
                }


            }
        });
        simularDialog.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // fecha o dialogo
            }
        });
        simularDialog.show();
        notifyDataSetChanged();
    }
}
