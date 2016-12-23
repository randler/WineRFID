package br.edu.ifba.tcc.iot.winerfid.fachadaBD;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifba.tcc.iot.winerfid.bean.WineBean;

/**
 * Created by randler on 16/10/16.
 */
public class BancoController {

    private SQLiteDatabase bd;
    private FachadaBD banco;

    public BancoController(Context context){
        banco = new FachadaBD(context);
    }

    public boolean insereWine(WineBean wine){
        ContentValues valores;
        long resultado = -1;
        boolean saida;

        bd = banco.getWritableDatabase();

        for (int i = 0; i < wine.getTag_id().size(); i++) {
            valores = new ContentValues();

            valores.put(FachadaBD.getnPalete(), wine.getN_palete());
            valores.put(FachadaBD.getTagId(), wine.getTag_id().get(i));

            resultado = bd.insert(FachadaBD.getTABELA(), null, valores);
        }

        bd.close();

        if (resultado == -1)
            saida = false;
        else
            saida = true;

        return saida;
    }

    public List<WineBean> consultaBD(){
        List<WineBean> array = new ArrayList<>();
/*
        String sql = "SELECT * FROM "+ FachadaBD.getTABELA1();
        bd = banco.getWritableDatabase();
        Cursor cursor = bd.rawQuery(sql, null);

        if(cursor.moveToFirst())
            do{
                WineBean w = new WineBean();
                w.setN_palete   (cursor.getString(1));
                array.add(w);

            }while (cursor.moveToNext());
*/
        return array;
        }

}
