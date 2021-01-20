package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import app.Config;
import app.WoWClientManager;

public class CheckboxesListener implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent e) {
		Config.keepOpen=WoWClientManager.keepOpenCheck.isSelected();
		Config.clearAtStart=WoWClientManager.clearCacheOnStartCheck.isSelected();
		Config.writeConfiguration();
	}

}
