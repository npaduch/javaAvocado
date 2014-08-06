import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;


public class login extends JFrame implements ActionListener{

	public JTextField email;
	public JPasswordField password;
	public JButton submit;
	
	public login(){
		 JPanel mainLayout = new JPanel();
		 mainLayout.setLayout(new BoxLayout(mainLayout, BoxLayout.Y_AXIS));
		 
		 JLabel emailLabel = new JLabel("Email");
		 JLabel passwordLabel = new JLabel("Password");
		 Container fields = new Container();
		 Container firstColumn = new Container();
		 Container secondColumn = new Container();
		 fields.setLayout(new BoxLayout(fields, BoxLayout.X_AXIS));
		 firstColumn.setLayout(new BoxLayout(firstColumn, BoxLayout.Y_AXIS));
		 secondColumn.setLayout(new BoxLayout(secondColumn, BoxLayout.Y_AXIS));
		
		 email = new JTextField();
		 email.addKeyListener(EnterCheck);
		 password = new JPasswordField();
		 password.addKeyListener(EnterCheck);
		 
		 firstColumn.add(emailLabel);
		 firstColumn.add(Box.createRigidArea(new Dimension(0,10)));
		 firstColumn.add(passwordLabel);
		 secondColumn.add(email);
		 secondColumn.add(Box.createRigidArea(new Dimension(0,10)));
		 secondColumn.add(password);
		 
		 submit = new JButton();
		 submit.setText("Login");
		 submit.setActionCommand("Login");
		 submit.addActionListener(this);
		 
		 fields.add(firstColumn);
		 fields.add(Box.createRigidArea(new Dimension(20,0)));
		 fields.add(secondColumn);
		 
		 
		 mainLayout.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		 mainLayout.add(fields);
		 mainLayout.add(Box.createRigidArea(new Dimension(0,10)));
		 mainLayout.add(submit);
		 
		 add(mainLayout);
		 setDefaultCloseOperation(EXIT_ON_CLOSE);
		 setSize(300,150);
		 setVisible(true);
		 setTitle("Avocado Login");
	}
	@Override
	public void actionPerformed(ActionEvent action) {
		if(action.getActionCommand().equals("Login")){
		    AvocadoSignTest signTest = new AvocadoSignTest();
		    AvocadoSignTest.AuthClient authClient = signTest.new AuthClient();
			String tempe = "n.paduch1@gmail.com";
			char tempp[] = {'l','a','u','r','e','n','n','o','l','a','n'};
		    //authClient.setCredentials(email.getText(),password.getPassword());
		    authClient.setCredentials(tempe, tempp);
		    AvocadoSignTest.AvocadoAPI api = signTest.new AvocadoAPI(authClient);
		    api.updateFromCommandLine();
			new window1(api);
			dispose();	// close window
		}
	}
	
	KeyAdapter EnterCheck = new KeyAdapter(){
		public void keyReleased(KeyEvent e) {
		}

		public void keyTyped(KeyEvent e) {
		}

		public void keyPressed(KeyEvent e) {
			System.out.println("Pressed "+Integer.toString(e.getKeyCode()));
			if(e.getKeyCode() == KeyEvent.VK_ENTER){
				submit.doClick();
			}
		}
	};

}
