package oto.agent.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

public class MainUI extends JFrame {

	private JPanel contentPane;
	private JPanel headerPanel;
	private JEditorPane chatEditorPane;

	public MainUI() {
		setMinimumSize(new Dimension(800, 600));
		setPreferredSize(new Dimension(800, 600));
		setTitle("Test Bench");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1176, 589);
		contentPane = new JPanel();
		contentPane.setBorder(new CompoundBorder(null, new EmptyBorder(10, 10, 10, 10)));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(10, 10));

		headerPanel = new JPanel();
		headerPanel.setPreferredSize(new Dimension(10, 25));
		contentPane.add(headerPanel, BorderLayout.NORTH);
		headerPanel.setLayout(new GridLayout(1, 2, 0, 0));

		JLabel customerLabel = new JLabel("Customer");
		headerPanel.add(customerLabel);

		JLabel agentLabel = new JLabel("Agent");
		agentLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		headerPanel.add(agentLabel);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		contentPane.add(scrollPane, BorderLayout.CENTER);

		chatEditorPane = new JEditorPane();
		chatEditorPane.setEditable(false);
		chatEditorPane.setContentType("text/html");
		scrollPane.setViewportView(chatEditorPane);

		JPanel footerPanel = new JPanel();
		footerPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		footerPanel.setPreferredSize(new Dimension(10, 25));
		contentPane.add(footerPanel, BorderLayout.SOUTH);
		footerPanel.setLayout(new GridLayout(1, 2, 0, 0));

		JLabel statusLabel = new JLabel("Ready");
		statusLabel.setFont(new Font("Dialog", Font.BOLD, 10));
		footerPanel.add(statusLabel);

		this.pack();
		this.setLocationRelativeTo(null);
	}

}
