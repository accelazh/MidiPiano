package org.accela.midi.piano;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.HashSet;
import java.util.Set;

import javax.sound.midi.MidiUnavailableException;
import javax.swing.*;

public class TestFrame
{
	private static Piano piano = new Piano();
	private static Set<Character> pressedKeys = new HashSet<Character>();

	public static void main(String[] args)
	{
		try
		{
			piano.open();
		}
		catch (MidiUnavailableException ex)
		{
			ex.printStackTrace();
		}
		try
		{
			FileInputStream firstIn = new FileInputStream("conf.xml");
			try
			{
				piano.loadConf(firstIn);
			}
			catch (IllegalXMLFormatException ex)
			{
				ex.printStackTrace();
			}
			firstIn.close();
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}

		JFrame frame = new JFrame("Piano");
		frame.setLayout(new BorderLayout());
		JPanel panel = new JPanel();
		frame.add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout());
		JButton button = new JButton("reload");
		button.setPreferredSize(new Dimension(100, 60));
		panel.add(button, BorderLayout.CENTER);
		button.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					FileInputStream in = new FileInputStream("conf.xml");
					try
					{
						piano.loadConf(in);
					}
					catch (IllegalXMLFormatException ex)
					{
						ex.printStackTrace();
					}
					in.close();
				}
				catch (IOException ex)
				{
					ex.printStackTrace();
				}
			}
		});

		button.setFocusable(false);
		panel.setFocusable(false);

		frame.addKeyListener(new KeyListener()
		{

			@Override
			public void keyPressed(KeyEvent e)
			{
				note(true, e.getKeyChar());
			}

			@Override
			public void keyReleased(KeyEvent e)
			{
				note(false, e.getKeyChar());
			}

			@Override
			public void keyTyped(KeyEvent e)
			{
				// do nothing
			}

		});

		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		// 从文件总读取音符
		new Thread(new ReadAndPlay("music1.txt")).start();
		new Thread(new ReadAndPlay("music2.txt")).start();
		new Thread(new ReadAndPlay("music3.txt")).start();
		
		new Thread(new ReadAndPlay("music4.txt")).start();
		new Thread(new ReadAndPlay("music5.txt")).start();
		new Thread(new ReadAndPlay("music6.txt")).start();

	}

	public static void note(boolean noteOn, char keyChar)
	{
		if (noteOn)
		{
			if (!pressedKeys.contains(keyChar))
			{
				pressedKeys.add(keyChar);
				piano.note(true, keyChar);
			}
		}
		else
		{
			pressedKeys.remove(keyChar);
			piano.note(false, keyChar);
		}
	}

	public static class ReadAndPlay implements Runnable
	{

		private String file=null;

		public ReadAndPlay(String file)
		{
			this.file=file;
		}
		
		@Override
		public void run()
		{
			char lastChar = 0;
			BufferedReader in = null;
			try
			{
				in = new BufferedReader(new FileReader(file));
			}
			catch (FileNotFoundException ex)
			{
				ex.printStackTrace();
			}

			char curChar = 0;
			try
			{
				while ((curChar = (char) in.read()) != -1)
				{
					if ('~' == curChar)
					{
						continue; // 这里故意递进变lastChar
					}
					if (' ' == curChar)
					{
						continue; // 这里故意递进变lastChar
					}

					if (curChar == lastChar)
					{
						continue;
					}
					else
					{
						//note(false, lastChar);
						note(true, curChar);
					}

					lastChar = curChar;

					try
					{
						Thread.sleep(150);
					}
					catch (InterruptedException ex)
					{
						ex.printStackTrace();
					}
				}
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}

			note(false, lastChar);

			try
			{
				in.close();
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}

	}

}
