package oto.agent;

import java.awt.EventQueue;

import oto.agent.ui.MainUI;

public class Application {

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				MainUI frame = new MainUI();
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
