package app;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.json.JSONObject;

public class Config {
	
	private static final String configFilename = "WoWClientManager.json";
	
	public static boolean keepOpen;
	public static boolean clearAtStart;
	public static ArrayList<String> realmlists;
	
	public static void initConfiguration() {
		File configFile = new File(configFilename);
		if(configFile.exists()) {
			try {
				String sourceText = new String(Files.readAllBytes(Paths.get(configFilename)), StandardCharsets.UTF_8);
				
				JSONObject mainObj = new JSONObject(sourceText);
				keepOpen = mainObj.getBoolean("keepOpen");
				clearAtStart = mainObj.getBoolean("clearAtStart");
				realmlists=new ArrayList<String>();
				for(Object o : mainObj.getJSONArray("realmlists").toList()) {
					realmlists.add(o.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(WoWClientManager.mainFrame, "Cannot read configuration file (corrupted)", "Error", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			keepOpen=true;
			clearAtStart=false;
			realmlists=new ArrayList<String>();
		}
		
		WoWClientManager.keepOpenCheck.setSelected(keepOpen);
		WoWClientManager.clearCacheOnStartCheck.setSelected(clearAtStart);
		for(String s : realmlists) {
			WoWClientManager.otherRealmlistModel.addElement(s);
		}
	}
	
	public static void update() {
		keepOpen=WoWClientManager.keepOpenCheck.isSelected();
		clearAtStart=WoWClientManager.clearCacheOnStartCheck.isSelected();
		realmlists=new ArrayList<String>();
		for(int i=0;i<WoWClientManager.otherRealmlistModel.getSize();i++) {
			realmlists.add(WoWClientManager.otherRealmlistModel.get(i));
		}
	}
	
	public static void writeConfiguration() {
		JSONObject mainObj = new JSONObject();
		mainObj.put("keepOpen", keepOpen);
		mainObj.put("clearAtStart", clearAtStart);
		mainObj.put("realmlists", realmlists);
		
		File configFile = new File(configFilename);
		if(configFile.exists()) {
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(WoWClientManager.mainFrame, "Cannot create configuration file", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		
		try {
			FileWriter myWriter = new FileWriter(configFile);
			myWriter.write(mainObj.toString());
			myWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(WoWClientManager.mainFrame, "Cannot write configuration file", "Error", JOptionPane.ERROR_MESSAGE);
		}
		
	}
}
