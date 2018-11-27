package akkaHW2017F.messages;

public class SecondRequestMessage {
	
	private final String text;
	
	public SecondRequestMessage (String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
}
