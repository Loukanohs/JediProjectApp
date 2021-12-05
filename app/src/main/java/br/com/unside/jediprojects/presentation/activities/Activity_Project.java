package br.com.unside.jediprojects.presentation.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rtoshiro.util.format.MaskFormatter;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.UUID;

import br.com.unside.jediprojects.model.Projeto;
import br.com.unside.jediprojects.R;

public class Activity_Project extends AppCompatActivity {

    //variaveis de referencia ao front
    private TextView txt_titulo;
    private EditText edt_nome_projeto, edt_data_inicio, edt_data_termino,
            edt_participantes, edt_valor_projeto;
    private Button btn_concluir,btn_voltar;
    private Spinner spinner_risco;

    //variavel de referencia ao banco do firebase
    private DatabaseReference databaseReference;

    //variavel de projeto
    private Projeto obj_projeto = new Projeto();

    //variaveis de controle
    private boolean isEdit = false;
    private String uuidProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        //resgata a referencia ao banco do fire base
        databaseReference = FirebaseDatabase.getInstance().getReference();
        //resgatar se é edicao ou nao
        isEdit = getIntent().getBooleanExtra("isEdit",false);


        //referenciar variaveis
        txt_titulo = findViewById(R.id.txt_titulo);
        edt_nome_projeto = findViewById(R.id.edt_nome_projeto);
        edt_data_inicio = findViewById(R.id.edt_data_inicio);
        edt_data_termino = findViewById(R.id.edt_data_termino);

