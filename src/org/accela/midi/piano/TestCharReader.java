package org.accela.midi.piano;

import javax.sound.midi.MidiUnavailableException;

public class TestCharReader
{
	public static void main(String[] args)
	{
		final Piano piano = new Piano();
		try
		{
			piano.open();
		}
		catch (MidiUnavailableException ex)
		{
			ex.printStackTrace();
		}
	}
}
