
package com.nathan.meuscontatos;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class ContatoAdapter extends CursorAdapter {

    private final int[] coresFundo = {
            Color.parseColor("#DCEBFF"),
            Color.parseColor("#FCE7F3"),
            Color.parseColor("#DCFCE7"),
            Color.parseColor("#FEF3C7"),
            Color.parseColor("#EDE9FE"),
            Color.parseColor("#CFFAFE")
    };

    private final int[] coresTexto = {
            Color.parseColor("#1765C1"),
            Color.parseColor("#BE185D"),
            Color.parseColor("#15803D"),
            Color.parseColor("#B45309"),
            Color.parseColor("#6D28D9"),
            Color.parseColor("#0E7490")
    };

    public ContatoAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(
                R.layout.item_contato,
                parent,
                false
        );
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView txtIniciais = view.findViewById(R.id.txtIniciais);
        TextView txtNome = view.findViewById(R.id.txtNome);
        TextView txtTelefone = view.findViewById(R.id.txtTelefone);

        int id = cursor.getInt(
                cursor.getColumnIndexOrThrow("id")
        );

        String nome = cursor.getString(
                cursor.getColumnIndexOrThrow("nome")
        );

        String sobrenome = cursor.getString(
                cursor.getColumnIndexOrThrow("sobrenome")
        );

        String telefone = cursor.getString(
                cursor.getColumnIndexOrThrow("telefone")
        );

        txtNome.setText(nome + " " + sobrenome);
        txtTelefone.setText(telefone);

        // Gerar iniciais
        String iniciais = "";

        if (!nome.isEmpty()) {
            iniciais += nome.substring(0, 1).toUpperCase();
        }

        if (!sobrenome.isEmpty()) {
            iniciais += sobrenome.substring(0, 1).toUpperCase();
        }

        txtIniciais.setText(iniciais);

        // Escolher uma cor conforme o ID do contato
        int indiceCor = id % coresFundo.length;

        GradientDrawable fundo = new GradientDrawable();
        fundo.setShape(GradientDrawable.OVAL);
        fundo.setColor(coresFundo[indiceCor]);

        txtIniciais.setBackground(fundo);
        txtIniciais.setTextColor(coresTexto[indiceCor]);
    }
}
