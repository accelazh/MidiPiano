package org.accela.midi.piano;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface XMLParsable
{
	public void readXML(Document doc, Element root) throws IllegalXMLFormatException;

	public void writeXML(Document doc, Element root);
}
