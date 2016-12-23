package br.edu.ifba.tcc.iot.winerfid.libr900;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class R900Manager
{
	public final int REQUEST_ENABLE_BT = 1;

	// ---
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothDevice mBluetoothDevice;
	private BluetoothSocket mBluetoothSocket;
	private UUID mUuid;
	private OnBtEventListener mBtEventListener;

	// private Handler mHandler;
	// public static final int MESSAGE_READ = 100;
	private ConnectThread mConnectThread;
	private ConnectedThread mConnectedThread;

	private static final boolean mUseDbgDump = false;
	private FileOutputStream mDbgOutStream;

	private R900RecvPacketParser mPacketParser = new R900RecvPacketParser();
	private Handler mHandlerNoti;

	public R900Manager( Handler handler )
	{
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		mHandlerNoti = handler;

		if( mUseDbgDump )
		{
			try
			{
				String ext = Environment.getExternalStorageState();
				if( ext.equals(Environment.MEDIA_MOUNTED) )
				{
					String strPath = Environment.getExternalStorageDirectory()
							.getAbsolutePath();
					mDbgOutStream = new FileOutputStream(strPath
							+ "/rfid_dbg.txt");
				}
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}
		}
	}

	public void finalize()
	{
		if( mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering() )
			mBluetoothAdapter.cancelDiscovery();

		disconnect();
		/*
		 * if( mConnectedThread != null ) { mConnectedThread.cancel();
		 * mConnectedThread.stop(); } mConnectedThread = null;
		 */

		if( mDbgOutStream != null )
		{
			try
			{
				mDbgOutStream.close();
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}
		}
	}

	public final R900RecvPacketParser getRecvPacketParser()
	{
		return mPacketParser;
	}

	public void setOnBtEventListener( OnBtEventListener listener )
	{
		mBtEventListener = listener;
	}

	public boolean isBluetoothEnabled()
	{
		if( mBluetoothAdapter == null )
			return false;

		return mBluetoothAdapter.isEnabled();
	}

	public void enableBluetooth( Activity host )
	{
		if( mBluetoothAdapter == null || mBluetoothAdapter.isEnabled() == false )
		{
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			host.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
	}

	public Set<BluetoothDevice> queryPairedDevices()
	{
		if( mBluetoothAdapter != null )
			return mBluetoothAdapter.getBondedDevices();
		return null;
	}

	public void startDiscovery()
	{
		stopDiscovery();
		mBluetoothAdapter.startDiscovery();
	}

	public void stopDiscovery()
	{
		if( mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering() )
			mBluetoothAdapter.cancelDiscovery();
	}

	public BluetoothDevice getBluetoothDevice( String address )
	{
		if( mBluetoothAdapter != null )
			return mBluetoothAdapter.getRemoteDevice(address);
		return null;
	}

	// ------------- For Connecting to Bluetooth
	private class ConnectThread extends Thread
	{
		public ConnectThread()
		{
			if( mBluetoothDevice != null )
			{
				try
				{
					mBluetoothSocket = mBluetoothDevice
							.createRfcommSocketToServiceRecord(mUuid);
				}
				catch( IOException e )
				{
					if( mBtEventListener != null )
						mBtEventListener.onBtConnectFail(mBluetoothDevice,
								e.getMessage());
				}
			}
			else
			{
				if( mBtEventListener != null )
					mBtEventListener.onBtConnectFail(null,
							"BluetoothDevice is null.");
			}
		}

		public void run()
		{
			if( mBluetoothAdapter != null )
				mBluetoothAdapter.cancelDiscovery();

			if( mBluetoothSocket != null )
			{
				try
				{
					mBluetoothSocket.connect();
				}
				catch( IOException connectException )
				{
					try
					{
						mBluetoothSocket.close();
					}
					catch( IOException closeException )
					{
					}

					if( mBtEventListener != null )
						mBtEventListener.onBtConnectFail(mBluetoothDevice,
								connectException.getMessage());
					mConnectThread = null;
					return;
				}

				// manageConnectedSocket(mmSocket);
				mConnectedThread = new ConnectedThread(mBluetoothSocket);
				if( mConnectedThread.isInitOk() )
				{
					mConnectedThread.start();

					if( mBtEventListener != null )
						mBtEventListener.onBtConnected(mBluetoothDevice);
				}
			}
			else
			{
				mBtEventListener.onBtConnectFail(mBluetoothDevice,
						"BluetothSocket is null.");
			}

			mConnectThread = null;
		}

		public void cancel()
		{
			/*
			try
			{
				mBluetoothSocket.close();
				if( mBtEventListener != null )
					mBtEventListener.onBtDisconnected(mBluetoothDevice);
			}
			catch( IOException e )
			{
			}
			mConnectThread = null;
			*/
		}
	}

	public void connectToBluetoothDevice( String address, UUID uuid )
	{
		try
		{
    		final BluetoothDevice DEVICE = getBluetoothDevice(address);
    		if( DEVICE != null )
    			connectToBluetoothDevice(DEVICE, uuid);
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

	public void connectToBluetoothDevice( BluetoothDevice device, UUID uuid )
	{
		mBluetoothDevice = device;
		mUuid = uuid;
		disconnect();
		mConnectThread = new ConnectThread();
		mConnectThread.start();
	}

	public void disconnect()
	{
		// ----------
		try
		{
			if( mConnectThread != null )
			{
				mConnectThread.cancel();
				mConnectThread.stop();
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
		mConnectThread = null;

		// ----------
		try
		{
			if( mConnectedThread != null )
			{
				mConnectedThread.cancel();
				mConnectedThread.stop();
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
		mConnectedThread = null;
	}

	// ------------- For Manage bluetooth
	private class ConnectedThread extends Thread
	{
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;
		private boolean mInitOk;
		
		public ConnectedThread( BluetoothSocket socket )
		{
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;
			mPacketParser.reset();
			mInitOk = false;
			try
			{
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
				mInitOk = true;
			}
			catch( IOException e )
			{
				mBtEventListener.onBtConnectFail(mBluetoothDevice,
				"BluetothSocket is null.");
				mInitOk = false;
			}
			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		public boolean isInitOk()
		{
			return mInitOk;
		}
		
		public void run()
		{
			byte[] buffer = new byte[1024];
			int bytes;
			while( mInitOk )
			{
				try
				{
					bytes = mmInStream.read(buffer);

					// BluetoothDevice device = mmSocket.getRemoteDevice();

					if( mDbgOutStream != null )
						mDbgOutStream.write(buffer, 0, bytes);

					mPacketParser.pushPacket(buffer, bytes);

					// if( mBtEventListener != null )
					// mBtEventListener.onBtDataRecv(buffer, bytes);
					mHandlerNoti
							.sendEmptyMessage(BluetoothActivity.MSG_BT_DATA_RECV);
				}
				catch( IOException e )
				{
					if( mBtEventListener != null )
						mBtEventListener.onBtDataTransException(
								mBluetoothDevice,
								"[Bluetooth Socket] Read Fail : "
										+ e.getMessage());
					break;
				}
			}
		}

		/* Call this from the main Activity to send data to the remote device */
		public void write( byte[] bytes )
		{
			try
			{
				mmOutStream.write(bytes);
				if( mBtEventListener != null )
					mBtEventListener.onBtDataSent(bytes);
			}
			catch( IOException e )
			{
				if( mBtEventListener != null )
					mBtEventListener
							.onBtDataTransException(
									mBluetoothDevice,
									"[Bluetooth Socket] Write Fail : "
											+ e.getMessage());
			}
		}

		/* Call this from the main Activity to shutdown the connection */
		public void cancel()
		{
			try
			{
				mmSocket.close();
			}
			catch( IOException e )
			{

			}
		}
	}

	public void sendData( byte[] bytes )
	{
		if( mConnectedThread != null )
		{
			Log.d(BluetoothActivity.TAG, "Send : " + new String(bytes));
			mConnectedThread.write(bytes);
		}
		else
		{
			if( mBtEventListener != null )
				mBtEventListener
						.onBtDataTransException(mBluetoothDevice,
								"[Bluetooth Socket] Write Fail : Bluetooth Thread is not running.");
		}
	}

	public boolean isTryingConnect()
	{
		return mConnectThread != null;
	}
}
