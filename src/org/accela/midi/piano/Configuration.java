package org.accela.midi.piano;

import java.util.*;
import java.io.*;

import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class Configuration
{
	private KeyBindingConf bindConf;
	private ChannelConf defaultChannelConf;
	private List<ChannelConf> channelConfs;

	public Configuration()
	{
		bindConf = new KeyBindingConf();
		defaultChannelConf = new ChannelConf();
		channelConfs = new LinkedList<ChannelConf>();

		clear();
	}

	public KeyBindingConf getKeyBindingConf()
	{
		return bindConf;
	}

	public ChannelConf getDefaultChannelConf()
	{
		return defaultChannelConf;
	}

	public void addChannelConf(ChannelConf cc)
	{
		if (null == cc)
		{
			throw new IllegalArgumentException("cc should not be null");
		}
		if (containsChannelConf(cc.getNumber()))
		{
			throw new IllegalArgumentException(
					"already contains a channel conf having the same number with cc");
		}

		channelConfs.add(cc);
	}

	public ChannelConf removeChannelConf(int number)
	{
		if (number < 0)
		{
			throw new IllegalArgumentException("number should not be negative");
		}

		ChannelConf removed = getChannelConf(number);
		if (removed != null)
		{
			channelConfs.remove(removed);
		}

		return removed;
	}

	public ChannelConf getChannelConf(int number)
	{
		if (number < 0)
		{
			throw new IllegalArgumentException("number should not be negative");
		}

		for (int i = 0; i < channelConfs.size(); i++)
		{
			if (channelConfs.get(i).getNumber() == number)
			{
				return channelConfs.get(i);
			}
		}

		return null;
	}

	public boolean containsChannelConf(int number)
	{
		if (number < 0)
		{
			throw new IllegalArgumentException("number should not be negative");
		}

		return getChannelConf(number) != null;
	}

	public int numChannelConfs()
	{
		return channelConfs.size();
	}

	public void clearChannelConfs()
	{
		channelConfs.clear();
	}

	public ChannelConf[] getChannelConfs()
	{
		return channelConfs.toArray(new ChannelConf[0]);
	}

	// ============================================================================

	public void clear()
	{
		bindConf.setMinNoteData(0);
		bindConf.setMaxNoteData(0);
		bindConf.clear();

		defaultChannelConf.setActive(false);
		defaultChannelConf.setChanelPressure(0);
		defaultChannelConf.setInstrument(0);
		defaultChannelConf.setMomo(false);
		defaultChannelConf.setMute(false);
		defaultChannelConf.setNumber(0);
		defaultChannelConf.setOmni(false);
		defaultChannelConf.setPitchBend(0);
		defaultChannelConf.setSolo(false);
		defaultChannelConf.setVelocity(90);

		clearChannelConfs();
	}

	// ===========================================================================

	public void load(InputStream in) throws IOException,
			IllegalXMLFormatException
	{
		clear();

		DocumentBuilderFactory domFactory = DocumentBuilderFactory
				.newInstance();
		domFactory.setNamespaceAware(true);
		DocumentBuilder builder = null;
		try
		{
			builder = domFactory.newDocumentBuilder();
		}
		catch (ParserConfigurationException ex)
		{
			ex.printStackTrace();
		}
		Document doc;
		try
		{
			doc = builder.parse(in);
		}
		catch (SAXException ex)
		{
			throw new IllegalXMLFormatException(ex);
		}
		Element root = doc.getDocumentElement();

		NodeList bindConfNodes = root.getElementsByTagName("bindConf");
		boolean succBindConf = false;
		for (int i = 0; i < bindConfNodes.getLength(); i++)
		{
			try
			{
				bindConf.readXML(doc, (Element) bindConfNodes.item(i));
			}
			catch (IllegalXMLFormatException ex)
			{
				continue;
			}

			succBindConf = true;
		}

		NodeList defaultChannelConfNodes = root
				.getElementsByTagName("defaultChannelConf");
		boolean succDefaultChannelConf = false;
		for (int i = 0; i < defaultChannelConfNodes.getLength(); i++)
		{
			try
			{
				defaultChannelConf.readXML(
						doc,
						(Element) defaultChannelConfNodes.item(i));
			}
			catch (IllegalXMLFormatException ex)
			{
				continue;
			}

			succDefaultChannelConf = true;
		}

		NodeList channelConfNodes = root.getElementsByTagName("channelConf");
		boolean succChannelConfs = false;
		for (int i = 0; i < channelConfNodes.getLength(); i++)
		{
			ChannelConf cc = new ChannelConf();
			try
			{
				cc.readXML(doc, (Element) channelConfNodes.item(i));
			}
			catch (IllegalXMLFormatException ex)
			{
				continue;
			}

			for (ChannelConf cc_inner : channelConfs)
			{
				if (cc_inner.getNumber() == cc.getNumber())
				{
					channelConfs.remove(cc_inner);
					break;
				}
			}

			channelConfs.add(cc);
			succChannelConfs = true;
		}

		if (!succBindConf)
		{
			throw new IllegalXMLFormatException("failed to read bindConf");
		}
		if (!succDefaultChannelConf)
		{
			throw new IllegalXMLFormatException(
					"failed to read defaultChannelConf");
		}
		if (!succChannelConfs)
		{
			// do nothing
		}
	}

	public void store(OutputStream out) throws IOException
	{
		if (null == out)
		{
			throw new IllegalArgumentException("out should not be null");
		}

		// 生成doc文档
		DocumentBuilderFactory domFactory = DocumentBuilderFactory
				.newInstance();
		domFactory.setNamespaceAware(true);
		DocumentBuilder builder = null;
		try
		{
			builder = domFactory.newDocumentBuilder();
		}
		catch (ParserConfigurationException ex)
		{
			ex.printStackTrace();
			assert (false);
		}
		Document doc = builder.newDocument();

		Element root = doc.createElement("configuration");
		doc.appendChild(root);

		Element bindConfNode = doc.createElement("bindConf");
		bindConf.writeXML(doc, bindConfNode);
		root.appendChild(bindConfNode);

		Element defaultChannelConfNode = doc
				.createElement("defaultChannelConf");
		defaultChannelConf.writeXML(doc, defaultChannelConfNode);
		root.appendChild(defaultChannelConfNode);

		for (ChannelConf cc : channelConfs)
		{
			Element channelConfNode = doc.createElement("channelConf");
			cc.writeXML(doc, channelConfNode);
			root.appendChild(channelConfNode);
		}

		// 将doc文档写入输出流
		Transformer t = null;
		TransformerFactory tf = TransformerFactory.newInstance();
		try
		{
			t = tf.newTransformer();
		}
		catch (TransformerConfigurationException ex)
		{
			ex.printStackTrace();
			assert (false);
		}
		catch (TransformerFactoryConfigurationError ex)
		{
			ex.printStackTrace();
			assert (false);
		}
		t.setOutputProperty(OutputKeys.INDENT, "yes");
		try
		{
			t.transform(new DOMSource(doc), new StreamResult(out));
		}
		catch (TransformerException ex)
		{
			throw new IOException(ex);
		}
	}

	public void loadDefault()
	{
		clear();

		final String KEY_SORTED_BY_NOTE_DATA = "ZzAaQq!1XxSsWw@2CcDdEe#3VvFfRr$4BbGgTt%5Yy^6&7 HhUu*8NnJjIi(9MmKkOo)0<,LlPp_->.:;{[+=?/\"'}]|\\";
		bindConf.setMinNoteData(23);
		bindConf.setMaxNoteData(115);
		assert (bindConf.getMaxNoteData() - bindConf.getMinNoteData() + 1 == KEY_SORTED_BY_NOTE_DATA
				.length());
		for (int i = bindConf.getMinNoteData(); i <= bindConf.getMaxNoteData(); i++)
		{
			bindConf.put(KEY_SORTED_BY_NOTE_DATA.charAt(i - bindConf.getMinNoteData()), i);
		}
		bindConf.put('`', bindConf.get(' '));

		defaultChannelConf.setActive(false);
		defaultChannelConf.setChanelPressure(0);
		defaultChannelConf.setInstrument(0);
		defaultChannelConf.setMomo(false);
		defaultChannelConf.setMute(false);
		defaultChannelConf.setNumber(0);
		defaultChannelConf.setOmni(false);
		defaultChannelConf.setPitchBend(0);
		defaultChannelConf.setSolo(false);
		defaultChannelConf.setVelocity(90);

		for (int i = 0; i < 16; i++)
		{
			ChannelConf cc = new ChannelConf();

			cc.setActive(false);
			cc.setChanelPressure(0);
			cc.setInstrument(0);
			cc.setMomo(false);
			cc.setMute(false);
			cc.setNumber(i); // not the same with default
			cc.setOmni(false);
			cc.setPitchBend(0);
			cc.setSolo(false);
			cc.setVelocity(90);

			addChannelConf(cc);
		}
		getChannelConf(0).setActive(true);

	}

	// ============================测试==================================

	public static void main(String[] args) throws IOException,
			ParserConfigurationException, TransformerFactoryConfigurationError,
			TransformerException, IllegalXMLFormatException
	{
		Configuration conf = new Configuration();
		conf.loadDefault();
		FileOutputStream out = new FileOutputStream("conf.xml");
		conf.store(out);

		Configuration conf2 = new Configuration();
		conf2.load(new FileInputStream("conf.xml"));
		System.out.println(conf2.getKeyBindingConf().size());
		assert (conf2.getKeyBindingConf().size() == 94);
	}

}
