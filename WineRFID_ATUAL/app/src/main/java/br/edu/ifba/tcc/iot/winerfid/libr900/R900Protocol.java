package br.edu.ifba.tcc.iot.winerfid.libr900;

/***********************************************************************************
* R900Protocol revision history                                                    *
*************+*************+********+***********************************************
* 2012.12.12	ver 1.0.0  	  eric     1. Generated(First release)                 *
************************************************************************************/

public class R900Protocol
{
	public static final byte[] NULL_1 =
	{ 0x0d, 0x0a };
	public static final byte[] NULL_2 =
	{ 0x0d };
	public static final byte[] OPEN_INTERFACE_1 = new byte[]
	{ 0x0d, 0x0a, 0x0d, 0x0a, 0x0d, 0x0a, 0x0d, 0x0a };
	public static final byte[] OPEN_INTERFACE_2 = new byte[]
	{ 0x0d, 0x0d, 0x0d, 0x0d, 0x0d, 0x0d, 0x0d, 0x0d };

	public static final byte[] BYE =
	{ 'b', 'y', 'e', 0x0d, 0x0a };
	public static final int SKIP_PARAM = 0xffffffff;

	public static int N_TYPE = 1;

	// ---
	public static final String CMD_INVENT = "I";
	public static final String CMD_STOP = "s";
	public static final String CMD_GET_VERSION = "ver";
	public static final String CMD_SET_DEF_PARAM = "Default";
	public static final String CMD_INVENT_PARAM = "Iparam";
	public static final String CMD_GET_PARAM = "g";
	public static final String CMD_SEL_MASK = "M";
	public static final String CMD_SET_TX_POWER = "Txp";
	public static final String CMD_GET_MAX_POWER = "Maxp";
	public static final String CMD_SET_TX_CYCLE = "Txc";
	public static final String CMD_CHANGE_CH_STATE = "Chs";
	public static final String CMD_SET_COUNTRY = "Cc";
	public static final String CMD_GET_COUNTRY_CAP = "ccap";
	public static final String CMD_READ_TAG_MEM = "R";
	public static final String CMD_WRITE_TAG_MEM = "W";
	public static final String CMD_KILL_TAG = "Kill";
	public static final String CMD_LOCK_TAG_MEM = "Lock";
	public static final String CMD_SET_LOCK_TAG_MEM = "lockperm";
	public static final String CMD_PAUSE_TX = "Pause";
	public static final String CMD_HEART_BEAT = "Online";
	public static final String CMD_STATUS_REPORT = "alert";
	public static final String CMD_INVENT_REPORT_FORMAT = "Ireport";
	public static final String CMD_SYSTEM_TIME = "Time";
	public static final String CMD_DISLINK = "bye";

	public static final String CMD_UPLOAD_TAG_DATA = "Br.upl";
	public static final String CMD_CLEAR_TAG_DATA = "Br.clrlist";
	public static final String CMD_ALERT_READER_STATUS = "Br.alert";
	public static final String CMD_GET_STATUS_WORD = "Br.sta";
	public static final String CMD_SET_BUZZER_VOL = "Br.vol";
	public static final String CMD_BEEP = "Br.beep";
	public static final String CMD_SET_AUTO_POWER_OFF_DELAY = "Br.autooff";
	public static final String CMD_GET_BATT_LEVEL = "Br.batt";
	public static final String CMD_REPORT_BATT_STATE = "Br.reportbatt";
	public static final String CMD_TURN_READER_OFF = "Br.off";
	public static final String CMD_READER_PROPRIETARY = "Br.bt.config";
	//<--eric 2013.10.18
	public static final String CMD_GET_BT_MAC_ADDRESS = "Br.bt.mac";
	//--> eric 2013.10.18

	public static final String tagErrorCodeToString( int code )
	{
		switch( code )
		{
			case 0x00:
				return "general error";
			case 0x03:
				return "specified memory location does not exist or the PC value is not supported by the tag";
			case 0x04:
				return "specified memory location is locked and/or permalocked and is not writeable";
			case 0x0B:
				return "tag has insufficient power to perform the memory write";
			case 0x0F:
				return "tag does not support error-specific codes";
		}
		return "Unknown error";
	}

	public static final String moduleErrorCodeToString( int code )
	{
		switch( code )
		{
			case 0x01:
				return "Read after write verify failed.";
			case 0x02:
				return "Problem transmitting tag command.";
			case 0x03:
				return "CRC error on tag response to a write.";
			case 0x04:
				return "CRC error on the read packet when verifying the write.";
			case 0x05:
				return "Maximum retry's on the write exceeded.";
			case 0x06:
				return "Failed waiting for read data from tag, possible timeout.";
			case 0x07:
				return "Failure requesting a new tag handle.";
			case 0x0A:
				return "Error waiting for tag response, possible timeout.";
			case 0x0B:
				return "CRC error on tag response to a kill.";
			case 0x0C:
				return "Problem transmitting 2nd half of tag kill.";
			case 0x0D:
				return "Tag responded with an invalid handle on first kill command.";
			case 0x0F:
				return "Bad Access Password.";
		}
		return "Internal Use";
	}

