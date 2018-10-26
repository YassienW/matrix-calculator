

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.text.NumberFormatter;

//NEED TO REPLACE THE SHITTY MAX SIZED ARRAY WITH AN ARRAY LIST AND CHANGE ACCORDIGNLY
//ITERATION FOR LOOPS WITH FOR EACH LOOPS. Also separate logic from UI (sux2sucktbh)
@SuppressWarnings("serial")
public class UI extends JFrame implements ActionListener{
	private int currentRows = 3, currentColumns = 3;
	private ArrayList<ArrayList<Double>> matrix = new ArrayList<ArrayList<Double>>();
	//using Integer since ComboBox doesn't take primitives
	private Integer[] sizeOptions = {1,2,3,4,5};
	private ArrayList<ArrayList<JFormattedTextField>> inputMatrix = new ArrayList<ArrayList<JFormattedTextField>>();
	private NumberFormatter formatter = new NumberFormatter();
	private JTextField  multiplyBy;
	private JPanel controls, matrixArea, gridFixer, operationsPanel;
	private JLabel selectRow, selectColumn, to, to2, by, with ;
	private JButton clear, reduceMatrix, export, add, swap, multiply, multiplyAndAdd;
	private JComboBox<Integer> rowsSelector, columnsSelector,  addFrom, addTo, swapFrom, swapTo, multiplyFrom, multiplyTo;
	private JCheckBox option1, option2;
	private JTextArea console;
	private JScrollPane consoleContainer;
	private OperationsManager operations = new OperationsManager(this);
	private DocXManager docX = new DocXManager(this);
	
	public UI(){
		ArrayList<Image> icons = new ArrayList<Image>();
		icons.add(new ImageIcon(ClassLoader.getSystemResource("MatrixIconLarge.png")).getImage());
		icons.add(new ImageIcon(ClassLoader.getSystemResource("MatrixIconSmall.png")).getImage());
		
		setTitle("Matrix Calculator");
		setSize(425,500);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setIconImages(icons);
	
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout(0,0));
		
		controls = new JPanel();
		controls.setLayout(new FlowLayout());
		controls.setPreferredSize(new Dimension(400,75));
		/*we use a flow layout (gridFixer) to contain the grid layout(matrixArea) in order to prevent grid layout from 
		resizing the components to maximum size. Set preferred size is used to set the component size*/
		matrixArea = new JPanel();
		matrixArea.setLayout(new GridLayout(currentRows, currentColumns, 0, 0));
		gridFixer = new JPanel();
		gridFixer.setLayout(new FlowLayout());
		//width -1 to make space for the automatic padding, otherwise they intersect
		gridFixer.setPreferredSize(new Dimension(224,200));
		gridFixer.add(matrixArea);
		operationsPanel = new JPanel();
		operationsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		operationsPanel.setPreferredSize(new Dimension(200,200));
		
		//initialize and add the matrix size selection combo boxes, and their respective labels 
		rowsSelector = new JComboBox<Integer>(sizeOptions);
		rowsSelector.setSelectedItem(currentRows);
		selectRow = new JLabel("Rows:");
		controls.add(selectRow);
		controls.add(rowsSelector);
		columnsSelector = new JComboBox<Integer>(sizeOptions);
		columnsSelector.setSelectedItem(currentColumns);
		selectColumn = new JLabel("Columns:");
		controls.add(selectColumn);
		controls.add(columnsSelector);
		//initialize and add buttons to controls panel
		clear = new JButton("Clear");
		controls.add(clear);
		reduceMatrix = new JButton("Reduce");
		reduceMatrix.setToolTipText("Reduce the matrix to row echelon form");
		controls.add(reduceMatrix);
		export = new JButton("DocX");
		export.setToolTipText("Select a .docx file to save matrix changes to");
		controls.add(export);
		option1 = new JCheckBox("Print matrix to console on changes", true);
		controls.add(option1);
		option2 = new JCheckBox("Save operations to Docx", false);
		controls.add(option2);
		
		formatter.setValueClass(Double.class);
		
