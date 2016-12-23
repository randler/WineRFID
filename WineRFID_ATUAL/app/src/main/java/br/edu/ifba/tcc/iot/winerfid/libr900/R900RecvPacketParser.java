package br.edu.ifba.tcc.iot.winerfid.libr900;

import android.util.Log;

import java.util.StringTokenizer;

public class R900RecvPacketParser
{
	private StringBuilder mPacket = new StringBuilder();
	private char[] mCharBuff = null;
	private int mCharBuffSize = 0;
	
	synchronized public void reset()
	{
		mPacket.setLength(0);
	}

	synchronized public void pushPacket( byte[] buffer, int len )
	{
		if( mCharBuffSize < len )
		{
			mCharBuffSize = ( len << 1 );
			mCharBuff = new char[ mCharBuffSize ];
		}
		
		for( int i = 0; i < len; ++i )
			mCharBuff[ i ] = (char)( buffer[ i ] & 0xff );
		mPacket.append( mCharBuff, 0, len );
	}

	synchronized public String popPacket( int offset, int len )
	{
		String pop = mPacket.substring(offset, offset + len);
		mPacket.delete(0, offset + len);
		return pop;
	}

	synchronized public String popPacket()
	{
		final String STR_PACKET = mPacket.toString();
		final String DELIMETER = R900Protocol.getDelimeter();

		int cmdIndex = STR_PACKET.indexOf("$>");
		if( cmdIndex >= 0 )
		{
			if( STR_PACKET.replace("\n", "").indexOf("$>") == 0 )
				return popPacket(cmdIndex, 2);
		}

		StringTokenizer st = new StringTokenizer(STR_PACKET, DELIMETER);
		if( st.hasMoreTokens() )
		{
			final String str = st.nextToken();
			if( str != null && str.length() > 0 )
			{
				if( str.length() + DELIMETER.length() > mPacket.length() )
					return null;
				if( str.length() == 1 || str.length() > 40 )
					Log.d("kueen108", "Somethins is wrong!!!");
				mPacket.delete(
						0,
						Math.min(mPacket.length(),
								str.length() + DELIMETER.length()));
				return str;
			}
		}
		return null;
	}
}
