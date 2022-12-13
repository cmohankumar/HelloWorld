import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Objects;
import java.util.Vector;
/**
 * @Classname 	:	SiteCloningUtil
 * @Author 	  	:	Mohan Kumar Chandrabose
 * @Description	:
 * 					This is a UI Utility class for SiteCloning implementation, please enter appropriate
 * values for HostName, UserName, Password, Source Site, Destination Site, and Source Project Path.
 *
 * This class would invoke the static method SiteClone() in SiteCloningImplementation class with all the
 * parameters entered by the user.
 * @Warning		:
 * 					Please create the Destination Site(s) prior to use this utility for Site Cloning.
 * @Note		:
 * 					This utility can be extended to clone multiple sites at single shot, however the current UI
 * 	supports to clone one site at a time.
 *
 */

public class SiteCloningUtil {

	private final JComboBox rpSiteCloneCmbHostName;
	private final JComboBox rpSiteCloneCmbSourceSite;
	private final JComboBox rpSiteCloneCmbDestinationSite;
	private final JComboBox rpSiteCloneCmbSourceProjectPath;
	private final JCheckBox rpSiteCloneChkIsFirstRun;
	
	private final JTextArea rpSiteCloneTxAWarningMessage;
	private final JTextField rpSiteCloneTxtUserName;
	private final JPasswordField rpSiteClonePwdPassword;
	
	private String strHostName;
	private String strUserName;
	private String strPassword;
	private String strSourceSite;
	private String strDestinationSite;
	private String strSourceProjectPath;

	private static final int MAXIMUM_ROWS				= 12;
	private static final int MAXIMUM_COLUMNS			= 2;
	private static final int HEADER_FONT_SIZE			= 20;

