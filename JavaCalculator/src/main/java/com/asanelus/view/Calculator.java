package com.asanelus.view;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;
import java.util.ResourceBundle;
import java.util.Stack;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class Calculator {

	@FXML
	private TextField input;
	@FXML
	private TextField computation;
	@FXML
	private ResourceBundle resources;

	private Stack<?> stack;
	private Queue<?> queue;
	private Deque<?> deque;

	/**
	 * The button click listener for all the buttons
	 * 
	 * @param event
	 */
	public void onButtonClick(ActionEvent event) {
		Button source = (Button) event.getSource();

		String expr = source.getText();
		if (isNumeric(expr) || expr.equals(".") || expr.equals("(")
				|| expr.equals(")")) {
			input.insertText(input.getLength(), expr);
		} else {
			if (expr.equals("C")) {
				input.clear();
				computation.clear();
			} else {
				if (expr.equals("CE")) {
					input.clear();
				} else {
					if (expr.equals("=")) {
						computation.insertText(computation.getLength(),
								input.getText());
						computate();
					} else if (expr.equals("+/-")) {
						if (input.getText().toString() != ""
								&& input.getText().charAt(0) != '-') {
							input.setText("-" + input.getText());
						} else {
							if (input.getText().charAt(0) == '-' && input.getText().charAt(0) == '0') {
								input.setText(input.getText().substring(1,
										input.getLength()));
							}
						}
					} else {

						if (computation.getLength() != 0
								&& isOperator(computation.getText().substring(
										computation.getLength() - 1,
										computation.getLength())) && isOperator(expr)) {
							String tempComputation = computation.getText()
									.substring(0, computation.getLength() - 1);
							tempComputation += expr;
							computation.setText(tempComputation);
							input.clear();

						} else {
							if (!input.getText().equals("")) {
								computation.setText("");
								computation.insertText(computation.getLength(),
										input.getText() + expr);
								input.clear();

							} else {
								if (computation.getLength() != 0
										&& isNumeric(computation
												.getText()
												.substring(
														computation.getLength() - 1,
														computation.getLength()))) {
									computation.setText(input.getText() + expr);
									input.clear();
								} else {
									computation.setText(input.getText() + expr);
									input.clear();
								}
							}

							// }
						}
					}
				}
			}
		}
	}

	@FXML
	private void initialize() {

		stack = new Stack<Object>();
		queue = new ArrayDeque<Object>();
		deque = new ArrayDeque<Object>();

	}

	private void computate() {
		// TODO implement
		Queue<Comparable> a = toQueue(computation.getText());
		System.out.println("in computate after toQ");
		 Double answer = evaluateExpression(a);
		System.out.println("q: " + a.toString());
		 input.setText(String.valueOf(answer));

	}
	
	/**
	 * Method that evaluates the expression
	 * 
	 */
	private double evaluateExpression(Queue<Comparable> aqueue) {
		Stack<Object> numstack = new Stack();
		Deque adeque = new ArrayDeque<Object>();
		Queue<Comparable> queue1 = aqueue;

		double op1;
		double op2;
		double result = 0;
		Object objInQueue;

		while (!queue1.isEmpty()) {
			objInQueue = queue1.poll();
			//when operator, we want to get the result of two previous numbers operation
			if (objInQueue != null && isOperator(String.valueOf(objInQueue))) {
				char operation = Character.valueOf(String.valueOf(objInQueue)
						.charAt(0));
				op2 = ((Double) numstack.pop()).doubleValue();
				op1 = ((Double) numstack.pop()).doubleValue();
				adeque.addFirst(op1);
				adeque.add(operation);
				adeque.add(op2);
				result = evalSingleOp(adeque);
				numstack.push(new Double(result));
			} else
				numstack.push(new Double(Double.parseDouble(String
						.valueOf(objInQueue))));
		}
		return result;

	}
/**
 * Transfers string into postfix string
 * 
 * @param expression
 * @return Queue<Comparable>
 */
	private Queue<Comparable> toQueue(String expression) {

		Queue<Comparable> tempQ = new ArrayDeque<Comparable>();
		Stack<Character> tempS = new Stack<Character>();

		String tempnum = "";
		int length = expression.length();
		int ctr = 0;

		while (ctr < length) {

			if (isNumeric(String.valueOf(expression.charAt(ctr)))) {

				if (ctr + 1 < expression.length()
						&& String.valueOf(expression.charAt(ctr + 1)).equals(
								".")) {
					// if its a decimal we want to cocat
					tempnum += String.valueOf(expression.charAt(ctr) + ".");
					ctr++;
				} else {
					tempnum += String.valueOf(expression.charAt(ctr));
				}

				ctr++;
			} else {

				if (tempnum != "" && isNumeric(tempnum)) {

					double number = Double.parseDouble(tempnum);
					tempQ.add(number);
					tempnum = "";

				}
				if (String.valueOf(expression.charAt(ctr)).equals("(")) {
					tempS.add(expression.charAt(ctr));
					ctr++;
				} else {

					if (String.valueOf(expression.charAt(ctr)).equals(")")) {
						while (!tempS.empty()
								&& !String.valueOf(tempS.peek()).equals("(")) {
							tempQ.add(tempS.pop());
							ctr++;
						}
						if (!tempS.empty()
								&& String.valueOf(tempS.peek()).equals("("))
							tempS.pop();
						ctr++;
					} else {
						if (isOperator(String.valueOf(expression.charAt(ctr)))) {
							if (tempS.isEmpty()) {
								tempS.add(expression.charAt(ctr));
								ctr++;
							} else {
								//
								char tempChar = (char) tempS.peek();
								if (precedenceLevel(tempChar) <= precedenceLevel(expression
										.charAt(ctr))) {

									tempS.add(tempS.pop());
									ctr++;

								} else {
									// pop and add the operators to queue
									while (!tempS.empty()
											&& precedenceLevel((char) tempS
													.peek()) >= precedenceLevel(expression
													.charAt(ctr))) {
										tempQ.add(tempS.pop());
									}
									tempS.add(expression.charAt(ctr));
									// adds the operator in the stack
								}

							}
						}
					}

				}
			}
		}

		if (tempnum != "" && isNumeric(tempnum)) {

			double number = Double.parseDouble(tempnum);
			tempQ.add(number);
			tempnum = "";

		}
		for (int i = 0; i < tempS.size(); i++) {
			if (String.valueOf(tempS.peek()).equals("(")
					|| String.valueOf(tempS.peek()).equals(")")) {
				tempS.pop();
			} else {
				tempQ.add(tempS.pop());
				System.out.println("q in for" + i + " " + tempQ);
			}
		}

		return tempQ;
	}
/**
 * returns the priority levels of operators
 * @param op
 * @return
 */
	private int precedenceLevel(char op) {
		switch (op) {
		case '+':
		case '-':
		case '(':
		case ')':
			return 0;
		case '*':
		case '/':		
			return 1;
		case '^':
			return 2;
		default:
			throw new IllegalArgumentException("Operator unknown: " + op);
		}
	}
/**
 * Evaluates an expression in the deque
 * @param arrayDeque
 * @return
 */
	private double evalSingleOp(Deque arrayDeque) {
		double result = 0;
		double num1 = (double) arrayDeque.poll();
		double num2 = (double) arrayDeque.pollLast();
		char operation = (Character) arrayDeque.poll();

		switch (operation) {
		case '+':
			result = num1 + num2;
			break;
		case '-':
			result = num1 - num2;
			break;
		case '*':
			result = num1 * num2;
			break;
		case '/':
			result = num1 / num2;
		}
		return result;
	}
/**
 * Returns if a string is numeric
 * @param str
 * @return boolean
 */
	private boolean isNumeric(String str) {
		try {
			double d = Double.parseDouble(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}
	/**
	 * returns if a string is an operator
	 * @param string
	 * @return boolean
	 */
	private boolean isOperator(String string) {
		return (string.equals("+") || string.equals("-") || string.equals("*") || string
				.equals("/"));
	}
}
