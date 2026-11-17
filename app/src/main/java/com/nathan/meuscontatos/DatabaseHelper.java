
package com.nathan.meuscontatos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String NOME_BANCO = "MeusContatos.db";
    private static final int VERSAO_BANCO = 2;
    private static final String TABELA_CONTATOS = "contatos";

    public DatabaseHelper(Context context) {
        super(context, NOME_BANCO, null, VERSAO_BANCO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABELA_CONTATOS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome TEXT NOT NULL, " +
                "sobrenome TEXT NOT NULL, " +
                "telefone TEXT NOT NULL)";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 2) {
            db.execSQL(
                    "CREATE TABLE contatos_nova (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nome TEXT NOT NULL, " +
                    "sobrenome TEXT NOT NULL, " +
                    "telefone TEXT NOT NULL)"
            );

            db.execSQL(
                    "INSERT INTO contatos_nova (id, nome, sobrenome, telefone) " +
                    "SELECT id, nome, '', telefone FROM contatos"
            );

            db.execSQL("DROP TABLE contatos");
            db.execSQL("ALTER TABLE contatos_nova RENAME TO contatos");
        }
    }

    // Adicionar contato
    public boolean adicionarContato(
            String nome, String sobrenome, String telefone) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put("nome", nome);
        valores.put("sobrenome", sobrenome);
        valores.put("telefone", telefone);

        long resultado = db.insert(TABELA_CONTATOS, null, valores);

        return resultado != -1;
    }

    // Listar e pesquisar contatos
    public Cursor listarContatos(String busca) {

        SQLiteDatabase db = this.getReadableDatabase();

        String consulta = "SELECT id AS _id, id, nome, sobrenome, telefone " +
                "FROM contatos " +
                "WHERE nome LIKE ? OR sobrenome LIKE ? OR telefone LIKE ? " +
                "ORDER BY sobrenome ASC, nome ASC";

        String filtro = "%" + busca + "%";

        return db.rawQuery(
                consulta,
                new String[]{filtro, filtro, filtro}
        );
    }

    // Buscar contato pelo ID
    public Cursor buscarContatoPorId(int id) {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM contatos WHERE id = ?",
                new String[]{String.valueOf(id)}
        );
    }

    // Atualizar contato
    public boolean atualizarContato(
            int id, String nome, String sobrenome, String telefone) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put("nome", nome);
        valores.put("sobrenome", sobrenome);
        valores.put("telefone", telefone);

        int resultado = db.update(
                TABELA_CONTATOS,
                valores,
                "id = ?",
                new String[]{String.valueOf(id)}
        );

        return resultado > 0;
    }

    // Excluir contato
    public boolean excluirContato(int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        int resultado = db.delete(
                TABELA_CONTATOS,
                "id = ?",
                new String[]{String.valueOf(id)}
        );

        return resultado > 0;
    }
}
