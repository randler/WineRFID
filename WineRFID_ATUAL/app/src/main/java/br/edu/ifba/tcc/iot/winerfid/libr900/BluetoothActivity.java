package br.edu.ifba.tcc.iot.winerfid.libr900;

/***********************************************************************************
* BluetoothActivity revision history                                               *
*************+*************+********+***********************************************
* 2012.12.12	ver 1.0.0  	  eric     1. Generated(First release)                 *
//-----------+-------------+--------+-----------------------------------------------
* 2013.10.18    ver 1.1.0     eric     1. Added, sendGettingBTmacAddress function.
************************************************************************************/

import android.Manifest;
import android.app.TabActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.UUID;

//import com.dotel.rfid.RFIDHostActivity.LockPattern;

public class BluetoothActivity extends TabActivity
{

	protected static String TAG = "BluetoothActivity";

	protected static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	protected ArrayAdapter<String> mArrayAdapter;
	protected R900Manager mR900Manager;
	protected String mLastCmd;

	
	// protected Map<String, BluetoothDevice> mMapBtDevice;
	private OnBtEventListener mBtEventListener;
	public static final int MSG_BT_DATA_RECV = 10;
	
	protected boolean mSingleTag;
	protected boolean mUseMask;
	protected int mTimeout;
	protected boolean mQuerySelected;
	
	protected boolean mConnected;

