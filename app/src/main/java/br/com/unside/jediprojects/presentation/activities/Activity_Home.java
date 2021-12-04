package br.com.unside.jediprojects.presentation.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.unside.jediprojects.model.Projeto;
import br.com.unside.jediprojects.presentation.adapter.ProjetosAdapter;
import br.com.unside.jediprojects.R;

public class Activity_Home extends AppCompatActivity {


    private Button btn_cadastrar, btn_atualizar;
    private RecyclerView rv_lista_projetos;

    private List<Projeto> listProjetos = new ArrayList<>();

    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //resgata a referencia ao banco do fire base
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //referenciar variaveis
        btn_cadastrar = findViewById(R.id.btn_cadastrar);
        btn_atualizar = findViewById(R.id.btn_atualizar);
        rv_lista_projetos = findViewById(R.id.rv_lista_projetos);

        //definir o tipo ded exibicao da lista
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv_lista_projetos.setLayoutManager(layoutManager);

        listarProjetos();

        //acao do botao mudar de tela para o cadastrar
        btn_cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity_Home.this, Activity_Project.class);
                intent.putExtra("isEdit",false);
                startActivity(intent);
                finish();
            }
        });

        //atualiza a lista de projetos
        btn_atualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listarProjetos();
            }
        });
    }


    //funcao que preenche a lista com os objetos do banco de dados
    private void listarProjetos(){
        //limpo a lista pra evitar erros
        listProjetos.clear();

        //metodo que retorna todos os objetos do nó referente a 'projeto' no banco de dados do firebase
        databaseReference.child("projeto").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //adiciono os objetos a minha lista de Projetos
                for(DataSnapshot objeto : snapshot.getChildren()){
                    listProjetos.add(objeto.getValue(Projeto.class));
                }
                if(!listProjetos.isEmpty()) {
                    ProjetosAdapter projetosAdapter = new ProjetosAdapter(Activity_Home.this,listProjetos);
                    rv_lista_projetos.setAdapter(projetosAdapter);
                }
                else{
                    Toast.makeText(Activity_Home.this, "NÃO HÁ PROJETOS CADASTRADOS!", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Activity_Home.this, "ERRO AO CARREGAR LISTA DE PROJETOS!", Toast.LENGTH_LONG).show();
            }
        });
    }
}