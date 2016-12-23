package br.edu.ifba.tcc.iot.winerfid.activitys;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import br.edu.ifba.tcc.iot.winerfid.R;
import br.edu.ifba.tcc.iot.winerfid.adaptercustom.WineListAdapterCustom;
import br.edu.ifba.tcc.iot.winerfid.bean.WineBeanTemp;
import br.edu.ifba.tcc.iot.winerfid.fachadaBD.FachadaBD;
import br.edu.ifba.tcc.iot.winerfid.fachadaWEB.FachadaWeb;

/**
 * Created by randler on 24/10/16.
 */
public class Estoque extends Activity {

    private ListView listViewProducts;
    private CursorAdapter dataSource;
    private WineListAdapterCustom adapter;
    private FachadaBD fachadaBD;
    private FachadaWeb fachadaWeb;
    private SQLiteDatabase database;

    private  static final String campos[] = {"tag_id", "produto","n_palete", "lote", "destino", "_id"};

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.estoque);

        listViewProducts = (ListView) findViewById(R.id.list_view_products);

        fachadaBD = new FachadaBD(getApplicationContext());

        database = fachadaBD.getWritableDatabase();

        adapter = new WineListAdapterCustom(this, R.layout.list_products);
        listViewProducts.setAdapter(adapter);

        if(isConnected())
            initiListWEB();
        else
            initiListBD();


    }

    private void initiListWEB() {
        fachadaWeb = new FachadaWeb();
        ArrayList <WineBeanTemp> arrayWEB = new ArrayList<WineBeanTemp>();
        String resposta = "";


        try {
            resposta = fachadaWeb.execute(fachadaWeb.getLIST_WINE()).get();


            JSONObject json = new JSONObject(resposta);
            JSONArray jsonArray = json.getJSONArray("resp");

            for (int i = 0; i < jsonArray.length(); i++){
                WineBeanTemp wine = new WineBeanTemp();

                wine.setTagId(jsonArray.getJSONObject(i).getString("tag_id"));
                wine.setN_palete(jsonArray.getJSONObject(i).getString("n_palete"));

                arrayWEB.add(wine);
            }

           /* if (arrayWEB.size() > 0)
                adapter.addAll(arrayWEB);
            else
                Toast.makeText(getApplicationContext(), "Nenhum registro encontrado!", Toast.LENGTH_LONG).show();
*/
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void initiListBD() {
        Cursor dados = database.query("wine", campos, null, null, null, null, null);
        ArrayList<WineBeanTemp> arrayBD = new ArrayList<WineBeanTemp>();
        if(dados.getCount() > 0){
            while (dados.moveToNext()){
                WineBeanTemp w = new WineBeanTemp();
                w.setTagId     (dados.getString(0));
                w.setN_palete   (dados.getString(2));

                arrayBD.add(w);
            }
            //adapter.addAll(arrayBD);

        }else{
            Toast.makeText(getApplicationContext(), "Nenhum registro encontrado!", Toast.LENGTH_LONG).show();
        }
    }

    public boolean isConnected() {
        boolean connected = false;
        fachadaWeb = new FachadaWeb();
        try {
            String resposta = fachadaWeb.execute(fachadaWeb.getHOME()).get();
            if(resposta.equalsIgnoreCase("OK"))
                connected = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return connected;
    }
}
