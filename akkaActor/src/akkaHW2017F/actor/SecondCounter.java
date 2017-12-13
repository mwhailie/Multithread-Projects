package akkaHW2017F.actor;

import java.util.Arrays;
import java.util.List;

import akka.actor.Props;
import akka.actor.UntypedActor;
import akkaHW2017F.messages.SecondRequestMessage;
import akkaHW2017F.messages.SecondRespondMessage;

public class SecondCounter  extends UntypedActor {
	
	public static Props props = Props.create(SecondCounter.class);
	
	public SecondCounter () {

	}

	
	@Override
	public void onReceive(Object msg) throws Throwable {
		if (msg instanceof SecondRequestMessage) {
			SecondRequestMessage payload = (SecondRequestMessage)msg;
			String text = payload.getText();

			SecondRespondMessage respond = new SecondRespondMessage(vowelCount(text));
			getSender().tell(respond, getSelf());
		}
		else {
			System.out.printf("Error in SecondCounter:  bad message %s%n", msg);
			unhandled(msg);
		}
	}
	
	
	Character[] vowels = { 'A', 'E', 'I', 'O','U','Y', 'a','e','i','o','u', 'y'};
	private List<Character> vowelList = Arrays.asList(vowels);
	
	private int vowelCount(String input) {
		int count = 0;
		for( int i=0; i<input.length(); i++ ) {
		    if( vowelList.contains(input.charAt(i))) {
		        count++;
		    } 
		}
		return count;
	}
}
