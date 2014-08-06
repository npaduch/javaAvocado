import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLEditorKit;

class window1 extends JFrame implements ActionListener{

	// elements

	  //private JLabel label;
	  //private JTextField conversation;
	  private JTextField message;
	  //private JTextField tf;
	  private JButton send;
	  private JButton login;
	  private JButton logout;
	  private JEditorPane ta;
	  private HTMLEditorKit htmlEdit;
	  public static AvocadoSignTest.AvocadoAPI api;
	  
	  
  // Constructor:
  public window1(AvocadoSignTest.AvocadoAPI _api) {
	  
	  api = _api;
	  
	  Container mainLayout = new Container();
	  mainLayout.setLayout(new BoxLayout(mainLayout, BoxLayout.Y_AXIS));
	  
	  // main items
	  //label = new JLabel("Email:", SwingConstants.CENTER);
	  ta = new JEditorPane("text/html", "");
	  ta.setBackground(new Color(0,0,0));
	  
	  message = new JTextField();
	  message.setText("");
	  message.addKeyListener(EnterCheck);
	  
	  
	  
	  // buttons
	  login = new JButton("Login");
	  login.addActionListener(this);
	  logout = new JButton("Logout");
	  logout.addActionListener(this);
	  logout.setEnabled(false);
	  send = new JButton("Send");
	  send.setActionCommand("Send");
	  send.addActionListener(this);
	  
	  Container textPane = new Container();
	  textPane.setLayout(new BoxLayout(textPane, BoxLayout.Y_AXIS));
	  ta.setSize(new Dimension(290,400));
	  ta.setText((String)api.getMessages());
	  ta.setEditable(false);
	  htmlEdit = new HTMLEditorKit();
	  //ta.setEditorKit(htmlEdit);
	  textPane.add(new JScrollPane(ta));
	  mainLayout.add(textPane);
	  textPane.setVisible(true);
	  
	  Container messagePane = new Container();
	  messagePane.setLayout(new BoxLayout(messagePane, BoxLayout.X_AXIS));
	  message.setPreferredSize(new Dimension(290,40));
	  messagePane.add(message);
	  messagePane.add(send);
	  messagePane.setPreferredSize(new Dimension(290,40));
	  messagePane.setVisible(true);
	  mainLayout.add(messagePane);
	  mainLayout.setVisible(true);
	  
	  add(mainLayout);
	  
	  Thread thread = new Thread(new GetMessagesThread(api,ta,htmlEdit));
	  thread.start();
	  
	  setTitle("Avocado");
	  
	  setPreferredSize(new Dimension(290,500));
	  
	  pack();
	  setDefaultCloseOperation(EXIT_ON_CLOSE);
	  setSize(300,450);
	  setVisible(true);
	  
	  message.setEditable(true);
	  message.requestFocusInWindow();
  }

  public void actionPerformed(ActionEvent e){
	 System.out.print("Something Happened\n");
	 String messageText = message.getText(); 
	 System.out.println("Sending: "+messageText);
	 if(messageText.equals(""))	// Check if send hit without message
		 return;
	 api.sendMessage(messageText);
	 message.setText("");
  }
  
  public static void main(String[] args) {
    new login();
  } //main
  
	KeyAdapter EnterCheck = new KeyAdapter(){
		public void keyReleased(KeyEvent e) {
		}

		public void keyTyped(KeyEvent e) {
		}

		public void keyPressed(KeyEvent e) {
			System.out.println("Pressed "+Integer.toString(e.getKeyCode()));
			if(e.getKeyCode() == KeyEvent.VK_ENTER){
				send.doClick();
			}
		}
	};
	
} //class EmptyFrame1