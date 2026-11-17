package com.nathan.meuscontatos;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private EditText edtBusca;
    private ListView listaContatos;
    private Button btnAdicionar;
    private LinearLayout areaListaVazia;
    private TextView txtListaVazia;

    private DatabaseHelper banco;
    private ContatoAdapter adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtBusca = findViewById(R.id.edtBusca);
        listaContatos = findViewById(R.id.listaContatos);
        btnAdicionar = findViewById(R.id.btnAdicionar);
        areaListaVazia = findViewById(R.id.areaListaVazia);
        txtListaVazia = findViewById(R.id.txtListaVazia);

        banco = new DatabaseHelper(this);

        adaptador = new ContatoAdapter(this, null);
        listaContatos.setAdapter(adaptador);

        listaContatos.setOnItemClickListener((parent, view, position, id) -> {

            Cursor cursor = (Cursor) adaptador.getItem(position);

            int idContato = cursor.getInt(
                    cursor.getColumnIndexOrThrow("id")
            );

            mostrarDetalhesContato(idContato);
        });

        btnAdicionar.setOnClickListener(v -> abrirFormulario());

        edtBusca.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                carregarContatos(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        carregarContatos("");
    }

    // Criar campo personalizado
    private EditText criarCampo(String dica, String valor, int tipo) {

        float densidade = getResources().getDisplayMetrics().density;

        EditText campo = new EditText(this);

        campo.setHint(dica);
        campo.setText(valor);
        campo.setInputType(tipo);
        campo.setSingleLine(true);
        campo.setTextSize(15);
        campo.setTextColor(Color.parseColor("#172B4D"));
        campo.setHintTextColor(Color.parseColor("#718096"));

        campo.setPadding(
                (int) (16 * densidade),
                (int) (12 * densidade),
                (int) (16 * densidade),
                (int) (12 * densidade)
        );

        GradientDrawable fundo = new GradientDrawable();
        fundo.setColor(Color.WHITE);
        fundo.setCornerRadius(14 * densidade);
        fundo.setStroke(
                (int) (1 * densidade),
                Color.parseColor("#E2E8F0")
        );

        campo.setBackground(fundo);

        LinearLayout.LayoutParams parametros =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        (int) (54 * densidade)
                );

        parametros.bottomMargin = (int) (12 * densidade);
        campo.setLayoutParams(parametros);

        return campo;
    }

    // Criar título dos formulários
    private TextView criarTituloFormulario(String texto) {

        float densidade = getResources().getDisplayMetrics().density;

        TextView titulo = new TextView(this);

        titulo.setText(texto);
        titulo.setTextSize(14);
        titulo.setTextColor(Color.parseColor("#718096"));
        titulo.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams parametros =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        parametros.bottomMargin = (int) (14 * densidade);
        titulo.setLayoutParams(parametros);

        return titulo;
    }

    // Criar layout dos formulários
    private LinearLayout criarLayoutFormulario() {

        float densidade = getResources().getDisplayMetrics().density;

        LinearLayout layout = new LinearLayout(this);

        layout.setOrientation(LinearLayout.VERTICAL);

        layout.setPadding(
                (int) (24 * densidade),
                (int) (16 * densidade),
                (int) (24 * densidade),
                (int) (4 * densidade)
        );

        return layout;
    }

    // Adicionar contato
    private void abrirFormulario() {

        LinearLayout layout = criarLayoutFormulario();

        layout.addView(criarTituloFormulario(
                "Preencha os dados do novo contato"
        ));

        EditText edtNome = criarCampo("Nome", "", 1);
        EditText edtSobrenome = criarCampo("Sobrenome", "", 1);
        EditText edtTelefone = criarCampo("Telefone", "", 3);

        layout.addView(edtNome);
        layout.addView(edtSobrenome);
        layout.addView(edtTelefone);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Novo contato")
                .setView(layout)
                .setPositiveButton("Salvar", null)
                .setNegativeButton("Cancelar", null)
                .create();

        dialog.setOnShowListener(v -> {

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(botao -> {

                        String nome = edtNome.getText().toString().trim();
                        String sobrenome = edtSobrenome.getText().toString().trim();
                        String telefone = edtTelefone.getText().toString().trim();

                        if (nome.isEmpty() || sobrenome.isEmpty()
                                || telefone.isEmpty()) {

                            Toast.makeText(this,
                                    "Preencha todos os campos",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        boolean salvou = banco.adicionarContato(
                                nome, sobrenome, telefone
                        );

                        if (salvou) {

                            Toast.makeText(this,
                                    "Contato salvo com sucesso!",
                                    Toast.LENGTH_SHORT).show();

                            carregarContatos(edtBusca.getText().toString());
                            dialog.dismiss();

                        } else {

                            Toast.makeText(this,
                                    "Erro ao salvar contato",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        dialog.show();
    }

    // Mostrar detalhes
    private void mostrarDetalhesContato(int idContato) {

        Cursor cursor = banco.buscarContatoPorId(idContato);

        if (cursor == null) {
            Toast.makeText(this,
                    "Contato não encontrado",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (cursor.moveToFirst()) {

            String nome = cursor.getString(
                    cursor.getColumnIndexOrThrow("nome")
            );

            String sobrenome = cursor.getString(
                    cursor.getColumnIndexOrThrow("sobrenome")
            );

            String telefone = cursor.getString(
                    cursor.getColumnIndexOrThrow("telefone")
            );

            cursor.close();

            new AlertDialog.Builder(this)
                    .setTitle(nome + " " + sobrenome)
                    .setMessage("Telefone: " + telefone)
                    .setPositiveButton("Editar", (dialog, which) -> {
                        abrirFormularioEdicao(
                                idContato, nome, sobrenome, telefone
                        );
                    })
                    .setNegativeButton("Excluir", (dialog, which) -> {
                        confirmarExclusao(idContato);
                    })
                    .setNeutralButton("Fechar", null)
                    .show();

        } else {

            cursor.close();

            Toast.makeText(this,
                    "Contato não encontrado",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // Editar contato
    private void abrirFormularioEdicao(
            int id,
            String nomeAtual,
            String sobrenomeAtual,
            String telefoneAtual) {

        LinearLayout layout = criarLayoutFormulario();

        layout.addView(criarTituloFormulario(
                "Atualize as informações do contato"
        ));

        EditText edtNome = criarCampo("Nome", nomeAtual, 1);
        EditText edtSobrenome = criarCampo(
                "Sobrenome", sobrenomeAtual, 1
        );
        EditText edtTelefone = criarCampo(
                "Telefone", telefoneAtual, 3
        );

        layout.addView(edtNome);
        layout.addView(edtSobrenome);
        layout.addView(edtTelefone);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Editar contato")
                .setView(layout)
                .setPositiveButton("Salvar alterações", null)
                .setNegativeButton("Cancelar", null)
                .create();

        dialog.setOnShowListener(v -> {

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(botao -> {

                        String nome = edtNome.getText().toString().trim();
                        String sobrenome = edtSobrenome.getText().toString().trim();
                        String telefone = edtTelefone.getText().toString().trim();

                        if (nome.isEmpty() || sobrenome.isEmpty()
                                || telefone.isEmpty()) {

                            Toast.makeText(this,
                                    "Preencha todos os campos",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        boolean atualizado = banco.atualizarContato(
                                id, nome, sobrenome, telefone
                        );

                        if (atualizado) {

                            Toast.makeText(this,
                                    "Contato atualizado com sucesso!",
                                    Toast.LENGTH_SHORT).show();

                            carregarContatos(edtBusca.getText().toString());
                            dialog.dismiss();

                        } else {

                            Toast.makeText(this,
                                    "Erro ao atualizar contato",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        dialog.show();
    }

    // Confirmar exclusão
    private void confirmarExclusao(int idContato) {

        new AlertDialog.Builder(this)
                .setTitle("Excluir contato")
                .setMessage("Tem certeza de que deseja excluir este contato?")
                .setPositiveButton("Excluir", (dialog, which) -> {

                    boolean excluido = banco.excluirContato(idContato);

                    if (excluido) {

                        Toast.makeText(this,
                                "Contato excluído com sucesso!",
                                Toast.LENGTH_SHORT).show();

                        carregarContatos(edtBusca.getText().toString());

                    } else {

                        Toast.makeText(this,
                                "Erro ao excluir contato",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // Carregar contatos e controlar mensagem de lista vazia
    private void carregarContatos(String busca) {

        Cursor novoCursor = banco.listarContatos(busca);

        int quantidade = novoCursor.getCount();

        Cursor cursorAntigo = adaptador.swapCursor(novoCursor);

        if (cursorAntigo != null) {
            cursorAntigo.close();
        }

        if (quantidade == 0) {

            listaContatos.setVisibility(ListView.GONE);
            areaListaVazia.setVisibility(LinearLayout.VISIBLE);

            if (busca.trim().isEmpty()) {
                txtListaVazia.setText("Nenhum contato cadastrado");
            } else {
                txtListaVazia.setText("Nenhum contato encontrado");
            }

        } else {

            listaContatos.setVisibility(ListView.VISIBLE);
            areaListaVazia.setVisibility(LinearLayout.GONE);
        }
    }

    @Override
    protected void onDestroy() {

        if (adaptador != null) {

            Cursor cursor = adaptador.swapCursor(null);

            if (cursor != null) {
                cursor.close();
            }
        }

        if (banco != null) {
            banco.close();
        }

        super.onDestroy();
    }
}