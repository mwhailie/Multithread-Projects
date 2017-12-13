package akkaHW2017F.messages;

import java.io.File;

public class StartProcessingFolderMessage {
	private final File file;

	public StartProcessingFolderMessage(File file) {
		this.file = file;
	}

	public File getFile() {
		return file;
	}
	
	
}
