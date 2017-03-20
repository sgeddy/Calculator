import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.swing.*;
import javax.swing.text.*;

public class ExpressionCalculator implements ActionListener
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
	JButton evalButton = new JButton("Evaluate");
	JLabel resultLabel = new JLabel("",JLabel.RIGHT);
	JLabel errorLabel = new JLabel("",JLabel.CENTER);
	JPanel centerPanel = new JPanel();
	GridLayout centerLayout = new GridLayout(3,1,0,0);
	JTextField variableVal = new JTextField();
	JLabel variableLabel = new JLabel("x = ",JLabel.RIGHT);
	JPanel variablePanel = new JPanel();
	GridLayout variableLayout = new GridLayout(1,2,0,0);
	JLabel expressionLabel = new JLabel("Enter your expression below.",JLabel.CENTER);
	JPanel northPanel = new JPanel();
	GridLayout northLayout = new GridLayout(2,1,0,0);

	public ExpressionCalculator()
	{	
		// Build the number buttons panel
		buttonsPanel.setLayout(buttonsLayout);
		buttonsPanel.add(evalButton);
		buttonsPanel.add(clearButton);
		buttonsPanel.add(logButton);
		evalButton.addActionListener(this);
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
		
		// Build the input fields
		inputField.setHorizontalAlignment(JTextField.RIGHT);
		variableVal.setHorizontalAlignment(JTextField.RIGHT);
		
		// Build the error label
		errorLabel.setForeground(Color.red);
		
		// Build the center panel
		variablePanel.setLayout(variableLayout);
		variablePanel.add(variableLabel);
		variablePanel.add(variableVal);
		centerPanel.setLayout(centerLayout);
		centerPanel.add(variablePanel);
		centerPanel.add(resultLabel);
		centerPanel.add(errorLabel);
		
		// Build the north panel
		northPanel.setLayout(northLayout);
		northPanel.add(expressionLabel);
		northPanel.add(inputField);
		
		// Build the main calculator window
		calcWindow.getContentPane().add(buttonsPanel,"South");
		calcWindow.getContentPane().add(centerPanel,"Center");
		calcWindow.getContentPane().add(northPanel,"North");
		calcWindow.setSize(300,160);
		calcWindow.setLocation(800,300);
		calcWindow.getRootPane().setDefaultButton(evalButton); // Hitting "enter" will trigger the eval button
		calcWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		calcWindow.setTitle("Calculator");
		calcWindow.setVisible(true);
	}

	public static void main(String[] args)
	{
		new ExpressionCalculator();
	}

	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getSource() == evalButton)
		{
			if(!inputField.getText().contains("="))
			{
				String result = new String(evaluate(inputField.getText(),variableVal.getText()));
				if(!result.contains("error"))
				{
					errorLabel.setText("");
					resultLabel.setText("Answer = " + result);
				}
			}
			else // Evaluate both sides of the expression and compare
			{
				String[] splitInput = inputField.getText().split("=");
				String result1 = new String(evaluate(splitInput[0],variableVal.getText()));
				String result2 = new String(evaluate(splitInput[1],variableVal.getText()));
					if(Double.parseDouble(result1) == Double.parseDouble(result2))
					{
						errorLabel.setText("");
						resultLabel.setText("True");
					}
				else
				{
						errorLabel.setText("");
						resultLabel.setText("False");
				}
			}
		}
		if(ae.getSource() == clearButton)
		{
			inputField.setText("");
			variableVal.setText("");
			errorLabel.setText("");
			resultLabel.setText("");
		}
		if(ae.getSource() == logButton)
		{
			logWindow.setVisible(true);
		}
	}
	
	public String evaluate(String expression, String variable)
	{
		String answer = new String();
		String panswer = new String();
		double e = Math.E;
		double pi = Math.PI;
		char[] expressionArrayUntrimmed = expression.toCharArray();
		char[] expressionArray = expression.replaceAll(" ","").toCharArray(); // Expression with all spaces removed.
		int open = 0; // Counts open parenthesis
		String base = new String(); // Used in exponent conversion loop
		String exponent = new String(); // Used in exponent conversion loop
		
		for(int i = 0; (i + 2) < expressionArrayUntrimmed.length; i++) // Check for spaces between numbers
		{
			if(Character.isDigit(expressionArrayUntrimmed[i]) && expressionArrayUntrimmed[i+1] == ' '
					&& Character.isDigit(expressionArrayUntrimmed[i+2]))
			{
				errorLabel.setText("Spaces between numbers are not permitted.");
				return "error";
			}
		}
		
		for(int i = 0; i < expressionArray.length; i++) // Check for unidentified operators
		{
			if(expressionArray[i] == '(') open++;
			if(expressionArray[i] == ')') open--;
		}
		if(open != 0) // Checking for mismatched parenthesis.
		{
			errorLabel.setText("Mismatched parenthesis.");
			return "error";
		}
		
		for(int i = 0; (i + 1) < expressionArray.length; i++) // Checking for implied multiplication
		{
			if((Character.isDigit(expressionArray[i]) || expressionArray[i] == 'e'
					|| expressionArray[i] == 'i' || expressionArray[i] == 'x') 
					&& (expressionArray[i+1] == '(' || expressionArray[i+1] == 'p'
					|| expressionArray[i+1] == 'e' || expressionArray[i+1] == 'x'))
			{
				errorLabel.setText("Implied multiplication is not permitted.");
				return "error";
			}
			if(expressionArray[i] == ')' && (Character.isDigit(expressionArray[i+1])
					|| expressionArray[i+1] == '(' || expressionArray[i+1] == 'p'
					|| expressionArray[i+1] == 'e' || expressionArray[i+1] == 'x'))
			{
				errorLabel.setText("Implied multiplication is not permitted.");
				return "error";
			}
		}
		
		// Replace symbols with their numeric values
		expression = expression.replaceAll("x",variable);
		expression = expression.replaceAll("X",variable);
		expression = expression.replaceAll("e",Double.toString(e));
		expression = expression.replaceAll("pi",Double.toString(pi));
		expressionArray = expression.replaceAll(" ","").toCharArray();
		
		for(int i = 0; i < expressionArray.length; i++) // Checking for unidentified operators
		{
			if(!Character.isDigit(expressionArray[i]) && expressionArray[i] != '.' && expressionArray[i] != '+'
					&& expressionArray[i] != '-' && expressionArray[i] != '*' && expressionArray[i] != '/'
					&& expressionArray[i] != '^' && expressionArray[i] != 'r' && expressionArray[i] != '('
					&& expressionArray[i] != ')')
			{
				errorLabel.setText("Unidentified operator.");
				return "error";
			}
		}
		
		for(int i = 0; (i + 1) < expressionArray.length; i++) // Checking for operators contacting parenthesis
		{
			if(((expressionArray[i] == '+' || expressionArray[i] == '-' || expressionArray[i] == '*' 
					|| expressionArray[i] == '/' || expressionArray[i] == '^' || expressionArray[i] == 'r')
					&& expressionArray[i+1] == ')') || ((expressionArray[i+1] == '+' || expressionArray[i+1] == '*' 
					|| expressionArray[i+1] == '/' || expressionArray[i+1] == '^' || expressionArray[i+1] == 'r')
					&& expressionArray[i] == '('))
			{
				errorLabel.setText("Operator contacting parenthesis.");
				return "error";
			}
		}
		
		if(expressionArray[0] == '+') // Checks for expressions beginning with a positive unary operator
		{
			errorLabel.setText("Positive unary operators are not permitted.");
			return "error";
		}
		
		expression = expression.replaceAll(" ", ""); // Remove all spaces for easier manipulation
		
		for(int i = 0; i < expressionArray.length; i++)
		{

		//
		// Check for parenthesis sets, solve, and replace
		//
			if(expressionArray[i] == ')') {
				String pset = new String();
				String pset2 = new String();
				int j = i;
				String cp = new String();
				cp += expressionArray[i];
				--j;
				while(expressionArray[j] != '(') {
					pset += expressionArray[j];
					--j;
				}
				pset = new StringBuilder(pset).reverse().toString();
				char[] psetArray = pset.toCharArray();
				String op = new String();
				op += expressionArray[j];
				pset2 = pset;
				
				for(int y = 0; y < psetArray.length; y++)
				{	
					double result = 0; 	// Used to replace exponents and roots
					if(psetArray[y] == '^')
					{
						for(int m = 1; (y - m) >= 0 && (Character.isDigit(psetArray[y - m]) || psetArray[y - m] == '.'); m++)
						{
							base += psetArray[y - m]; // Gets the base (in reverse order)
						}
						base = new StringBuilder(base).reverse().toString(); // Reverse base to proper order
						for(int m = 1; (y + m) < psetArray.length && (Character.isDigit(psetArray[y + m]) || psetArray[y + m] == '.'); m++)
						{
							exponent += psetArray[y + m]; // Gets the exponent
						}
						result = Math.pow(Double.parseDouble(base),Double.parseDouble(exponent));
						pset2 = pset2.replace(base + "^" + exponent, Double.toString(result));
						psetArray = pset2.toCharArray();
						y = 0;
						base = "";
						exponent = "";
					}
					if(psetArray[y] == 'r')
					{
						for(int m = 1; (y - m) >= 0 && (Character.isDigit(psetArray[y - m]) || psetArray[y - m] == '.'); m++)
						{
							base += psetArray[y - m]; // Gets the base (in reverse order)
						}
						base = new StringBuilder(base).reverse().toString(); // Reverse base to proper order
						for(int m = 1; (y + m) < psetArray.length && (Character.isDigit(psetArray[y + m]) || psetArray[y + m] == '.'); m++)
						{
							exponent += psetArray[y + m]; // Gets the exponent
						}
						result = Math.pow(Double.parseDouble(base),1/Double.parseDouble(exponent));
						pset2 = pset2.replace(base + "r" + exponent, Double.toString(result));
						psetArray = pset2.toCharArray();
						y = 0;
						base = "";
						exponent = "";
					}
				}
				
				ScriptEngineManager sem = new ScriptEngineManager();
				ScriptEngine parenthesis = sem.getEngineByName("JavaScript"); // Load built in script engine
				    try{panswer = parenthesis.eval(pset2).toString();} // Evaluate expression
				    catch(Exception ex)
				    {
				    	System.out.println(ex.toString());
				    	if(ex.toString().contains("Expected an operand but found eof"))
				    	{
				    		errorLabel.setText("Unfinished expression.");
							return "error";
				    	}
				    }
				expression = expression.replace(op + pset + cp, panswer);
				expressionArray = expression.toCharArray();
				i = 0;
				op = "";
				cp = "";
				pset = "";
				pset2 = "";
				panswer = "";
			}
		}
	    	
	    for(int i = 0; i < expressionArray.length; i++)
		{	
			double result = 0; // Used to replace exponents and roots
			if(expressionArray[i] == '^')
			{
				for(int j = 1; (i - j) >= 0 && (Character.isDigit(expressionArray[i - j]) || expressionArray[i - j] == '.'); j++)
				{
					base += expressionArray[i - j]; // Gets the base (in reverse order)
				}
				base = new StringBuilder(base).reverse().toString(); // Reverse base to proper order
				for(int j = 1; (i + j) < expressionArray.length && (Character.isDigit(expressionArray[i + j]) || expressionArray[i + j] == '.'); j++)
				{
					exponent += expressionArray[i + j]; // Gets the exponent
				}
				result = Math.pow(Double.parseDouble(base),Double.parseDouble(exponent));
				expression = expression.replace(base + "^" + exponent, Double.toString(result));
				expressionArray = expression.toCharArray();
				i = 0;
				base = "";
				exponent = "";
			}
			if(expressionArray[i] == 'r')
			{
				for(int j = 1; (i - j) >= 0 && (Character.isDigit(expressionArray[i - j]) || expressionArray[i - j] == '.'); j++)
				{
					base += expressionArray[i - j]; // Gets the base (in reverse order)
				}
				base = new StringBuilder(base).reverse().toString(); // Reverse base to proper order
				for(int j = 1; (i + j) < expressionArray.length && (Character.isDigit(expressionArray[i + j]) || expressionArray[i + j] == '.'); j++)
				{
					exponent += expressionArray[i + j]; // Gets the exponent
				}
				result = Math.pow(Double.parseDouble(base),1/Double.parseDouble(exponent));
				expression = expression.replaceFirst(base + "r" + exponent, Double.toString(result));
				expressionArray = expression.toCharArray();
				i = 0;
				base = "";
				exponent = "";
			}
		}
	    
	    for(int i = 0; i < expressionArray.length; i++) {
	    	if(expressionArray[i] == '-' && expressionArray[i+1] == '-') {
	    		String m1 = new String();
	    		String m2 = new String();
	    		String p = new String();
	    		m1 += expressionArray[i];
	    		m2 += expressionArray[i+1];
	    		p += '+';
	    		expression = expression.replace(m1 + m2, p);
				expressionArray = expression.toCharArray();
				i = 0;
	    	}
	    }
	    
		ScriptEngineManager sem = new ScriptEngineManager();
	    ScriptEngine calculator = sem.getEngineByName("JavaScript"); // Load built in script engine
	    try{answer = calculator.eval(expression).toString();} // Evaluate expression
	    catch(Exception ex)
	    {
	    	System.out.println(ex.toString());
	    	if(ex.toString().contains("Expected an operand but found eof"))
	    	{
	    		errorLabel.setText("Unfinished expression.");
				return "error";
	    	}
	    }
	    if(answer == "-Infinity") {
	    	errorLabel.setText("Cannot divide by zero.");
	    	return "error";
	    }
		return answer;
	}
}