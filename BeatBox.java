import java.util.ArrayList;
import javax.sound.midi.*;
import java.util.Scanner;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Graphics;
import java.awt.*;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.event.*;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
public class BeatBox
{

	JButton Save,Restore;
	JList incomingList;
	ArrayList<JCheckBox> checkboxList;
	JTextField userMessage;

	Sequencer sequencer;
	String userName,name;
	HashMap<String, boolean[]> otherSeqsMap =new HashMap<String, boolean[]>();
	int nextNum;
	ObjectOutputStream out;
	ObjectInputStream in;
	Track track;
	Sequence sequence;
	Sequence mySequence=null;
	JFrame Name;
	JTextField tf;
	JLabel lb;
	Vector<String> listVector= new Vector<String>();
	String instrumentNames[]=
	{   "Bass Drum","Closed Hi-Bat",
		"Open Hi-Bat","Acoustic Snare","Snare", "Crash Cymbal", "Hand Clap",
		"High Tom","High Bongo", "Maracas", "Whistle", "Low Conga",
		"Cowbell", "Vibraslap","Low-mid Tom", "High Aqoqo","0pen Hi Conga"
	};
	int[] instruments = {35,42,46,38,49,39,50,60,70,72,64,56,59,47,67,63};
	public static void main(String args[])
	{
		new BeatBox().startup("");
	}
	public void startup(String name)
	{
		Name=new JFrame("Enter Your Name");
		tf = new JTextField("",20);
     	lb = new JLabel("");
		JButton enter=new JButton("Enter");
		JPanel background=new JPanel();
		enter.addActionListener(new EnterListener());
		background.add(tf);
		background.add(enter,"wrap");
		background.add(lb,"wrap");
		Name.getContentPane().add(background);
		Name.setBounds(50,50,300,300);
		Name.setVisible(true);
	}
	public void buildGUI()
	{
		BufferedImage buttonIcon = new BufferedImage(200,200,BufferedImage.TYPE_INT_RGB);
		try
		{
			buttonIcon = ImageIO.read(new File("play.jpg"));
		}
		catch(Exception ex)
		{
			System.out.println("kida");
		}
		JFrame frame=new JFrame(name+"'s BeatBox");
		JPanel buttonbox=new JPanel();
		JPanel chatbox=new JPanel();
		Box list=new Box(BoxLayout.Y_AXIS);
		JPanel message=new JPanel();
		buttonbox.add(Box.createRigidArea(new Dimension(220,48)));
		chatbox.add(Box.createRigidArea(new Dimension(90,48)));
		JButton start=new JButton(new ImageIcon(buttonIcon));
		start.setPreferredSize(new Dimension(48, 48));
		start.addActionListener(new MyStartListener());
		buttonbox.add(start);

		try
		{
			buttonIcon = ImageIO.read(new File("stop.jpg"));
		}
		catch(Exception ex)
		{
			System.out.println("kida");
		}

		JButton stop=new JButton(new ImageIcon(buttonIcon));
		stop.setPreferredSize(new Dimension(48, 48));
		stop.addActionListener(new MyStopListener());
		buttonbox.add(stop);

		try
		{
			buttonIcon = ImageIO.read(new File("upTempo.jpg"));
		}
		catch(Exception ex)
		{
			System.out.println("kida");
		}

		JButton upTempo=new JButton(new ImageIcon(buttonIcon));
		upTempo.setPreferredSize(new Dimension(48, 48));
		upTempo.addActionListener(new MyupTempoListener());
		buttonbox.add(upTempo);

		try
		{
			buttonIcon = ImageIO.read(new File("downTempo.jpg"));
		}
		catch(Exception ex)
		{
			System.out.println("kida");
		}

		JButton downTempo=new JButton(new ImageIcon(buttonIcon));
		downTempo.setPreferredSize(new Dimension(48, 48));
		downTempo.addActionListener(new MydownTempoListener());
		buttonbox.add(downTempo);

		try
		{
			buttonIcon = ImageIO.read(new File("save.jpg"));
		}
		catch(Exception ex)
		{
			System.out.println("kida");
		}

		Save=new JButton(new ImageIcon(buttonIcon));
		Save.setPreferredSize(new Dimension(48, 48));
		Save.addActionListener(new SaveListener());
		buttonbox.add(Save);

		try
		{
			buttonIcon = ImageIO.read(new File("open.jpg"));
		}
		catch(Exception ex)
		{
			System.out.println("kida");
		}

		Restore=new JButton(new ImageIcon(buttonIcon));
		Restore.setPreferredSize(new Dimension(48, 48));
		Restore.addActionListener(new RestoreListener());
		buttonbox.add(Restore);

		try
		{
			buttonIcon = ImageIO.read(new File("send.jpg"));
		}
		catch(Exception ex)
		{
			System.out.println("kida");
		}
		userMessage=new JTextField("",20);
		chatbox.add(userMessage);


		JButton sendit=new JButton(new ImageIcon(buttonIcon));
		sendit.setPreferredSize(new Dimension(48, 48));
		sendit.addActionListener(new MySendListener());
		chatbox.add(sendit);
		try
		{
			buttonIcon = ImageIO.read(new File("beatShare.jpg"));
		}
		catch(Exception ex)
		{
			System.out.println("kida");
		}

		JButton sharebeat=new JButton(new ImageIcon(buttonIcon));
		sharebeat.setPreferredSize(new Dimension(48, 48));
		sharebeat.addActionListener(new MyBeatShareListener());
		chatbox.add(sharebeat);


		incomingList=new JList();
		incomingList.addListSelectionListener(new MyListSelectionListener());
		incomingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane theList=new JScrollPane(incomingList);
		theList.setPreferredSize(new Dimension(350,500));
		list.add(new JLabel("Messaging and Beat Sharing Portal :"));
		list.add(Box.createRigidArea(new Dimension(0,20)));
		list.add(theList);

	    incomingList.setListData(listVector);

		Box nameBox=new Box(BoxLayout.Y_AXIS);
		nameBox.add(Box.createRigidArea(new Dimension(0,20)));
		for(int i=0;i<16;i++)
		{
			JLabel l = new JLabel(instrumentNames[i]);
			nameBox.add(l);
			nameBox.add(Box.createRigidArea(new Dimension(0,20)));
		}
		GridLayout layout = new GridLayout(16,16);
		layout.setVgap(15);
    	layout.setHgap(10);
		JPanel centerpanel=new JPanel(layout);
		checkboxList=new ArrayList<JCheckBox>();
		for(int i=0;i<256;i++)
		{
			JCheckBox c=new JCheckBox();
			c.setSelected(false);
			checkboxList.add(c);
			centerpanel.add(c);
		}
		FlowLayout lay = new FlowLayout();
		lay.setHgap(50);

		JPanel background=new JPanel(lay);
		background.add(nameBox);
		background.add(centerpanel);
		background.add(list);
		background.add(buttonbox);
		background.add(chatbox);

		frame.getContentPane().add(background);
		frame.setBounds(0,0,1200,750);
		frame.setVisible(true);
	}
	public void setupMidi()
	{
		try
		{
			sequencer=MidiSystem.getSequencer();
			sequencer.open();
			sequence=new Sequence(Sequence.PPQ,4);
			track=sequence.createTrack();
			sequencer.setTempoInBPM(120);
		}
		catch(Exception e)
		{
		}

	}
	void buildTrackandStart()
	{
		int [] trackList=null;
		sequence.deleteTrack(track);
		track=sequence.createTrack();
		for(int i=0;i<16;i++)
		{
			trackList=new int[16];
			for(int j=0;j<16;j++)
			{
				JCheckBox jc=(JCheckBox) checkboxList.get(j+16*i);
				if(jc.isSelected())
				{
					int key=instruments[i];
					trackList[j]=key;
				}
				else
				{
					trackList[j]=0;
				}
			}
			makeTracks(trackList);
			track.add(makeEvent(192,9,1,0,16));
		}
		track.add(makeEvent(192,9,1,0,15));
		try
		{
			sequencer.setSequence(sequence);
			sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
			sequencer.start();
			sequencer.setTempoInBPM(120);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	class MyStartListener implements ActionListener
	{
		public void actionPerformed(ActionEvent a)
		{
			buildTrackandStart();
		}
	}
	class MyStopListener implements ActionListener
	{
		public void actionPerformed(ActionEvent a)
		{
			sequencer.stop();
		}
	}
	class MyupTempoListener implements ActionListener
	{
		public void actionPerformed(ActionEvent a)
		{
			float tempofactor=sequencer.getTempoFactor();
		    sequencer.setTempoFactor((float)(tempofactor * 1.4));
		}
	}
	class MydownTempoListener implements ActionListener
	{
		public void actionPerformed(ActionEvent a)
		{
		    float tempofactor=sequencer.getTempoFactor();
		    sequencer.setTempoFactor((float)(tempofactor /1.4));
		}
	}
	class EnterListener implements ActionListener
	{
		public void actionPerformed(ActionEvent a)
		{
		    if(tf.getText().equals(""))
		    {
				lb.setText("Please enter your name");
			}
			else
			{
				name=tf.getText();
				System.out.println(name);
				Name.setVisible(false);
				if(!name.equals(""))
				{
					userName=name;
					try
					{
						Socket sock=new Socket("127.0.0.1",4242);
						out=new ObjectOutputStream(sock.getOutputStream());
						in=new ObjectInputStream(sock.getInputStream());
						Thread remote=new Thread(new RemoteReader());
						remote.start();
					}
					catch(Exception ex)
					{
					}
					setupMidi();
					buildGUI();
		}
			}
		}
	}
	class SaveListener implements ActionListener
	{
		public void actionPerformed(ActionEvent a)
		{
			boolean[] checkboxState=new boolean[256];
			for(int i=0;i<256;i++)
			{
				JCheckBox check=(JCheckBox)checkboxList.get(i);
				if(check.isSelected())
				{
					checkboxState[i]=true;
				}
			} // loop close
			JFileChooser fileChooser = new JFileChooser();
			if (fileChooser.showSaveDialog(Save) == JFileChooser.APPROVE_OPTION)
			{
			  	File file = fileChooser.getSelectedFile();
				try
				{
					FileOutputStream fileStream = new FileOutputStream(file);
					ObjectOutputStream os = new ObjectOutputStream(fileStream) ;
					os.writeObject(checkboxState);
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}


		}
	 }
	 class RestoreListener implements ActionListener
	 {
		public void actionPerformed(ActionEvent a)
		{
			boolean[] checkboxState=null;
			JFileChooser fileChooser = new JFileChooser();
			if (fileChooser.showOpenDialog(Restore) == JFileChooser.APPROVE_OPTION)
			{
				File file = fileChooser.getSelectedFile();
				try
				{
					FileInputStream fileIn = new FileInputStream(file);
					ObjectInputStream is = new ObjectInputStream(fileIn);
					checkboxState = (boolean[]) is.readObject();
				}
				catch(Exception ex)
				{

				}
			}
			for(int i=0;i<256;i++)
			{
				JCheckBox check=(JCheckBox)checkboxList.get(i);
				if(checkboxState[i])
				{
					check.setSelected(true);
				}
				else
				{
					check.setSelected(false);
				}
			} // loop close
			sequencer.stop();
			buildTrackandStart();

		}
	 }
	 class MySendListener implements ActionListener
	 {
		public void actionPerformed(ActionEvent a)
		{
			boolean[] checkboxState=null;
			try
			{
				out.writeObject(userName+ nextNum++ + ": " + userMessage.getText());
				out.writeObject(checkboxState);
			}
			catch(Exception ex)
			{
				System.out.println("sorry couldnot connect \n");
			}
			userMessage.setText("");
		}
	 }
	 class MyBeatShareListener implements ActionListener
	 {
		public void actionPerformed(ActionEvent a)
		{
			boolean[] checkboxState=new boolean[256];
			for(int i=0;i<256;i++)
			{
				JCheckBox check=(JCheckBox)checkboxList.get(i);
				if(check.isSelected())
					{checkboxState[i]=true;}
			} // loop close
			try
			{
				out.writeObject(userName+ nextNum++ + ": Click Here to Play My Beat");
				out.writeObject(checkboxState);
			}
			catch(Exception ex)
			{
				System.out.println("sorry couldnot connect \n");
			}
			userMessage.setText("");
		}
	 }
	 class MyListSelectionListener implements ListSelectionListener
	 {
		public void valueChanged(ListSelectionEvent le)
		{
			if(le.getValueIsAdjusting())
		 	{
				String selected=(String)incomingList.getSelectedValue();
			 	if(selected!=null)
			 	{
					boolean[] selectedState=(boolean[]) otherSeqsMap.get(selected);
					if(selectedState!=null)
					{
						changeSequence(selectedState);
			 			sequencer.stop();
						buildTrackandStart();
					}
				}
			}
		}
	 }
	 public class RemoteReader implements Runnable
	 {
	 	boolean [] checkboxState=null;
	 	String nameToShow=null;
	 	Object obj=null;
	 	public void run()
	 	{
			try
		 	{
		 		while((obj=in.readObject())!=null)
		 		{
					System.out.println("got message from server\n");
		  			System.out.println(obj.getClass());
		  			String nameToShow=(String)obj;
		  			checkboxState=(boolean[])in.readObject();
		  			otherSeqsMap.put(nameToShow,checkboxState);
		  			listVector.add(nameToShow);
		  			incomingList.setListData(listVector);
		  		}
			}
			catch(Exception ex)
			{
			}
		}
	}
	public class MyPlayMineListener implements ActionListener
	{
		 public void actionPerformed(ActionEvent e)
		 {
			 if(mySequence!=null)sequence=mySequence;
		 }
	}
	public void changeSequence(boolean[] checkboxState)
	{
		for(int i=0;i<256;i++)
		{
			JCheckBox check=(JCheckBox)checkboxList.get(i);
			if(checkboxState[i])
			{
			 	check.setSelected(true);
			}
			else
			{
			    check.setSelected(false);
			}
		}
	}
	void makeTracks(int list[])
	{
		for(int i=0;i<16;i++)
		{
			int key=list[i];
			if(key!=0)
			{
				track.add(makeEvent(144,9,key,100,i));
				track.add(makeEvent(128,9,key,100,i+1));
			}
		}
	}
	MidiEvent makeEvent(int comd,int chan,int one,int two,int tick)
	{
		MidiEvent event=null;
		try
		{
			ShortMessage a=new ShortMessage();
			a.setMessage(comd,chan,one,two);
			event=new MidiEvent(a,tick);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return event;
	}
}