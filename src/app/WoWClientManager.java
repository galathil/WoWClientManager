package app;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;

import com.formdev.flatlaf.FlatDarculaLaf;

import listeners.ButtonsListener;
import listeners.CheckboxesListener;

public class WoWClientManager {
	
	public static JFrame mainFrame;
	public static JDialog addRealmlistFrame;
	
	private static JTextField currentRealmlistField;
	public static JTextField addRealmlistTextField;
	public static String realmlistWtfRelativePath;
	public static JButton openWowDirButton;
	public static JButton startWowButton;
	public static JButton clearCacheNow;
	public static JButton useRealmlistButton;
	public static JButton addRealmlistButton;
	public static JButton delRealmlistButton;
	public static JButton addRealmlistButton2; // In JDialog frame
	public static JCheckBox keepOpenCheck;
	public static JCheckBox clearCacheOnStartCheck;
	public static DefaultListModel<String> otherRealmlistModel;
	public static JList<String> otherRealmlistList;

	public static void main(String[] args) {
		realmlistWtfRelativePath="";
		
		// Initialize swing theme
		try {
    	    FlatDarculaLaf.install();
    	    JFrame.setDefaultLookAndFeelDecorated(true);
    	    JDialog.setDefaultLookAndFeelDecorated(true);
    	} catch( Exception ex ) {
    		ex.printStackTrace();
    		JOptionPane.showMessageDialog(null, "Failed to initialize LaF", "Error", JOptionPane.ERROR_MESSAGE);
    	}
		
		// Need to be in the same repo as wow.exe
		File wowExe = new File("wow.exe");
		if(!wowExe.exists()) {
			JOptionPane.showMessageDialog(null, "Wow.exe not found. Please, place the manager in WoW Directory.", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		
		// Find realmlist.wtf in wow dir
		// Need to find it entire wow directory. It can be placed at root or langage folder.
		try (Stream<Path> walkStream = Files.walk(Paths.get(System.getProperty("user.dir")))) {
		    walkStream.filter(p -> p.toFile().isFile()).forEach(f -> {
		    	String path = f.toAbsolutePath().toString().replace(System.getProperty("user.dir"), "");
		    	path=path.startsWith(File.separator) ? path.substring(1) : path;
		    	if(path.toLowerCase().endsWith("realmlist.wtf")) {
		    		realmlistWtfRelativePath = path;
		    	}
		    });
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Internal error : cannot walk user.dir", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		if(realmlistWtfRelativePath.equals("")) {
			JOptionPane.showMessageDialog(null, "realmlist.wtf not found. Please, place the manager in WoW Directory.", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		
		// Create and show the frame
		initMainFrame();
		
		// Search and display current realmlist value
		loadCurrentRealmlist();
		
		// Init configuration
		Config.initConfiguration();
		
		// Init "Add realmlist" frame
		initAddRealmlistFrame();
	}
	
	private static void initMainFrame() {
		mainFrame = new JFrame("WoW Client Manager 1.0.0 (by Galathil)");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setResizable(false);
		
		try {
			ImageIcon icon = new ImageIcon(ImageIO.read(WoWClientManager.class.getResource("/assets/icon_32x32.png")));
			mainFrame.setIconImage(icon.getImage());
		} catch(Exception exx) {
			exx.printStackTrace();
		}

		Container mainPanel = mainFrame.getContentPane();
		mainPanel.setLayout(new GridBagLayout());
		buildCurrentRealmlistZone(mainPanel);
		buildOtherRealmlistZone(mainPanel);
		buildCacheZone(mainPanel);
		buildKeepOpenZone(mainPanel);
		buildSouthButtonsZone(mainPanel);
		
		mainFrame.pack();
		
		// Center frame on window
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		mainFrame.setLocation(dim.width/2-mainFrame.getSize().width/2, dim.height/2-mainFrame.getSize().height/2);
		
		mainFrame.setVisible(true);
	}
	
	private static void buildCurrentRealmlistZone(Container mainPanel) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0,0,0,0);
	    gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridheight = 1;
	    gbc.gridwidth = GridBagConstraints.REMAINDER;
	    gbc.weightx = 1;
	    
		Border currentRealmlistBorder = BorderFactory.createTitledBorder("Current realmlist :");
		JPanel currentRealmlistPanel = new JPanel(new GridBagLayout());
		currentRealmlistField = new JTextField("");
		currentRealmlistField.setEditable(false);
		currentRealmlistPanel.setBorder(currentRealmlistBorder);

		currentRealmlistPanel.add(currentRealmlistField,gbc);
	    mainPanel.add(currentRealmlistPanel,gbc);
	}
	
	private static void buildOtherRealmlistZone(Container mainPanel) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0,0,0,0);
	    gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridheight = 1;
	    gbc.gridwidth = GridBagConstraints.REMAINDER;
	    gbc.weightx = 1;
	    
	    // Other realmlist zone
	    Border otherRealmlistsBorder = BorderFactory.createTitledBorder("Other realmlists :");
	    JPanel otherRealmlistPanel = new JPanel(new GridBagLayout());
	    otherRealmlistPanel.setBorder(otherRealmlistsBorder);
	    
	    GridBagConstraints gbc2 = new GridBagConstraints();

	    JPanel leftPanel = new JPanel(new GridBagLayout());
	    buildOtherRealmlistZoneLeftPanel(leftPanel);
	    gbc2.insets = new Insets(0,0,0,0);
	    gbc2.fill = GridBagConstraints.BOTH;
	    gbc2.weightx = 1;
	    otherRealmlistPanel.add(leftPanel,gbc2);
	    
	    JPanel rightPanel = new JPanel(new GridBagLayout());
	    buildOtherRealmlistZoneRightPanel(rightPanel);
	    gbc2.gridwidth = GridBagConstraints.REMAINDER;
	    gbc2.fill = GridBagConstraints.BOTH;
	    gbc2.weightx = 0;
	    otherRealmlistPanel.add(rightPanel,gbc2);

	    mainPanel.add(otherRealmlistPanel,gbc);
	}
	
