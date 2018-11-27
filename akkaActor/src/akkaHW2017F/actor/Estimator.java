package akkaHW2017F.actor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akkaHW2017F.messages.FirstRespondMessage;
import akkaHW2017F.messages.FirstRequestMessage;
import akkaHW2017F.messages.SecondRequestMessage;
import akkaHW2017F.messages.SecondRespondMessage;
import akkaHW2017F.messages.StartProcessingFolderMessage;
/**
 * This is the main actor and the only actor that is created directly under the
 * {@code ActorSystem} This actor creates more child actors
 * {@code WordCountInAFileActor} depending upon the number of files in the given
 * directory structure
 * 
 * @author hailie
 *
 */
public class Estimator extends UntypedActor {
	
	public static Props props = Props.create(Estimator.class);
	
	final static double PARAMETER_P2 = 0.9;
	final static double PARAMETER_P3 = 1.1;
	String text = null;
	int result1 = 0;
	int result2 = 0;
	int estimatedResult = 0;
	double coefficient_p1 = 1.0;
	boolean receivedFirstMessage = false;
	boolean receivedSecondMessage = false;
	
	public Estimator() {

	}
	
	
	@Override
	public void onReceive(Object msg) throws Throwable {
		if (msg instanceof StartProcessingFolderMessage) {
			StartProcessingFolderMessage payload = (StartProcessingFolderMessage)msg;
			File file = payload.getFile();
			
			//read the text file
			text = readFile(file);
			//divide the text file
			String text1 = text.substring(0 , text.length()/2);
			//create FirstCounter
			ActorRef firstCounter = getContext().actorOf(FirstCounter.props);
			//send message to first, first start counting
			firstCounter.tell(new FirstRequestMessage(text1), getSelf());
			//divide the text file
			String text2 = text.substring(text.length()/2);
			//create SecondCounter
			ActorRef secondCounter =  getContext().actorOf(SecondCounter.props);
			secondCounter.tell(new SecondRequestMessage(text2), getSelf());
		}
		if (msg instanceof FirstRespondMessage) {
			
			System.out.println("======= First Message Received =========");
			FirstRespondMessage payload = (FirstRespondMessage)msg;
			
			result1 = payload.getResult();
			estimatedResult = (int)(result1 * 2 * coefficient_p1);
//			coefficient_p1 = payload.getResult1();
			System.out.println("result1:  " + result1);
			
			receivedFirstMessage = true;
			if(receivedSecondMessage) {
				System.out.println("======= Fixing P1 =========");
				int trueResult = result1 + result2 ;
				System.out.println("trueResult:   " + trueResult);
				System.out.println("estimatedResult:  " + estimatedResult);
				System.out.println("coefficient_p1 before:  " + coefficient_p1);
				if(result1 + result2 > estimatedResult) {
					coefficient_p1 *=PARAMETER_P3;
					
				}else if (result1 + result2 < estimatedResult){
					coefficient_p1 *= PARAMETER_P2;
				}
				
				System.out.println("coefficient_p1 fixed:   " + coefficient_p1);
			}
		}
		if (msg instanceof SecondRespondMessage) {
			System.out.println("======= Second Message Received =========");
			SecondRespondMessage payload = (SecondRespondMessage)msg;
			result2 = payload.getResult();
			System.out.println("result2:  " + result2);
			receivedSecondMessage = true;
			if(receivedFirstMessage) {
				
				System.out.println("======= Fixing P1 =========");
				
				int trueResult = result1 + result2 ;
				System.out.println("trueResult:   " + trueResult);
				System.out.println("estimatedResult:  " + estimatedResult);
				System.out.println("coefficient_p1 before:  " + coefficient_p1);
				if(result1 + result2 > estimatedResult) {
					coefficient_p1 *=PARAMETER_P3;
					
				}else if (result1 + result2 < estimatedResult){
					coefficient_p1 *= PARAMETER_P2;
				}
				
				System.out.println("coefficient_p1 fixed:   " + coefficient_p1);
			}
			

		}
		else {
			unhandled(msg);
		}
	}

	public String readFile(File file) {
//        String fileName = "/Users/hailie/NEU/7215 Multi-thread/Homework/Akka_Text/Akka1.txt";
        String line = null;
        String text = null;
        try {
            FileReader fileReader = new FileReader(file.getPath());
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while((line = bufferedReader.readLine()) != null) {
//            		System.out.println(line);           
            		text = text + line;
            } 
            bufferedReader.close();
        }catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + 
                		file.getName() + "'");                
        }catch(IOException ex) {
            System.out.println(
                "Error reading file '" 
                + file.getName() + "'");
        }
        return text;
    }

}