		//loop to initialize the matrix input fields
		for(int i = 0; i < currentRows; i++){
			inputMatrix.add(new ArrayList<JFormattedTextField>());
			for(int j = 0; j < currentColumns; j++){
				inputMatrix.get(i).add(new JFormattedTextField(formatter)/*{
					public void processFocusEvent(final FocusEvent e) {
						//THIS FIXES THE BUG WHERE IT DOESNT ACCEPT THE DELETION OF VALUES, SINCE THE
						//VALUE IN THE FIELD BECOMES NULL/"" AFTER DELETION, WHICH IS NOT ACCEPTED BY THE FORMATTER (Double)
				        if (e.isTemporary()) {
				            return;
				        }
				        if (e.getID() == FocusEvent.FOCUS_LOST) {
				            if (getText() == null || getText().isEmpty()) {
				                setValue(null);
				            }
				        }
				        super.processFocusEvent(e);
				    }
				}*/);
				inputMatrix.get(i).get(j).setPreferredSize(new Dimension(40,40));
				inputMatrix.get(i).get(j).setFont(new Font("OCR A Extended", Font.BOLD, 16));
				inputMatrix.get(i).get(j).setHorizontalAlignment(JTextField.CENTER);
				matrixArea.add(inputMatrix.get(i).get(j));
			}
		}
		
		//initialize and add buttons, fields, and labels to operations panel
		add = new JButton("Add");
		add.setPreferredSize(new Dimension(66,25));
		addFrom = new JComboBox<Integer>(sizeOptions);
		to = new JLabel("To");
		addTo = new JComboBox<Integer>(sizeOptions);
		operationsPanel.add(add);
		operationsPanel.add(addFrom);
		operationsPanel.add(to);
		operationsPanel.add(addTo);
		
		swap = new JButton("Swap");
		swap.setPreferredSize(new Dimension(66,25));
		swapFrom = new JComboBox<Integer>(sizeOptions);
		with = new JLabel("With");
		swapTo = new JComboBox<Integer>(sizeOptions);
		operationsPanel.add(swap);
		operationsPanel.add(swapFrom);
		operationsPanel.add(with);
		operationsPanel.add(swapTo);
		
		multiply = new JButton("Multiply");
		multiply.setPreferredSize(new Dimension(66,25));
		multiply.setMargin(new Insets(0,0,0,0));
		multiplyFrom = new JComboBox<Integer>(sizeOptions);
		by = new JLabel("By");
		multiplyBy = new JTextField();
		multiplyBy.setColumns(3);
		multiplyBy.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
		operationsPanel.add(multiply);
		operationsPanel.add(multiplyFrom);
		operationsPanel.add(by);
		operationsPanel.add(multiplyBy);
		
		multiplyAndAdd = new JButton("Multiply & Add");
		to2 = new JLabel("To");
		multiplyTo = new JComboBox<Integer>(sizeOptions);
		operationsPanel.add(multiplyAndAdd);
		operationsPanel.add(to2);
		operationsPanel.add(multiplyTo);
		
		console = new JTextArea();
		console.setEditable(false);
		console.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
		consoleContainer = new JScrollPane(console);
		consoleContainer.setPreferredSize(new Dimension(425,175));
		
		//since the default combo box options are 1,2,3,4,5; we call this function to set them to the matrix size
		resizeComboBoxes();
		
		cp.add(controls, BorderLayout.NORTH);
		cp.add(gridFixer, BorderLayout.WEST);
		cp.add(operationsPanel, BorderLayout.EAST);
		cp.add(consoleContainer, BorderLayout.SOUTH);
		revalidate();
		repaint();
		
		rowsSelector.addActionListener(this);
		columnsSelector.addActionListener(this);
		clear.addActionListener(this);
		add.addActionListener(this);
		swap.addActionListener(this);
		multiply.addActionListener(this);
		multiplyAndAdd.addActionListener(this);
		reduceMatrix.addActionListener(this);
		export.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		Object buttonPressed = e.getSource();
		
