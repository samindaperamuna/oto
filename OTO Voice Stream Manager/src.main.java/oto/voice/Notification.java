package oto.voice;

public class Notification {

	private MessageSource source;
	private String message;

	public Notification(MessageSource source, String message) {
		this.source = source;
		this.message = message;
	}

	public MessageSource getSource() {
		return source;
	}

	public void setSource(MessageSource source) {
		this.source = source;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
