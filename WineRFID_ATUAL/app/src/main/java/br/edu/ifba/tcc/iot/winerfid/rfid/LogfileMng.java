package br.edu.ifba.tcc.iot.winerfid.rfid;

import java.io.File;
import java.io.FileOutputStream;

public class LogfileMng
{
	private boolean mInitOk;
	private FileOutputStream mFileOut;
	
	public LogfileMng()
	{
		
	}
	
	public void initialize()
	{
		try
		{
			mFileOut = new FileOutputStream( new File( "/sdcard/rfid.log" ) );
			mInitOk = true;
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
			mInitOk = false;
		}
	}
	
	public void finalize()
	{
		try
		{
			if( mFileOut != null )
				mFileOut.close();
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}
	
	public void write( byte[] buffer )
	{
		if( mInitOk == false )
			initialize();
		
		try
		{
			if( mFileOut != null )
				mFileOut.write( buffer );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}
}
