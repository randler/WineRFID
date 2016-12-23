package br.edu.ifba.tcc.iot.winerfid.rfid;

public class MaskActivity {
	public static boolean UseMask = false;
	public static int Type;
	public static String Tag;
	public static class SelectMask
	{
		public int Bank;
		public int Offset;
		public int Bits;
		public String Pattern;
		public String TagId;
	}
	public static SelectMask SelMask = new SelectMask();
	
	public static SelectMask getSelectMask()
	{
		SelectMask selMask = new SelectMask();
		if( Type == 0 )
		{
			selMask.Bank = 1;//0;
			selMask.Offset = 16;//SelMask.Offset;
			
			final String pattern = selMask.Pattern = SelMask.TagId;
			if( pattern != null )
			{
				final int LEN = pattern.length();
				selMask.Bits = LEN * 4;
			}
			else
			{
				selMask.Bits = 0;
				selMask.Pattern = null;
			}
		}
		else
		{
			if( SelMask.Bank == 4/*0*/ )
			{
				selMask.Bits = 0;
			}
			else
			{
				selMask.Bank = SelMask.Bank;
				selMask.Offset = SelMask.Offset;
				
				final String pattern = selMask.Pattern = SelMask.Pattern;
				if( pattern != null )
				{
					final int LEN = pattern.length();
					selMask.Bits = LEN * 4;
				}
				else
				{
					selMask.Bits = 0;
					selMask.Pattern = null;
				}
			}
		}
		return selMask;
	}
	
	public static void clearSelectMask()
	{
		UseMask = false;
		Type = 0;
		SelMask.Bank = 4;
		SelMask.Bits = 0;
		SelMask.Offset = 0x10;
	}
	

}
