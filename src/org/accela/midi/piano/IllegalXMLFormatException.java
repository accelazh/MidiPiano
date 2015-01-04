package org.accela.midi.piano;

public class IllegalXMLFormatException extends Exception
{
	private static final long serialVersionUID = 1L;
	
	public IllegalXMLFormatException()
	{
		super();
	}

	public IllegalXMLFormatException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public IllegalXMLFormatException(String message)
	{
		super(message);
	}

	public IllegalXMLFormatException(Throwable cause)
	{
		super(cause);
	}
	
}
