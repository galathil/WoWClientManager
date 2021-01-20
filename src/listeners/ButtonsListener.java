package listeners;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JOptionPane;

import app.Config;
import app.WoWClientManager;

public class ButtonsListener implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource().equals(WoWClientManager.openWowDirButton)) {
			try {
				Desktop.getDesktop().open(new File(System.getProperty("user.dir")));
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(WoWClientManager.mainFrame, "Cannot open dir", "Error", JOptionPane.ERROR_MESSAGE);
			}
		} else if(event.getSource().equals(WoWClientManager.startWowButton)) {
			try {
				if(WoWClientManager.clearCacheOnStartCheck.isSelected()) {
					WoWClientManager.clearCache();
				}
				Runtime.getRuntime().exec("Wow.exe");
				if(!WoWClientManager.keepOpenCheck.isSelected()) {
					System.exit(0);
				}
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(WoWClientManager.mainFrame, "Cannot launch WoW", "Error", JOptionPane.ERROR_MESSAGE);
			}
			
		} else if(event.getSource().equals(WoWClientManager.clearCacheNow)) {
			WoWClientManager.clearCache();
		} else if(event.getSource().equals(WoWClientManager.addRealmlistButton)) {
			WoWClientManager.addRealmlistFrame.setVisible(true);
		} else if(event.getSource().equals(WoWClientManager.addRealmlistButton2)) {
			// Just a domain or ip, should not have spaces
			String realmlistAddr = "set realmlist "+WoWClientManager.addRealmlistTextField.getText().replaceAll(" ","");
			if(!realmlistAddr.equals("")) {
				if(!WoWClientManager.otherRealmlistModel.contains(realmlistAddr)) {
					WoWClientManager.otherRealmlistModel.addElement(realmlistAddr);
					Config.update();
					Config.writeConfiguration();
					WoWClientManager.addRealmlistTextField.setText("");
					WoWClientManager.addRealmlistFrame.setVisible(false);
				} else {
					JOptionPane.showMessageDialog(WoWClientManager.addRealmlistFrame, "Already on list.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		} else if(event.getSource().equals(WoWClientManager.delRealmlistButton)) {
			if(!WoWClientManager.otherRealmlistList.isSelectionEmpty()) {
				int selectedIndex = WoWClientManager.otherRealmlistList.getSelectedIndex();
				String selectedString = WoWClientManager.otherRealmlistModel.get(selectedIndex);
				int choice = JOptionPane.showConfirmDialog(WoWClientManager.mainFrame, "Delete \""+selectedString+"\" ?", "Warning",JOptionPane.YES_NO_OPTION);
				if(choice==JOptionPane.YES_OPTION) {
					WoWClientManager.otherRealmlistModel.remove(selectedIndex);
					Config.update();
					Config.writeConfiguration();
				}
			}
		} else if(event.getSource().equals(WoWClientManager.useRealmlistButton)) {
			if(!WoWClientManager.otherRealmlistList.isSelectionEmpty()) {
				int selectedIndex = WoWClientManager.otherRealmlistList.getSelectedIndex();
				String selectedString = WoWClientManager.otherRealmlistModel.get(selectedIndex);
				try {
					File realmlistFile = new File(WoWClientManager.realmlistWtfRelativePath);
					FileWriter myWriter = new FileWriter(realmlistFile);
					myWriter.write(selectedString);
					myWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(WoWClientManager.mainFrame, "Cannot write realmlist file", "Error", JOptionPane.ERROR_MESSAGE);
				}
				WoWClientManager.loadCurrentRealmlist();
			}
		}
	}
}
