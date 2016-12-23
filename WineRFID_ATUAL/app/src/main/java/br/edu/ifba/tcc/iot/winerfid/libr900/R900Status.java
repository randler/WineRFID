package br.edu.ifba.tcc.iot.winerfid.libr900;

public class R900Status
{
	/*
	 *  Library Version
	 */
	private static String mLibVer = "1.0.0.0"; 
	
	/*
	 *  Interface Mode
	 *  0 is Non detect module
	 *  1 is BT interface
	 *  2 is USB interface
	 */
	private static int InterfaceMode = 0;
	
	/*
	 *  Operation Mode
	 *  0 is Non operation
	 *  1 is operation
	 */
	private static int Operation = 0;
	
	/*
	 *  Battery Level
	 */
	private static int Battery = 0;
	
	/*
	 *  R900 Stop status
	 */
	private static boolean mStop = false;
	
	
	/*
	 *  R900 mStopped status    
	 */
	private static boolean mStopped = true;
	
	/*
	 *  R900 Mac ver
	 */
	private static byte[] mMacVer;
	
	/*
	 *  R900 Receive Mac ver flag
	 */
	private static boolean mMacVer_Flag = false;
	
	/*
	 *  R900 USB Connection
	 */
	private static boolean mConnect = false;
	
	
	/*
	 * single tag
	 */
	public static boolean mSingleTag = false;
	
	/*
	 * use Mask
	 */
	private static boolean mUseMask = false;
	
	/*
	 * timeout
	 */
	private static int mTimeOut = 0;
	
	/*
	 * mQuerySelected	
	 */
	protected static boolean mQuerySelected = false;
	
	
	//--> < class global variable >
	//--------------------------------------------------
	//--> < class method >
	
	/*
	 *  Get interface mode
	 */
	public static int getInterfaceMode()
	{
		return InterfaceMode;
	}
	
	/*
	 *  Set interface mode
	 */
	public static void setInterfaceMode(int mode)
	{
		if(mode > 0)
			R900Status.InterfaceMode = mode;
	}
	
	/*
	 *  Get operation mode
	 */
	public static int getOperationMode()
	{
		return Operation;
	}
	
	/*
	 *  Set operation mode  
	 */
	public static void setOperationMode(int value)
	{
		if(value > 0)
			R900Status.Operation = value;
	}
	
	
	/*
	 *  Get Battery level  
	 */
	public static int getBatteryLevel()
	{
		return Battery;
	}
	
	/*
	 *  Set Battery level
	 */
	public static void setBatteryLevel(int value)
	{
		if(value > 0)
			R900Status.Battery = value;
	}
	
	/*
	 *  Get stop status
	 */
	public static boolean getStop()
	{
		return mStop;
	}
	
	/*
	 *  Set stop status
	 */
	public static void setStop(boolean value)
	{
		R900Status.mStop = value;
	}
	
	/*
	 *  Get stopped status
	 */
	public static boolean getStopped()
	{
		return mStopped;
	}
	
	/*
	 *  Set stopped status  
	 */
	public static void setStopped(boolean value)
	{
		R900Status.mStopped = value;
	}
	
	/*
	 *  Get Mac Version
	 */
	public static byte[] getMacVer()
	{
		return mMacVer;
	}
	
	/*
	 *  Set Mac Version
	 */
	public static void setMacVer(byte[] value)
	{
		if(value != null)
			R900Status.mMacVer = value;
	}
	
	/*
	 *  Get Mac Version Flag
	 */
	public static boolean getMacVerFlag()
	{
		return mMacVer_Flag;
	}
	
	/*
	 *  Set Mac Version Flag
	 */
	public static void setMacVerFlag(boolean value)
	{
		R900Status.mMacVer_Flag = value;
	}
	
	/*
	 *  Get USB Connect status
	 */
	public static boolean getConnect()
	{
		return mConnect;
	}
	
	/*
	 *  Set USB Connect status
	 */
	public static void setConnect(boolean value)
	{
		R900Status.mConnect = value;
	}
	
	/*
	 * Get single tag
	 */
	public static boolean getSingleTag()
	{
		return mSingleTag;
	}
	
	/*
	 * Set single tag
	 */
	public static void setSingleTag(boolean value)
	{
		R900Status.mSingleTag = value;
	}
	
	/*
	 * Get use mask
	 */
	public static boolean getUseMask()
	{
		return mUseMask;
	}
	
	/*
	 * Set use mask
	 */
	public static void setUseMask(boolean value)
	{
		R900Status.mUseMask = value;
	}
	
	/*
	 * Get time out
	 */
	public static int getTimeOut()
	{
		return mTimeOut;
	}
	
	/*
	 * set time out
	 */
	public static void setTimeOut(int value)
	{
		R900Status.mTimeOut = value;
	}
	
	/*
	 * get query selected
	 */
	public static boolean getQuerySelected()
	{
		return mQuerySelected;
	}
	
	/*
	 * set query selected 
	 */
	public static void setQuerySelected(boolean value)
	{
		R900Status.mQuerySelected = value;
	}
	
	public static String getLibraryVersion()
	{
		return mLibVer;
	}
	
	public static class LockPattern
	{
		/*
		 * public short lockMask; public short lockEnable;
		 */
		public boolean enableUser;
		public boolean enableTid;
		public boolean enableUii;
		public boolean enableAcsPwd;
		public boolean enableKillPwd;

		public boolean indexUser;
		public boolean indexTid;
		public boolean indexUii;
		public boolean indexAcsPwd;
		public boolean indexKillPwd;

		public boolean lockPerma;
	}
	
}