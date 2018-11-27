package akkaHW2017F.messages;

public class FirstRequestMessage {

	private final String text;
	
	public FirstRequestMessage(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
	
}
