package org.accela.midi.piano;

import java.util.*;

import org.w3c.dom.*;

public class KeyBindingConf implements XMLParsable
{
	// key asc code to note.data
	private Map<Character, Integer> map;

	private int maxNoteData;

	private int minNoteData;

	public KeyBindingConf()
	{
		map = new HashMap<Character, Integer>();
		maxNoteData = 0;
		minNoteData = 0;
	}

	public void put(char key, int noteData)
	{
		if (contains(key))
		{
			throw new IllegalArgumentException("already contains keyCode: "
					+ key);
		}

		Integer ret = map.put(key, noteData);
		assert (null == ret);
	}

	public void remove(char key)
	{
		if (!contains(key))
		{
			throw new IllegalArgumentException("can't find keyCode: " + key);
		}

		Integer ret = map.remove(key);
		assert (ret != null);
	}

	public boolean contains(char key)
	{
		return map.containsKey(key);
	}

	public int get(char key)
	{
		if (!contains(key))
		{
			throw new IllegalArgumentException("can't find keyCode: " + key);
		}

		Integer ret = map.get(key);
		if (null == ret)
		{
			assert (false);
			throw new IllegalArgumentException("can't find keyCode: " + key);
		}

		return ret;
	}

	public void clear()
	{
		map.clear();
	}

	public boolean isEmpty()
	{
		return map.isEmpty();
	}
	
	public int size()
	{
		return map.size();
	}

	// ====================================================================

	public int getMaxNoteData()
	{
		return maxNoteData;
	}

	public void setMaxNoteData(int maxNoteData)
	{
		this.maxNoteData = maxNoteData;
	}

	public int getMinNoteData()
	{
		return minNoteData;
	}

	public void setMinNoteData(int minNoteData)
	{
		this.minNoteData = minNoteData;
	}

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

		NodeList children = root.getChildNodes();
		for (int i = 0; i < children.getLength(); i++)
		{
			Node child = children.item(i);
			if (!(child instanceof Element))
			{
				continue;
			}

			Element childElement = (Element) child;
			if (!childElement.getTagName().equals("map"))
			{
				continue;
			}

			NodeList entries = childElement.getChildNodes();
			for (int j = 0; j < entries.getLength(); j++)
			{
				Node entry = entries.item(j);
				if (!(entry instanceof Element))
				{
					continue;
				}

				Element entryElement = (Element) entry;
				if (!entryElement.getTagName().equals("entry"))
				{
					continue;
				}

				String key = "";
				String value = "";

				NodeList entryChildren = entry.getChildNodes();
				for (int k = 0; k < entryChildren.getLength(); k++)
				{
					Node entryChild = entryChildren.item(k);
					if (!(entryChild instanceof Element))
					{
						continue;
					}

					Element entryChildElement = (Element) entryChild;
					if (entryChildElement.getTagName().equals("key"))
					{
						key = entryChildElement.getTextContent();
					}
					if (entryChildElement.getTagName().equals("value"))
					{
						value = entryChildElement.getTextContent();
					}
				}

				char keyChar = key.length() > 0 ? key.charAt(0) : '\0';
				int noteData = 0;
				try
				{
					noteData = Integer.parseInt(value);
				}
				catch (NumberFormatException ex)
				{
					continue;
				}

				if (contains(keyChar))
				{
					remove(keyChar);
				}
				put(keyChar, noteData);
				
			}// inner for
		}// outer for
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

		Element mapNode = doc.createElement("map");
		root.appendChild(mapNode);

		for (Character c : map.keySet())
		{
			Element entry = doc.createElement("entry");
			mapNode.appendChild(entry);

			Element keyNode = doc.createElement("key");
			keyNode.setTextContent(c.toString());
			entry.appendChild(keyNode);

			Element valueNode = doc.createElement("value");
			valueNode.setTextContent(map.get(c).toString());
			entry.appendChild(valueNode);
		}
	}

}
