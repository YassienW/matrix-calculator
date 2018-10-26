import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.text.NumberFormatter;

//NEED TO REPLACE THE SHITTY MAX SIZED ARRAY WITH AN ARRAY LIST AND CHANGE ACCORDIGNLY
//ITERATION FOR LOOPS WITH FOR EACH LOOPS. Also separate logic from UI (sux2sucktbh)
@SuppressWarnings("serial")
public class UI extends JFrame{
	private Matrix matrix = new Matrix();
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
	private DocXManager docX = new DocXManager(this);
	
	public UI(){
		ArrayList<Image> icons = new ArrayList<Image>();
		icons.add(new ImageIcon(ClassLoader.getSystemResource("MatrixIconLarge.png")).getImage());
		icons.add(new ImageIcon(ClassLoader.getSystemResource("MatrixIconSmall.png")).getImage());
		
		formatter.setValueClass(Double.class);
		
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
		rowsSelector.setSelectedItem(null);
		selectRow = new JLabel("Rows:");
		controls.add(selectRow);
		controls.add(rowsSelector);
		columnsSelector = new JComboBox<Integer>(sizeOptions);
		columnsSelector.setSelectedItem(1);
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
		
		//initialize and add buttons, fields, and labels to operations panel
		add = new JButton("Add");
		add.setPreferredSize(new Dimension(66,25));
		addFrom = new JComboBox<Integer>();
		to = new JLabel("To");
		addTo = new JComboBox<Integer>();
		operationsPanel.add(add);
		operationsPanel.add(addFrom);
		operationsPanel.add(to);
		operationsPanel.add(addTo);
		
		swap = new JButton("Swap");
		swap.setPreferredSize(new Dimension(66,25));
		swapFrom = new JComboBox<Integer>();
		with = new JLabel("With");
		swapTo = new JComboBox<Integer>();
		operationsPanel.add(swap);
		operationsPanel.add(swapFrom);
		operationsPanel.add(with);
		operationsPanel.add(swapTo);
		
		multiply = new JButton("Multiply");
		multiply.setPreferredSize(new Dimension(66,25));
		multiply.setMargin(new Insets(0,0,0,0));
		multiplyFrom = new JComboBox<Integer>();
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
		multiplyTo = new JComboBox<Integer>();
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
		
		rowsSelector.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				JComboBox<?> cb = (JComboBox<?>)e.getSource();
				matrix.setDimensions((int)cb.getSelectedItem(), matrix.getColumns());
				buildUI();
			}
		});
		columnsSelector.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				JComboBox<?> cb = (JComboBox<?>)e.getSource();
				matrix.setDimensions(matrix.getRows(), (int)cb.getSelectedItem());
				buildUI();
			}
		});
		clear.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				for(ArrayList<JFormattedTextField> list : inputMatrix){
					for(JFormattedTextField f : list){
						f.setText("");
					}
				}
				console.setText("");
			}
		});
		add.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				updateMatrix();
				matrix.addMultipleOfRowToRow((int)addFrom.getSelectedItem(), (int)addTo.getSelectedItem(), 1);
				updateUI();
			}
		});
		swap.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				updateMatrix();
				matrix.swapRowWithRow((int)swapFrom.getSelectedItem(), (int)swapTo.getSelectedItem());
				updateUI();
			}
		});
		multiply.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				updateMatrix();
				if(multiplyBy.getText().contains("/")){
					matrix.multiplyRow((int)multiplyFrom.getSelectedItem(), new Fraction(multiplyBy.getText()).toDecimal());
				}else{
					matrix.multiplyRow((int)multiplyFrom.getSelectedItem(), Double.valueOf(multiplyBy.getText()));
				}
				updateUI();
			}
		});
		multiplyAndAdd.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				updateMatrix();
				if(multiplyBy.getText().contains("/")){
					matrix.addMultipleOfRowToRow((int)multiplyFrom.getSelectedItem(), (int)multiplyTo.getSelectedItem(), new Fraction(multiplyBy.getText()).toDecimal());
				}else{
					matrix.addMultipleOfRowToRow((int)multiplyFrom.getSelectedItem(), (int)multiplyTo.getSelectedItem(), Double.valueOf(multiplyBy.getText()));		
				}
				updateUI();
			}
		});
		reduceMatrix.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				updateMatrix();
				matrix.reduce();
				updateUI();
			}
		});
		export.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				JFileChooser fileChooser = new JFileChooser();
				if(fileChooser.showOpenDialog(((Component)e.getSource()).getParent()) == JFileChooser.APPROVE_OPTION){
					docX.setDocxFile(fileChooser.getSelectedFile());
				}
			}
		});
	}
	//builds the matrix object data into the UI
	public void buildUI(){
		//remove all matrix input fields
		matrixArea.removeAll();
		matrixArea.revalidate();
		matrixArea.repaint();	
		//set up the gridLayout with new matrix size and reconstruct the matrix fields 
		matrixArea.setLayout(new GridLayout(matrix.getRows(), matrix.getColumns(), 0, 0));
		inputMatrix.removeAll(inputMatrix);
		for(int i = 0; i < 	matrix.getRows(); i++){
			inputMatrix.add(new ArrayList<JFormattedTextField>());
			for(int j = 0; j < matrix.getColumns(); j++){
				inputMatrix.get(i).add(new JFormattedTextField(formatter));/*{
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
				});*/
				inputMatrix.get(i).get(j).setPreferredSize(new Dimension(40,40));
				inputMatrix.get(i).get(j).setFont(new Font("OCR A Extended", Font.BOLD, 16));
				inputMatrix.get(i).get(j).setHorizontalAlignment(JTextField.CENTER);
				matrixArea.add(inputMatrix.get(i).get(j));
 			}
 		}
		//resize the operation combo boxes to fit the row number that the user picked
		resizeComboBoxes();
 	}
	public void updateMatrix(){
		for(int i = 0; i < 	matrix.getRows(); i++){
			for(int j = 0; j < matrix.getColumns(); j++){
				matrix.setValue(i, j, Double.valueOf(inputMatrix.get(i).get(j).getText()));
 			}
 		}
 	}
	
	//copies the values back to the text field array for after required operations are done
	//updates the UI with the data from the matrix object
	public void updateUI(){
		for(int i = 0; i <  matrix.getRows(); i++){
			for(int j = 0; j < matrix.getColumns(); j++){
				inputMatrix.get(i).get(j).setText(String.valueOf(matrix.getValue(i, j)));
 				//if the value isn't an irrational number, convert to int first then to string, to remove the decimal point
				if(matrix.getValue(i, j) % 1 == 0){
					inputMatrix.get(i).get(j).setText(String.valueOf((int)matrix.getValue(i, j)));
 				}else{			
 					inputMatrix.get(i).get(j).setText(String.valueOf(matrix.getValue(i, j)));
 				}
 			}
 		}
 	}
	
	//changes the size of the selection combo boxes to reflect the changes in matrix size
	public void resizeComboBoxes(){
		//if the selection list is smaller than the current rows selected by the user, add more items, else remove items
		if(addFrom.getItemCount() < matrix.getRows()){
			while(addFrom.getItemCount() != matrix.getRows()){
				addFrom.addItem(addFrom.getItemCount() + 1);
				addTo.addItem(addTo.getItemCount() + 1);
				swapFrom.addItem(swapFrom.getItemCount() + 1);
				swapTo.addItem(swapTo.getItemCount() + 1);
				multiplyFrom.addItem(multiplyFrom.getItemCount()+ 1);
				multiplyTo.addItem(multiplyTo.getItemCount()+ 1);
			}
		}else{
			while(addFrom.getItemCount() != matrix.getRows()){
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
/*	public void toDocx(String s){
		if(option2.isSelected()){
			docX.addMatrix(matrix, s);
		}
	}*/
	
}
