import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;
 
public class JTextFieldHintListener implements FocusListener {
	private String hintText;
	private JTextField textField;
	public JTextFieldHintListener(JTextField jTextField, String hintText) {
		this.textField = jTextField;
		this.hintText = hintText;
		jTextField.setText(hintText);  // Display hint text
		jTextField.setForeground(Color.GRAY);
	}
 
	@Override
	public void focusGained(FocusEvent e) {
		// Get focus, clear the field
		String temp = textField.getText();
		if(temp.equals(hintText)) {
			textField.setText("");
			textField.setForeground(Color.BLACK);
		}
		
	}
 
	@Override
	public void focusLost(FocusEvent e) {	
		// No focus, no input, display hint text;
		String temp = textField.getText();
		if(temp.equals("")) {
			textField.setForeground(Color.GRAY);
			textField.setText(hintText);
		}
		
	}
 
}