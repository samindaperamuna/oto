package oto.agent.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import oto.voice.MessageObserver;
import oto.voice.Notification;

public class MainUI extends JFrame implements MessageObserver {
	private HTMLEditorKit customerEditorKit, agentEditorKit;
	private HTMLDocument customerHtmlDocument, agentHtmlDocument;
	private JPanel contentPane;
	private JPanel headerPanel;
	private JEditorPane customerEditorPane;
	private JEditorPane agentEditorPane;
	private JLabel statusLabel;

	public MainUI() {
		this.customerEditorKit = new HTMLEditorKit();
		this.agentEditorKit = new HTMLEditorKit();
		StyleSheet styleSheet = new StyleSheet();

		styleSheet.addRule(".customer { color: blue; }");
		styleSheet.addRule(".agent { color: red; }");

		this.customerEditorKit.setStyleSheet(styleSheet);
		this.agentEditorKit.setStyleSheet(styleSheet);
		this.customerHtmlDocument = (HTMLDocument) customerEditorKit.createDefaultDocument();
		this.agentHtmlDocument = (HTMLDocument) agentEditorKit.createDefaultDocument();

		initializeUI();
	}

	private void initializeUI() {
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
		agentLabel.setHorizontalAlignment(SwingConstants.LEFT);
		headerPanel.add(agentLabel);

		JPanel bodyPanel = new JPanel();
		contentPane.add(bodyPanel, BorderLayout.CENTER);
		bodyPanel.setLayout(new GridLayout(1, 2, 0, 0));

		JScrollPane customerScrollPane = new JScrollPane();
		customerScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		bodyPanel.add(customerScrollPane);

		customerEditorPane = new JEditorPane();
		customerEditorPane.setEditorKit(customerEditorKit);
		customerEditorPane.setDocument(customerHtmlDocument);
		customerEditorPane.setText("<html><head></head><body></body></html>");
		customerEditorPane.setPreferredSize(new Dimension(30, 30));
		customerEditorPane.setMinimumSize(new Dimension(30, 30));
		customerEditorPane.setEditable(false);
		customerScrollPane.setViewportView(customerEditorPane);

		JScrollPane agentScrollPane = new JScrollPane();
		agentScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		bodyPanel.add(agentScrollPane);

		agentEditorPane = new JEditorPane();
		agentEditorPane.setEditorKit(agentEditorKit);
		agentEditorPane.setDocument(agentHtmlDocument);
		agentEditorPane.setText("<html><head></head><body></body></html>");
		agentEditorPane.setMinimumSize(new Dimension(30, 30));
		agentEditorPane.setPreferredSize(new Dimension(30, 30));
		agentEditorPane.setEditable(false);

		agentScrollPane.setViewportView(agentEditorPane);

		JPanel footerPanel = new JPanel();
		footerPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		footerPanel.setPreferredSize(new Dimension(10, 25));
		contentPane.add(footerPanel, BorderLayout.SOUTH);
		footerPanel.setLayout(new GridLayout(1, 2, 0, 0));

		statusLabel = new JLabel("Ready");
		statusLabel.setFont(new Font("Dialog", Font.BOLD, 10));
		footerPanel.add(statusLabel);

		this.pack();
		this.setLocationRelativeTo(null);
	}

	/**
	 * Display the customer message in chat box.
	 * 
	 * @param message
	 * @throws IOException
	 * @throws BadLocationException
	 */
	public void displayCustomerMessage(String message) throws BadLocationException, IOException {
		HTMLDocument doc = (HTMLDocument) customerEditorPane.getDocument();
		doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()), createHTMLMessage(message, "left", "customer"));
		doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()), "<br>");
	}

	/**
	 * Display the message from the agent in the chat box.
	 * 
	 * @param message
	 * @throws IOException
	 * @throws BadLocationException
	 */
	public void displayAgentMessage(String message) throws BadLocationException, IOException {
		HTMLDocument doc = (HTMLDocument) agentEditorPane.getDocument();
		doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()), createHTMLMessage(message, "right", "agent"));
		doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()), "<br>");
	}

	/**
	 * Create the HTML formatted string.
	 * 
	 * @param color
	 * @param username
	 * @param message
	 * @return
	 */
	private String createHTMLMessage(String message, String align, String styleClasses) {
		Date now = new Date();
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		StringBuilder sb = new StringBuilder();

		sb.append("<p class='" + styleClasses + "'>");
		sb.append("<b>[" + df.format(now) + "]</b> " + message);
		sb.append("</p>");

		return sb.toString();
	}

	@Override
	public void handleNotification(Notification notification) {
		String message = notification.getMessage();

		try {
			switch (notification.getSource()) {
			case CUSTOMER:
				displayCustomerMessage(message);
				break;
			case AGENT:
				displayAgentMessage(message);
				break;
			case SYSTEM:
				this.statusLabel.setText(message);
				break;
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Internal error occurred. " + e.getLocalizedMessage(),
					"Cannot create output", JOptionPane.ERROR_MESSAGE);
		}
	}
}
