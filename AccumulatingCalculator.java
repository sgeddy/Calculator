import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.math.BigDecimal;
import javax.swing.*;
import javax.swing.text.*;

public class AccumulatingCalculator implements ActionListener, KeyListener
{
	// Instance variables
	JButton clearButton = new JButton("Clear");
	JButton logButton = new JButton("Log");
	JFrame calcWindow = new JFrame();
	JFrame logWindow = new JFrame();
	JTextField inputField = new JTextField();
	JTextPane pastInputsPane = new JTextPane();
	JScrollPane logScrollPane = new JScrollPane(pastInputsPane);
	StyledDocument doc = pastInputsPane.getStyledDocument();
	SimpleAttributeSet right = new SimpleAttributeSet();
	JPanel buttonsPanel = new JPanel();
	GridLayout buttonsLayout = new GridLayout(1,3,0,0); // Rows, columns, horizontal gap, vertical gap
	JButton addButton = new JButton("Add");
	BigDecimal total = new BigDecimal(0);
	JLabel totalLabel = new JLabel("Total = 0",JLabel.RIGHT);
	boolean decimalTyped = false;
	boolean minusTyped = false;
	boolean plusTyped = false;
	JLabel errorLabel = new JLabel("",JLabel.CENTER);
	JPanel centerPanel = new JPanel();
	GridLayout centerLayout = new GridLayout(2,1,0,0);
	boolean valid = false;
	boolean totalCleared = true;

	public AccumulatingCalculator()
	{
		// Build the number buttons panel
		buttonsPanel.setLayout(buttonsLayout);
		buttonsPanel.add(addButton);
		buttonsPanel.add(clearButton);
		buttonsPanel.add(logButton);
		addButton.addActionListener(this);
		clearButton.addActionListener(this);
		logButton.addActionListener(this);
		
		// Build the past inputs window
		StyleConstants.setAlignment(right, StyleConstants.ALIGN_RIGHT);
		doc.setParagraphAttributes(0, doc.getLength(), right, false);
		pastInputsPane.setEditable(false);
		logWindow.add(logScrollPane);
		logWindow.setSize(300,300);
		logWindow.setLocation(1050,300);
		logWindow.setTitle("Log");
		
		// Build the input field
		inputField.setHorizontalAlignment(JTextField.RIGHT);
		inputField.addKeyListener(this);
		
		// Build the error label
		errorLabel.setForeground(Color.red);
		
		// Build the center panel
		centerPanel.setLayout(centerLayout);
		centerPanel.add(inputField);
		centerPanel.add(errorLabel);
		
		// Build the main calculator window
		calcWindow.getContentPane().add(buttonsPanel,"South");
		calcWindow.getContentPane().add(centerPanel,"Center");
		calcWindow.getContentPane().add(totalLabel,"North");
		calcWindow.setSize(250,120);
		calcWindow.setLocation(800,300);
		calcWindow.getRootPane().setDefaultButton(addButton); // Hitting "enter" will trigger the equals button
		calcWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		calcWindow.setTitle("Calculator");
		calcWindow.setVisible(true);
	}

	public static void main(String[] args)
	{
		new AccumulatingCalculator();
	}

	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getSource() == addButton)
		{
			if(inputField.getText().equals("")) return;
			totalCleared = false;
			BigDecimal inputNum = new BigDecimal(inputField.getText().replaceAll(",", ""));
			total = total.add(inputNum);
			if(minusTyped == false) pastInputsPane.setText(pastInputsPane.getText() + "+" + inputField.getText() + "\n");
			else pastInputsPane.setText(pastInputsPane.getText() + inputField.getText() + "\n");
			inputField.setText("");
			totalLabel.setText("Total = " + total.toPlainString());
			decimalTyped = false;
			minusTyped = false;
			plusTyped = false;
			errorLabel.setText("");
		}
		if(ae.getSource() == clearButton)
		{
			if(totalCleared == false)
			{
				total = BigDecimal.ZERO;
				pastInputsPane.setText(pastInputsPane.getText() + "Total cleared.\n");
				totalLabel.setText("Total = " + total.toPlainString());
			}
			totalCleared = true;
			inputField.setText("");
			decimalTyped = false;
			minusTyped = false;
			plusTyped = false;
			errorLabel.setText("");
		}
		if(ae.getSource() == logButton)
		{
			logWindow.setVisible(true);
		}
	}

	public void keyPressed(KeyEvent ke)
	{
		int keyCode = ke.getKeyCode();
		if(keyCode == KeyEvent.VK_BACK_SPACE || keyCode == KeyEvent.VK_ENTER)
		{
			valid = true;
		}
	}

	public void keyReleased(KeyEvent ke)
	{
		
	}

	public void keyTyped(KeyEvent ke)
	{
		char keyChar = ke.getKeyChar();
		if (!Character.isDigit(keyChar) && keyChar != '.' && keyChar != '-' && keyChar != '+' && valid == false)
		{
			errorLabel.setText("Error: Please only enter numbers.");
			ke.consume();
	    }
		valid = false;
		if(ke.getKeyChar() == '.')
		{
			if(decimalTyped == true)
			{
				errorLabel.setText("Error: You have already entered a decimal.");
				ke.consume();
			}
			else decimalTyped = true;
		}
		if(ke.getKeyChar() == '-')
		{
			if(minusTyped == true)
			{
				errorLabel.setText("Error: You have already entered a minus.");
				ke.consume();
			}
			else
			{
				if(plusTyped == true)
				{
					StringBuilder sb = new StringBuilder(inputField.getText());
					sb.deleteCharAt(0);
					inputField.setText("-" + sb.toString());
					plusTyped = false;
					minusTyped = true;
					ke.consume();
					return;
				}
				minusTyped = true;
				plusTyped = false;
				inputField.setText("-" + inputField.getText());
				ke.consume();
			}
		}
		if(ke.getKeyChar() == '+')
		{
			if(plusTyped == true)
			{
				errorLabel.setText("Error: You have already entered a plus.");
				ke.consume();
			}
			else
			{
				if(minusTyped == true)
				{
					StringBuilder sb = new StringBuilder(inputField.getText());
					sb.deleteCharAt(0);
					inputField.setText("+" + sb.toString());
					plusTyped = true;
					minusTyped = false;
					ke.consume();
					return;
				}
				plusTyped = true;
				minusTyped = false;
				inputField.setText("+" + inputField.getText());
				ke.consume();
			}
		}
	}
}