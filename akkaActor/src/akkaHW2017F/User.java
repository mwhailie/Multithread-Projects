package akkaHW2017F;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akkaHW2017F.actor.Estimator;
import akkaHW2017F.messages.StartProcessingFolderMessage;

/**
 * Main class for your estimation actor system.
 *
 * @author akashnagesh
 *
 */
public class User {


	public static void main(String[] args) throws Exception {
		
		
		
		ActorSystem system = ActorSystem.create("EstimationSystem");

		/*
		 * Create the Estimator Actor and send it the StartProcessingFolder
		 * message. Once you get back the response, use it to print the result.
		 * Remember, there is only one actor directly under the ActorSystem.
		 * Also, do not forget to shutdown the actorsystem
		 */
		
        ActorRef estimator = system.actorOf(Estimator.props);
        File[] files = readDirectory();
        for(File f : files) {
        		if(f.getName().startsWith(".")) {
        			continue;
        		}
        		
        		estimator.tell(new StartProcessingFolderMessage(f), null);
        		
        }
//        estimator.tell(new StartProcessingFolderMessage(files[1]), null);
        Thread.sleep(10000);
		system.terminate();
	}
	
	public static File[] readDirectory() {
		File folder = new File("/Users/hailie/NEU/7215 Multi-thread/Homework/Akka_Text");
		File[] listOfFiles = folder.listFiles();
		return listOfFiles;
    }

}
