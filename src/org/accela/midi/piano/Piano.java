package org.accela.midi.piano;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import org.accela.midi.groove.Channel;
import org.accela.midi.groove.Groove;
import org.accela.midi.groove.Instrument;
import org.accela.midi.groove.Note;

public class Piano
{
	private Groove groove;

	private Configuration conf;

	public Piano()
	{
		groove = new Groove();
		conf = new Configuration();
	}

	public void close()
	{
		groove.close();
	}

	public boolean isOpen()
	{
		return groove.isOpen();
	}

	public void open() throws MidiUnavailableException
	{
		groove.open();
	}

	public void loadConf(InputStream in) throws IOException,
			IllegalXMLFormatException
	{
		conf.load(in);
		refreshGrooveConf();
	}

	public void note(boolean noteOn, char keyChar)
	{
		int noteData = 0;
		if (conf.getKeyBindingConf().contains(keyChar))
		{
			noteData = conf.getKeyBindingConf().get(keyChar);
		}
		else
		{
			noteData = 0;
		}

		Channel[] channels = groove.getChannels();
		ChannelConf defConf = conf.getDefaultChannelConf();
		for (int i = 0; i < channels.length; i++)
		{
			ChannelConf cConf = conf.getChannelConf(i);
			if (null == cConf)
			{
				cConf = defConf;
			}

			if (!cConf.isActive())
			{
				continue;
			}

			Note note = new Note(noteData, cConf.getVelocity(), 0, noteOn, i);
			try
			{
				groove.note(note);
			}
			catch (InvalidMidiDataException ex)
			{
				ex.printStackTrace();
			}
		}

	}

	private void refreshGrooveConf()
	{
		Channel[] channels = groove.getChannels();
		for (int i = 0; i < channels.length; i++)
		{
			ChannelConf cconf = conf.getChannelConf(i);
			if (null == cconf)
			{
				refreshGrooveChannel(channels[i], conf.getDefaultChannelConf());
			}
			else
			{
				refreshGrooveChannel(channels[i], cconf);
			}
		}
	}

	private void refreshGrooveChannel(Channel channel, ChannelConf conf)
	{
		channel.setChannelPressure(conf.getChanelPressure());

		Instrument[] instruments = groove.getInstruments();
		if (conf.getInstrument() < instruments.length)
		{
			channel.setInstrument(instruments[conf.getInstrument()]);
		}

		channel.setMono(conf.isMomo());
		channel.setMute(conf.isMute());
		channel.setOmni(conf.isOmni());
		channel.setPitchBend(conf.getPitchBend());
		channel.setSolo(conf.isSolo());

	}

	public Groove getGroove()
	{
		return groove;
	}

}