	private static void buildOtherRealmlistZoneLeftPanel(Container mainPanel) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0,0,0,0);
	    gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridheight = 1;
	    gbc.gridwidth = GridBagConstraints.REMAINDER;
	    gbc.weightx = 1;
	    otherRealmlistModel = new DefaultListModel<String>();
	    otherRealmlistList = new JList<String>(otherRealmlistModel);
	    otherRealmlistList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    otherRealmlistList.setLayoutOrientation(JList.VERTICAL);
	    JScrollPane otherRealmlistScroll = new JScrollPane(otherRealmlistList);
	    mainPanel.add(otherRealmlistScroll,gbc);
	}
	
	private static void buildOtherRealmlistZoneRightPanel(Container mainPanel) {
		GridBagConstraints gbc = new GridBagConstraints();
	    gbc.insets = new Insets(0,0,0,0);
	    gbc.gridwidth = GridBagConstraints.REMAINDER;
	    gbc.anchor = GridBagConstraints.NORTH;
		
		useRealmlistButton = new JButton("Use");
		useRealmlistButton.addActionListener(new ButtonsListener());
		addRealmlistButton = new JButton("Add");
		addRealmlistButton.addActionListener(new ButtonsListener());
		delRealmlistButton = new JButton("Delete");
		delRealmlistButton.addActionListener(new ButtonsListener());

		mainPanel.add(useRealmlistButton,gbc);
		mainPanel.add(addRealmlistButton,gbc);

		gbc.weighty = 1; // Make all button stick on top
		mainPanel.add(delRealmlistButton,gbc);
	}
	
	private static void buildCacheZone(Container mainPanel) {
		JPanel cachePanel = new JPanel(new GridBagLayout());
		
		Border cacheBorder = BorderFactory.createTitledBorder("Cache :");
		cachePanel.setBorder(cacheBorder);
		
		clearCacheOnStartCheck = new JCheckBox("Clear on startup");
		clearCacheOnStartCheck.addActionListener(new CheckboxesListener());
		GridBagConstraints gbc2 = new GridBagConstraints();
		gbc2.weightx=1;
		gbc2.anchor=GridBagConstraints.WEST;
		cachePanel.add(clearCacheOnStartCheck,gbc2);
		
		clearCacheNow = new JButton("Clear now");
		clearCacheNow.addActionListener(new ButtonsListener());
		cachePanel.add(clearCacheNow);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0,0,0,0);
	    gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridheight = 1;
	    gbc.gridwidth = GridBagConstraints.REMAINDER;
	    gbc.weightx = 1;
		
		mainPanel.add(cachePanel,gbc);
	}
	
	public static void buildKeepOpenZone(Container mainPanel) {
		JPanel keepOpenPanel = new JPanel(new GridBagLayout());

	    keepOpenCheck = new JCheckBox("Keep WoW Client Manager open");
	    keepOpenCheck.addActionListener(new CheckboxesListener());
	    GridBagConstraints gbc2 = new GridBagConstraints();
	    gbc2.anchor=GridBagConstraints.EAST;
	    gbc2.weightx=1;
	    keepOpenPanel.add(keepOpenCheck,gbc2);
	    
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0,0,0,0);
	    gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridheight = 1;
	    gbc.gridwidth = GridBagConstraints.REMAINDER;
	    gbc.weightx = 1;
		mainPanel.add(keepOpenPanel,gbc);
	}
	
	public static void buildSouthButtonsZone(Container mainPanel) {
		JPanel southButtonsPanel = new JPanel(new GridBagLayout());

		GridBagConstraints gbc2 = new GridBagConstraints();
		gbc2.weightx=1;
		gbc2.fill = GridBagConstraints.HORIZONTAL;
		openWowDirButton = new JButton("Open WoW dir");
		openWowDirButton.addActionListener(new ButtonsListener());
		southButtonsPanel.add(openWowDirButton,gbc2);
		startWowButton = new JButton("Launch WoW");
		startWowButton.addActionListener(new ButtonsListener());
		southButtonsPanel.add(startWowButton,gbc2);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(20,0,0,0);
	    gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridheight = 1;
	    gbc.gridwidth = GridBagConstraints.REMAINDER;
	    gbc.weightx = 1;
	    mainPanel.add(southButtonsPanel,gbc);
	}
	
	public static void clearCache() {
		try {
			deleteDirectoryStream(Paths.get(System.getProperty("user.dir")+File.separator+"Cache"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// https://softwarecave.org/2018/03/24/delete-directory-with-contents-in-java/
	private static void deleteDirectoryStream(Path path) throws IOException {
		  Files.walk(path)
		    .sorted(java.util.Comparator.reverseOrder())
		    .map(Path::toFile)
		    .forEach(File::delete);
	}
	
	private static void initAddRealmlistFrame() {
		addRealmlistFrame = new JDialog(mainFrame, "Add realmlist", true);
		addRealmlistFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		addRealmlistFrame.setResizable(false);
		
		Container mainPanel = addRealmlistFrame.getContentPane();
		mainPanel.setLayout(new GridBagLayout());
		
		addRealmlistTextField = new JTextField(20);
		addRealmlistButton2 = new JButton("Add");
		addRealmlistButton2.addActionListener(new ButtonsListener());
		JLabel setRealmlistLabel = new JLabel("set realmlist ");
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5,5,5,5);
	    gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridheight = 1;
	    gbc.weightx = 1;
		
	    mainPanel.add(setRealmlistLabel,gbc);
		mainPanel.add(addRealmlistTextField,gbc);
		mainPanel.add(addRealmlistButton2,gbc);
		
		addRealmlistFrame.pack();
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		addRealmlistFrame.setLocation(dim.width/2-addRealmlistFrame.getSize().width/2, dim.height/2-addRealmlistFrame.getSize().height/2);
		
		addRealmlistFrame.setVisible(false);
	}
	
	public static void loadCurrentRealmlist() {
		File realmlistFile = new File(realmlistWtfRelativePath);
		try (BufferedReader br = new BufferedReader(new FileReader(realmlistFile))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		       if(!line.equals("") && !line.startsWith("#")) { // Ignore commented lines
		    	   currentRealmlistField.setText(line);
		       }
		    }
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "realmlist.wtf found but not readable.", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		if(currentRealmlistField.getText().equals("")) {
			JOptionPane.showMessageDialog(null, "No active realmlist found in realmlist.wtf", "Warning", JOptionPane.WARNING_MESSAGE);
		}
	}
	
}
