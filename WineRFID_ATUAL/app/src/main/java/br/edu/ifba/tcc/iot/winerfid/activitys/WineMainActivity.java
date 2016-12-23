package br.edu.ifba.tcc.iot.winerfid.activitys;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import br.edu.ifba.tcc.iot.winerfid.R;
import br.edu.ifba.tcc.iot.winerfid.bean.WineBean;
import br.edu.ifba.tcc.iot.winerfid.bean.WineBeanTemp;
import br.edu.ifba.tcc.iot.winerfid.fachadaBD.BancoController;
import br.edu.ifba.tcc.iot.winerfid.fachadaWEB.FachadaWeb;
import br.edu.ifba.tcc.iot.winerfid.libr900.R900Status;

public class WineMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    public static final int MODE_NOT_DETECTED = 0;
    public static final int MODE_BT_INTERFACE = 1;

    private static FachadaWeb webInstance;
    private BancoController bdInstance;
    private ImageButton mIBsync;
    private ImageButton mIBstart;
    private ImageButton mIBlistProducts;
    private ProgressDialog dialog;

    private int ENABLE_BLUETOOTH = 1;

    private static final int CONEXAO_OK = 1;
    private static final int SYNC_OK = 2;
    private static final int FAIL_SYNC = 3;
    private static final int SYNCING = 4;
    private static final int FAIL = -1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_activity_wine_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mIBstart = (ImageButton) findViewById(R.id.imageButtonStart);
        mIBstart.setOnClickListener(this);

        mIBsync = (ImageButton) findViewById(R.id.imageButtonSync);
        mIBsync.setOnClickListener(this);

        mIBlistProducts = (ImageButton) findViewById(R.id.imageButtonListProducts);
        mIBlistProducts.setOnClickListener(this);

        /* R900 */
        R900Status.setInterfaceMode(MODE_NOT_DETECTED);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(!drawer.isDrawerOpen(GravityCompat.START)){
            new AlertDialog.Builder(this)
                    .setTitle("SAIR")
                    .setMessage("Deseja Sair?")
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener()
                    {
                        public void onClick( DialogInterface dialog, int whichButton )
                        {
                            finish();
                        }
                    })
                    .setNegativeButton("Não", new DialogInterface.OnClickListener()
                    {
                        public void onClick( DialogInterface dialog, int whichButton )
                        {
                        }
                    }).create().show();
        }

    }

    @Override
    protected void onDestroy(){
    super.onDestroy();
//       bdInstance.dropTable();
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        String id = ""+item.getTitle();

        if (id.equalsIgnoreCase("RFID")) {
            iniciarWineRFID();

        } else if (id.equalsIgnoreCase("LISTAR PRODUTOS")) {
            iniciarListProduct();

        } else if (id.equalsIgnoreCase("CONFIGURAÇÃO")) {

        } else if(id.equalsIgnoreCase("SINCRONIZAR DADOS")){
            iniciarSync();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void iniciarListProduct() {
        Intent intent = new Intent(getBaseContext(), Estoque.class);
        startActivity(intent);
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.imageButtonSync:
               iniciarSync();
                break;
            case R.id.imageButtonStart:
                iniciarWineRFID();
                break;
            case R.id.imageButtonListProducts:
                iniciarListProduct();
                break;
        }



    }

    private void iniciarSync() {
        dialog = ProgressDialog.show(WineMainActivity.this, "Sincronizando","Aguarde...",false, true);
        dialog.setIcon(R.drawable.clousync);
        dialog.setCancelable(false);

        new Thread(){

            public void run(){
                try {
                    webInstance = new FachadaWeb();
                    String resposta = webInstance.execute(webInstance.getHOME()).get();

                    if (resposta.equalsIgnoreCase("OK")) {
                        webInstance = new FachadaWeb();
                        progressHandler.sendEmptyMessage(SYNCING);
                        resposta = webInstance.execute(webInstance.getLIST_WINE()).get();
                        sincDados(resposta);
                        Thread.sleep(2000);
                    }else{
                        progressHandler.sendEmptyMessage(FAIL);
                        Thread.sleep(2000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                dialog.dismiss();

            }
        }.start();
    }

    private void sincDados(String resposta) {

        try {
            bdInstance = new BancoController(getBaseContext());
            int cont = 0, contWeb = 0;

            List<WineBean> wBD = bdInstance.consultaBD();
            List<WineBeanTemp> wWEB = new ArrayList<WineBeanTemp>();

            JSONObject json = new JSONObject(resposta);
            JSONArray respJson = json.getJSONArray("resp"); // get articles array
            contWeb = respJson.length();

            for (int i = 0; i <contWeb; i++){
                WineBeanTemp wine = new WineBeanTemp();

                wine.setTagId(respJson.getJSONObject(i).getString("tag_id"));
                wine.setN_palete(respJson.getJSONObject(i).getString("n_palete"));
                //wine.setData_cad(respJson.getJSONObject(i).getString("data_cad"));

                wWEB.add(wine);

            }
            if(wWEB.size() < wBD.size()) {
                for (int i = 0; i < wBD.size(); i++) {
                    if (!wWEB.contains(wBD.get(i).getTag_id())) {
                        webInstance = new FachadaWeb();
                        webInstance.setWine(wBD.get(i));
                        contWeb = respJson.length();

                        try {
                            String respSinc = webInstance.execute(webInstance.getSAVE_WINE()).get();

                            if(respSinc.equalsIgnoreCase("Cadastrado com sucesso!"))
                                contWeb++;

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }/*else if (wWEB.size() > wBD.size()){
                for (int i = 0; i < wWEB.size();i++){
                    if(bdInstance.insereWine(wWEB.get(i)))
                        cont++;
                }

            }*/

            wBD = bdInstance.consultaBD();

            if(wBD.size() == contWeb)
                progressHandler.sendEmptyMessage(SYNC_OK);
            else
                progressHandler.sendEmptyMessage(FAIL_SYNC);

            Thread.sleep(1000);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void iniciarWineRFID() {
        R900Status.setInterfaceMode(MODE_BT_INTERFACE);
        Intent intent = new Intent(getBaseContext(), WineRFIDReadBluetoothActivity.class);
        startActivity(intent);
    }

    // handler for the background updating
    Handler progressHandler = new Handler() {
        public void handleMessage(Message msg) {;

            switch (msg.what) {
                case CONEXAO_OK:
                    dialog.setMessage("Conectado...");
                    break;
                case FAIL:
                    dialog.setIcon(R.drawable.clousync_fail);
                    dialog.setMessage("Falha ao conectar...");
                    break;
                case SYNCING:
                    dialog.setMessage("Sincronizando Dados!");
                    dialog.setIcon(R.drawable.clousync_progress);
                    break;
                case SYNC_OK:
                    dialog.setMessage("Sincronizado com sucesso!");
                    dialog.setIcon(R.drawable.clousync_ok);
                    break;
                case FAIL_SYNC:
                    dialog.setMessage("Falha ao Sincronizar!");
                    break;
            }


        }
    };
}
