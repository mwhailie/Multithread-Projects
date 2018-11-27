package akkaHW2017F.actor;

import java.util.Arrays;
import java.util.List;

import akka.actor.Props;
import akka.actor.UntypedActor;
import akkaHW2017F.messages.FirstRespondMessage;
import akkaHW2017F.messages.FirstRequestMessage;
/**
 * this actor reads the file, counts the vowels and sends the result to
 * Estimator. 
 *
 * @author hailie
 *
 */
public class FirstCounter extends UntypedActor {

	public static Props props = Props.create(FirstCounter.class);

	public FirstCounter () {
	}

	
	@Override
	public void onReceive(Object msg) throws Throwable {
		if (msg instanceof FirstRequestMessage) {

			FirstRequestMessage payload = (FirstRequestMessage)msg;
			String text = payload.getText();
			
			int result1 = vowelCount(text);
			FirstRespondMessage respond = new FirstRespondMessage(result1);
			getSender().tell(respond, getSelf());

		}
		else {
			System.out.printf("Error in FirstCounter:  bad message %s%n", msg);
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
