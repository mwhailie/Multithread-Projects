package akkaHW2017F.messages;

import akka.actor.ActorRef;

public class SecondRequestMessage {
	
	private final String text;
	
	public SecondRequestMessage (String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
}
