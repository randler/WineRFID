package br.edu.ifba.tcc.iot.winerfid.fachadaBD;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by randler on 16/10/16.
 */
public class FachadaBD extends SQLiteOpenHelper {

    private static final String NOME_BANCO = "bancowine.db";
    private static final String TABELA = "wine";
    private static final int VERSAO = 1;
    private static final String ID = "_id";
    private static final String TAG_ID = "tag_id";
    private static final String N_PALETE = "n_palete";
    private static final String PRODUTO = "produto";
    private static final String LOTE = "lote";
    private static final String DATA_CAD = "data_cad";
    private static final String DESTINO = "destino";

    public FachadaBD(Context context) {
        super(context, NOME_BANCO, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABELA + " (" +
                ID +        " integer primary key autoincrement," +
                TAG_ID +    " text UNIQUE," +
                N_PALETE +  " text) ";

        db.execSQL(sql);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS WINE");
        onCreate(db);
    }

    public static String getNomeBanco() {
        return NOME_BANCO;
    }

    public static String getTABELA() {
        return TABELA;
    }

    public static int getVERSAO() {
        return VERSAO;
    }

    public static String getID() {
        return ID;
    }

    public static String getTagId() {
        return TAG_ID;
    }

    public static String getnPalete() {
        return N_PALETE;
    }

    public static String getPRODUTO() {
        return PRODUTO;
    }

    public static String getLOTE() {
        return LOTE;
    }

    public static String getDataCad() {
        return DATA_CAD;
    }

    public static String getDESTINO() {
        return DESTINO;
    }
}
