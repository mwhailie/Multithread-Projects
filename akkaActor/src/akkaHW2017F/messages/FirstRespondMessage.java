package akkaHW2017F.messages;

import akka.actor.ActorRef;

public class FirstRespondMessage {
	
	private final Integer result;
//	private final Integer estimatedResult;
//	private final double p1;
	
	public FirstRespondMessage(Integer result) {
		this.result = result;
	}

	public Integer getResult() {
		return result;
	}

}
