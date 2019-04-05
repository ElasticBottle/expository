import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.event.EventListenerList;

import ExpositoryConstant.ExpositoryConstant;
import GUI.Button;
import GUI.PlusMinusBtn;
import GUI.PlusMinusListener;
import GUI_Event_Handlers.ButtonEvent;
import GUI_Event_Handlers.ButtonListener;

public class Nanobot extends JPanel implements ExpositoryConstant {
	private int maxFreeCapacity = 5;
	private int currFreeCapacity = maxFreeCapacity;
	private Button fuel;
	private Button health;
	private Button upgrade;
	private PlusMinusBtn parameters;
	private JPanel controls;
	private HashMap<String, Integer> params = new HashMap<String, Integer>();
	private EventListenerList listenerList = new EventListenerList();	
	
	public Nanobot (String name) {
		this.setLayout(new BorderLayout());
		this.setBackground(BG_COLOR);
		addNameLabel(name);
		addControls();
		Thread t = new Thread(new Runnable () {
			public void run() {
				Timer timer = new Timer(CONVERT_TO_MSEC, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						eventOccurred();
					}
				});
				timer.start();
			}
		});
		t.start();
	}
	
	private void addNameLabel(String name) {
		JLabel nameLabel = new JLabel(name);
		nameLabel.setForeground(NORMAL_COLOR);
		nameLabel.setFont(new Font (STORY_FONT, Font.BOLD, 15));
		nameLabel.setAlignmentY(BOTTOM_ALIGNMENT);
		add(nameLabel, BorderLayout.LINE_START);
	}

	private void addControls() {
		controls = new JPanel();
		controls.setBackground(BG_COLOR);
		controls.setLayout(new FlowLayout(FlowLayout.LEADING));
		initControlsPanel(controls);
		add(controls, BorderLayout.CENTER);
	}

	private void initControlsPanel(JPanel controls) {
		fuel = new Button("Fuel", false);
		fuel.addBtn("Fuel", 20, true);
		
		health = new Button("Health", false);
		health.addBtn("Repair", 20, true);
		
		upgrade = new Button("Capacity: " + currFreeCapacity + "/" + maxFreeCapacity, true);
		upgrade.addBtn("Upgrade", NO_WAIT, true);
		upgrade.addButtonListener(new ButtonListener () {
			public void buttonPressed (ButtonEvent be) {
				if (repairOccurred()) {
					upgrade();
				}
			}
		});
		upgrade.addToolTip("Upgrade", "Cost: ");
		
		parameters = new PlusMinusBtn("Parameters", false);
		parameters.addPlusMinus(0, "Water", true);
		params.put("Water", 0);
		parameters.addPlusMinusListener(new PlusMinusListener () {
			public boolean plusMinusClicked (String command, String param) {
				boolean canIncrement = false;
				if (command.equals(DECREASE)) {
					if (currFreeCapacity < maxFreeCapacity) {
						currFreeCapacity ++;
						params.put(param, params.get(param) - 1);
						canIncrement = true;
					}
				} else if (command.equals(INCREASE)) {
					if (currFreeCapacity > 0) {
						currFreeCapacity --;
						params.put(param, params.get(param) + 1);
						canIncrement = true;
					}
				}
				upgrade.setTitle("Capacity: " + currFreeCapacity + "/" + maxFreeCapacity);
				upgrade.repaint();
				return canIncrement;
			}
		});
		
		controls.add(fuel);
		controls.add(health);
		controls.add(parameters);
		controls.add(upgrade);
		
	}

	public void addParam (String param) {
		parameters.addPlusMinus(0, param, true);
		params.put(param, 0);
	}
	
	public void upgrade() {
		maxFreeCapacity += 5;
		currFreeCapacity += 5;
		upgrade.setTitle("Capacity: " + currFreeCapacity + "/" + maxFreeCapacity);
		upgrade.repaint();
	}
	
	private void eventOccurred() {
		Object[] listeners = listenerList.getListenerList();
		for (int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == NanobotListener.class) {
				((NanobotListener)listeners[i + 1]).nanobotEventOccurred(params);
			}
		}
	}
	
	private boolean repairOccurred() {
		Object[] listeners = listenerList.getListenerList();
		for (int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == NanobotListener.class) {
				return ((NanobotListener)listeners[i + 1]).nanobotRepairOccurred();
			}
		}
		return false;
	}
	
	public void addNanobotListener(NanobotListener nl) {
		listenerList.add(NanobotListener.class, nl);
	}
	
	public void removeNanobotListener(NanobotListener nl) {
		listenerList.remove(NanobotListener.class, nl);
	}
}