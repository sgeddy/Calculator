import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.BigDecimal;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.swing.*;
import javax.swing.text.*;

public class Calculator implements ActionListener, KeyListener, MouseListener 
{
	// Other instance variables
	JMenuBar menuBar = new JMenuBar();
	JMenu modeMenu = new JMenu("Mode");
	JRadioButtonMenuItem accumulating = new JRadioButtonMenuItem("Accumulating Mode");
	JRadioButtonMenuItem expression = new JRadioButtonMenuItem("Expression Mode");
	JRadioButtonMenuItem graphing = new JRadioButtonMenuItem("Graphing Mode");
	
	// Accumulating calculator instance variables
	boolean decimalTyped = false;
	boolean minusTyped = false;
	boolean plusTyped = false;
	boolean valid = false;
	boolean totalCleared = true;
	boolean accumulatingMode = false;
	BigDecimal total = new BigDecimal(0);
	
	// Expression calculator instance variables
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
	JLabel topLabel = new JLabel("Enter your expression below.",JLabel.CENTER);
	JPanel northPanel = new JPanel();
	GridLayout northLayout = new GridLayout(2,1,0,0);
	JFrame graphWindow;
	JPanel graphPanel;
	JPopupMenu pointPanel;
	Graphics g;
	Graphics h;
	boolean expressionMode = true;
	
	
	
	// Graphing calculator instance variables
	boolean graphingMode = false;

	public Calculator()
	{	
		// Build the mode menu
		modeMenu.add(accumulating);
		modeMenu.add(expression);
		modeMenu.add(graphing);
		menuBar.add(modeMenu);
		expression.setSelected(true);
		accumulating.addActionListener(this);
		expression.addActionListener(this);
		graphing.addActionListener(this);
		graphing.addMouseListener(this);
		
		// Build the buttons panel
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
		logWindow.setLocation(1100,300);
		logWindow.setTitle("Log");
		
		// Build the input fields
		inputField.setHorizontalAlignment(JTextField.RIGHT);
		inputField.addKeyListener(this);
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
		northPanel.add(topLabel);
		northPanel.add(inputField);
		
		// Build the main calculator window
		calcWindow.getContentPane().add(buttonsPanel,"South");
		calcWindow.getContentPane().add(centerPanel,"Center");
		calcWindow.getContentPane().add(northPanel,"North");
		calcWindow.setSize(300,190);
		calcWindow.setLocation(800,300);
		calcWindow.getRootPane().setDefaultButton(evalButton); // Hitting "enter" will trigger the eval button
		calcWindow.setJMenuBar(menuBar);
		calcWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		calcWindow.setTitle("Calculator");
		calcWindow.setVisible(true);
	}