	public static void setType( int type )
	{
		if( type == 1 )
			N_TYPE = 1;
		else
			N_TYPE = 2;
	}

	public static int getTypeSize()
	{
		if( N_TYPE == 1 )
			return 2;
		return 1;
	}

	public static final String getDelimeter()
	{
		if( N_TYPE == 1 )
			return "\r\n";
		return "\r";
	}

	public static final byte[] getType()
	{
		if( N_TYPE == 1 )
			return NULL_1;
		return NULL_2;
	}

	public static final byte[] makeProtocol( String cmd, int[] param )
	{
		StringBuilder protocol = new StringBuilder();
		protocol.append(cmd);

		if( param != null && param.length > 0 )
		{
			for( int i = 0; i < param.length; ++i )
			{
				protocol.append(',');
				if( param[ i ] != SKIP_PARAM )
					protocol.append(param[ i ]);
			}
		}

		return string2bytes( protocol.toString() );
	}
	
	public static final byte[] makeProtocol( String cmd )
	{
		StringBuilder protocol = new StringBuilder();
		protocol.append(cmd);
		return string2bytes( protocol.toString() );
	}
	
	//<-- eric 2012.12.12
	public static final byte[] makeProtocol( String cmd, int[] param, String param1, String param2)
	{
		StringBuilder protocol = new StringBuilder();
		protocol.append(cmd);
			
		// param
		if( param != null && param.length > 0)
		{
			for( int i=0; i<param.length; ++i)
			{
				protocol.append(',');
				if( param[i] != SKIP_PARAM)
					protocol.append(param[i]);				
			}		
		}
			
		// param1
		protocol.append( "," );
	
		if( param1 != null )
			protocol.append( param1 );
			
		// param2
		protocol.append( "," );
		
		if( param1 != null )
			protocol.append( param2 );	
		
		return string2bytes( protocol.toString() );
	}	
	//--> eric 2012.12.12
	
	public static final byte[] makeProtocol( String cmd, String[] options )
	{
		StringBuilder protocol = new StringBuilder();
		protocol.append(cmd);

		if( options != null && options.length > 0)
		{
			for( int i = 0; i < options.length; ++i )
			{
				protocol.append( "," );
				if( options[ i ] != null )
					protocol.append( options[ i ] );
			}
		}
		
		return string2bytes( protocol.toString() );
	}
	
	public static final byte[] makeProtocol( String cmd, int[] param, String[] options, int[] param2 )
	{
		StringBuilder protocol = new StringBuilder();
		protocol.append(cmd);

		if( param != null && param.length > 0 )
		{
			for( int i = 0; i < param.length; ++i )
			{
				protocol.append(',');
				if( param[ i ] != SKIP_PARAM )
					protocol.append(param[ i ]);
			}
		}
		
		
		if( options != null )
		{
			for( int i = 0; i < options.length; ++i )
			{
				protocol.append( "," );
				if( options[ i ] != null )
					protocol.append( options[ i ] );
			}
		}
		
		if( param2 != null && param2.length > 0 )
		{
			for( int i = 0; i < param2.length; ++i )
			{
				protocol.append(',');
				if( param2[ i ] != SKIP_PARAM )
					protocol.append(param2[ i ]);
			}
		}
		
		return string2bytes( protocol.toString() );
	}
	
	public static final byte[] makeProtocol( String cmd, int[] param, String option, int[] param2 )
	{
		StringBuilder protocol = new StringBuilder();
		protocol.append(cmd);

		if( param != null && param.length > 0 )
		{
			for( int i = 0; i < param.length; ++i )
			{
				protocol.append(',');
				if( param[ i ] != SKIP_PARAM )
					protocol.append(param[ i ]);
			}
		}
		
		protocol.append( "," );
		if( option != null )
			protocol.append( option );
		
		if( param2 != null && param2.length > 0 )
		{
			for( int i = 0; i < param2.length; ++i )
			{
				protocol.append(',');
				if( param2[ i ] != SKIP_PARAM )
					protocol.append(param2[ i ]);
			}
		}
		
		return string2bytes( protocol.toString() );
	}
	
	public static final byte[] makeProtocol( String cmd, String option, int[] param2 )
	{
		StringBuilder protocol = new StringBuilder();
		protocol.append(cmd);

		protocol.append( "," );
		if( option != null )
			protocol.append( option );
		
		if( param2 != null && param2.length > 0 )
		{
			for( int i = 0; i < param2.length; ++i )
			{
				protocol.append(',');
				if( param2[ i ] != SKIP_PARAM )
					protocol.append(param2[ i ]);
			}
		}
		
		return string2bytes( protocol.toString() );
	}
	
	public static final byte[] string2bytes( String str )
	{
		char[] charProtocol = str.toCharArray();
		byte[] byteProtocol = new byte[charProtocol.length + getTypeSize()];
		int index = 0;
		for( int i = 0; i < charProtocol.length; ++i, ++index )
			byteProtocol[ index ] = (byte) ( charProtocol[ i ] & 0xff );

		// ---
		for( int i = 0; i < getTypeSize(); ++i, ++index )
			byteProtocol[ index ] = getType()[ i ];

		return byteProtocol;
	}
}