        edt_valor_projeto = findViewById(R.id.edt_valor_projeto);
        edt_valor_projeto.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);

        edt_participantes = findViewById(R.id.edt_participantes);
        spinner_risco = findViewById(R.id.spinner_risco);
        btn_concluir = findViewById(R.id.btn_concluir);
        btn_voltar = findViewById(R.id.btn_voltar);

        //inciar spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.select_risco, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_risco.setAdapter(adapter);
        adicionarMascaraData();


        //Ações dos botões
        //Voltar ao menu
        btn_voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity_Project.this, Activity_Home.class);
                startActivity(intent);
                finish();
            }
        });
        //Concluir cadastro ou edicao
        btn_concluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insert_edit_projeto();
            }
        });

        //caso seja edit
        if(isEdit){
            //receber o uuid do obj que vai ser editado
            uuidProject = getIntent().getStringExtra("uuidProject");
            preencherDados();
            txt_titulo.setText("EDITAR PROJETO");

        }else{
            txt_titulo.setText("CADASTRAR PROJETO");
        }
    }


    /**Como so exite um objeto no banco com mesmo uuid, ele vai resgatar esse objeto, porem se alguem
     *apagar esse objeto um pouco antes de resgatar esse objeto pra edicao, ele voltara
     *ao menu. Caso alguem apague antes de terminar de editar, ao concluir a edicao,
     *o objeto será criado novamente com o mesmo uuid anterior, evitando que alguem
     * delete algo que outra pessoa está usando **/
    private void preencherDados(){

        //faz uma requisicao no banco do firebase procurando umm uuid recebido, caso seja edit
        Query query = databaseReference.child("projeto").orderByChild("uuidProjeto").equalTo(uuidProject);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //resgata o objeto encontrado no banco
                for(DataSnapshot objeto : snapshot.getChildren()){
                    obj_projeto = objeto.getValue(Projeto.class);
                }

                //se encontrar um objeto com esse uuid, ele irá preencher a tela com os dados dele
                if(obj_projeto!=null) {
                    edt_nome_projeto.setText(obj_projeto.getNomeProjeto());
                    SimpleDateFormat dtformat = new SimpleDateFormat("dd/MM/yyyy");
                    edt_data_inicio.setText(dtformat.format(obj_projeto.getDataInicio()));
                    edt_data_termino.setText(dtformat.format(obj_projeto.getDataTermino()));
                    edt_valor_projeto.setText(""+obj_projeto.getValorProjeto());
                    edt_participantes.setText(obj_projeto.getListaParticipante());
                    spinner_risco.setSelection(obj_projeto.getRiscoProjeto());
                }else{
                    Toast.makeText(Activity_Project.this, "ERRO AO BUSCAR O PROJETO!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Activity_Project.this, Activity_Home.class);
                    startActivity(intent);
                    finish();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Activity_Project.this, "ERRO AO BUSCAR O PROJETO!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Activity_Project.this, Activity_Home.class);
                startActivity(intent);
                finish();
            }
        });

    }

    /**SIM. A funcao de inserir e editar é identica. O que muda é que ao editar, esterei atualizando
     * um projeto com uuid existente, e ao inserir um novo, eu crio um novo uuid para ele.
     * Esse processo é realizado ao resgatar os valores*/
    private void insert_edit_projeto(){
        if(resgatarValores()){
            databaseReference.child("projeto").child(obj_projeto.getUuidProjeto()).setValue(obj_projeto, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    String mensagem;

                    if(isEdit)mensagem = "PROJETO EDITADO COM SUCESSO!";
                    else  mensagem = "PROJETO CADASTRADO COM SUCESSO!";

                    Toast.makeText(Activity_Project.this, mensagem, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Activity_Project.this, Activity_Home.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    /**Resgata os valores dos EditText e verifica se estão corretos
     * e totalmente preenchidos
     */
    private boolean resgatarValores()  {
        if(isPreenchidos()){
            //caso seja cadastro, gero um UUID novo
            if(!isEdit){
                obj_projeto.setUuidProjeto(UUID.randomUUID().toString());
            }

            //preenchendo o objeto com as informcaoes digitadas
            obj_projeto.setNomeProjeto(edt_nome_projeto.getText().toString());

            //Configurar formato de data dd/mm/yyyy
            SimpleDateFormat dtformat = new SimpleDateFormat("dd/MM/yyyy");
            dtformat.setLenient(false);
            //validacao de datas
            try{
                String dtini = edt_data_inicio.getText().toString();
                obj_projeto.setDataInicio(dtformat.parse(dtini));
            }catch (ParseException e){
                e.printStackTrace();
                edt_data_inicio.requestFocus();
                Toast.makeText(Activity_Project.this, "DATA DE INICIO INVÁLIDA, INSIRA UMA DATA VÁLIDA", Toast.LENGTH_LONG).show();
                return false;
            }
            try{
                String dtfn = edt_data_termino.getText().toString();
                obj_projeto.setDataTermino(dtformat.parse(dtfn));
            }catch (ParseException e){
                e.printStackTrace();
                edt_data_termino.requestFocus();
                Toast.makeText(Activity_Project.this, "DATA DE TÉRMINO INVÁLIDA, INSIRA UMA DATA VÁLIDA", Toast.LENGTH_LONG).show();
                return false;
            }

            //validadao de dados de entrada.
            // Double nao aceita separacao da casa decimal com ','
            try{
                obj_projeto.setValorProjeto(Double.parseDouble(edt_valor_projeto.getText().toString()));
            }catch (Exception e){
                e.printStackTrace();
                edt_valor_projeto.requestFocus();
                Toast.makeText(Activity_Project.this, "VALOR INVÁLIDA (USE . EM VEZ DE ,)", Toast.LENGTH_LONG).show();
                return false;
            }

            obj_projeto.setRiscoProjeto(spinner_risco.getSelectedItemPosition());
            obj_projeto.setListaParticipante(edt_participantes.getText().toString());

            //se tudo tiver ok, posso cadastrar no banco
            return true;
        }else{
            Toast.makeText(Activity_Project.this, "TODOS OS CAMPOS DEVEM ESTÁ PREENCHIDOS!", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    /**Verifica se todos os campos estao preenchidos, ou seja, se nenhum se encontra vazio
    e gera um focus nele*/
    private boolean isPreenchidos(){
        if(TextUtils.isEmpty(edt_nome_projeto.getText().toString())){
            edt_nome_projeto.requestFocus();
            return false;
        }
        else if(TextUtils.isEmpty(edt_data_inicio.getText().toString())){
            edt_data_inicio.requestFocus();
            return false;
        }
        else if(TextUtils.isEmpty(edt_data_termino.getText().toString())){
            edt_data_termino.requestFocus();
            return false;
        }
        else if(TextUtils.isEmpty(edt_valor_projeto.getText().toString())){
            edt_valor_projeto.requestFocus();
            return false;
        }
        else if(TextUtils.isEmpty(edt_participantes.getText().toString())){
            edt_participantes.requestFocus();
            return false;
        }
        return true;
    }

    /**utilizei uma biblioteca externa para criar a mascar.
     * LINK: https://github.com/rtoshiro/MaskFormatter
     */
    private void adicionarMascaraData(){
        SimpleMaskFormatter smf = new SimpleMaskFormatter("NN/NN/NNNN");
        MaskTextWatcher mtw1 = new MaskTextWatcher(edt_data_inicio, smf);
        edt_data_inicio.addTextChangedListener(mtw1);
        MaskTextWatcher mtw2 = new MaskTextWatcher(edt_data_termino, smf);
        edt_data_termino.addTextChangedListener(mtw2);
    }

}