	public static void main(String[] args)
	{
		new Calculator();
	}

	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getSource() == evalButton)
		{
			if(accumulatingMode == true)
			{
				if(inputField.getText().equals("")) return;
				totalCleared = false;
				BigDecimal inputNum = new BigDecimal(inputField.getText().replaceAll(",", ""));
				total = total.add(inputNum);
				if(minusTyped == false) pastInputsPane.setText(pastInputsPane.getText() + "+" + inputField.getText() + "\n");
				else pastInputsPane.setText(pastInputsPane.getText() + inputField.getText() + "\n");
				inputField.setText("");
				topLabel.setText("Total = " + total.toPlainString());
				decimalTyped = false;
				minusTyped = false;
				plusTyped = false;
				errorLabel.setText("");
			}
			if(expressionMode == true)
			{
				if(!inputField.getText().contains("="))
				{
					String result = new String(evaluate(inputField.getText(),variableVal.getText()));
					if(!result.contains("error"))
					{
						errorLabel.setText("");
						resultLabel.setText("Answer = " + result);
						pastInputsPane.setText(pastInputsPane.getText() + "Question: " + inputField.getText() + "\n");
						pastInputsPane.setText(pastInputsPane.getText() + "Answer = " + result + "\n");
					}
				}
				else // Evaluate both sides of the expression and compare
				{
					String[] splitInput = inputField.getText().split("=");
					String result1 = new String(evaluate(splitInput[0],variableVal.getText()));
					String result2 = new String(evaluate(splitInput[1],variableVal.getText()));
					if(!result1.contains("error") && !result2.contains("error"))
					{
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
						pastInputsPane.setText(pastInputsPane.getText() + "Question: " + inputField.getText() + "\n");
						pastInputsPane.setText(pastInputsPane.getText() + "Answer: " + resultLabel.getText() + "\n");
					}
				}
			}
			if(graphingMode == true) graph();
		}
		if(ae.getSource() == clearButton)
		{
			if(expressionMode == true)
			{
				inputField.setText("");
				variableVal.setText("");
				errorLabel.setText("");
				resultLabel.setText("");
			}
			
			if(accumulatingMode == true)
			{
				if(totalCleared == false)
				{
					total = BigDecimal.ZERO;
					pastInputsPane.setText(pastInputsPane.getText() + "Total cleared.\n");
					topLabel.setText("Total = " + total.toPlainString());
				}
				totalCleared = true;
				inputField.setText("");
				decimalTyped = false;
				minusTyped = false;
				plusTyped = false;
				errorLabel.setText("");
			}
			
			if(graphingMode == true)
			{
				inputField.setText("");
				variableVal.setText("");
				errorLabel.setText("");
				resultLabel.setText("");
			}
		}
		if(ae.getSource() == logButton)
		{
			logWindow.setVisible(true);
		}
		if(ae.getSource() == accumulating)
		{
			accumulatingMode = true; // Set booleans and menu items to the corresponding mode
			expressionMode = false;
			graphingMode = false;
			expression.setSelected(false);
			graphing.setSelected(false);
			
			resultLabel.setText(""); // Clear fields
			inputField.setText("");
			variableVal.setText("");
			
			pastInputsPane.setText(pastInputsPane.getText() + "Switched to accumulating mode." + "\n");
			variableLabel.setText("");
			variableVal.setEditable(false); // Disable variable value input
			topLabel.setHorizontalAlignment(JLabel.RIGHT);
			topLabel.setText("Total = 0");
			evalButton.setText("Add");
		}
		if(ae.getSource() == expression)
		{
			accumulatingMode = false; // Set booleans and menu items to the corresponding mode
			expressionMode = true;
			graphingMode = false;
			accumulating.setSelected(false);
			graphing.setSelected(false);
			
			resultLabel.setText(""); // Clear fields
			inputField.setText("");
			variableVal.setText("");
			
			pastInputsPane.setText(pastInputsPane.getText() + "Switched to expression mode." + "\n");
			topLabel.setHorizontalAlignment(JLabel.CENTER);
			topLabel.setText("Enter your expression below.");
			evalButton.setText("Evaluate");
			variableLabel.setText("x = ");
			variableVal.setEditable(true); // Enable variable value input			
		}
		if(ae.getSource() == graphing)
		{
			accumulatingMode = false; // Set booleans and menu items to the corresponding mode
			expressionMode = false;
			graphingMode = true;
			accumulating.setSelected(false);
			expression.setSelected(false);
			
			resultLabel.setText(""); // Clear fields
			inputField.setText("");
			variableVal.setText("");
			
			pastInputsPane.setText(pastInputsPane.getText() + "Switched to graphing mode." + "\n");
			evalButton.setText("Graph");
			topLabel.setHorizontalAlignment(JLabel.CENTER);
			topLabel.setText("Enter an expression for Y.");
			variableLabel.setText("x increment = ");
			variableVal.setEditable(true);
		}
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////// Graph Function ////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void graph()
	{
		String errorCheck = new String(evaluate(inputField.getText(), "1")); // Check for errors in expression before proceeding.
		if(errorCheck.contains("error")) return; // Stop if errors are found.
		if(variableVal.getText().isEmpty())
		{
			errorLabel.setText("Please enter an x increment value.");
			return;
		}
		
		// Calculate the points
		double[] xvals = {0,0,0,0,0,0,0,0,0,0,0};
		double[] yvals = {0,0,0,0,0,0,0,0,0,0,0};
		double ymax = 0;
		double ymin = 0;
		for(int i = 0; i < 11; i++)
		{
			xvals[i] = (i-5)*Double.parseDouble(variableVal.getText());
			String result1 = new String(evaluate(inputField.getText(),Double.toString(xvals[i])));
			System.out.println("Point " + Integer.toString(i) + ": " + result1); //debug
			yvals[i] = Double.valueOf(result1);
			if(i == 0)
			{
				ymax = yvals[i];
				ymin = yvals[i];
			}
			else
			{
				ymax = Double.max(ymax, yvals[i]);
				ymin = Double.min(ymin, yvals[i]);
			}
		}
		
		final double ymaxfinal = ymax;
		final double yminfinal = ymin;
		System.out.println("ymaxfinal: " + ymaxfinal);
		System.out.println("yminfinal: " + yminfinal);
		
		// Build the panel
		graphWindow = new JFrame();
		graphPanel = new JPanel() {
		    @Override
		    protected void paintComponent(Graphics g) {
		    	g.setColor(Color.white);
		    	g.fillRect(0,0,5000,5000);
		    	g.setColor(Color.black);
		    	int xinc = Integer.valueOf(variableVal.getText());
				double yscale = graphWindow.getHeight()/(ymaxfinal - yminfinal);
				System.out.println("yscale: " + yscale);
				
				//Horizontal Axis
				g.drawLine(0, graphWindow.getHeight()/2, graphWindow.getWidth(), graphWindow.getHeight()/2);
				
				for(int i=0; i<11; i++){
					//notches
					g.drawLine(i*graphWindow.getWidth()/10, graphWindow.getHeight()/2+3, i*graphWindow.getWidth()/10, graphWindow.getHeight()/2-3);
					//labels
					String data1 = Integer.toString((i-5)*xinc);
					if(i<6) g.drawString(data1, i*graphWindow.getWidth()/10, graphWindow.getHeight()/2+12);
					else g.drawString(data1, i*graphWindow.getWidth()/10-8, graphWindow.getHeight()/2+12);
				}
				
				/*for(int i=graphWindow.getWidth()/21; i<graphWindow.getWidth()/2; i+=graphWindow.getWidth()/21) {
					//notches
					g.drawLine(graphWindow.getWidth()/2 + i, graphWindow.getHeight()/2+3, graphWindow.getWidth()/2 + i , graphWindow.getHeight()/2-3);
					g.drawLine(graphWindow.getWidth()/2 - i, graphWindow.getHeight()/2+3, graphWindow.getWidth()/2 - i , graphWindow.getHeight()/2-3);
					//labels
					String data1 = Integer.toString(i/graphWindow.getWidth()/21);
					int length1 = data1.length();
					int offset1 = 0;
					String data2 = "-" + Integer.toString(i/graphWindow.getWidth()/21);
					int length2 = data2.length();
					int offset2 = 0;
					g.drawString(data1, , graphWindow.getHeight()/2-4);
					//g.drawChars(data1.toCharArray(), offset1, length1, graphWindow.getWidth()/2 + i - 20, graphWindow.getHeight()/2-8);
					//g.drawChars(data2.toCharArray(), offset2, length2, graphWindow.getWidth()/2 - i, graphWindow.getHeight()/2-8);
				}*/
				//Vertical Axis
				g.drawLine(graphWindow.getWidth()/2, 0, graphWindow.getWidth()/2, graphWindow.getHeight());
				
				/*for(int i=0; i<11; i++){
					//notches
					g.drawLine(graphWindow.getWidth()/2-3, i*graphWindow.getHeight()/10, graphWindow.getWidth()/2+3, i*graphWindow.getHeight()/10);
					//labels
					int t = (int) ymaxfinal;
					if(i!=5) {
						if(i<6) {
							String data1 = Integer.toString(t-(t*i)/5);
							g.drawString(data1, graphWindow.getWidth()/2+5, i*graphWindow.getHeight()/10+8);
						}
						else {
							String data1 = Integer.toString(t-(t*i)/5);
							g.drawString(data1, graphWindow.getWidth()/2+5, i*graphWindow.getHeight()/10-1);
						}
					}
				}*/
				
				// Draw the line and vertical axis
				g.setColor(Color.red);
				for(int i = 0; i < 10; i++)
				{
					int ypoint = (int)Math.round(-(yvals[i]*yscale)+graphWindow.getHeight()/2);
					int ypointnext = (int)Math.round(-(yvals[i+1]*yscale)+graphWindow.getHeight()/2);
					System.out.println("ypoint: " + ypoint);
					String data1 = Integer.toString(ypoint);
					String data2 = Integer.toString();
					g.drawLine(i*graphWindow.getWidth()/10, ypoint, (i+1)*graphWindow.getWidth()/10, ypointnext);
					g.drawLine(graphWindow.getWidth()/2-3, ypoint, graphWindow.getWidth()/2+3, ypoint);
					g.drawLine(graphWindow.getWidth()/2-3, ypointnext, graphWindow.getWidth()/2+3, ypointnext);
					g.drawString(data1, graphWindow.getWidth()/2+5, ypoint);
					g.drawString(data2, graphWindow.getWidth()/2+5, ypointnext);
					
					/*if(i<6) {
						String data1 = Integer.toString((int)Math.round(-(yvals[i])));
						String data2 = Integer.toString((int)Math.round(-(yvals[i+1])));
						g.drawString(data1, graphWindow.getWidth()/2+5, i*graphWindow.getHeight()/10+8);
						g.drawString(data2, graphWindow.getWidth()/2+5, (i+1)*graphWindow.getHeight()/10+8);
					}
					else {
						String data1 = Integer.toString((int)Math.round(-(yvals[i])));
						String data2 = Integer.toString((int)Math.round(-(yvals[i+1])));
						g.drawString(data1, graphWindow.getWidth()/2+5, i*graphWindow.getHeight()/10-1);
						g.drawString(data2, graphWindow.getWidth()/2+5, (i+1)*graphWindow.getHeight()/10-1);
					}*/
				}
		    }
		};
		graphWindow.setTitle("y = " + inputField.getText());
		graphWindow.getContentPane().add(graphPanel,"Center");
		graphWindow.setSize(600, 600);
		graphWindow.setLocation(200,300);
		graphPanel.addMouseListener(this);
		graphWindow.setVisible(true);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////// Evaluate Function ///////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public String evaluate(String expression, String variable)
	{
		if(expression.length() == 0) return "error";
		
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
							if(((y - m) - 1) >= 0) // Check for intended negative base
							{
								if(expressionArray[(y - m) - 1] == '-' && ((y - m) - 2) < 0)
								{
									base += "-";
								}
								else if(expressionArray[(y - m) - 1] == '-' && !Character.isDigit(expressionArray[(y - m) - 2]))
								{
									base += "-";
								}
							}
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
					if(((i - j) - 1) >= 0) // Check for intended negative base
					{
						if(expressionArray[(i - j) - 1] == '-' && ((i - j) - 2) < 0)
						{
							base += "-";
						}
						else if(expressionArray[(i - j) - 1] == '-' && !Character.isDigit(expressionArray[(i - j) - 2]))
						{
							base += "-";
						}
					}
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
	    	//System.out.println(ex.toString()); // For debug purposes
	    	if(ex.toString().contains("Expected") && ex.toString().contains("but found"))
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
	
	public void keyPressed(KeyEvent ke)
	{
		if(accumulatingMode == true)
		{
			int keyCode = ke.getKeyCode();
			if(keyCode == KeyEvent.VK_BACK_SPACE || keyCode == KeyEvent.VK_ENTER)
			{
				valid = true;
			}
		}
	}

	public void keyReleased(KeyEvent ke)
	{
		
	}

	public void keyTyped(KeyEvent ke)
	{
		if(accumulatingMode == true)
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

	public void mouseClicked(MouseEvent me)
	{
		
	}

	public void mouseEntered(MouseEvent me)
	{
		
	}

	public void mouseExited(MouseEvent me)
	{
		
	}

	public void mousePressed(MouseEvent me)
	{
		g = graphPanel.getGraphics();
		h = g;
		int xPosition = me.getX();
	    int yPosition = me.getY();
	    System.out.println("x:" + xPosition + " y:" + yPosition);
	    
	    
	    /*xPosition = xPosition/Conversionfactor;
	    if(xPosition>5 || xPosition<-5) reutrn;*/
	    
	    String data = "y=" + (graphWindow.getHeight()/2-yPosition) + "  x=" + (xPosition-graphWindow.getWidth()/2);
		int length = data.length();
		int offset = 0;
		g.drawChars(data.toCharArray(), offset, length, xPosition, yPosition);
	    
		
	    System.out.println("Mouse clicked at x=" + (xPosition-graphWindow.getWidth()/2) + ",y=" + (graphWindow.getHeight()/2-yPosition));
	}

	public void mouseReleased(MouseEvent me)
	{
		graphPanel.paint(h);
		g=h;
	}
}