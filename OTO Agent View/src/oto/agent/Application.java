package oto.agent;

import java.awt.EventQueue;

import oto.agent.ui.MainUI;

public class Application {

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				MainUI frame = new MainUI();
				frame.setVisible(true);
				frame.displayCustomerMessage("<b>[06:20]</b> Hello There !");
				frame.displayAgentMessage("<b>[06:21]</b>  How Are you?");
				frame.displayCustomerMessage("<b>[06:22]</b> I am fine thank you");
				frame.displayAgentMessage("<b>[06:23]</b> Good");
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