		if(buttonPressed.equals(rowsSelector) || buttonPressed.equals(columnsSelector)){
			//set current rows = the rows selected via the combo box
			currentRows = (int)rowsSelector.getSelectedItem();
			currentColumns = (int)columnsSelector.getSelectedItem();
			printStringToConsole("Size Change, Rows = " + currentRows + " Columns = " + currentColumns);
			//remove all matrix input fields
			matrixArea.removeAll();
			matrixArea.revalidate();
			matrixArea.repaint();	
			//set up the gridLayout with new matrix size and reconstruct the matrix fields 
			matrixArea.setLayout(new GridLayout(currentRows, currentColumns, 0, 0));
			inputMatrix.removeAll(inputMatrix);
			for(int i = 0; i < 	currentRows; i++){
				inputMatrix.add(new ArrayList<JFormattedTextField>());
				for(int j = 0; j < currentColumns; j++){
					inputMatrix.get(i).add(new JFormattedTextField(formatter)/*{
						public void processFocusEvent(final FocusEvent e){
							//THIS FIXES THE BUG WHERE IT DOESNT ACCEPT THE DELETION OF VALUES, SINCE THE
							//VALUE IN THE FIELD BECOMES NULL/"" AFTER DELETION, WHICH IS NOT ACCEPTED BY THE FORMATTER (Double)
					        if (e.isTemporary()) {
					            return;
					        }
					        if (e.getID() == FocusEvent.FOCUS_LOST) {
					            if (getText() == null || getText().isEmpty()) {
					                setValue(null);
					            }
					        }
					        super.processFocusEvent(e);
					    }
					}*/);
					inputMatrix.get(i).get(j).setPreferredSize(new Dimension(40,40));
					inputMatrix.get(i).get(j).setFont(new Font("OCR A Extended", Font.BOLD, 16));
					inputMatrix.get(i).get(j).setHorizontalAlignment(JTextField.CENTER);
					matrixArea.add(inputMatrix.get(i).get(j));
				}
			}
			//resize the operation combo boxes to fit the row number that the user picked
			resizeComboBoxes();
		}else if(buttonPressed.equals(clear)){
			for(ArrayList<JFormattedTextField> list : inputMatrix){
				for(JFormattedTextField f : list){
					f.setText("");
				}
			}
			console.setText("");
			
		}else if(buttonPressed.equals(add)){
			textfieldToMatrix(matrix, inputMatrix);
			operations.addMultipleOfRowToRow((int)addFrom.getSelectedItem(), (int)addTo.getSelectedItem(), 1, matrix);
			matrixToTextfield(matrix, inputMatrix);
			
		}else if(buttonPressed.equals(swap)){
			textfieldToMatrix(matrix, inputMatrix);
			operations.swapRowWithRow((int)swapFrom.getSelectedItem(), (int)swapTo.getSelectedItem(), matrix);
			matrixToTextfield(matrix, inputMatrix);		
			
		}else if(buttonPressed.equals(multiply)){
			textfieldToMatrix(matrix, inputMatrix);
			if(multiplyBy.getText().contains("/")){
				operations.multiplyRow((int)multiplyFrom.getSelectedItem(), new Fraction(multiplyBy.getText()).toDecimal(), matrix);
			}else{
				operations.multiplyRow((int)multiplyFrom.getSelectedItem(), Double.valueOf(multiplyBy.getText()), matrix);
			}
			matrixToTextfield(matrix, inputMatrix);
			
		}else if(buttonPressed.equals(multiplyAndAdd)){	
			textfieldToMatrix(matrix, inputMatrix);
			if(multiplyBy.getText().contains("/")){
				operations.addMultipleOfRowToRow((int)multiplyFrom.getSelectedItem(), (int)multiplyTo.getSelectedItem(), new Fraction(multiplyBy.getText()).toDecimal(), matrix);
			}else{
				operations.addMultipleOfRowToRow((int)multiplyFrom.getSelectedItem(), (int)multiplyTo.getSelectedItem(), Double.valueOf(multiplyBy.getText()), matrix);		
			}
			matrixToTextfield(matrix, inputMatrix);	
		
		}else if(buttonPressed.equals(export)){
			JFileChooser fileChooser = new JFileChooser();
			if(fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
				docX.setDocxFile(fileChooser.getSelectedFile());
			}
		}else if(buttonPressed.equals(reduceMatrix)){
			textfieldToMatrix(matrix, inputMatrix);
			operations.reduceMatrix(matrix);
			matrixToTextfield(matrix, inputMatrix);
		}
	}
	
	//copies the text field array to the BigDecimal array to perform operations
	public void textfieldToMatrix(ArrayList<ArrayList<Double>> matrix, ArrayList<ArrayList<JFormattedTextField>> textFieldArray){
		matrix.removeAll(matrix);
		for(int i = 0; i < 	currentRows; i++){
			matrix.add(new ArrayList<Double>());
			for(int j = 0; j < currentColumns; j++){
				matrix.get(i).add(Double.valueOf(textFieldArray.get(i).get(j).getText()));
			}
		}
	}
	
	//copies the values back to the text field array for after required operations are done
	public void matrixToTextfield(ArrayList<ArrayList<Double>> matrix, ArrayList<ArrayList<JFormattedTextField>> textFieldArray){
		for(int i = 0; i <  matrix.size(); i++){
			for(int j = 0; j < matrix.get(i).size(); j++){
				//if the value isn't an irrational number, convert to int first then to string, to remove the decimal point
				if(matrix.get(i).get(j) % 1 == 0){
					textFieldArray.get(i).get(j).setText(String.valueOf(matrix.get(i).get(j).intValue()));
				}else{			
					textFieldArray.get(i).get(j).setText(String.valueOf(matrix.get(i).get(j)));
				}
			}
		}
	}
	
	//changes the size of the selection combo boxes to reflect the changes in matrix size
	public void resizeComboBoxes(){
		//if the selection list is smaller than the current rows selected by the user, add more items, else remove items
		if(addFrom.getItemCount() < currentRows){
			while(addFrom.getItemCount() != currentRows){
				addFrom.addItem(addFrom.getItemCount() + 1);
				addTo.addItem(addTo.getItemCount() + 1);
				swapFrom.addItem(swapFrom.getItemCount() + 1);
				swapTo.addItem(swapTo.getItemCount() + 1);
				multiplyFrom.addItem(multiplyFrom.getItemCount()+ 1);
				multiplyTo.addItem(multiplyTo.getItemCount()+ 1);
			}
		}else{
			while(addFrom.getItemCount() != currentRows){
				addFrom.removeItemAt(addFrom.getItemCount() - 1);
				addTo.removeItemAt(addTo.getItemCount()- 1);
				swapFrom.removeItemAt(swapFrom.getItemCount()- 1);
				swapTo.removeItemAt(swapTo.getItemCount()- 1);
				multiplyFrom.removeItemAt(multiplyFrom.getItemCount()- 1);
				multiplyTo.removeItemAt(multiplyTo.getItemCount()- 1);
			}
		}
	}
	
	//prints a string to the console
	public void printStringToConsole(String s){
		console.append(s + "\n");
	}
	
	//prints the contents of the matrix (double array) to the console
	public void printMatrixToConsole(ArrayList<ArrayList<Double>> matrix){
		if(option1.isSelected()){
			for(ArrayList<Double> arr : matrix){
				console.append("\t |   ");
				for(Double d : arr){
					if(d % 1 == 0){
						console.append(String.valueOf(d.intValue()) + "   ");
					}else{
						console.append(new Fraction(d).toString() + "   ");
						//console.append(matrix[i][j] + "   ");
					}	
				}
				console.append("|\n");
			}
			//for auto scrolling, set caret position to end of the text area after displaying the matrix
			console.setCaretPosition(console.getDocument().getLength());
		}
	}
	public void toDocx(String s){
		if(option2.isSelected()){
			docX.addMatrix(matrix, s);
		}
	}
	
}
