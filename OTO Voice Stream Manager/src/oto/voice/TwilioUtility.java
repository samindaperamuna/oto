package oto.voice;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import com.twilio.Twilio;
import com.twilio.twiml.Dial;
import com.twilio.twiml.Number;
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
	private int port;

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
			this.port = Integer.parseInt(properties.getProperty("port"));

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		Twilio.init(accountSID, authToken);

		TwilioServer server = new TwilioServer(this.agentNumber, this.callerId, this.ipAddress, this.port);
		server.listen();
	}

	public String getAccountSID() {
		return accountSID;
	}

	public String getAuthToken() {
		return authToken;
	}

	private class TwilioServer {

		private final static String LISTEN_PATH = "/voice";
		private String agentNumber;
		private String callerId;
		private String ipAddress;
		private int port;

		/**
		 * Start the spark Server.
		 */
		private TwilioServer(String agentNumber, String callerId, String ipAddress, int port) {
			System.out.println("Configuring spark server ...");

			this.agentNumber = agentNumber;
			this.callerId = callerId;
			this.ipAddress = ipAddress;
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
			Say voiceMessage = new Say.Builder("Please bare with us while we connect you to the agent.").build();
			Say failMessage = new Say.Builder("The call failed, or the remote party hung up. Goodbye.").build();
			Number agentNumber = new Number.Builder(this.agentNumber).build();
			Dial dial = new Dial.Builder().callerId(this.callerId).number(agentNumber).build();

			VoiceResponse voiceResponse = new VoiceResponse.Builder()
					.say(voiceMessage)
					.dial(dial)
					.say(failMessage)
					.build();

			Spark.post(LISTEN_PATH, "application/x-www-form-urlencoded", (request, response) -> {
				System.out.println("Sending the TwiML");
				System.out.println(voiceResponse.toXml().toString());

				response.header("Content-Type", "text/xml");
				return voiceResponse.toXml();
			});
		}
	}
}