	static final int REQUEST_COARSE_LOCATION_PERMISSIONS = 1;
	
	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage( final Message msg )
		{
			switch( msg.what )
			{
				case MSG_BT_DATA_RECV:
					mBtEventListener.onNotifyBtDataRecv();
					break;
			}
		}
	};


	//-------
	private boolean mUnregister = false;
	private final BroadcastReceiver mReceiver = new BroadcastReceiver()
	{
		public void onReceive( Context context, Intent intent )
		{
			String action = intent.getAction();
			// When discovery finds a device
			if( BluetoothDevice.ACTION_FOUND.equals(action) )
			{
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	
				final String strBTName = device.getName() + "\n"
						+ device.getAddress();
					Log.d(TAG, "[Bluetooth] Found Device : " + strBTName);
					// mMapBtDevice.put(device.getAddress(), device);
	
				// Notify to client
				if( mBtEventListener != null )
					mBtEventListener.onBtFoundNewDevice(device);
			}
			else if( BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action) )
			{
				if( mBtEventListener != null )
					mBtEventListener.onBtScanCompleted();
			}
			else
			{
				Log.d(TAG, "[Bluetooth] Other event : " + action);
			}
		}
	};

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		int mode = R900Status.getInterfaceMode();
		
		//mMapBtDevice = new HashMap<String, BluetoothDevice>();
		mR900Manager = new R900Manager( mHandler );
		
		// ---
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mReceiver, filter);
		
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(mReceiver, filter);
		
		if( mR900Manager.isBluetoothEnabled() == false )
		{
			Intent discoverableIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(
						BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
		}				

	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();	
			
		finalize();

	}
	
	protected void finalize()
	{
		if( mR900Manager != null )
			mR900Manager.finalize();
		mR900Manager = null;
				
		if( mUnregister == false )
			unregisterReceiver(mReceiver);
		mUnregister = true;	
	}
	
	@Override
	public void onBackPressed()
	{
		Log.d("BluetoothActivity", "onBackPressed" );
	}
	
	
	public void setOnBtEventListener( OnBtEventListener listener )
	{
		mBtEventListener = listener;
		if( mR900Manager != null )
			mR900Manager.setOnBtEventListener(listener);
	}
	public void scanBluetoothDevice()
	{
		// mMapBtDevice.clear();
		if( mR900Manager != null ) {
			int hasPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
			if (hasPermission == PackageManager.PERMISSION_GRANTED) {
				mR900Manager.startDiscovery();

				return;
			}
			ActivityCompat.requestPermissions(this,
					new String[]{
							Manifest.permission.ACCESS_COARSE_LOCATION},
					REQUEST_COARSE_LOCATION_PERMISSIONS);
		}

			mR900Manager.startDiscovery();

	}



	public void byeBluetoothDevice()
	{
		if( mR900Manager != null )
			mR900Manager.sendData(R900Protocol.BYE);
	}

	public void connectToBluetoothDevice( String address, UUID uuid )
	{
		// BluetoothDevice device = mMapBtDevice.get(mac);
		if( mR900Manager != null && address != null )
			mR900Manager.connectToBluetoothDevice(address, uuid);
	}
	
	// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Send
	// Command
	public void sendCmdOpenInterface1()
	{
		if( mR900Manager != null )
			mR900Manager.sendData(R900Protocol.OPEN_INTERFACE_1);
	}
	
	public void sendCmdOpenInterface2()
	{
		if( mR900Manager != null )
			mR900Manager.sendData(R900Protocol.OPEN_INTERFACE_2);
	}
	
	public void sendCmdInventory()
	{
		sendCmdInventory( mSingleTag ? 1 : 0, mUseMask ? ( mQuerySelected ? 3 : 2 ) : 0, mTimeout  );
	}
	
	public void sendCmdInventory( int f_s, int f_m, int to )
	{
		if( mR900Manager != null )
		{
			R900Status.setOperationMode(1);	// eric 2012.11.29
			mLastCmd = R900Protocol.CMD_INVENT;
			mR900Manager.sendData(R900Protocol.makeProtocol(mLastCmd, new int[]
			{ f_s, f_m, to }));
		}
	}

	public void sendCmdStop()
	{
		if( mR900Manager != null )
		{
			R900Status.setOperationMode(0);	// eric 2012.11.29
			mLastCmd = R900Protocol.CMD_STOP;
			mR900Manager.sendData(R900Protocol.makeProtocol(mLastCmd, (int[])null));
		}
	}
	
	public void sendHeartBeat( int value )
	{
		if( mR900Manager != null )
		{
			mR900Manager.sendData(R900Protocol.makeProtocol( R900Protocol.CMD_HEART_BEAT, new int[]{ value }));
		}
	}
	
	public void sendCmdSelectMask( int n, int bits, int mem, int b_offset, String pattern, int target, int action )
	{
		if( mR900Manager != null )
		{
			mR900Manager.sendData(R900Protocol.makeProtocol( R900Protocol.CMD_SEL_MASK,
					new int[]{ n, bits, mem, b_offset}, pattern, new int[]{ target, action } ));
		}
	}
	
	//--- Access
	public void sendSetSession( int session )
	{
		if( mR900Manager != null )
		{
			mR900Manager.sendData(R900Protocol.makeProtocol( R900Protocol.CMD_INVENT_PARAM,
					new int[]{ session, R900Protocol.SKIP_PARAM, R900Protocol.SKIP_PARAM } ) );
		}
	}
	
	public void sendSetQValue( int q )
	{
		if( mR900Manager != null )
		{
			mR900Manager.sendData(R900Protocol.makeProtocol( R900Protocol.CMD_INVENT_PARAM,
					new int[]{ R900Protocol.SKIP_PARAM, q, R900Protocol.SKIP_PARAM } ) );
		}
	}
	
	public void sendSetInventoryTarget( int m_ab )
	{
		if( mR900Manager != null )
		{
			mR900Manager.sendData(R900Protocol.makeProtocol( R900Protocol.CMD_INVENT_PARAM,
					new int[]{ R900Protocol.SKIP_PARAM, R900Protocol.SKIP_PARAM, m_ab } ) );
		}
	}
	
	public void sendInventParam( int session, int q, int m_ab )
	{
		if( mR900Manager != null )
		{
			mR900Manager.sendData(R900Protocol.makeProtocol( R900Protocol.CMD_INVENT_PARAM,
					new int[]{ session, q, m_ab } ) );
		}
	}
	
	public void sendSetSelectAction( int bits, int mem, int b_offset, String pattern, int action )
	{
		if( mR900Manager != null )
		{
			mR900Manager.sendData(R900Protocol.makeProtocol( R900Protocol.CMD_SEL_MASK,
					new int[]{ 0, bits, mem, b_offset}, pattern, new int[]{ R900Protocol.SKIP_PARAM, action } ));
		}
	}
	
	public void setOpMode( boolean singleTag, boolean useMask, int timeout, boolean querySelected  )
	{
		mSingleTag = singleTag;
		mUseMask = useMask;
		mTimeout = timeout;
		mQuerySelected = querySelected;
	}
	
	public void sendReadTag( int w_count, int mem, int w_offset, String ACS_PWD )
	{
		if( mR900Manager != null )
		{
			mR900Manager.sendData(R900Protocol.makeProtocol( R900Protocol.CMD_READ_TAG_MEM,
					new int[]{ w_count, mem, w_offset }, 
					ACS_PWD, 
					new int[]{ mSingleTag ? 1 : 0, mUseMask ? ( mQuerySelected ? 3 : 2 ) : 0, mTimeout } ) );
		}
	}
	
	public void sendWriteTag( int w_count, int mem, int w_offset, String ACS_PWD, String wordPattern )
	{
		if( mR900Manager != null )
		{
			mR900Manager.sendData(R900Protocol.makeProtocol( R900Protocol.CMD_WRITE_TAG_MEM,
					new int[]{ w_count, mem, w_offset }, 
					new String[]{ wordPattern, ACS_PWD }, 
					new int[]{ mSingleTag ? 1 : 0, mUseMask ? ( mQuerySelected ? 3 : 2 ) : 0, mTimeout } ) );
		}
	}
	
	private int convertLockIndex( boolean enable, boolean index )
	{
		return enable ? ( index ? 1 : 0 ) : -1;
	}
	
	public void sendLockTag( R900Status.LockPattern lockPattern, String ACS_PWD )
	{
		if( mR900Manager != null )
		{
			final int user = convertLockIndex( lockPattern.enableUser, lockPattern.indexUser );//( ( lockPattern.indexUser == false ) ? ( R900Protocol.SKIP_PARAM ) : ( lockPattern.enableUser ? 1 : 0 ) );
			final int tid = convertLockIndex( lockPattern.enableTid, lockPattern.indexTid );//( ( lockPattern.indexTid == false ) ? ( R900Protocol.SKIP_PARAM ) : ( lockPattern.enableTid ? 1 : 0 ) );
			final int epc = convertLockIndex( lockPattern.enableUii, lockPattern.indexUii );//( ( lockPattern.indexUii == false ) ? ( R900Protocol.SKIP_PARAM ) : ( lockPattern.enableUii ? 1 : 0 ) );
			final int acs_pwd = convertLockIndex( lockPattern.enableAcsPwd, lockPattern.indexAcsPwd );//( ( lockPattern.indexAcsPwd == false ) ? ( R900Protocol.SKIP_PARAM ) : ( lockPattern.enableAcsPwd ? 1 : 0 ) );
			final int kill_pwd = convertLockIndex( lockPattern.enableKillPwd, lockPattern.indexKillPwd );//( ( lockPattern.indexKillPwd == false ) ? ( R900Protocol.SKIP_PARAM ) : ( lockPattern.enableKillPwd ? 1 : 0 ) );
			
			mR900Manager.sendData(R900Protocol.makeProtocol( R900Protocol.CMD_LOCK_TAG_MEM,
					new int[]{ user, tid, epc, acs_pwd, kill_pwd },
					ACS_PWD,
					new int[]{ mSingleTag ? 1 : 0, mUseMask ? ( mQuerySelected ? 3 : 2 ) : 0, mTimeout } ) );
		}
	}
	
	public void sendLockTag( int lockMask, int lockEnable, String ACS_PWD )
	{
		if( mR900Manager != null )
		{
			int bitFlag;
			boolean mask;
			boolean enable;
			
			//---
			bitFlag = 0x200;//0x02;
			mask = ( lockMask & bitFlag ) == bitFlag;
			enable = ( lockEnable & bitFlag ) == bitFlag;
			final int user = ( ( mask == false ) ? ( R900Protocol.SKIP_PARAM ) : ( enable ? 1 : 0 ) );
			
			//---
			bitFlag = 0x80;//0x08;
			mask = ( lockMask & bitFlag ) == bitFlag;
			enable = ( lockEnable & bitFlag ) == bitFlag;
			final int tid = ( ( mask == false ) ? ( R900Protocol.SKIP_PARAM ) : ( enable ? 1 : 0 ) );
			
			//---
			bitFlag = 0x20;
			mask = ( lockMask & bitFlag ) == bitFlag;
			enable = ( lockEnable & bitFlag ) == bitFlag;
			final int epc = ( ( mask == false ) ? ( R900Protocol.SKIP_PARAM ) : ( enable ? 1 : 0 ) );
			
			//---
			bitFlag = 0x08;//0x80;
			mask = ( lockMask & bitFlag ) == bitFlag;
			enable = ( lockEnable & bitFlag ) == bitFlag;
			final int acs_pwd = ( ( mask == false ) ? ( R900Protocol.SKIP_PARAM ) : ( enable ? 1 : 0 ) );
			
			//---
			bitFlag = 0x02;//0x200;
			mask = ( lockMask & bitFlag ) == bitFlag;
			enable = ( lockEnable & bitFlag ) == bitFlag;
			final int kill_pwd = ( ( mask == false ) ? ( R900Protocol.SKIP_PARAM ) : ( enable ? 1 : 0 ) );
			
			//---
			mR900Manager.sendData(R900Protocol.makeProtocol( R900Protocol.CMD_LOCK_TAG_MEM,
					new int[]{ user, tid, epc, acs_pwd, kill_pwd },
					ACS_PWD,
					new int[]{ mSingleTag ? 1 : 0, mUseMask ? ( mQuerySelected ? 3 : 2 ) : 0, mTimeout } ) );
		}
	}
	
	public void sendKillTag( String killPwd )
	{
		if( mR900Manager != null )
		{
			mR900Manager.sendData(R900Protocol.makeProtocol( R900Protocol.CMD_KILL_TAG,
					killPwd,
					new int[]{ mSingleTag ? 1 : 0, mUseMask ? ( mQuerySelected ? 3 : 2 ) : 0, mTimeout } ) );
					
		}
	}
	
	//---- other command
	public void sendGetVersion()
	{
		if( mR900Manager != null )
		{
			mR900Manager.sendData(R900Protocol.makeProtocol( R900Protocol.CMD_GET_VERSION ) );
		}
	}
	
	public void sendSetDefaultParameter()
	{
		if( mR900Manager != null )
		{
			mR900Manager.sendData(R900Protocol.makeProtocol( R900Protocol.CMD_SET_DEF_PARAM ) );
		}
	}
	
	public void sendGettingParameter( String cmd, String p )
	{
		if( mR900Manager != null )
		{
			mR900Manager.sendData(R900Protocol.makeProtocol( R900Protocol.CMD_GET_PARAM, new String[]{ cmd, p } ) );
		}
	}
	
	public void sendSettingTxPower( int a )
	{
		if( mR900Manager != null )
		{
			mR900Manager.sendData(R900Protocol.makeProtocol( R900Protocol.CMD_SET_TX_POWER, new int[]{ a } ) );
		}
	}
	
	public void sendGetMaxPower()
	{
		if( mR900Manager != null )
		{
			mR900Manager.sendData(R900Protocol.makeProtocol( R900Protocol.CMD_GET_MAX_POWER ) );
		}
	}
	
	public void sendSettingTxCycle( int on, int off)
	{
		if( mR900Manager != null )
		{
			mR900Manager.sendData(R900Protocol.makeProtocol( R900Protocol.CMD_SET_TX_CYCLE, new int[]{ on, off } ) );
		}
	}
	
	public void sendChangeChannelState( int n, int f_e )
	{
		if( mR900Manager != null )
		{
			mR900Manager.sendData(R900Protocol.makeProtocol( R900Protocol.CMD_CHANGE_CH_STATE, new int[]{ n, f_e } ) );
		}
	}
	
	public void sendSettingCountry( int code )
	{
		if( mR900Manager != null )
		{
			mR900Manager.sendData(R900Protocol.makeProtocol( R900Protocol.CMD_CHANGE_CH_STATE, new int[]{ code } ) );
		}
	}
	
	public void sendGettingCountry()
	{
		if( mR900Manager != null )
		{
			mR900Manager.sendData(R900Protocol.makeProtocol( R900Protocol.CMD_GET_COUNTRY_CAP ) );
		}
	}
	
	public void sendSetLockTagMemStatePerm( int mem_id, int f_l, String ACS_PWD )
	{
		if( mR900Manager != null )
		{
			mR900Manager.sendData(R900Protocol.makeProtocol( R900Protocol.CMD_SET_LOCK_TAG_MEM,
					new int[]{ mem_id, f_l },
					ACS_PWD,
					new int[]{ mSingleTag ? 1 : 0, mUseMask ? ( mQuerySelected ? 3 : 2 ) : 0, mTimeout } ) );
		}
	}
	
	public void sendPauseTx()
	{
		if( mR900Manager != null )
		{
			mR900Manager.sendData(R900Protocol.makeProtocol( R900Protocol.CMD_PAUSE_TX ) );
		}
	}
	
	public void sendStatusReporting( int f_link )
	{
		if( mR900Manager != null )
		{
			mR900Manager.sendData(R900Protocol.makeProtocol( R900Protocol.CMD_STATUS_REPORT,
					new int[]{ f_link } ) );
		}
	}
	
	public void sendInventoryReportingFormat( int f_time, int f_rssi )
	{
		if( mR900Manager != null )
		{
			mR900Manager.sendData(R900Protocol.makeProtocol( R900Protocol.CMD_INVENT_REPORT_FORMAT, 
					new int[]{ f_time, f_rssi } ) );
		}
	}
	
	public void sendDislink()
	{
		if( mR900Manager != null )
		{
			mR900Manager.sendData(R900Protocol.makeProtocol( R900Protocol.CMD_DISLINK ) );
		}
	}
	
	//---- R900 Controls
	public void sendUploadingTagData( int index, int count )
	{
		if( mR900Manager != null )
		{
			mR900Manager.sendData(R900Protocol.makeProtocol( R900Protocol.CMD_UPLOAD_TAG_DATA,
					new int[]{ index, count } ) );
		}
	}
		
	public void sendClearingTagData()
	{
		if( mR900Manager != null )
		{
			mR900Manager.sendData(R900Protocol.makeProtocol( R900Protocol.CMD_CLEAR_TAG_DATA ) );
		}
	}
	
	public void sendAlertReaderStatus( int f_link, int f_trigger, int f_lowbat, int f_autooff, int f_pwr )
	{
		if( mR900Manager != null )
		{
			mR900Manager.sendData(R900Protocol.makeProtocol( R900Protocol.CMD_ALERT_READER_STATUS,
					new int[]{ f_link, f_trigger, f_lowbat, f_autooff, f_pwr } ) );
		}
	}
	
	public void sendGettingStatusWord()
	{
		if( mR900Manager != null )
		{
			mR900Manager.sendData(R900Protocol.makeProtocol( R900Protocol.CMD_GET_STATUS_WORD ) );
		}
	}
	
	public void sendSettingBuzzerVolume( int volume, int f_nv )
	{
		if( mR900Manager != null )
		{
			mR900Manager.sendData(R900Protocol.makeProtocol( R900Protocol.CMD_SET_BUZZER_VOL,
					new int[]{ volume, f_nv } ) );
		}
	}
		
	public void sendBeep( int f_on )
	{
		if( mR900Manager != null )
		{
			mR900Manager.sendData(R900Protocol.makeProtocol( R900Protocol.CMD_BEEP,
					new int[]{ f_on } ) );
		}
	}
	
	public void sendSettingAutoPowerOffDelay( int delay, int f_nv )
	{
		if( mR900Manager != null )
		{
			mR900Manager.sendData(R900Protocol.makeProtocol( R900Protocol.CMD_SET_AUTO_POWER_OFF_DELAY,
					new int[]{ delay, f_nv } ) );
		}
	}
	
	public void sendGettingBatteryLevel( int f_ext )
	{
		if( mR900Manager != null )
		{
			mR900Manager.sendData(R900Protocol.makeProtocol( R900Protocol.CMD_GET_BATT_LEVEL,
					new int[]{ f_ext } ) );
		}
	}
	
	public void sendReportingBatteryState( int f_report )
	{
		if( mR900Manager != null )
		{
			mR900Manager.sendData(R900Protocol.makeProtocol( R900Protocol.CMD_REPORT_BATT_STATE,
					new int[]{ f_report } ) );
		}
	}
	
	public void sendTurningReaderOff()
	{
		if( mR900Manager != null )
		{
			mR900Manager.sendData(R900Protocol.makeProtocol( R900Protocol.CMD_TURN_READER_OFF ) );
		}
	}
	
	//<-- eric 2012.12.12
	public void sendDeviceProprietary(int mode, String password, String name)
	{
		if( mR900Manager != null )
		{
			mR900Manager.sendData(R900Protocol.makeProtocol( R900Protocol.CMD_READER_PROPRIETARY,
					new int[]{ mode },
					password,
					name ));
		}		
	}
	//--> eric 2012.12.12
	
	//<-- eric 2013.10.18
	public void sendGettingBTmacAddress()
	{
		if( mR900Manager != null)
		{
			mR900Manager.sendData(R900Protocol.makeProtocol( R900Protocol.CMD_GET_BT_MAC_ADDRESS) );
		}	
	}
	//--> eric 2013.10.18
}
