package oto.agent;

import java.awt.EventQueue;

import oto.agent.ui.MainUI;
import oto.voice.TwilioUtility;

public class Application {

	private static MainUI mainUI;

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				mainUI = new MainUI();
				mainUI.setVisible(true);

				initiateVSM();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Start a new instance of the Voice Stream Manager.
	 */
	private static void initiateVSM() {
		TwilioUtility tu = new TwilioUtility();
		tu.registerObserver(mainUI);
	}
}
