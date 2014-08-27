import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSlider;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.synth.SynthLookAndFeel;

import schedulers.FCFS;
import schedulers.Feedback;
import schedulers.HRRN;
import schedulers.Process;
import schedulers.RoundRobin;
import schedulers.SPN;
import schedulers.SRT;
import schedulers.Scheduler;

@SuppressWarnings("unused")
public class Animation {
	int x = 0;
	int y = 0;
	long timer = 0;
	int FPS = 30;
	//int SLEEPTIME = 33;
	Scheduler sched = new RoundRobin();
	ArrayList<Process> q = new ArrayList<Process>();
	ArrayList<Rectangle> graph = new ArrayList<Rectangle>();

	public static void main(String[] args) {
		Animation gui = new Animation();

		gui.play();
	}

	public void play() {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1200, 800);
		DrawPanel draw = new DrawPanel();
		frame.getContentPane().add(draw);
		//------------------------------------------------------------Mouse clicked - KILL PROCESS
		frame.getContentPane().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent m) {
				if (m.getButton() == MouseEvent.BUTTON1) {
					for (int i = 0; i < q.size(); i++) {
						int x = (q.get(i).getPID()-1) * 50;
						int y = 30;
						int w = 30;
						int h = q.get(i).getTimeToFinish();
						if ((m.getX() < x + w) && (m.getX() > x)
								&& (m.getY() < y + h) && (m.getY() > y)) {
							sched.killProcess(q.get(i).getPID());
						}
					}
				}
			}
		});
		//------------------------------------------------------------Mouse clicked - KILL PROCESS
		frame.setVisible(true);
		//------------------------------------------------------------MAIN LOOP
		for (timer = 0;; timer++) {
			sched.iterate(timer);
			draw.repaint();
			try {
				Thread.sleep(1000/FPS);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//------------------------------------------------------------MAIN LOOP
	}

	class DrawPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		JMenuBar menuBar;
		JSlider sliderSlice;
		JSlider sliderSpeed;
		JButton addProc;
		JButton reset;
		
		public DrawPanel() {
			setLayout(null);
			//------------------------------------------------------------TEMA
			try {
	            SynthLookAndFeel laf = new SynthLookAndFeel();
	            laf.load(Animation.class.getResourceAsStream("ui/style.xml"), Animation.class);
	            UIManager.setLookAndFeel(laf);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
			
			
			sliderSlice = new JSlider();
			sliderSlice.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					JSlider source = (JSlider) e.getSource();
					sched.setTimeSlice(source.getValue());
				}
			});
			sliderSlice.setValue(10);
			add(sliderSlice);
			
			sliderSpeed = new JSlider();
			sliderSpeed.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					JSlider source = (JSlider) e.getSource();
					FPS = source.getValue();
					
				}
			});
			sliderSpeed.setValue(30);
			sliderSpeed.setMaximum(120);
			sliderSpeed.setMinimum(1);
			add(sliderSpeed);


			addProc = new JButton("Dodaj");
			add(addProc);
			
			addProc.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					int id = sched.getNewProcID();
					Random r = new Random();
					Process proc = new Process(id,timer+1,r.nextInt(200)+50,1);
					sched.addProcess(proc);
					
				}
			});
			
			reset = new JButton("Reset");
			add(reset);
			
			reset.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					timer = 0;
					q.clear();
					graph.clear();
					sched.reset();
				}
			});
			
			
			menuBar = new JMenuBar();
			JMenu mnNewMenu = new JMenu("Algoritam");
			menuBar.add(mnNewMenu);
			
			
			JMenu help = new JMenu("Help");
			menuBar.add(help);
			
			JMenuItem about = new JMenuItem("About");
			about.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					//JOptionPane.showMessageDialog(null, "Seminarski rad iz predmeta Operativni Sistemi 2\nDusan Stefanovic"); https://github.com/lexluthor91/rasporedjivac/wiki/Uputstvo
					JOptionPane optionPane = new JOptionPane(new JLabel("<html><body style=\"text-align:center;padding:20px;\">Seminarski rad iz predmeta Operativni Sistemi 2<br/> Dusan Stefanovic (83/10)<br/></body></html>",JLabel.CENTER));  
					 
					JDialog dialog = optionPane.createDialog("");  
				    dialog.setModal(true);  
				    dialog.setVisible(true);  
				}
			});
			help.add(about);
			
			//https://github.com/lexluthor91/rasporedjivac/wiki/Uputstvo
			JMenuItem uput = new JMenuItem("Uputstvo");
			uput.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					URI uri;
					if (Desktop.isDesktopSupported()) {
					      try {
					    	  uri = new URI("https://github.com/lexluthor91/rasporedjivac/wiki/Uputstvo");
					    	  Desktop.getDesktop().browse(uri);
					      } catch (Exception e) { /* TODO: error handling */ }
					    } else { /* TODO: error handling */ }
				}
			});
			help.add(uput);
			
			JMenuItem gh = new JMenuItem("GitHub");
			gh.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					URI uri;
					if (Desktop.isDesktopSupported()) {
					      try {
					    	  uri = new URI("https://github.com/lexluthor91/rasporedjivac/");
					    	  Desktop.getDesktop().browse(uri);
					      } catch (Exception e) { /* TODO: error handling */ }
					    } else { /* TODO: error handling */ }
				}
			});
			help.add(gh);
			
			ButtonGroup myGroup = new ButtonGroup();
			
			JRadioButtonMenuItem menufcfs = new JRadioButtonMenuItem("FCFS");
			//JMenuItem menufcfs = new JMenuItem("FCFS");
			mnNewMenu.add(menufcfs);
			myGroup.add(menufcfs);
			
			menufcfs.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					sched = new FCFS(sched.getProcesses());
				}
			});
			
			JRadioButtonMenuItem menurr = new JRadioButtonMenuItem("Round Robin");
			menurr.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					sched = new RoundRobin(sched.getProcesses());
					sched.setTimeSlice(sliderSlice.getValue());
				}
			});
			mnNewMenu.add(menurr);
			myGroup.add(menurr);
			myGroup.setSelected(menurr.getModel(), true);
			
			JRadioButtonMenuItem menuspn = new JRadioButtonMenuItem("Shortest Process Next");
			menuspn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					sched = new SPN(sched.getProcesses());
				}
			});
			mnNewMenu.add(menuspn);
			myGroup.add(menuspn);
			//add(menuBar);
			
			JRadioButtonMenuItem menusrt = new JRadioButtonMenuItem("Shortest Remaining Time");
			menusrt.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					sched = new SRT(sched.getProcesses());
				}
			});
			mnNewMenu.add(menusrt);
			myGroup.add(menusrt);
			//add(menuBar);
			
			JRadioButtonMenuItem menuhrrn = new JRadioButtonMenuItem("Highest Response Ratio Next");
			menuhrrn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					sched = new HRRN(sched.getProcesses());
					sched.setTimeSlice(sliderSlice.getValue());
				}
			});
			mnNewMenu.add(menuhrrn);
			myGroup.add(menuhrrn);
			//add(menuBar);
			
			JRadioButtonMenuItem menufb = new JRadioButtonMenuItem("Feedback");
			menufb.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					sched = new Feedback(sched.getProcesses());
					sched.setTimeSlice(sliderSlice.getValue());
				}
			});
			mnNewMenu.add(menufb);
			myGroup.add(menufb);
			add(menuBar);
		}
		//------------------------------------------------------------REDISPLAY
		public void paintComponent(Graphics g) {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
			this.repositionControlls();
			g.setColor(Color.ORANGE);
			q.clear();
			int activePID = sched.getLastActivePID();
			q = sched.getActiveProcesses();
			if(graph.size() > 1920){
				graph.remove(0);
			}
			for (int i = 0; i < q.size(); i++) {
				q.get(i).getRemainingTime();
				g.setColor(Color.WHITE);
				int pos = q.get(i).getPID()-1;
				g.fillRect(pos * 50, 50, 30, q.get(i).getTimeToFinish());
				if (q.get(i).getPID() == activePID) {
					graph.add(new Rectangle(20,300+activePID*20,1,20));
					g.setColor(Color.BLUE);
				} else{
					g.setColor(Color.ORANGE);
				}
				g.fillRect(pos * 50, 50, 30, q.get(i).getRemainingTime());
				
				writeLabels(g,i);
				writeLabelsVertical(g, i);
				writeLabelsPriority(g, i);
			}
			g.setColor(Color.WHITE);
			for(int j = 0; j < graph.size();j++){
				graph.get(j).x++;
				g.fillRect(graph.get(j).x,graph.get(j).y,graph.get(j).width,graph.get(j).height);
			}
			g.setFont(new Font("Arial", Font.PLAIN, 20));
			g.drawString(sched.toString(), this.getWidth()-66,43);
			
			g.setFont(new Font("Arial", Font.PLAIN, 14));
			g.drawString("Working ticks: "+timer, this.getWidth()-126, 73);
			g.drawString("Context switch: "+sched.getNumContextSwitches(), this.getWidth()-126, 93);
			DecimalFormat df = new DecimalFormat("#.#");
			g.drawString("Efficiency: "+df.format((100.0*((1.0*timer)/(timer+(1.0*sched.getNumTicksForContextSwitch()*sched.getNumContextSwitches()))))), this.getWidth()-126, 113);
		}
		private void writeLabels(Graphics g,int i){
			g.setColor(Color.WHITE);
			g.setFont(new Font("Arial", Font.BOLD, 16));
			g.drawString(String.valueOf(q.get(i).getPID()), ((q.get(i).getPID()-1) * 50) + 10,43);
			g.setFont(new Font("Dialog", Font.PLAIN, 14));
			g.drawString("Brzina",this.getWidth()-200,this.getHeight()-35);
			g.drawString("Dužina slice-a",1,this.getHeight()-35);
		}
		
		private void writeLabelsPriority(Graphics g,int i){
			g.setColor(Color.BLACK);
			g.setFont(new Font("Arial", Font.BOLD, 12));
			DecimalFormat df = new DecimalFormat("#.##");
			g.drawString(String.valueOf(df.format(q.get(i).getPriority())), ((q.get(i).getPID()-1) * 50) + 4,63);
			
		}
		
		private void writeLabelsVertical(Graphics g,int i){
			g.setColor(Color.WHITE);
			g.setFont(new Font("Arial", Font.BOLD, 14));
			g.drawString(String.valueOf(q.get(i).getPID()), 10,q.get(i).getPID()*20+314);
			
		}
		private void repositionControlls(){
			menuBar.setBounds(0, 0, this.getWidth(), 21);
			sliderSlice.setBounds(0, this.getHeight()-28, 200, 28);
			sliderSpeed.setBounds(this.getWidth()-200, this.getHeight()-28, 200, 28);
			addProc.setBounds(this.getWidth()-400, this.getHeight()-30, 120, 30);
			reset.setBounds(this.getWidth()-560, this.getHeight()-30, 120, 30);
		}
	}
}