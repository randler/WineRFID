package br.edu.ifba.tcc.iot.winerfid.rfid;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.HashMap;

public class SoundManager
{
	private SoundPool mSoundPool;
	private HashMap mSoundPoolMap;
	private AudioManager mAudioManager;
	private Context mContext;
	
	public SoundManager()
	{
		
	}
	
	public void initSounds( Context theContext )
	{
		mContext = theContext;
		mSoundPool = new SoundPool( 4, AudioManager.STREAM_MUSIC, 0 );
		mSoundPoolMap = new HashMap<Integer, Integer>();
		mAudioManager = (AudioManager)mContext.getSystemService( Context.AUDIO_SERVICE );
	}
	
	public void addSound( int index, int soundId )
	{
		mSoundPoolMap.put( index, mSoundPool.load( mContext, soundId, index ) );
	}
	
	public void playSound( int index, float volume )
	{
		int streamVolume = mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
		mSoundPool.play( (Integer)mSoundPoolMap.get( index ), streamVolume * volume, streamVolume * volume, 1, 0, 1f );
	}
	
	public void playSound( int index )
	{
		playSound( index, 1.0f );
	}
	
	public void playLoopedSound( int index )
	{
		int streamVolume = mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
		mSoundPool.play( (Integer)mSoundPoolMap.get( index ), streamVolume, streamVolume, 1, -1, 1f );
	}
}
