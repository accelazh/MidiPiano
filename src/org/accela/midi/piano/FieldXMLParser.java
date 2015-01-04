package org.accela.midi.piano;

import java.lang.reflect.*;
import java.util.*;
import org.w3c.dom.*;

public class FieldXMLParser
{
	public static <T extends Object> void readXML(T aim, Document doc,
			Element root) throws IllegalXMLFormatException
	{
		if (null == aim)
		{
			throw new IllegalArgumentException("aim should not be null");
		}
		if (null == doc)
		{
			throw new IllegalArgumentException("doc should not be null");
		}
		if (null == root)
		{
			throw new IllegalArgumentException("root should not be null");
		}

		List<Field> unfindFields = new LinkedList<Field>();
		for (Field f : aim.getClass().getDeclaredFields())
		{
			if (Modifier.isStatic(f.getModifiers())
					|| Modifier.isFinal(f.getModifiers()))
			{
				continue;
			}

			boolean find = false;

			NodeList children = root.getChildNodes();
			for (int i = 0; i < children.getLength(); i++)
			{
				Node child = children.item(i);
				if (!(child instanceof Element))
				{
					continue;
				}

				Element childElement = (Element) child;
				if (!childElement.getTagName().equals(f.getName()))
				{
					continue;
				}

				String content = childElement.getTextContent();
				Object value = null;

				try
				{
					if (f.getType().equals(int.class)
							|| f.getType().equals(Integer.class))
					{
						value = Integer.parseInt(content);
					}
					else if (f.getType().equals(boolean.class)
							|| f.getType().equals(Boolean.class))
					{
						value = Boolean.parseBoolean(content);
					}
					else if (f.getType().equals(long.class)
							|| f.getType().equals(Long.class))
					{
						value = Long.parseLong(content);
					}
					else if (f.getType().equals(short.class)
							|| f.getType().equals(Short.class))
					{
						value = Short.parseShort(content);
					}
					else if (f.getType().equals(char.class)
							|| f.getType().equals(Character.class))
					{
						value = content.length() > 0 ? content.charAt(0) : '\0';
					}
					else if (f.getType().equals(double.class)
							|| f.getType().equals(Double.class))
					{
						value = Double.parseDouble(content);
					}
					else if (f.getType().equals(float.class)
							|| f.getType().equals(Float.class))
					{
						value = Float.parseFloat(content);
					}
					else if (f.getType().equals(byte.class)
							|| f.getType().equals(Byte.class))
					{
						value = Byte.parseByte(content);
					}
					else if (f.getType().equals(String.class))
					{
						value = String.valueOf(content);
					}
					else
					{
						// 如果这个field不是基本类型的话，那么就忽略它
						find = true;
						break;
					}
				}
				catch (NumberFormatException ex)
				{
					continue;
				}

				f.setAccessible(true);
				try
				{
					f.set(aim, value);
				}
				catch (IllegalArgumentException ex)
				{
					ex.printStackTrace();
					assert (false);
				}
				catch (IllegalAccessException ex)
				{
					ex.printStackTrace();
					assert (false);
				}

				find = true;

			}// inner for

			if (!find)
			{
				unfindFields.add(f);
			}

		}// outer for

		if (!unfindFields.isEmpty())
		{
			String fieldNames = "";
			for (Field f : unfindFields)
			{
				fieldNames += f.getName() + ", ";
			}

			fieldNames = fieldNames.substring(0, fieldNames.length() - 2);

			throw new IllegalXMLFormatException("properties for "
					+ fieldNames
					+ " not found");
		}
	}

	public static <T extends Object> void writeXML(T aim, Document doc,
			Element root)
	{
		if (null == aim)
		{
			throw new IllegalArgumentException("aim should not be null");
		}
		if (null == doc)
		{
			throw new IllegalArgumentException("doc should not be null");
		}
		if (null == root)
		{
			throw new IllegalArgumentException("root should not be null");
		}

		for (Field f : aim.getClass().getDeclaredFields())
		{
			if (Modifier.isStatic(f.getModifiers())
					|| Modifier.isFinal(f.getModifiers()))
			{
				continue;
			}
			if (!(f.getType().equals(int.class)
					|| f.getType().equals(Integer.class)
					|| f.getType().equals(long.class)
					|| f.getType().equals(Long.class)
					|| f.getType().equals(short.class)
					|| f.getType().equals(Short.class)
					|| f.getType().equals(double.class)
					|| f.getType().equals(Double.class)
					|| f.getType().equals(float.class)
					|| f.getType().equals(Float.class)
					|| f.getType().equals(byte.class)
					|| f.getType().equals(Byte.class)
					|| f.getType().equals(char.class)
					|| f.getType().equals(Character.class)
					|| f.getType().equals(boolean.class)
					|| f.getType().equals(Boolean.class) || f.getType().equals(
					String.class)))
			{
				continue;
			}

			f.setAccessible(true);
			Element fieldNode = doc.createElement(f.getName());
			try
			{
				fieldNode.setTextContent(f.get(aim).toString());
			}
			catch (DOMException ex)
			{
				ex.printStackTrace();
				assert (false);
			}
			catch (IllegalArgumentException ex)
			{
				ex.printStackTrace();
				assert (false);
			}
			catch (IllegalAccessException ex)
			{
				ex.printStackTrace();
				assert (false);
			}
			root.appendChild(fieldNode);
		}
	}
}