	public SiteCloningUtil() {


		Vector<String> hostNameArray = new Vector<>();
		hostNameArray.add("Select One...");
		hostNameArray.add("localhost");
		hostNameArray.add("saerpvgnd01-m");
		hostNameArray.add("saerpvgnd02-m");
		hostNameArray.add("saerpvgnt01-m");
		hostNameArray.add("saerpvgnt02-m");
		hostNameArray.add("saerpvgnq01-m");
		hostNameArray.add("saerpvgnq02-m");
		hostNameArray.add("saerpvgnp01-m");

		Vector<String> sourceSiteArray = new Vector<>();
		sourceSiteArray.add("Select One...");
		sourceSiteArray.add("NokiaRetailPortal");
		sourceSiteArray.add("SiteCloning");

		Vector<String> destinationSiteArray = new Vector<>();
		destinationSiteArray.add("Select One...");
		destinationSiteArray.add("NokiaRetailPortalAus");
		destinationSiteArray.add("NokiaRetailPortalInd");
		destinationSiteArray.add("NokiaRetailPortalNwz");
		destinationSiteArray.add("NokiaRetailPortalBan");
		destinationSiteArray.add("NokiaRetailPortalIna");
		destinationSiteArray.add("NokiaRetailPortalJpn");
		destinationSiteArray.add("NokiaRetailPortalMal");
		destinationSiteArray.add("NokiaRetailPortalPhp");
		destinationSiteArray.add("SiteCloneDest");

		Vector<String> projectPathArray = new Vector<>();
		projectPathArray.add("Select One...");
		projectPathArray.add("/NokiaRetailPortal/Australia");
		projectPathArray.add("/NokiaRetailPortal/India");
		projectPathArray.add("/NokiaRetailPortal/Newzeland");
		projectPathArray.add("/NokiaRetailPortal/Bangladesh");
		projectPathArray.add("/NokiaRetailPortal/Indonesia");
		projectPathArray.add("/NokiaRetailPortal/Japan");
		projectPathArray.add("/NokiaRetailPortal/Malaysia");
		projectPathArray.add("/NokiaRetailPortal/Philippiness");
		projectPathArray.add("/NokiaRetailPortal/SiteCloning");

		JFrame rpSiteCloneFrame = new JFrame("Retail Portal... Site Cloning Utility");
//		rpSiteCloneFrame.setIconImage(new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("NokiaRetailPortal.jpg"))).getImage());

		JButton rpSiteCloneSubmit = new JButton("Submit");
		JButton rpSiteCloneClear = new JButton("Reset Fields");
		
		rpSiteCloneTxtUserName				= new JTextField(50);
		rpSiteCloneCmbDestinationSite		= new JComboBox(destinationSiteArray);

		rpSiteCloneCmbHostName 				= new JComboBox(hostNameArray);
		rpSiteCloneCmbSourceProjectPath		= new JComboBox(projectPathArray);
		rpSiteCloneCmbSourceSite			= new JComboBox(sourceSiteArray);
		rpSiteCloneChkIsFirstRun			= new JCheckBox();
		
		rpSiteClonePwdPassword				= new JPasswordField(50);

		JPanel rpSiteClonePanel = new JPanel();
		JPanel rpSiteCloneTopPanel = new JPanel();
		JPanel rpSiteCloneBottomPanel = new JPanel();
		JPanel rpSiteCloneButtonPanel = new JPanel();
		JPanel rpSiteCloneCosmeticPanel = new JPanel();
		JPanel rpSiteCloneHostNamePanel = new JPanel();
		JPanel rpSiteCloneUserNamePanel = new JPanel();
		JPanel rpSiteClonePasswordPanel = new JPanel();
		JPanel rpSiteCloneSourceSitePanel = new JPanel();
		JPanel rpSiteCloneDestinationSitePanel = new JPanel();
		JPanel rpSiteCloneSourceProjectPathPanel = new JPanel();
		JPanel rpSiteCloneIsFirstRunPanel = new JPanel();

		JLabel rpSiteCloneLblHostName = new JLabel("Host Name *");
		JLabel rpSiteCloneLblUserName = new JLabel("User Name *");
		JLabel rpSiteCloneLblPassword = new JLabel("Password *");
		JLabel rpSiteCloneLblSourceSite = new JLabel("Source Site Name *");
		JLabel rpSiteCloneLblDestinationSite = new JLabel("Destination Site Name *");
		JLabel rpSiteCloneLblSourceProjectPath = new JLabel("Source Project Path *");
		JLabel rpSiteCloneLblIsFirstRun = new JLabel("First Run");
		JLabel rpSiteCloneLblFooterMessage = new JLabel("WARNING: Please create the Destination Site in Admin Console before clicking on Submit Button");
		JLabel rpSiteCloneLblHeaderMessage = new JLabel("Retail Portal Site Cloning Utility");
		rpSiteCloneTxAWarningMessage		= new JTextArea("* - Mandatory fields, Enter Valid Values.");
		
		Font rpSiteCloneTextAreaFont		= new Font(rpSiteCloneTxAWarningMessage.getFont().getName(), Font.BOLD, rpSiteCloneTxAWarningMessage.getFont().getSize());
		Font rpSiteCloneHeaderLabelFont		= new Font(rpSiteCloneLblHeaderMessage.getFont().getName(), Font.BOLD, HEADER_FONT_SIZE);

		rpSiteCloneTxAWarningMessage.setEditable(false);
		rpSiteCloneTxAWarningMessage.setLineWrap(true);
		rpSiteCloneLblHeaderMessage.setFont(rpSiteCloneHeaderLabelFont);
		rpSiteCloneTxAWarningMessage.setFont(rpSiteCloneTextAreaFont);
		rpSiteCloneTxAWarningMessage.setBackground(new Color(205,247,251));
				
	    Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
	    
	    int frameWidth 		= rpSiteCloneFrame.getSize().width;
	    int frameHeight 	= rpSiteCloneFrame.getSize().height;
	    
	    int xCoordinate 	= (screenDimension.width - frameWidth) / 4;
	    int yCoordinate 	= (screenDimension.height - frameHeight) / 4;
	    
	    int xSizeCoordinate = (screenDimension.width - frameWidth) / 2;
	    int ySizeCoordinate = (screenDimension.height - frameHeight) / 2;
	    
	    rpSiteCloneFrame.setLocation(xCoordinate, yCoordinate);
	    rpSiteCloneFrame.setSize(xSizeCoordinate, ySizeCoordinate);
	    
		rpSiteClonePanel.setBackground(new Color(205,247,251));
		rpSiteCloneTopPanel.setBackground(new Color(205,247,251));
		rpSiteCloneBottomPanel.setBackground(new Color(205,247,251));
		rpSiteCloneButtonPanel.setBackground(new Color(205,247,251));
		rpSiteCloneCosmeticPanel.setBackground(new Color(205,247,251));
		rpSiteCloneHostNamePanel.setBackground(new Color(205,247,251));
		rpSiteCloneUserNamePanel.setBackground(new Color(205,247,251));
		rpSiteClonePasswordPanel.setBackground(new Color(205,247,251));
		rpSiteCloneSourceSitePanel.setBackground(new Color(205,247,251));
		rpSiteCloneDestinationSitePanel.setBackground(new Color(205,247,251));
		rpSiteCloneSourceProjectPathPanel.setBackground(new Color(205,247,251));
		rpSiteCloneIsFirstRunPanel.setBackground(new Color(205,247,251));
		
		rpSiteCloneSubmit.setMnemonic(KeyEvent.VK_S);
		rpSiteCloneSubmit.addActionListener(new ActionListener() {
			
		boolean	isFirstRunSelected	= false;

		public void actionPerformed(ActionEvent e) {
			
				if (rpSiteCloneCmbHostName.getSelectedIndex() == 0)
				{
					rpSiteCloneTxAWarningMessage.setText("Error : Host Name field is mandatory. Please enter valid value for Host Name...");
				}
				else if (rpSiteCloneCmbSourceSite.getSelectedIndex() == 0)
				{
					rpSiteCloneTxAWarningMessage.setText("Error : Source Site field is mandatory. Please select valid value for Source Site...");
				}
				else if (rpSiteCloneCmbDestinationSite.getSelectedIndex() == 0)
				{
					rpSiteCloneTxAWarningMessage.setText("Error : Destination Site field is mandatory. Please enter valid value for Destination Site...");
				}
				else if(rpSiteCloneCmbSourceProjectPath.getSelectedIndex() == 0)
				{
					rpSiteCloneTxAWarningMessage.setText("Error : Source Project Path field is mandatory. Please select valid Source Project Path...");
				}
				else if (rpSiteCloneTxtUserName.getText() != null && rpSiteCloneTxtUserName.getText().equalsIgnoreCase(""))
				{
					rpSiteCloneTxAWarningMessage.setText("Error : User Name field is mandatory. Please enter valid value for User Name...");
				}
				else if (rpSiteClonePwdPassword.getPassword() != null && new String(rpSiteClonePwdPassword.getPassword()).equalsIgnoreCase(""))
				{
					rpSiteCloneTxAWarningMessage.setText("Error : Password field is mandatory. Please enter valid value for Password...");
				}
				else
				{
					rpSiteCloneTxAWarningMessage.setText("");
					
					strUserName 			= rpSiteCloneTxtUserName.getText();
					strPassword 			= new String (rpSiteClonePwdPassword.getPassword());
					strHostName 			= Objects.requireNonNull(rpSiteCloneCmbHostName.getSelectedItem()).toString();
					strSourceSite 			= Objects.requireNonNull(rpSiteCloneCmbSourceSite.getSelectedItem()).toString();
					strDestinationSite 		= Objects.requireNonNull(rpSiteCloneCmbDestinationSite.getSelectedItem()).toString();
					strSourceProjectPath 	= Objects.requireNonNull(rpSiteCloneCmbSourceProjectPath.getSelectedItem()).toString();
					isFirstRunSelected 		= rpSiteCloneChkIsFirstRun.isSelected();
					
					rpSiteCloneTxAWarningMessage.setText("Site Cloning in Progress... Please wait until its Completed.");
					/*try
					{
						SiteCloningImplementation.SiteClone(strHostName, strUserName, strPassword, strSourceSite, strDestinationSite, strSourceProjectPath, isFirstRunSelected);
						rpSiteCloneTxAWarningMessage.setText("Site Cloning is Completed... Please Check your new site : " + strDestinationSite + " for content integrity.");
					}
					catch (Exception ex)
					{
						rpSiteCloneTxAWarningMessage.setText("Exception Occured in Site Cloning : " + ex + "Please correct the error and proceed.");
					}*/
				}
			}
		});

		rpSiteCloneClear.setMnemonic(KeyEvent.VK_R);
		rpSiteCloneClear.addActionListener(new ActionListener() {

		public void actionPerformed(ActionEvent e) {
			
			rpSiteCloneTxtUserName.setText("");
			rpSiteClonePwdPassword.setText("");
			
			rpSiteCloneChkIsFirstRun.setSelected(false);
			rpSiteCloneCmbHostName.setSelectedIndex(0);
			rpSiteCloneCmbSourceSite.setSelectedIndex(0);
			rpSiteCloneCmbDestinationSite.setSelectedIndex(0);
			rpSiteCloneCmbSourceProjectPath.setSelectedIndex(0);
			
			rpSiteCloneTxAWarningMessage.setText("* - Mandatory fields, Enter Valid Values.");
			}
		});
		
		GridLayout rpSiteCloneGridLayout = new GridLayout(MAXIMUM_ROWS, MAXIMUM_COLUMNS);
		rpSiteClonePanel.setLayout(rpSiteCloneGridLayout);
		
		rpSiteCloneTopPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		rpSiteCloneBottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		rpSiteCloneButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		rpSiteCloneCosmeticPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		rpSiteCloneHostNamePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		rpSiteClonePasswordPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		rpSiteCloneUserNamePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		rpSiteCloneSourceSitePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		rpSiteCloneDestinationSitePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		rpSiteCloneSourceProjectPathPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		rpSiteCloneIsFirstRunPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		rpSiteCloneButtonPanel.add(rpSiteCloneSubmit);
		rpSiteCloneButtonPanel.add(rpSiteCloneClear);
		
		rpSiteCloneHostNamePanel.add(rpSiteCloneLblHostName);
		rpSiteCloneHostNamePanel.add(rpSiteCloneCmbHostName);
		
		rpSiteCloneUserNamePanel.add(rpSiteCloneLblUserName);
		rpSiteCloneUserNamePanel.add(rpSiteCloneTxtUserName);
		
		rpSiteClonePasswordPanel.add(rpSiteCloneLblPassword);
		rpSiteClonePasswordPanel.add(rpSiteClonePwdPassword);
		
		rpSiteCloneSourceSitePanel.add(rpSiteCloneLblSourceSite);
		rpSiteCloneSourceSitePanel.add(rpSiteCloneCmbSourceSite);
		
		rpSiteCloneDestinationSitePanel.add(rpSiteCloneLblDestinationSite);
		rpSiteCloneDestinationSitePanel.add(rpSiteCloneCmbDestinationSite);
		
		rpSiteCloneSourceProjectPathPanel.add(rpSiteCloneLblSourceProjectPath);
		rpSiteCloneSourceProjectPathPanel.add(rpSiteCloneCmbSourceProjectPath);

		rpSiteCloneIsFirstRunPanel.add(rpSiteCloneLblIsFirstRun);
		rpSiteCloneIsFirstRunPanel.add(rpSiteCloneChkIsFirstRun);
		
		rpSiteCloneTopPanel.add(rpSiteCloneLblHeaderMessage);
		rpSiteCloneBottomPanel.add(rpSiteCloneLblFooterMessage);
		
		rpSiteClonePanel.add(rpSiteCloneTopPanel);
		rpSiteClonePanel.add(rpSiteCloneHostNamePanel);
		rpSiteClonePanel.add(rpSiteCloneSourceSitePanel);
		rpSiteClonePanel.add(rpSiteCloneDestinationSitePanel);
		rpSiteClonePanel.add(rpSiteCloneSourceProjectPathPanel);
		rpSiteClonePanel.add(rpSiteCloneIsFirstRunPanel);
		rpSiteClonePanel.add(rpSiteCloneUserNamePanel);
		rpSiteClonePanel.add(rpSiteClonePasswordPanel);
		
		rpSiteClonePanel.add(rpSiteCloneButtonPanel);
		rpSiteClonePanel.add(rpSiteCloneBottomPanel);
		rpSiteClonePanel.add(rpSiteCloneCosmeticPanel);
		rpSiteClonePanel.add(rpSiteCloneTxAWarningMessage);
		
		rpSiteCloneFrame.getContentPane().add(rpSiteClonePanel, BorderLayout.CENTER);
		rpSiteCloneFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		rpSiteCloneFrame.setResizable(false);
		rpSiteCloneFrame.setVisible(true);
		rpSiteCloneFrame.pack();
	}

	public static void main(String[] args) {
		
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Look and Feel Settings Exception " + e);
		}
		new SiteCloningUtil();
	}
	
	public String toString() {
		
		return new String("");
	}
}