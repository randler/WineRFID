package br.edu.ifba.tcc.iot.winerfid.activitys;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import br.edu.ifba.tcc.iot.winerfid.R;
import br.edu.ifba.tcc.iot.winerfid.adaptercustom.WineListAdapterCustom;
import br.edu.ifba.tcc.iot.winerfid.bean.WineBean;
import br.edu.ifba.tcc.iot.winerfid.fachadaBD.FachadaBD;
import br.edu.ifba.tcc.iot.winerfid.fachadaWEB.FachadaWeb;
import br.edu.ifba.tcc.iot.winerfid.libr900.BluetoothActivity;
import br.edu.ifba.tcc.iot.winerfid.libr900.OnBtEventListener;
import br.edu.ifba.tcc.iot.winerfid.libr900.R900Protocol;
import br.edu.ifba.tcc.iot.winerfid.libr900.R900RecvPacketParser;
import br.edu.ifba.tcc.iot.winerfid.libr900.R900Status;
import br.edu.ifba.tcc.iot.winerfid.rfid.LogfileMng;
import br.edu.ifba.tcc.iot.winerfid.rfid.MaskActivity;
import br.edu.ifba.tcc.iot.winerfid.rfid.SoundManager;


public class WineRFIDReadBluetoothActivity extends BluetoothActivity implements
        View.OnClickListener, OnBtEventListener,
        AdapterView.OnItemSelectedListener, TabHost.OnTabChangeListener,
        SeekBar.OnSeekBarChangeListener
{


    private ProgressDialog dialog;
    private TabHost mTab;
    private BarcodeDetector detector;
    public static SoundManager mSoundManager = new SoundManager();
    private String mStrAccessErrMsg;
    private boolean mAllive;
    private boolean mForceDisconnect;
    private BluetoothDevice mConnectedDevice;
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";

    Context context = this;
    private boolean mBattflag;
    private boolean mBattRun;

    private static FachadaWeb webInstance;
    private static FachadaBD bdInstance;
    private SQLiteDatabase database;

    private  static final String campos[]   = {"tag_id", "produto","n_palete", "lote", "destino", "_id"};
    private static final int ALERT_DIALOG1  = 1;
    private static final int ALERT_DIALOG2  = 2;


    // -------------------------------------------------------------------------- Conectar Bluetooth
    private Button mBtnScan;
    private Button mBtnDisconnect;
    private ListView mListDevice;
    private ArrayList<HashMap<String, String>> mArrBtDevice;
    private BaseAdapter mAdapterDevice;

    // ------------------------------------------------------------------------------- Cadastrar TAG
    private Button mBtnReadTagCad;
    private Button mBtnGravarTagCad;
    private Button mBtnGravarOutraTagCad;
    private EditText mEdtTagCad;
    private Spinner mSpinnerProdutoTagCad;
    private ProgressBar mProgressCad;
    private TextView mBattPowerCad;
    private EditText mEdtTagGravada;
    private TextView mEdtTotalTagGravada;


    private boolean BOTAO_LER_TAG_CAD                   = false;
    private boolean BOTAO_GRAVAR_TAG_CAD                = false;
    private boolean BOTAO_GRAVAR_OUTRA_TAG_CAD          = false;
    private boolean BOTAO_LER_PALLET_CAD                = false;
    private boolean BOTAO_ADICIONAR_CAIXA_PALLET        = false;
    private boolean BOTAO_REMOVER_ULTIMA_CAIXA_PALLET   = false;
    private boolean BOTAO_SALVAR_PALLET                 = false;

    // ---------------------------------------------------------------------------- Cadastrar Pallet
    private Button mBtnLerPallet;
    private EditText mEdtTagPalletLido;
    private Button mBtnAdicionarCaixaPallet;
    private ListView mListCaixas;
    private TextView mTxtTotalcaixasPallet;
    private Button mBtnRemoverUltimaCaixa;
    private Button mBtnSalvarPallet;


    protected ListView mListTagPallet;
    private ArrayList<HashMap<String, String>> mArrTagPallet;
    private BaseAdapter mAdapterTagPallet;

    // ---------------------------------------------------------------------------------- Inventorio
    private Button mBtnInventory;
    private TextView mTxtTagCount;
    protected ListView mListTag;
    private ArrayList<HashMap<String, String>> mArrTag;
    private BaseAdapter mAdapterTag;

    // -------------------------------------------------------------------------------- Procurar Tag

    private ArrayList<WineBean> wineWebArray    = new ArrayList<WineBean>();
    private List<String> produtosList           = new ArrayList<String>();
    private List<String> produtosListCode       = new ArrayList<String>();

    private Button mBtnProcurar;
    private EditText mEdtTagProcurar;

    private ListView mListTagProcurar;
    private WineListAdapterCustom adapter;

    private TextView mTxtPotenciaProcurar;
    private TextView mTxtRaioProcurar;
    private SeekBar mSeekPotenciaProcurar;
    private SeekBar mSeekDutyProcurar;

    private RadioButton mRBTag;
    private RadioButton mRBLote;
    private RadioButton mRBDestino;

    private ImageView mImLampPrimeiroProcurar;
    private ImageView mImLampSegundoProcurar;
    private TextView mTxtImagemProcurar;


    // -------------------------------------------------------------------------------- Configuração
    private CheckBox mChkAutoLink;
    private CheckBox mChkDetectSound;
    private CheckBox mChkSkipSame;
    private CheckBox mChkSingleTag;
    private CheckBox mChkContinuous;
    private Spinner mSpinQuerySession;
    private Spinner mSpinTargetAB;
    private Spinner mSpinQueryQ;
    private EditText mEdtTimeout;
    private TextView mTxtPower;
    private TextView mTxtDuty;
    private SeekBar mSeekPower;
    private SeekBar mSeekDuty;
    private ProgressBar mProgress;
    private TextView mBattPower;
    private TextView mTxtBTmac;
    private TextView mTxtFWver;



    private static final String[] TXT_POWER =
            { "Max", "-1dB", "-2dB", "-3dB", "-4dB", "-5dB", "-6dB", "-7dB", "-8dB", "-9dB" };
    private static final int[] TX_POWER =
            { 0, -1, -2, -3, -4, -5, -6, -7, -8, -9 };

    private static final String[] TXT_DUTY =
            {"Max", "90%", "80%", "60%", "40%", "20%" };

    private static final int[] TX_DUTY_OFF =
            { 10, 40, 80, 100, 160, 180 };

    private static final int[] TX_DUTY_ON =
            { 190, 160, 70, 40, 20, 10 };


    private InputFilter mHexFilter = new InputFilter()
    {
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend )
        {
            for( int i = start; i < end; i++ )
            {
                final char CHAR = source.charAt(i);
                if( !Character.isDigit(CHAR)
                        && !( CHAR == 'a' || CHAR == 'A' || CHAR == 'b'
                        || CHAR == 'B' || CHAR == 'c' || CHAR == 'C'
                        || CHAR == 'd' || CHAR == 'D' || CHAR == 'e'
                        || CHAR == 'E' || CHAR == 'f' || CHAR == 'F' ) )
                {
                    return "";
                }
            }
            return null;
        }
    };

    private BroadcastReceiver mPowerOffReceiver = new BroadcastReceiver()
    {
        public void onReceive( Context context, Intent intent )
        {
            if( mConnected )
            {
                sendCmdStop();
                byeBluetoothDevice();
            }
        }
    };

    // ---
    public static final int MSG_QUIT                    = 9999;
    public static final int MSG_AUTO_REFRESH_DEVICE     = 1;
    public static final int MSG_ENABLE_LINK_CTRL        = 10;
    public static final int MSG_DISABLE_LINK_CTRL       = 11;
    public static final int MSG_ENABLE_DISCONNECT       = 12;
    public static final int MSG_DISABLE_DISCONNECT      = 13;
    public static final int MSG_SHOW_TOAST              = 20;
    public static final int MSG_REFRESH_LIST_DEVICE     = 21;
    public static final int MSG_REFRESH_LIST_TAG        = 22;
    public static final int MSG_LINK_ON                 = 30;
    public static final int MSG_LINK_OFF                = 31;
    public static final int MSG_SOUND_RX                = 40;
    public static final int MSG_SOUND_RX_HALF           = 41;
    public static final int MSG_AUTO_LINK               = 100;
    public static final int MSG_SEARCHED_TAG            = 110;
    public static final int MSG_NOT_SEARCHED_TAG        = 111;
    public static final int MSG_SEARCHING_TAG           = 112;
    public static final int MSG_SEARCH_STOP             = 113;

    public static boolean search = true;
    // ---
    private int mTabMode = TAB_LINK;
    public static final int TAB_LINK        = 0;
    public static final int TAB_INVENTORY   = 1;
    public static final int TAB_PROCURAR    = 2;
    public static final int TAB_CONFIG      = 3;
    public static final int TAB_CADASTRAR   = 4;
    public static final int TAB_PALLET      = 5;

    private int mAccessType = ACC_TYPE_READ;
    public static final int ACC_TYPE_READ   = 0;
    public static final int ACC_TYPE_WRITE  = 1;
    public static final int ACC_TYPE_LOCK   = 2;
    public static final int ACC_TYPE_KILL   = 3;

    // --- Battery Message
    public static final int MSG_BATTERY_CTRL = 50;

    private String mSelTag;
    public static boolean mExit = false;

    public static final int INTENT_MASK = 1;

    // --- Interface Mode
    public static final int MODE_NOT_DETECTED   = 0;
    public static final int MODE_BT_INTERFACE   = 1;
    public static final int MODE_USB_INTERFACE  = 2;

    //Android 6.0 permission
    private static final int REQUEST_COARSE_LOCATION_PERMISSIONS = 1;


    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage( final Message msg )
        {
            switch( msg.what )
            {
                case MSG_QUIT:
                    closeApp();
                    break;
                case MSG_AUTO_REFRESH_DEVICE:

                    break;
                case MSG_ENABLE_LINK_CTRL:
                    mListDevice.setEnabled(true);
                    mBtnScan.setEnabled(true);
                    break;
                case MSG_DISABLE_LINK_CTRL:
                    mListDevice.setEnabled(false);
                    mBtnScan.setEnabled(false);
                    break;
                case MSG_ENABLE_DISCONNECT:
                {
                    mBtnDisconnect.setEnabled(true);
                    mBtnInventory.setText("Iniciar Leitura");

                    break;
                }
                case MSG_DISABLE_DISCONNECT:
                    mBtnDisconnect.setEnabled(false);
                    break;
                case MSG_SHOW_TOAST:
                    Toast.makeText(WineRFIDReadBluetoothActivity.this, (String) msg.obj,
                            Toast.LENGTH_LONG).show();
                    break;
                case MSG_REFRESH_LIST_DEVICE:
                    mAdapterDevice.notifyDataSetChanged();
                    break;
                case MSG_REFRESH_LIST_TAG:
                {
                    mTxtTagCount.setText(String.valueOf(mArrTag.size()));
                    mAdapterTag.notifyDataSetChanged();
                    break;
                }
                case MSG_LINK_ON:
                {
                    mBtnInventory.setEnabled(true);
                    mBtnReadTagCad.setEnabled(true);
                    mBtnGravarTagCad.setEnabled(true);
                    mBtnGravarOutraTagCad.setEnabled(true);
                    mSpinnerProdutoTagCad.setEnabled(true);
                    mBtnProcurar.setEnabled(true);
                    break;
                }
                case MSG_LINK_OFF:
                {
                    mBtnInventory.setEnabled(false);
                    mBtnReadTagCad.setEnabled(false);
                    mBtnGravarTagCad.setEnabled(false);
                    mBtnGravarOutraTagCad.setEnabled(false);
                    mSpinnerProdutoTagCad.setEnabled(false);
                    mBtnProcurar.setEnabled(false);
                    break;
                }
                case MSG_SOUND_RX:
                {
                    if( WineRFIDReadBluetoothActivity.this.isDetectSoundOn() )
                        mSoundManager.playSound(0);
                    break;
                }
                case MSG_SOUND_RX_HALF:
                {
                    if( WineRFIDReadBluetoothActivity.this.isDetectSoundOn() )
                        mSoundManager.playSound(0, 0.5f);
                    break;
                }

                case MSG_BATTERY_CTRL:
                {
                    final String str = Integer.toString(R900Status.getBatteryLevel()) + "%";
                    mBattPower.setText(str);
                    mBattPowerCad.setText(str);

                    mProgressCad.setProgress(R900Status.getBatteryLevel());
                    mProgress.setProgress(R900Status.getBatteryLevel());
                    break;
                }

                case MSG_AUTO_LINK:
                {
                    if( mAllive == true && mForceDisconnect == false )
                    {
                        final String strAutoLinkDevice = getAutoConnectDevice();
                        if( mConnected == false
                                && strAutoLinkDevice != null
                                && strAutoLinkDevice.length() > 0
                                && WineRFIDReadBluetoothActivity.this.mChkAutoLink
                                .isChecked() )
                        {
                            try
                            {
                                if( mR900Manager.isTryingConnect() == false )
                                    mR900Manager.connectToBluetoothDevice(
                                            strAutoLinkDevice, MY_UUID);
                            }
                            catch( Exception ex )
                            {
                                ex.printStackTrace();
                            }
                        }
                    }

                    if( mExit == false )
                        sendEmptyMessageDelayed(MSG_AUTO_LINK, 5000);
                    break;
                }

                case MSG_SEARCHED_TAG:
                {
                    mTxtImagemProcurar.setText("ENCONTRADO");
                    mImLampSegundoProcurar.setImageResource(R.drawable.lamp_green);
                    break;
                }
                case MSG_NOT_SEARCHED_TAG:
                {
                    mTxtImagemProcurar.setText("PROCURANDO");
                    mImLampSegundoProcurar.setImageResource(R.drawable.lamp_red);
                    mImLampSegundoProcurar.setImageResource(R.drawable.lamp_gray);

                    break;
                }

                case MSG_SEARCH_STOP:
                {
                    mImLampSegundoProcurar.setImageResource(R.drawable.lamp_gray);
                    mImLampPrimeiroProcurar.setImageResource(R.drawable.lamp_red);


                    break;
                }
            }
        }
    };

    @Override
    public void onDestroy()
    {
        finalize();
        super.onDestroy();
    }

    @Override
    public void onStart()
    {
        //InWeb();
        //InBD();
        super.onStart();
        mExit = false;
        mAllive = true;
        mHandler.removeMessages( MSG_AUTO_LINK );
        mHandler.sendEmptyMessageDelayed(MSG_AUTO_LINK, 1000);
        mHandler.sendEmptyMessage(MSG_SEARCH_STOP);
    }



    @Override
    public void onResume()
    {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction( "android.intent.action.ACTION_SHUTDOWN" );
        registerReceiver( mPowerOffReceiver, filter );
    }

    @Override
    public void onPause()
    {
        super.onPause();
        unregisterReceiver( mPowerOffReceiver );
        if( mConnected )
            sendCmdStop();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        mAllive = false;
        mHandler.removeMessages(MSG_AUTO_LINK);

        int mode = R900Status.getInterfaceMode();
        if(mode == 1)
        {
            if( mConnected )
                sendCmdStop();
        }


    }

    @Override
    public void onRestart()
    {
        super.onRestart();
    }

    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "RFIDHostActivity.java......onCreate()~!~!~!~!~!~!~!~!~!");
        bdInstance = new FachadaBD(getApplicationContext());

        // -- ui
        mTab = getTabHost();
        LayoutInflater inflater = LayoutInflater.from(this);
        inflater.inflate(R.layout.rfid_host, mTab.getTabContentView(), true);

        mTab.addTab(mTab.newTabSpec("link").setIndicator("CON BTH")
                .setContent(R.id.opt_link));
        mTab.addTab(mTab.newTabSpec("inventory").setIndicator("LER TAG")
                .setContent(R.id.opt_inventory));
        mTab.addTab(mTab.newTabSpec("cad").setIndicator("CAD TAG")
                .setContent(R.id.opt_cadastrar));
        mTab.addTab(mTab.newTabSpec("pallet").setIndicator("CAD PALL")
                .setContent(R.id.opt_cadastrar_palete));
        mTab.addTab(mTab.newTabSpec("access").setIndicator("PROC TAG")
                .setContent(R.id.opt_procurar));
        mTab.addTab(mTab.newTabSpec("config").setIndicator("CONF")
                .setContent(R.id.opt_config));


        mTab.setOnTabChangedListener(this);

        //inicialização de dados necessários
        initBluetoothDeviceList(R.id.list_btdevice);
        initTagList(R.id.list_tag);
        initTagListPallet(R.id.listViewPaleteCad);
        initArrayProducts();


        // ---------------------------------------------------------------------- Conectar Bluetooth
        mBtnScan        = (Button) findViewById(R.id.btn_scan);
        mBtnDisconnect  = (Button) findViewById(R.id.btn_disconnect);

        mBtnScan.setOnClickListener(this);
        mBtnDisconnect.setOnClickListener(this);
        mBtnDisconnect.setEnabled(false);

        // ------------------------------------------------------------------------------- Iventorio
        mBtnInventory   = (Button) findViewById(R.id.btn_inventory);
        mTxtTagCount    = (TextView) findViewById(R.id.txt_tagcountInventory);

        mTxtTagCount.setOnClickListener(this);
        mBtnInventory.setOnClickListener(this);


        // --------------------------------------------------------------------------- Cadastrar Tag
        mEdtTagCad              = (EditText) findViewById(R.id.editTextCadTag);

        mBtnReadTagCad          = (Button) findViewById(R.id.buttonReadCadTag);
        mBtnReadTagCad.setOnClickListener(this);
        mBtnGravarTagCad        = (Button) findViewById(R.id.buttonGravarCadTag);
        mBtnGravarTagCad.setOnClickListener(this);
        mBtnGravarOutraTagCad   = (Button) findViewById(R.id.buttonGravarOutraCadTag);
        mBtnGravarOutraTagCad.setOnClickListener(this);


        mEdtTagGravada          = (EditText) findViewById(R.id.editTextTagGravadaFinal);
        mEdtTotalTagGravada     = (TextView) findViewById(R.id.textView1TotalTagGravada);

        mSpinnerProdutoTagCad   = (Spinner) findViewById(R.id.spinnerCadTag);
        mSpinnerProdutoTagCad.setOnItemSelectedListener(this);


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, produtosList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        mSpinnerProdutoTagCad.setAdapter(dataAdapter);

        // ------------------------------------------------------------------------ Cadastrar Pallet

        mBtnLerPallet               = (Button) findViewById(R.id.buttonLerPaleteCad) ;
        mEdtTagPalletLido           = (EditText) findViewById(R.id.editTextLerPaleteCad) ;
        mBtnAdicionarCaixaPallet    = (Button) findViewById(R.id.buttonAdicionarCaixaPaleteCad);
        mTxtTotalcaixasPallet       = (TextView) findViewById(R.id.textViewTotalPaleteCad) ;
        mBtnRemoverUltimaCaixa      = (Button) findViewById(R.id.buttonRemoveCaixaPaleteTag);
        mBtnSalvarPallet            = (Button) findViewById(R.id.buttonSalvarPaleteCad);

        mBtnLerPallet.setOnClickListener(this);
        mBtnAdicionarCaixaPallet.setOnClickListener(this);
        mBtnRemoverUltimaCaixa.setOnClickListener(this);
        mBtnSalvarPallet.setOnClickListener(this);


        // ---------------------------------------------------------------------------- Procurar Tag

        mBtnProcurar            = (Button) findViewById(R.id.btn_procurar);
        mEdtTagProcurar         = (EditText) findViewById(R.id.editTextProcurar);

        mListTagProcurar        = (ListView) findViewById(R.id.list_tagProcurar);
        adapter = new WineListAdapterCustom(getApplicationContext(), R.layout.list_products);
        mListTagProcurar.setAdapter(adapter);

        mTxtPotenciaProcurar    = (TextView) findViewById(R.id.txt_potenciaProcurar) ;
        mTxtRaioProcurar        = (TextView) findViewById(R.id.txt_raioProcurar);
        mSeekPotenciaProcurar   = (SeekBar) findViewById(R.id.seek_potenciaProcurar) ;
        mSeekDutyProcurar       = (SeekBar) findViewById(R.id.seek_raioProcurar) ;

        mSeekPotenciaProcurar.setMax(9);
        mSeekDutyProcurar.setMax(5);
        mSeekPotenciaProcurar.setOnSeekBarChangeListener(this);
        mSeekDutyProcurar.setOnSeekBarChangeListener(this);


        mRBTag                  = (RadioButton) findViewById(R.id.rbTag) ;
        mRBLote                 = (RadioButton) findViewById(R.id.rbLote);
        mRBDestino              = (RadioButton) findViewById(R.id.rbDestino);

        mImLampPrimeiroProcurar = (ImageView) findViewById(R.id.imageViewPrimeiro) ;
        mImLampSegundoProcurar  = (ImageView) findViewById(R.id.imageViewSegundo);
        mTxtImagemProcurar      = (TextView) findViewById(R.id.textViewImagemProcurar);

        mBtnProcurar.setOnClickListener(this);



        // ------------------------------------------------------------------------------ Configurar
        mChkAutoLink    = (CheckBox) findViewById(R.id.chk_auto_link);
        mChkDetectSound = (CheckBox) findViewById(R.id.chk_detect_sound);
        mChkSkipSame    = (CheckBox) findViewById(R.id.chk_skip_same);
        mChkSingleTag   = (CheckBox) findViewById(R.id.chk_single_tag);
        mChkContinuous  = (CheckBox) findViewById(R.id.chk_continuous);

        mChkSingleTag.setOnClickListener(this);

        mSpinQuerySession   = (Spinner) findViewById(R.id.spin_query_session);
        mSpinTargetAB       = (Spinner) findViewById(R.id.spin_target_ab);
        mSpinQueryQ         = (Spinner) findViewById(R.id.spin_query_q);
        mEdtTimeout         = (EditText) findViewById(R.id.edt_query_timeout);
        mTxtPower           = (TextView) findViewById(R.id.txt_power);
        mSeekPower          = (SeekBar) findViewById(R.id.seek_power);

        mSeekPower.setMax(9);
        mSeekPower.setOnSeekBarChangeListener(this);

		/* Add Tx Duty UI control */
        mTxtDuty    = (TextView) findViewById(R.id.txt_duty);
        mSeekDuty   = (SeekBar) findViewById(R.id.seek_duty);

        mSeekDuty.setMax(4);
        mSeekDuty.setOnSeekBarChangeListener(this);

		/* Add Batter Gauge Monitor*/
        mBattPower  = (TextView) findViewById(R.id.batt_power);
        mProgress   = (ProgressBar) findViewById(R.id.batt_progress);

        mProgress.setMax(100);
        mProgress.setProgress(0);


        mTxtBTmac   = (TextView) findViewById(R.id.edt_btmac);
        mTxtFWver   = (TextView) findViewById(R.id.edt_mac);

        // --
        setOnBtEventListener(this);

        mHandler.removeMessages(MSG_AUTO_REFRESH_DEVICE);
        mHandler.sendEmptyMessageDelayed(MSG_AUTO_REFRESH_DEVICE, 3000);

        // ---
        mSoundManager.initSounds(this);
        mSoundManager.addSound(0, R.raw.success);

        //setBitsAndOffset("01", "7", false);
        //resetMemData(true, false);
       // mEdtPassword.setText("00000000");

        mChkAutoLink.setChecked(true);
        mChkSkipSame.setChecked(true);
        mChkDetectSound.setChecked(true);
        mChkContinuous.setChecked(true);

        // mSpinQuerySession.setSelection(0);
        mSpinQuerySession.setSelection(1);
        // mSpinTargetAB.setSelection(2);
        mSpinTargetAB.setSelection(0);
        mSpinQueryQ.setSelection(5);
        mEdtTimeout.setText("0");

        mSingleTag = mChkSingleTag.isChecked();
        mForceDisconnect = false;
        // setLinkStatus( false );
        setConnectionStatus(false);
    }

    private void initArrayProducts(){
        //produtos cadastrados
        produtosList.add("01-ADEGA DO VALE SECO");
        produtosList.add("02-ADEGA DO VALE SUAVE");

        produtosList.add("03-RIO SOL ASSEMBLAGE");
        produtosList.add("04-RIO SOL BRANCO");
        produtosList.add("05-RIO SOL BRUT BRANCO PREMIUM");
        produtosList.add("06-RIO SOL CABERNET SAUVIGNON");
        produtosList.add("07-RIO SOL ESPUMANTE BRUT BRANCO");
        produtosList.add("08-RIO SOL ESPUMANTE BRUT ROSÉ");
        produtosList.add("09-RIO SOL ESPUMANTE DEMI-SEC");
        produtosList.add("10-RIO SOL ESPUMANTE MOSCATEL");
        produtosList.add("11-RIO SOL GRAN RESERVA ALICANTE BOUSCHET");
        produtosList.add("12-RIO SOL GRAN RESERVA TOURIGA NACIONAL");
        produtosList.add("13-RIO SOL RESERVA");
        produtosList.add("14-RIO SOL SYRAH");
        produtosList.add("15-RIO SOL TEMPRANILLO");

        produtosList.add("16-RENDEIRAS SYRAH MEIO SECO");

        produtosList.add("17-VINHA MARIA RESERVA SELECIONADA");
        produtosList.add("18-VINHA MARIA NATURE");

        //codigo dos produtos acima
        produtosListCode.add("01");
        produtosListCode.add("02");

        produtosListCode.add("03");
        produtosListCode.add("04");
        produtosListCode.add("05");
        produtosListCode.add("06");
        produtosListCode.add("07");
        produtosListCode.add("08");
        produtosListCode.add("09");
        produtosListCode.add("10");
        produtosListCode.add("11");
        produtosListCode.add("12");
        produtosListCode.add("13");
        produtosListCode.add("14");
        produtosListCode.add("15");

        produtosListCode.add("16");

        produtosListCode.add("17");
        produtosListCode.add("18");

    }

    private String generateUUID(){
        String saida ="";
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String id = ""+timestamp.getTime();
        saida = id.substring(8);
        return saida;
    }


    public String getEndMsg( String strEnd )
    {
        if( strEnd == null )
            return null;

        if( strEnd.equals("-1") || strEnd.equals("0") )
            return null;// return "Stopped";
        else
            return "Operation Terminated by " + strEnd;
    }

    public String getTagErrorMsg( String strTagError )
    {
        if( strTagError.equals("0") )
            return "Tag Error : General Error";
        else if( strTagError.equals("3") )
            return "Tag Error : Memory Overrun";
        else if( strTagError.equals("4") )
            return "Tag Error : Memory Locked";
        else if( strTagError.equals("11") )
            return "Tag Error : Insufficient Power";
        else if( strTagError.endsWith("15") )
            return "Tag Error : Not Supported";
        else
            return "Tag Error : Unknown Error : " + strTagError;
    }

    public boolean isDetectSoundOn()
    {
        return mChkDetectSound.isChecked();
    }


    public void onTabChanged( String str )
    {
        sendCmdStop();

        mBtnInventory.setText("Iniciar Leitura");

        if( str.equalsIgnoreCase("link") ) {
            mTabMode = TAB_LINK;
            // finish the battery Gauge Monitoring..
            mBattRun = false;

        }else if( str.equalsIgnoreCase("inventory") ){
            mTabMode = TAB_INVENTORY;

        }else if(str.equals("cad")){
            mTabMode = TAB_CADASTRAR;

        }else if(str.equalsIgnoreCase("pallet")){
            mTabMode = TAB_PALLET;
        }else if( str.equalsIgnoreCase("access") ) {
            mTabMode = TAB_PROCURAR;

        }else if( str.equalsIgnoreCase("config") ) {
            mTabMode = TAB_CONFIG;

            // start the battery Gauge Monitoring..
            mBattRun = true;
            onBattGaugingStart();

            // get the bt mac address
            sendGettingBTmacAddress();

            // get the firmware version
            sendGetVersion();

        }
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode,
                                     Intent data )
    {
        switch( requestCode )
        {
            case INTENT_MASK:
            {
                //mRdoTag.setChecked(MaskActivity.Type == 0);
                break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected boolean initBluetoothDeviceList( int id )
    {
        mListDevice = (ListView) findViewById(id);
        if( mListDevice != null )
        {
            mArrBtDevice = new ArrayList<HashMap<String, String>>();
            mAdapterDevice = new SimpleAdapter(this, mArrBtDevice,
                    android.R.layout.simple_list_item_2, new String[]
                    { "name", "summary", "address", "status" }, new int[]
                    { android.R.id.text1, android.R.id.text2 });
            mListDevice.setAdapter(mAdapterDevice);
            mListDevice.setOnItemClickListener(mDeviceClickListener);
        }
        return mListDevice != null;
    }

    protected boolean initTagListPallet( int id )
    {
        mListTagPallet = (ListView) findViewById(id);
        if( mListTagPallet != null )
        {
            mArrTagPallet = new ArrayList<HashMap<String, String>>();
            mAdapterTagPallet = new SimpleAdapter(this, mArrTagPallet,
                    R.layout.list_item_inventory, new String[]
                    { "tag" }, new int[]
                    { android.R.id.text1 });
            mListTag.setAdapter(mAdapterTagPallet);

        }
        return mListTag != null;
    }
    protected boolean initTagList( int id )
    {
        mListTag = (ListView) findViewById(id);
        if( mListTag != null )
        {
            mArrTag = new ArrayList<HashMap<String, String>>();
            mAdapterTag = new SimpleAdapter(this, mArrTag,
                    R.layout.list_item_inventory, new String[]
                    { "tag", "summary", "count", "first", "last" }, new int[]
                    { android.R.id.text1 });
            mListTag.setAdapter(mAdapterTag);

            // -- Context Menu
            registerForContextMenu(mListTag);
        }
        return mListTag != null;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo )
    {
        if( v.getId() == R.id.list_tag )
        {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle("Menu");
            HashMap<String, String> hashMap = (HashMap<String, String>) mListTag
                    .getItemAtPosition(info.position);

            setSelTag(hashMap.get("tag"));
            String[] menuItems = getResources().getStringArray(
                    R.array.list_menu_inventory);
            for( int i = 0; i < menuItems.length; i++ )
                menu.add(Menu.NONE, i, i, menuItems[ i ]);
        }
    }

    @Override
    public boolean onContextItemSelected( MenuItem item )
    {
        Log.d(TAG, "onContenxtItemSelected : " + item.getItemId());
        switch( item.getItemId() )
        {
            case 0:
            {
                //startMaskActivity();
                break;
            }
            case 1:
            {
                clearArrTag();
                break;
            }
            case 2:
                break;
        }
        return true;
    }

    private void setBitsAndOffset( String Offset, String Wordcount,
                                   boolean bEnabled )
    {
        /*mEdtTagMemOffset.setText(Offset);
        mEdtTagMemWordcount.setText(Wordcount);

        mEdtTagMemOffset.setEnabled(bEnabled);
        mEdtTagMemWordcount.setEnabled(bEnabled);*/
    }

    public void onItemSelected( AdapterView<?> parent, View view, int position,
                                long id )
    {
    }

    public void onNothingSelected( AdapterView<?> parent )
    {
    }

    private void startMaskActivity()
    {
        // if( mSelTag != null )
        {
            Log.d(TAG, "onContextItemSelected : selTag = " + mSelTag);
            // sendCmdSelectMask( 0, 96, 1, 16, mSelTag, 4, 1 );
            Intent intent = new Intent(this, MaskActivity.class);
            intent.putExtra("tag", mSelTag);
            startActivityForResult(intent, INTENT_MASK);
        }
    }

    private void clearArrTag()
    {
        if( mArrTag != null )
            mArrTag.clear();
        setSelTag(null);
        mHandler.sendEmptyMessage(MSG_REFRESH_LIST_TAG);
        // if( mAdapterTag != null )
        // mAdapterTag.notifyDataSetChanged();
    }

    public void cleanBtList(){
        mArrBtDevice.clear();
        mAdapterDevice.notifyDataSetChanged();
    }

    public void onClick( View v )
    {
        switch( v.getId() ) {
            // --- Link
            case R.id.btn_scan:
                BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
                if (!btAdapter.isEnabled()) {
                    cleanBtList();
                    onBtOff();
                } else {
                    int mode = R900Status.getInterfaceMode();
                    resetBtDeviceList();
                    scanBluetoothDevice();
                }
                break;
            case R.id.btn_disconnect:
                // setEnabledBtnDisconnect(false);
                // setListStatus(null, "");
                mForceDisconnect = true;
                byeBluetoothDevice();
                setConnectionStatus(false);
                break;

            // --- Inventory
            case R.id.btn_inventory: {
                final String LABEL = ((Button) v).getText().toString().toUpperCase();
                if (LABEL.equalsIgnoreCase("INICIAR LEITURA")) {

                    configurarLeituraContinua();
                    ((Button) v).setText("Parar Leitura");
                    sendCmdInventory();

                } else {
                    ((Button) v).setText("Iniciar Leitura");
                    sendCmdStop();
                }
                break;

            }case R.id.buttonReadCadTag:{
                    setarButton("ReadCadTag");
                    setOperation();

                break;

            }case R.id.buttonGravarCadTag:{
                    setarButton("GravarCadTag");
                    setOperation();


                break;

            }
/*
            case R.id.btn_lerTagCad:
            {

                final String LABEL = ( (Button) v ).getText().toString().toUpperCase();
                if( LABEL.equalsIgnoreCase("LER") ) {
                    setupOperationParameter();
                    sendCmdInventory();

                }else if (LABEL.equalsIgnoreCase("PARAR")){
                    ( (Button) v ).setText("LER");
                    sendCmdStop();
                }


                break;

            }

            case R.id.btn_lerTagCadPalete: {
                final String LABEL = ((Button) v).getText().toString().toUpperCase();
                if (LABEL.equalsIgnoreCase("LER")) {
                    showDialog(ALERT_DIALOG2);

                } else if (LABEL.equalsIgnoreCase("PARAR")) {
                    ((Button) v).setText("LER");
                    sendCmdStop();
                }

                break;
            }*/
            // --- procurar
            case R.id.btn_procurar:
            {
                String tagEdit = mEdtTagProcurar.getText().toString();
                if(tagEdit.equals("")){
                    Toast.makeText(getApplicationContext(),"Preencha o campo!", Toast.LENGTH_LONG).show();
                }else {
                    final String LABEL = ((Button) v).getText().toString();
                    if (LABEL.equalsIgnoreCase("procurar")) {
                        ((Button) v).setText("PARAR");
                        searchCMD();
                    } else {
                        ((Button) v).setText("PROCURAR");
                        sendCmdStop();
                    }
                }

                break;
            }
            // --- Config
            case R.id.chk_single_tag:
                mSingleTag = mChkSingleTag.isChecked();
                break;

        }
    }

    private void setOperation() {
        if (BOTAO_LER_PALLET_CAD){
            configurarLeituraUnica();
            sendCmdInventory();
        }else if(BOTAO_GRAVAR_TAG_CAD || BOTAO_GRAVAR_OUTRA_TAG_CAD){
            configurarLeituraUnica();
            sendWriteTag();
        }else if (BOTAO_ADICIONAR_CAIXA_PALLET){

        }else if(BOTAO_REMOVER_ULTIMA_CAIXA_PALLET){

        }else if (BOTAO_SALVAR_PALLET){

        }
    }

    private void resetVarButonCadTag(){
        BOTAO_LER_TAG_CAD                   = false;
        BOTAO_GRAVAR_TAG_CAD                = false;
        BOTAO_GRAVAR_OUTRA_TAG_CAD          = false;
        BOTAO_LER_PALLET_CAD                = false;
        BOTAO_ADICIONAR_CAIXA_PALLET        = false;
        BOTAO_REMOVER_ULTIMA_CAIXA_PALLET   = false;
        BOTAO_SALVAR_PALLET                 = false;;
    }

    private void setarButton(String readCadTag) {
        switch (readCadTag){
            case "ReadCadTag":
                BOTAO_LER_TAG_CAD = true;
                break;
            case "GravarCadTag":
                BOTAO_GRAVAR_TAG_CAD = true;
                break;
            case "GravarOutraTag":
                BOTAO_GRAVAR_OUTRA_TAG_CAD = true;
                break;
            case "LerPalletTag":
                BOTAO_LER_PALLET_CAD = true;
                break;
            case "AdicionarCaixaPallet":
                BOTAO_ADICIONAR_CAIXA_PALLET = true;
                break;
            case "RemoverUltimaCaixaPallet":
                BOTAO_REMOVER_ULTIMA_CAIXA_PALLET = true;
                break;
            case "SalvarPallet":
                BOTAO_SALVAR_PALLET = true;
                break;

        }

    }

    private void searchCMD() {
        //mHandlerSearchView.sendEmptyMessage(MSG_SEARCHING_TAG);
        configurarLeituraContinua();
        sendCmdInventory();

        // setupOperationParameter();
        //sendCmdInventory();
    }

/*--------------------------------- Comandos para salvar no BD do Celular ou Online

      private void salvarDadosBD() {
       WineBean wine = new WineBean();
        BancoController bdInstance = new BancoController(getBaseContext());

        wine.setTag_id(mEdtTagIdCad.getText().toString());
        wine.setDestino(mEdtDestino.getText().toString());
        wine.setLote(mEdtLote.getText().toString());
        wine.setN_palete(mEdtLote.getText().toString());
            int position = mSpinnerProduto.getSelectedItemPosition();
            wine.setProduto(produtosList.get(position));
        wine.setTag_palete_id(mEdtTagIDPalete.getText().toString());

        if(bdInstance.insereWine(wine)){
            Toast.makeText(getBaseContext(),"Dados Salvo no Celular!", Toast.LENGTH_LONG).show();
            limparCamposCadastro();
        }else{
            Toast.makeText(getBaseContext(),"Erro ao Salvar!", Toast.LENGTH_LONG).show();
        }


    }

    private void salvarDadosWeb() {
        WineBean wine = new WineBean();

        wine.setTag_id(mEdtTagIdCad.getText().toString());
        wine.setDestino(mEdtDestino.getText().toString());
        wine.setLote(mEdtLote.getText().toString());
        wine.setN_palete(mEdtNumPalete.getText().toString());
            int position = mSpinnerProduto.getSelectedItemPosition();
            wine.setProduto(produtosList.get(position));

        wine.setTag_palete_id(mEdtTagIDPalete.getText().toString());

        webInstance = new FachadaWeb();
        webInstance.setWine(wine);

        try {
            String resposta = webInstance.execute(webInstance.getSAVE_WINE()).get();

            Toast.makeText(getBaseContext(),"Dados salvos na Internet: " + resposta ,Toast.LENGTH_LONG).show();
        }catch (Exception e){

        }
    }*/

    private boolean isConnection() {
        boolean saida = false;

        webInstance = new FachadaWeb();

        try {
            String resposta = webInstance.execute(webInstance.getHOME()).get();

            if(resposta.equalsIgnoreCase("OK"))
                saida = true;

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return saida;
    }




    // --- SeekBar Listener
    public void onProgressChanged( SeekBar seekBar, int progress,
                                   boolean fromUser )
    {
        if( seekBar == mSeekPower )
        {
            mTxtPower.setText(TXT_POWER[ progress ]);
            sendSettingTxPower(TX_POWER[ progress ]);

        }else if( seekBar == mSeekDuty )
        {
            mTxtDuty.setText(TXT_DUTY[ progress ]);
            sendSettingTxCycle(TX_DUTY_ON[ progress ],TX_DUTY_OFF[ progress ]);

        }else if(seekBar == mSeekPotenciaProcurar){
            mTxtPotenciaProcurar.setText(TXT_POWER[ progress ]);
            sendSettingTxPower(TX_POWER[ progress ]);

        }else if(seekBar == mSeekDutyProcurar){
            mTxtRaioProcurar.setText(TXT_DUTY[ progress ]);
            sendSettingTxCycle(TX_DUTY_ON[ progress ],TX_DUTY_OFF[ progress ]);

        }
    }

    public void onStartTrackingTouch( SeekBar seekBar )
    {
    }

    public void onStopTrackingTouch( SeekBar seekBar )
    {
    }


    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick( AdapterView<?> av, View v, int arg2, long arg3 )
        {
            mR900Manager.stopDiscovery();

            // ---
            HashMap<String, String> map = mArrBtDevice.get(arg2);
            if( map == null )
                return;

            // ---
            final String BT_ADDR = map.get("address");
            if( BT_ADDR == null )
                return;

            // ---
            setEnabledLinkCtrl(false);
            setAutoConnectDevice(BT_ADDR);
            mR900Manager.connectToBluetoothDevice(BT_ADDR, MY_UUID);
        }
    };

    private void setAutoConnectDevice( String strAddr )
    {
        if( strAddr != null )
        {
            SharedPreferences pref = getSharedPreferences("RFIDHost", 0);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("auto_link_device", strAddr);
            editor.commit();
        }
    }

    private String getAutoConnectDevice()
    {
        SharedPreferences pref = getSharedPreferences("RFIDHost", 0);
        return pref.getString("auto_link_device", "");
    }

    private void resetBtDeviceList()
    {
        mBtnScan.setEnabled(false);
        mBtnDisconnect.setEnabled(false);
        mArrBtDevice.clear();
        mAdapterDevice.notifyDataSetChanged();
    }

    private void refreshBluetoothDevice()
    {
        Set<BluetoothDevice> setDevice = mR900Manager.queryPairedDevices();
        Log.d(TAG, "refreshBluetoothDevice : " + setDevice.size());
    }

    public void configurarLeituraUnica(){

        sendInventParam(mSpinQuerySession.getSelectedItemPosition(),
                mSpinQueryQ.getSelectedItemPosition(),
                mSpinTargetAB.getSelectedItemPosition());
        final MaskActivity.SelectMask selMask = MaskActivity.getSelectMask();
        boolean bUseMask = true;
        boolean bQuerySelected = ( selMask.Bits > 0 );

        int timeout = 0;
        final String strTimeout = mEdtTimeout.getText().toString();
        if( strTimeout != null && strTimeout.length() > 0 )
        {
            timeout = Integer.parseInt(strTimeout);
            if( timeout < 0 )
                timeout = 0;
            timeout *= 1000;
        }

        if( MaskActivity.UseMask == true && selMask.Bits > 0 )
        {
            if( MaskActivity.Type == 0 )
            {
                if( selMask.Pattern != null && selMask.Pattern.length() != 0 )
                {
                    sendSetSelectAction(selMask.Bits, selMask.Bank,
                            selMask.Offset, selMask.Pattern, 0);
                    bUseMask = true;
                }
            }
            else
            {
                if( selMask.Bank != 4 )
                {
                    sendSetSelectAction(selMask.Bits, selMask.Bank,
                            selMask.Offset, selMask.Pattern, 0);
                    bUseMask = true;
                }
            }
        }
        setOpMode(true, bUseMask, timeout, bQuerySelected);
    }

    public void configurarLeituraContinua()
    {
        sendInventParam(mSpinQuerySession.getSelectedItemPosition(),
                mSpinQueryQ.getSelectedItemPosition(),
                mSpinTargetAB.getSelectedItemPosition());
        final MaskActivity.SelectMask selMask = MaskActivity.getSelectMask();
        boolean bUseMask = false;
        boolean bQuerySelected = ( selMask.Bits > 0 );

        int timeout = 0;
        final String strTimeout = mEdtTimeout.getText().toString();
        if( strTimeout != null && strTimeout.length() > 0 )
        {
            timeout = Integer.parseInt(strTimeout);
            if( timeout < 0 )
                timeout = 0;
            timeout *= 1000;
        }

        if( MaskActivity.UseMask == true && selMask.Bits > 0 )
        {
            if( MaskActivity.Type == 0 )
            {
                if( selMask.Pattern != null && selMask.Pattern.length() != 0 )
                {
                    sendSetSelectAction(selMask.Bits, selMask.Bank,
                            selMask.Offset, selMask.Pattern, 0);
                    bUseMask = true;
                }
            }
            else
            {
                if( selMask.Bank != 4 )
                {
                    sendSetSelectAction(selMask.Bits, selMask.Bank,
                            selMask.Offset, selMask.Pattern, 0);
                    bUseMask = true;
                }
            }
        }
        setOpMode(mChkSingleTag.isChecked(), bUseMask, timeout, bQuerySelected);
    }

    public static class AccessAddress
    {
        public int bank;
        public int offset;
        public int len;
    }

    private AccessAddress getAccessAddress()
    {
        AccessAddress accAddr = new AccessAddress();
        accAddr.bank = 0; //mSpinEpc.getSelectedItemPosition();
        accAddr.offset = 1;// Integer
               // .parseInt(mEdtTagMemOffset.getText().toString());
        accAddr.len = 07; // Integer
                //.parseInt(mEdtTagMemWordcount.getText().toString());
        switch( accAddr.bank )
        {
            case 0:
                accAddr.bank = 1;
                accAddr.offset = 0x01;
                accAddr.len = 7;
                break;
            case 1:
                accAddr.bank = 0;
                accAddr.offset = 0x02;
                accAddr.len = 2;
                break;
            case 2:
                accAddr.bank = 0;
                accAddr.offset = 0x00;
                accAddr.len = 2;
                break;
            case 3:
                accAddr.bank = 2;
                accAddr.offset = 0x00;
                accAddr.len = 2;
                break;
            default:
                accAddr.bank -= 4;
                break;
        }
        return accAddr;
    }

    private short SWAPLSB2( int mark )
    {
        return (short) ( ( ( ( ( ( mark ) & 1 ) << 1 ) | ( ( ( mark ) >> 1 ) & 1 ) ) & 3 ) & 0xffff );
    }

    private R900Status.LockPattern getLockPattern()
    {
        int index = 0;

        R900Status.LockPattern lockPattern = new R900Status.LockPattern();
        lockPattern.lockPerma = false;

        //index = mSpinKillPassword.getSelectedItemPosition();
        if( index > 2 )
            lockPattern.lockPerma = true;
        lockPattern.enableKillPwd = index != 0;
        lockPattern.indexKillPwd = ( ( index == 1 ) ? false
                : ( ( index == 2 ) ? true : false ) );

        //index = mSpinAccessPassword.getSelectedItemPosition();
        if( index > 2 )
            lockPattern.lockPerma = true;
        lockPattern.enableAcsPwd = index != 0;
        lockPattern.indexAcsPwd = ( ( index == 1 ) ? false
                : ( ( index == 2 ) ? true : false ) );

        if( index > 2 )
            lockPattern.lockPerma = true;
        lockPattern.enableUii = index != 0;
        lockPattern.indexUii = ( ( index == 1 ) ? false
                : ( ( index == 2 ) ? true : false ) );

        if( index > 2 )
            lockPattern.lockPerma = true;
        lockPattern.enableTid = index != 0;
        lockPattern.indexTid = ( ( index == 1 ) ? false
                : ( ( index == 2 ) ? true : false ) );

        if( index > 2 )
            lockPattern.lockPerma = true;
        lockPattern.enableUser = index != 0;
        lockPattern.indexUser = ( ( index == 1 ) ? false
                : ( ( index == 2 ) ? true : false ) );

        return lockPattern;
    }

    public void sendReadTag()
    {
        String strPwd = getPassword();// mEdtPassword.getText().toString();
        AccessAddress accAddr = getAccessAddress();
        sendReadTag(accAddr.len, accAddr.bank, accAddr.offset, strPwd);
    }

    public void sendWriteTag()
    {
        final String strPwd = getPassword();

        int item = (int) mSpinnerProdutoTagCad.getSelectedItemPosition();
        String uuid = generateUUID();
        int estoque = 1;
        String TagCad = mEdtTagCad.getText().toString();

        String newTagId = TagCad.substring(0,18) + uuid + produtosListCode.get(item) + String.format("%03d", estoque);

        final String strTagPadrao = newTagId;

        AccessAddress accAddr = getAccessAddress();

        if( strTagPadrao != null && ( strTagPadrao.length() == ( accAddr.len * 4 ) ) ) {
            sendWriteTag(accAddr.len, accAddr.bank, accAddr.offset, strPwd,
                    strTagPadrao);
        }
        else
        {

            new AlertDialog.Builder(this)
                    .setTitle("Erro")
                    .setMessage("Primeiro informe a Tag a ser Alterada.")
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener()
                            {
                                public void onClick( DialogInterface dialog,
                                                     int whichButton )
                                {
                                }
                            }).create().show();
        }
    }

    public static final int LM_KILL_PWD_RW_LOCK     = 1 << 9;
    public static final int LM_KILL_PWD_PERM_LOCK   = 1 << 8;
    public static final int LM_ACCESS_PWD_RW_LOCK   = 1 << 7;
    public static final int LM_ACCESS_PWD_PERM_LOCK = 1 << 6;
    public static final int LM_EPC_MEM_RW_LOCK      = 1 << 5;
    public static final int LM_EPC_MEM_PERM_LOCK    = 1 << 4;
    public static final int LM_TID_MEM_RW_LOCK      = 1 << 3;
    public static final int LM_TID_MEM_PERM_LOCK    = 1 << 2;
    public static final int LM_USER_MEM_RW_LOCK     = 1 << 1;
    public static final int LM_USER_MEM_PERM_LOCK   = 1 << 0;
    public static final int LOCK_PERMA              = ( LM_KILL_PWD_PERM_LOCK
                                                        | LM_ACCESS_PWD_PERM_LOCK | LM_EPC_MEM_PERM_LOCK
                                                        | LM_TID_MEM_PERM_LOCK | LM_USER_MEM_PERM_LOCK );

    public String getPassword()
    {
        String strPwd = "00000000"; //mEdtPassword.getText().toString();
        return "0x" + strPwd.replace("0x", "");
    }


    private void resetInventoryButton()
    {
        mBtnInventory.setText("Iniciar Leitura");
    }

    private void resetAccessButton()
    {
       // mBtnAccess.setText("Acesso");
       // mBtnMask.setEnabled(true);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_COARSE_LOCATION_PERMISSIONS: {
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if( mR900Manager != null ) {
                        mR900Manager.startDiscovery();
                    }
                } else {
                    Toast.makeText(this,
                            "Permission denied",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    // Bluetooth Event Listener
    public void onBtFoundNewDevice( BluetoothDevice device )
    {
        HashMap<String, String> item = new HashMap<String, String>();
        item.put("name", device.getName());
        if( mConnected == true &&
                mConnectedDevice != null &&
                mConnectedDevice.getAddress().equals( device.getAddress() ) )
            item.put("summary", device.getAddress() + " - Conectado!" );
        else
            item.put("summary", device.getAddress());
        item.put("address", device.getAddress());
        item.put("status", "");

        mArrBtDevice.add(item);
        mAdapterDevice.notifyDataSetChanged();
    }

    public void onBtOff(){

            HashMap<String, String> item = new HashMap<String, String>();
            item.put("name", "Bluetooth está desativado!");
            item.put("summary", "");
            item.put("address", "");
            item.put("status", "");
            mArrBtDevice.add(item);
            mAdapterDevice.notifyDataSetChanged();
    }

    public void onBtScanCompleted()
    {
        mBtnScan.setEnabled(true);
        if( mArrBtDevice.size() == 0 )
        {
            HashMap<String, String> item = new HashMap<String, String>();
            item.put("name", "Nenhum Dispositivo Encontrado!");
            item.put("summary", "");
            item.put("address", "");
            item.put("status", "");
            mArrBtDevice.add(item);
            mAdapterDevice.notifyDataSetChanged();
        }

        mBtnDisconnect.setEnabled( mConnected );

    }

    private void setEnabledLinkCtrl( boolean bEnable )
    {
        if( bEnable )
            mHandler.sendEmptyMessageDelayed(MSG_ENABLE_LINK_CTRL, 50);
        else
            mHandler.sendEmptyMessageDelayed(MSG_DISABLE_LINK_CTRL, 50);
    }

    private void setEnabledBtnDisconnect( boolean bEnable )
    {
        if( bEnable )
            mHandler.sendEmptyMessageDelayed(MSG_ENABLE_DISCONNECT, 50);
        else
            mHandler.sendEmptyMessageDelayed(MSG_DISABLE_DISCONNECT, 50);
    }

    private void showToastByOtherThread( String msg, int time )
    {
        mHandler.removeMessages(MSG_SHOW_TOAST);
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SHOW_TOAST, time, 0,
                msg));
    }

    private void setListStatus( BluetoothDevice device, String msg )
    {
        final String ADDR = device != null ? device.getAddress() : null;
        for( HashMap<String, String> map : mArrBtDevice )
        {
            final String tmpADDR = map.get("address");
            if( ADDR != null && ADDR.equals(tmpADDR) )
            {
                map.put("status", msg);
                map.put("summary", tmpADDR + " - " + msg);
            }
            else
            {
                map.put("status", "");
                map.put("summary", tmpADDR);
            }
        }
        mHandler.removeMessages(MSG_REFRESH_LIST_DEVICE);
        mHandler.sendEmptyMessage(MSG_REFRESH_LIST_DEVICE);
    }

    private void setLinkStatus( boolean bConnected )
    {
        if( mExit == true )
            return;


        mHandler.sendEmptyMessage(bConnected ? MSG_LINK_ON : MSG_LINK_OFF);
    }

    private void setConnectionStatus( boolean bConnected )
    {
        if( mExit == true )
            return;

        mConnected = bConnected;
        setEnabledBtnDisconnect(bConnected);
        setLinkStatus(bConnected);

        if( bConnected == false )
        {
            mConnectedDevice = null;
            setListStatus(null, "");
        }
    }

    public void sleep(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) { }
    }

    public void onBtConnected( BluetoothDevice device )
    {
        Log.d(TAG, "onBtConnected");

        setEnabledLinkCtrl(true);

        mConnectedDevice = device;
        setListStatus(device, "Conectado");
        showToastByOtherThread("Conexão OK", Toast.LENGTH_SHORT);

        // mPacketParser.reset();
        sendCmdOpenInterface1();

        setConnectionStatus(true);
        mForceDisconnect = false;

        // set the tx duty
        sendSettingTxCycle(TX_DUTY_ON[0], TX_DUTY_OFF[0]);
        mTxtDuty.setText(TXT_DUTY[0]);

        this.sleep(1000);
        sendInventoryReportingFormat(1,1); // you can see the time, RSSI value.

        //sendSettingAutoPowerOffDelay(600,1); If you want changed the power delay time, you can adjust.
    }

    // Battery Level Monitoring routine
    public void onBattGaugingStart()
    {
        mBattflag = true;
        Log.d(TAG, "******* Race Start~!! onBattGaugingStart");

        new Thread()
        {
            public void run()
            {
                // Reader Operation Check!!!!!
                Log.d(TAG, "******* Battery Runner~~~!!!! onBattGaugingStart");
                while(mBattRun)

                    try{

                        // 1. get current battery level
                        sendGettingBatteryLevel(0);

                        // 2. Check battery flag...
                        if(mBattflag == true)
                        {
                            // send message handler
                            Message msg = mHandler.obtainMessage();
                            mHandler.removeMessages(MSG_BATTERY_CTRL);
                            mHandler.sendEmptyMessage(MSG_BATTERY_CTRL);

                            mBattflag = false;
                        }

                        // as if 1sec, check the battery level..
                        Thread.sleep(2000);
                    }
                    catch (InterruptedException e){
                        // exeption debugging routine
                        e.printStackTrace();
                    }
            }
        }.start();	// thread structure
    }

    public void onBtDisconnected( BluetoothDevice device )
    {
        Log.d(TAG, "onBtDisconnected");
        setEnabledLinkCtrl(true);
        // setEnabledBtnDisconnect(false);
        setListStatus(device, "Conexão closed");
        showToastByOtherThread("Disconectado", Toast.LENGTH_SHORT);

        setConnectionStatus(false);
    }

    public void onBtConnectFail( BluetoothDevice device, String msg )
    {
        Log.d(TAG, "onBtConnectFail : " + msg);
        setEnabledLinkCtrl(true);
        setListStatus(device, "Connect Fail");
        // showToastByOtherThread("Connect Fail", Toast.LENGTH_SHORT);

        setConnectionStatus(false);
    }

    // public void onBtDataRecv( byte[] data, int len )
    public void onNotifyBtDataRecv()
    {
        if( mR900Manager == null )
            return;

        R900RecvPacketParser packetParser = mR900Manager.getRecvPacketParser();
        // ---
        while( true )
        {
            final String parameter = packetParser.popPacket();

            if( mConnected == false )
                break;

            if( parameter != null )
            {
                Log.d(TAG, "onBtDataRecv : [parameter = " + parameter + "]");
                processPacket(parameter);
            }
            else
                break;
        }

        // ---
        // mHandler.removeMessages(MSG_REFRESH_LIST_TAG);
        // if( mHandler.hasMessages( MSG_REFRESH_LIST_TAG ) == false )
        // mHandler.sendEmptyMessageDelayed(MSG_REFRESH_LIST_TAG, 10);
        mHandler.sendEmptyMessage(MSG_REFRESH_LIST_TAG);
    }

    private static final int MSG_TOAST = 100;
    private static final int MSG_DIALOG_INTERNET = 200;
    private static final int MSG_DIALOG_STOP_INTERNET = 201;

    private Handler mHandlerToast = new Handler()
    {
        @Override
        public void handleMessage( Message msg )
        {
            switch( msg.what )
            {
                case MSG_TOAST:
                {
                    if( mToast != null )
                        mToast.cancel();
                    Bundle bundle = msg.getData();
                    mToast = Toast.makeText(WineRFIDReadBluetoothActivity.this,
                            bundle.getString("msg"), Toast.LENGTH_LONG);
                    mToast.show();
                    break;
                }
                case MSG_DIALOG_INTERNET:
                {
                    dialog = ProgressDialog.show(getCurrentActivity(), "Pesquisando na Internet","Aguarde...",false, true);
                    dialog.setIcon(R.drawable.clousync);
                    dialog.setCancelable(false);

                    break;
                }
                case MSG_DIALOG_STOP_INTERNET:
                {
                    dialog.dismiss();
                    break;
                }
            }
        }
    };

    private Toast mToast;
    private LogfileMng mLogMng = new LogfileMng();

    public void onBtDataSent( byte[] data )
    {
        mLogMng.write(data);

    }

    public void onBtDataTransException( BluetoothDevice device, String msg )
    {

    }

    // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    // process packet
    private synchronized void processPacket( final String param )
    {
        if( param == null || param.length() <= 0 )
            return;

        final String CMD = param.toLowerCase();

        if( CMD.indexOf("^") == 0 || CMD.indexOf("$") == 0
                || CMD.indexOf("ok") == 0 || CMD.indexOf("err") == 0
                || CMD.indexOf("end") == 0 )
        {

            if( mConnected == false )
                return;

            if((CMD.indexOf("$btmac,") == 0))
            {
                final int idxComma = CMD.indexOf(",");
                final String mStrBTmac = CMD.substring(idxComma + 1, CMD.length());

                mTxtBTmac.setText("bt mac address : " + mStrBTmac);
                return;
            }

            if((CMD.indexOf("ok,ver=") == 0))
            {
                final int idxComma = CMD.indexOf("=");
                final String mStrver = CMD.substring(idxComma + 1, CMD.length());

                mTxtFWver.setText("mac." + mStrver);
                return;
            }

            // get current battery level
            if((CMD.indexOf("ok,") != -1) && (!mBattflag))
            {
                final int idxComma = CMD.indexOf(",");
                final String mStrBattLevel = CMD.substring(idxComma + 1, CMD.length());
                if(!BOTAO_GRAVAR_TAG_CAD)
                    R900Status.setBatteryLevel(Integer.parseInt(mStrBattLevel));

                // set flag...
                mBattflag = true;
                return;
            }


            if( CMD.indexOf("$trigger=1") == 0 )
            {
                if( mTabMode == TAB_INVENTORY )
                {
                    mBtnInventory.setText("Parar Leitura");
                    mBtnInventory.setEnabled(false);
                    configurarLeituraContinua();
                    sendCmdInventory();
                }
                else if( mTabMode == TAB_PROCURAR)
                {
                    //mBtnAccess.setText("Parar");
                   // mBtnAccess.setEnabled(false);
                   // configurarLeituraContinua();
                    //sendCmdAccess();
                }
                return;
            }
            else if( CMD.indexOf("$trigger=0") == 0 )
            {
                if( mTabMode == TAB_INVENTORY )
                {
                    mBtnInventory.setEnabled(true);
                    sendCmdStop();

                }else if( mTabMode == TAB_PROCURAR){
                    //mBtnAccess.setText("Access");
                   // mBtnAccess.setEnabled(true);
                    sendCmdStop();

                }else if(mTabMode == TAB_CADASTRAR){
                    sendCmdStop();

                }

                return;
            }
            else if( CMD.indexOf("$online=0") == 0 )
            {
                setConnectionStatus(false);
            }
            else if( CMD.indexOf("end") == 0 )
            {
                if( mTabMode == TAB_INVENTORY )
                {
                    final int idxComma = CMD.indexOf(",");
                    if( idxComma > 4 )
                    {
                        final String strNum = CMD.substring(4, idxComma);
                        final String strErr = getEndMsg(strNum);
                        if( strErr != null )
                            mStrAccessErrMsg = strErr;
                    }
                }
                else if( mTabMode == TAB_PROCURAR)
                {
                    final int idxComma = CMD.indexOf(",");
                    if( idxComma > 4 )
                    {
                        final String strNum = CMD.substring(4, idxComma);
                        //mStrAccessErrMsg = getEndMsg(strNum);
                        final String strErr = getEndMsg(strNum);
                        if( strErr != null )
                            mStrAccessErrMsg = strErr;
                    }
                } else if( mTabMode == TAB_CADASTRAR)
                {
                    final int idxComma = CMD.indexOf(",");
                    if( idxComma > 4 )
                    {
                        final String strNum = CMD.substring(4, idxComma);
                        //mStrAccessErrMsg = getEndMsg(strNum);
                        final String strErr = getEndMsg(strNum);
                        if( strErr != null )
                            mStrAccessErrMsg = strErr;
                    }
                }

                // ---
                resetAccessButton();
                resetInventoryButton();

            }
            else if( CMD.indexOf("err") == 0 || CMD.indexOf("ok") == 0 )
            {
                if( mTabMode == TAB_INVENTORY )
                {

                }
                else if( mTabMode == TAB_CADASTRAR )
                {
                   // updateAccessDataRead(param);
                    if(BOTAO_GRAVAR_TAG_CAD){
                        updateAccessDataWrite(param);
                    }
                }

                // ---
                if( CMD.indexOf("ok") == 0 )
                    mStrAccessErrMsg = null;
            }
        }
        else
        {
            if( mLastCmd == null )
                return;

            if( mTabMode == TAB_INVENTORY )
            {
                if( mLastCmd.equalsIgnoreCase(R900Protocol.CMD_INVENT) )
                    updateTagCount(param);
            }else if( mTabMode == TAB_CADASTRAR ){
                if(BOTAO_LER_TAG_CAD)
                    readTag(param);
                else if(BOTAO_GRAVAR_TAG_CAD)
                    updateAccessDataWrite(param);
            }else if( mTabMode == TAB_PROCURAR){
                readTag(param);
            }
        }
    }

    private void updateAccessDataWrite( final String param )
    {
        if( param == null || param.length() <= 4 )
            return;

        final String tagId = param.substring(0, param.length() - 4);

        if( param.equalsIgnoreCase("ok") )
        {
            mStrAccessErrMsg = null;
            return;
        }

        final String packet = param.toLowerCase();
        final int index = packet.indexOf(",e=");
        if( index > 0 && ( index + 3 ) < ( packet.length() - 4 ) )
        {
            final String strTagId = packet.substring(0, index);
            boolean bStatusOk = ( strTagId.indexOf("ok") == 0 );

            final String strTag = packet.substring(index + 3,
                    packet.length() - 4);

            if( bStatusOk == false )
            {
                final int errTagPrefix = strTagId.indexOf("err_tag=");
                if( errTagPrefix == 0 )
                {
                    final String strTagError = strTagId.substring(8,
                            strTagId.length());
                    mStrAccessErrMsg = getTagErrorMsg(strTagError);
                }
                else
                {
                    final int errOpPrefix = strTagId.indexOf("err_op=");
                    if( errOpPrefix == 0 )
                    {
                        final String strOpError = strTagId.substring(7,
                                strTagId.length());
                        mStrAccessErrMsg = "Operation Error : " + strOpError;
                    }
                    else
                        mStrAccessErrMsg = null;
                }
            }
            else
                mStrAccessErrMsg = null;

        }

        if( mChkSingleTag.isChecked() )
        {
            //mBtnAccess.setText("Access");

        }
    }

    private void updateAccessDataRead( final String param )
    {
        if( param == null || param.length() <= 4 )
            return;

        mStrAccessErrMsg = null;
        final String tagId = param.substring(0, param.length() - 4);


        if( mHandler.hasMessages(MSG_SOUND_RX) == false )
            mHandler.sendEmptyMessage(MSG_SOUND_RX);

        final String packet = param.toLowerCase();
        final int index = packet.indexOf(",e=");
        if( index > 0 && ( index + 3 ) < ( packet.length() - 4 ) )
        {
            final String strTagId = packet.substring(0, index);
            final String strTag = packet.substring(index + 3,
                    packet.length() - 4);
           // mEdtTagIdCad.setText(strTag);

            boolean bStatusOk = true;
            final int errTagPrefix = strTagId.indexOf("err_tag=");
            if( errTagPrefix == 0 )
            {
                final String strTagError = strTagId.substring(8,
                        strTagId.length());
                mStrAccessErrMsg = getTagErrorMsg(strTagError);
                bStatusOk = false;
            }
            else
            {
                final int errOpPrefix = strTagId.indexOf("err_op=");
                if( errOpPrefix == 0 )
                {
                    final String strOpError = strTagId.substring(7,
                            strTagId.length());
                    mStrAccessErrMsg = "Operation Error : " + strOpError;
                    bStatusOk = false;
                }
                else
                    mStrAccessErrMsg = null;
            }

            //if( bStatusOk )
                //mEdtTagMemData.setText(strTagId);
            //else
                //mEdtTagMemData.setText("");



        }


        // --
        if( mChkSingleTag.isChecked() )
        {
            //mBtnAccess.setText("Access");

        }
    }

    private void readTag( final String param ) {

        String arrayString[] = param.split(",");
        String tagId = arrayString[0].substring(0,arrayString[0].length()-4);

        if (param == null
                || tagId.length() < 28
                || param.contains("Txp")
                || param.contains("Txc"))
            return;


        if (mTabMode == TAB_CADASTRAR) {
            if(BOTAO_LER_TAG_CAD)
                mEdtTagCad.setText(tagId);

            resetVarButonCadTag();
        }
        else if( mTabMode == TAB_PROCURAR ){
            boolean bUpdate = false;

            String editTextProcurar = mEdtTagProcurar.getText().toString().trim();
            adapter.clear();

            if(mRBTag.isChecked()){
                if(editTextProcurar.equals(tagId)){
                    mHandler.sendEmptyMessage(MSG_SEARCHED_TAG);
                    adapter.add(preencheListaAndSet(tagId));
                }
                else{
                    mHandler.sendEmptyMessage(MSG_NOT_SEARCHED_TAG);
                    adapter.add(preencheListaNotSet(tagId));
                }

            } else if (mRBLote.isChecked()) {
               /* String lote = searchLote(tagId);
                if(editTextProcurar.equalsIgnoreCase(lote)){
                    mHandler.sendEmptyMessage(MSG_SEARCHED_TAG);
                    adapter.add(preencheListaAndSet(tagId));
                }else{
                    mHandler.sendEmptyMessage(MSG_NOT_SEARCHED_TAG);
                    adapter.add(preencheListaNotSet(tagId));
                }*/

            }else if(mRBDestino.isChecked()){
               /* String destino = searchDestino(tagId);
                if(editTextProcurar.equalsIgnoreCase(destino)){
                    mHandler.sendEmptyMessage(MSG_SEARCHED_TAG);
                    adapter.add(preencheListaAndSet(tagId));
                }else{
                    mHandler.sendEmptyMessage(MSG_NOT_SEARCHED_TAG);
                    adapter.add(preencheListaNotSet(tagId));
                }*/
            }
        }
    }

    private WineBean preencheListaNotSet(String tagId){
        WineBean saida = new WineBean();
        for (int i = 0; i < wineWebArray.size(); i++){
            if(wineWebArray.get(i).getTag_id().equals(tagId)) {
                saida = wineWebArray.get(i);
            }
        }
        return saida;
    }

    private WineBean preencheListaAndSet(String tagId){
        WineBean saida = new WineBean();
        for (int i = 0; i < wineWebArray.size(); i++){
            wineWebArray.get(i).setSearch(false);
            if(wineWebArray.get(i).getTag_id().equals(tagId)) {
                wineWebArray.get(i).setSearch(true);
                saida = wineWebArray.get(i);
            }
        }
        return saida;
    }


    private void updateTagCount( final String param )
    {

        String arrayString[] = param.split(",");
        String tagId = arrayString[0].substring(0,arrayString[0].length()-4);

        if (param == null
                || tagId.length() < 28
                || param.contains("Txp")
                || param.contains("Txc"))
            return;


        mStrAccessErrMsg = null;
        boolean bUpdate = false;

        if( mTabMode == TAB_CADASTRAR ){
            mChkSingleTag.setChecked(true);
            //mEdtTagIdCad.setText(tagId);
            sendCmdStop();
        }else {

            for (HashMap<String, String> map : mArrTag) {
                if (tagId.equals(map.get("tag"))) {
                    if (!mChkSkipSame.isChecked()) {
                        if (!mHandler.hasMessages(MSG_SOUND_RX))
                            mHandler.sendEmptyMessage(MSG_SOUND_RX);

                        final String count = String.valueOf(Integer.parseInt(map.get("count")) + 1);
                        Date curDate = new Date();
                        final String last = curDate.toLocaleString();
                        map.put("count", String.valueOf(count));
                        map.put("last", last);
                        map.put("summary", String.format("- count\t: %s", count));
                    } else {
                        if (mHandler.hasMessages(MSG_SOUND_RX_HALF) == false)
                            mHandler.sendEmptyMessage(MSG_SOUND_RX_HALF);
                    }
                    bUpdate = true;
                    break;
                }
            }

            // ---
            if (bUpdate == false) {
                if (mHandler.hasMessages(MSG_SOUND_RX) == false)
                    mHandler.sendEmptyMessage(MSG_SOUND_RX);

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("tag", tagId);
                mArrTag.add(map);

                // --
                if (mSelTag == null)
                    setSelTag(tagId);
            }

            // --
            if (mChkSingleTag.isChecked()) {
                resetInventoryButton();
                // mBtnInventory.setText("INVENTORY");
                // mInventLampTx.setLamp( LampView.LAMP_GRAY );
            }
        }
    }

    private void setSelTag( String strTag )
    {
        mSelTag = strTag;
    }

  /*  private void setAccessTagId( String strRead, String strTagId )
    {
        // ---
        if( strRead == null )
            mEdtTagId.setText("");
        else
            mEdtTagId.setText(strRead);

        // ---
        if( strTagId == null )
            mEdtTagMemData.setText("");
        else
            mEdtTagMemData.setText(strTagId);
    }
*/
    protected void finalize()
    {
        mExit = true;

        sendCmdStop();
        //mOp = false;	// eric 2012.11.23
        byeBluetoothDevice();

        mHandler.removeMessages(MSG_AUTO_LINK);

        //if( mLogMng != null )
           // mLogMng.finalize();

        super.finalize();
    }

    public void closeApp()
    {
        finalize();
        finish();
    }

    public void postCloseApp()
    {
        mHandler.sendEmptyMessageDelayed(MSG_QUIT, 1000);
    }

    @Override
    public void onBackPressed()
    {

        WineRFIDReadBluetoothActivity.this.postCloseApp();
        WineRFIDReadBluetoothActivity.this.closeApp();
        WineRFIDReadBluetoothActivity.this.finish();

    }
}
