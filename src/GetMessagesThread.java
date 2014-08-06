import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;


public class GetMessagesThread implements Runnable{
	
	private AvocadoSignTest.AvocadoAPI api;
	private JEditorPane myBox;
	private HTMLEditorKit myEditor;
	
	private int counter = 0;
	
	public GetMessagesThread(AvocadoSignTest.AvocadoAPI tempApi, JEditorPane box, HTMLEditorKit kit){
		this.api = tempApi;
		this.myBox = box;
		this.myEditor = kit;
	}
	
	@Override
	public void run() {
		while(counter < 600){
			try {
				System.out.println("Checking.");
				Document doc = myBox.getDocument();
				myEditor.insertHTML((HTMLDocument) doc, doc.getLength(), window1.api.getMessages(), 0, 0, null);
				myBox.setCaretPosition(doc.getLength());
				counter++;
				Thread.sleep(30*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
