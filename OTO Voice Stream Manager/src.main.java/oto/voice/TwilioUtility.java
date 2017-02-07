package oto.voice;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.twilio.Twilio;
import com.twilio.twiml.Dial;
import com.twilio.twiml.Event;
import com.twilio.twiml.Method;
import com.twilio.twiml.Number;
import com.twilio.twiml.Redirect;
import com.twilio.twiml.Say;
import com.twilio.twiml.VoiceResponse;

import spark.Spark;

public class TwilioUtility {

	private final static String PROPERTY_FILE = "twilio.properties";

	private String accountSID;
	private String authToken;
	private String agentNumber;
	private String callerId;
	private String ipAddress;
	private String callbackUrl;
	private int port;

	private List<MessageObserver> messageObservers = new ArrayList<>();

	public TwilioUtility() {
		Path path = Paths.get(PROPERTY_FILE);

		if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
			System.err.println("Missing twilio.properties file. Exiting application ...");
			System.exit(1);
		}

		// Load properties from the file.
		try (InputStream input = new FileInputStream("twilio.properties")) {
			Properties properties = new Properties();
			properties.load(input);

			this.accountSID = properties.getProperty("account_sid");
			this.authToken = properties.getProperty("auth_token");
			this.agentNumber = properties.getProperty("agent_number");
			this.callerId = properties.getProperty("caller_id");
			this.ipAddress = properties.getProperty("ip_address");
			this.callbackUrl = properties.getProperty("callback_url");
			this.port = Integer.parseInt(properties.getProperty("port"));

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		Twilio.init(accountSID, authToken);

		TwilioServer server = new TwilioServer(this.agentNumber, this.callerId, this.ipAddress, this.callbackUrl,
				this.port);
		server.listen();
	}

	public String getAccountSID() {
		return accountSID;
	}

	public String getAuthToken() {
		return authToken;
	}

	/**
	 * Register a message Observer which watches for changes in twilio requests/
	 * responses.
	 * 
	 * @param observer
	 */
	public void registerObserver(MessageObserver observer) {
		this.messageObservers.add(observer);
	}

	private class TwilioServer {

		private final static String LISTEN_PATH = "/voice";
		private final static String CALL_PATH = "/call";
		private final static String EVENT_PATH = "/event";
		private final static String END_PATH = "/end";

		private String agentNumber;
		private String callerId;
		private String ipAddress;
		private String callbackUrl;
		private int port;

		/**
		 * Start the spark Server.
		 */
		private TwilioServer(String agentNumber, String callerId, String ipAddress, String callbackUrl, int port) {
			System.out.println("Configuring spark server ...");

			this.agentNumber = agentNumber;
			this.callerId = callerId;
			this.ipAddress = ipAddress;
			this.callbackUrl = callbackUrl;
			this.port = port;

			Spark.ipAddress(this.ipAddress);
			System.out.println("IP Address set to " + ipAddress);
			Spark.port(this.port);
			System.out.println("Port set to " + port);

			Spark.get("/", (request,
					response) -> "Please use \"[APP_URL]/voice\" as the Voice Request URL within your TwiML Application.");

			System.out.println("Spark server configuartion complete ...");
		}

		/**
		 * Listen to incoming calls.
		 */
		private void listen() {
			List<Event> statusCallbackEvents = new ArrayList<>();

			for (Event event : Event.values()) {
				statusCallbackEvents.add(event);
			}

			// Listen for the call.
			Spark.post(LISTEN_PATH, "application/x-www-form-urlencoded", (request, response) -> {
				Say voiceMessage = new Say.Builder("Please bare with us while we connect you to the agent.").build();
				Redirect redirect = new Redirect.Builder().url(CALL_PATH).method(Method.POST).build();
				VoiceResponse voiceResponse = new VoiceResponse.Builder().say(voiceMessage).redirect(redirect).build();

				String fromNumber = request.queryParams("From");

				notifyObservers(new Notification(MessageSource.CUSTOMER, "Ringing ..."));
				notifyObservers(new Notification(MessageSource.CUSTOMER, "Caller ID : " + fromNumber));
				notifyObservers(new Notification(MessageSource.SYSTEM, "Customer connected ..."));

				System.out.println("Customer call received ...");
				System.out.println(voiceResponse.toXml().toString());

				response.header("Content-Type", "text/xml");
				return voiceResponse.toXml();
			});

			// Dial the agent
			Spark.post(CALL_PATH, "application/x-www-form-urlencoded", (request, response) -> {
				Say failMessage = new Say.Builder("The call failed, or the remote party hung up. Goodbye.").build();

				Number agentNumber = new Number.Builder(this.agentNumber).statusCallbackEvents(statusCallbackEvents)
						.statusCallback(this.callbackUrl).method(Method.POST).build();
				Dial dial = new Dial.Builder().action(END_PATH).callerId(this.callerId).number(agentNumber).build();

				VoiceResponse voiceResponse = new VoiceResponse.Builder().dial(dial).say(failMessage).build();

				notifyObservers(new Notification(MessageSource.AGENT, "Ringing ..."));

				response.header("Content-Type", "text/xml");
				return voiceResponse.toXml();
			});

			// Upon event trigger
			Spark.post(EVENT_PATH, "application/x-www-form-urlencoded", (request, response) -> {
				VoiceResponse voiceResponse = new VoiceResponse.Builder().build();

				String callStatus = request.queryParams("CallStatus");

				switch (callStatus) {
				case "completed":
					break;
				default:
					notifyObservers(new Notification(MessageSource.SYSTEM, "Call status : " + callStatus));
					notifyObservers(new Notification(MessageSource.CUSTOMER, callStatus));
					break;
				}

				response.header("Content-Type", "text/xml");
				return voiceResponse.toXml();
			});

			// Upon end call.
			Spark.post(END_PATH, "application/x-www-form-urlencoded", (request, response) -> {
				VoiceResponse voiceResponse = new VoiceResponse.Builder().build();

				notifyObservers(new Notification(MessageSource.CUSTOMER, "Hangup ..."));
				notifyObservers(new Notification(MessageSource.AGENT, "Call End ..."));

				notifyObservers(new Notification(MessageSource.SYSTEM, "Call status : completed"));

				response.header("Content-Type", "text/xml");
				return voiceResponse.toXml();
			});
		}

		/**
		 * Pass changes to the observers.
		 * 
		 * @param notification
		 */
		private void notifyObservers(Notification notification) {
			for (MessageObserver observer : messageObservers) {
				observer.handleNotification(notification);
			}
		}
	}
}
