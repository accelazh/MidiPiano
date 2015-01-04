package org.accela.midi.piano;

import org.w3c.dom.*;

public class ChannelConf implements XMLParsable
{
	private int number;
	private boolean active;
	private int velocity;
	private int instrument;

	private boolean mute;
	private boolean momo;
	private boolean solo;
	
	private int chanelPressure;
	private int pitchBend;
	
	private boolean omni;

	public ChannelConf()
	{
		number = 0;
		active=false;

		momo = false;
		mute = false;
		solo = false;
		chanelPressure = 0;
		pitchBend = 0;
		omni = false;
		instrument = 0;
		velocity = 0;
	}

	public boolean isMomo()
	{
		return momo;
	}

	public void setMomo(boolean momo)
	{
		this.momo = momo;
	}

	public boolean isMute()
	{
		return mute;
	}

	public void setMute(boolean mute)
	{
		this.mute = mute;
	}

	public boolean isSolo()
	{
		return solo;
	}

	public void setSolo(boolean solo)
	{
		this.solo = solo;
	}

	public int getChanelPressure()
	{
		return chanelPressure;
	}

	public void setChanelPressure(int chanelPressure)
	{
		if (chanelPressure < 0)
		{
			throw new IllegalArgumentException(
					"chanelPressure should not be negative");
		}
		this.chanelPressure = chanelPressure;
	}

	public int getPitchBend()
	{
		return pitchBend;
	}

	public void setPitchBend(int pitchBend)
	{
		if (pitchBend < 0)
		{
			throw new IllegalArgumentException(
					"pitchBend should not be negative");
		}
		this.pitchBend = pitchBend;
	}

	public boolean isOmni()
	{
		return omni;
	}

	public void setOmni(boolean omni)
	{
		this.omni = omni;
	}

	public int getInstrument()
	{
		return instrument;
	}

	public void setInstrument(int instrument)
	{
		if (instrument < 0)
		{
			throw new IllegalArgumentException(
					"instrument should not be negative");
		}
		this.instrument = instrument;
	}

	public int getNumber()
	{
		return number;
	}

	public void setNumber(int number)
	{
		if (number < 0)
		{
			throw new IllegalArgumentException("number should not be negative");
		}
		this.number = number;
	}

	public int getVelocity()
	{
		return velocity;
	}

	public void setVelocity(int velocity)
	{
		if (velocity < 0)
		{
			throw new IllegalArgumentException(
					"velocity should not be negative");
		}
		this.velocity = velocity;
	}
	
	public boolean isActive()
	{
		return active;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}

	// ===========================================================================

	@Override
	public void readXML(Document doc, Element root)
			throws IllegalXMLFormatException
	{
		if (null == doc)
		{
			throw new IllegalArgumentException("doc should not be null");
		}
		if (null == root)
		{
			throw new IllegalArgumentException("root should not be null");
		}

		FieldXMLParser.readXML(this, doc, root);
	}

	@Override
	public void writeXML(Document doc, Element root)
	{
		if (null == doc)
		{
			throw new IllegalArgumentException("doc should not be null");
		}
		if (null == root)
		{
			throw new IllegalArgumentException("root should not be null");
		}

		FieldXMLParser.writeXML(this, doc, root);
	}

	
}
