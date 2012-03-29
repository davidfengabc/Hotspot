package frame;


import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Rectangle;

public class HotspotFrame extends JFrame implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private Vector<String> interfaces = new Vector<String>(1,1);
	private JComboBox comboBoxIface;
	private JButton btnActivate, btnDeactivate;
	private String[] command = {"ps", "-e"};

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HotspotFrame frame = new HotspotFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public HotspotFrame() {
		setBounds(new Rectangle(0, 0, 200, 200));
		//Vector<String> interfaces = new Vector<String>(1,1);
		getInterfaces();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		comboBoxIface = new JComboBox(interfaces);
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblHotspotSettings = new JLabel("Hotspot Settings");
		lblHotspotSettings.setFont(new Font("Dialog", Font.BOLD, 16));
		lblHotspotSettings.setBounds(12, 12, 195, 15);
		contentPane.add(lblHotspotSettings);
		comboBoxIface.setFont(new Font("Dialog", Font.PLAIN, 12));
		comboBoxIface.setBounds(12, 69, 218, 24);
		contentPane.add(comboBoxIface);
		
		btnActivate = new JButton("Activate");
		btnActivate.setBounds(12, 115, 117, 25);
		btnActivate.setActionCommand("activate");
		contentPane.add(btnActivate);
		
		btnDeactivate = new JButton("Deactivate");
		btnDeactivate.setBounds(153, 115, 117, 25);
		btnDeactivate.setActionCommand("deactivate");
		btnDeactivate.setEnabled(false);
		contentPane.add(btnDeactivate);

		if (getHotspotState()) {
			btnDeactivate.setEnabled(true);
			btnActivate.setEnabled(false);
		} else {
			btnDeactivate.setEnabled(false);
			btnActivate.setEnabled(true);
		}

		
		btnActivate.addActionListener(this);
		btnDeactivate.addActionListener(this);
		
		JLabel lblConnectionToShare = new JLabel("Connection to share:");
		lblConnectionToShare.setBounds(12, 49, 195, 15);
		contentPane.add(lblConnectionToShare);
	}
	
	public void actionPerformed(ActionEvent e) {
        if ("deactivate".equals(e.getActionCommand())) {
            btnActivate.setEnabled(true);
            btnDeactivate.setEnabled(false);
        } else {
            btnActivate.setEnabled(false);
            btnDeactivate.setEnabled(true);
        }
    }
	
	/*
	 * Check to see if hostapd or dnsmasq are running
	 */
	private boolean getHotspotState() {
		boolean hotspotState = false;
		try {			
			ProcessBuilder pb = new ProcessBuilder(command);
			Process proc = pb.start();
			BufferedReader br = new BufferedReader(new InputStreamReader (proc.getInputStream()));
			String line;
			while ((line = br.readLine()) != null) {
				if((line.indexOf("hostapd") != -1) || line.indexOf("dnsmasq") != -1) {
					hotspotState = true;
					break;
				}
	        }
			br.close();
		} catch (IOException e) {
			System.out.println("error");
		}
		
		return hotspotState;
	}
	

	/*
	 * Populate interfaces with all active network interfaces except wlan0 and lo
	 */
	private void getInterfaces() {

		try {
			Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
			for(NetworkInterface iface : Collections.list(ifaces)) {
				String ifaceName = iface.getDisplayName();
				if ((ifaceName.equals("lo")) || (ifaceName.equals("wlan0")))
					continue;
				String textAddrs = " ";
				Enumeration<InetAddress> addrs= iface.getInetAddresses();
				for(InetAddress addr : Collections.list(addrs)) {
					textAddrs = textAddrs + " " + addr.getHostAddress();
				}
				interfaces.addElement(ifaceName + textAddrs);
			}

		} catch (SocketException e) {
			System.out.println("error");
		}
		
	}
}
