package oto.voice.test;

import com.twilio.twiml.Client;
import com.twilio.twiml.Dial;
import com.twilio.twiml.VoiceResponse;

import junit.framework.AssertionFailedError;
import oto.voice.TwilioUtility;
import spark.Spark;

public class TwilioUtilityTest {

	static TwilioUtility tu;

	public static void main(String[] args) {
		tu = new TwilioUtility();

		/**
		 * Run the test server ..
		 */
		Spark.post("/test", (request, response) -> {
			System.out.println("");
			Client client = new Client.Builder("oto-agent").build();
			Dial dial = new Dial.Builder().callerId("+12034235978").client(client).build();

			VoiceResponse voiceResponse = new VoiceResponse.Builder().dial(dial).build();

			response.header("Content-Type", "text/xml");
			return voiceResponse.toXml();
		});

		String accountSID = tu.getAccountSID();
		String authToken = tu.getAuthToken();

		if (accountSID.equals("") || authToken.equals("")) {
			throw new AssertionFailedError();
		}

		System.out.println("Your Account SID : " + accountSID);
		System.out.println("Your Auth Token : " + authToken);
	}
}
