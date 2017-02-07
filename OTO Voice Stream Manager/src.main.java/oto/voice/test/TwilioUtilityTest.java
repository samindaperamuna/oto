package oto.voice.test;

import junit.framework.AssertionFailedError;
import oto.voice.TwilioUtility;

public class TwilioUtilityTest {

	static TwilioUtility tu;
	
	public static void main(String[] args) {
		tu = new TwilioUtility();

		String accountSID = tu.getAccountSID();
		String authToken = tu.getAuthToken();

		if (accountSID.equals("") || authToken.equals("")) {
			throw new AssertionFailedError();
		}

		System.out.println("Your Account SID : " + accountSID);
		System.out.println("Your Auth Token : " + authToken);
	}
}
