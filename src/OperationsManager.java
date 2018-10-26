

import java.util.ArrayList;

import javax.swing.JFormattedTextField;

public class OperationsManager{
	private UI ui = new UI(this);
	private DocXManager docX = new DocXManager(ui);
	private Matrix matrix = new Matrix();
	
	public OperationsManager() {

	}
	public void setRows(int rows){
		matrix.setDimensions(rows, matrix.getColumns());
		ui.buildMatrixUI(matrix);
	}
	public void setColumns(int columns){
		matrix.setDimensions(matrix.getRows(), columns);
		ui.buildMatrixUI(matrix);
	}
	//updates the matrix object with the data input by the user
	private void updateMatrix(ArrayList<ArrayList<JFormattedTextField>> inputMatrix){
		for(int i = 0; i < 	matrix.getRows(); i++){
			for(int j = 0; j < matrix.getColumns(); j++){
				matrix.setValue(i, j, Double.valueOf(inputMatrix.get(i).get(j).getText()));
			}
		}
	}
	public void addMultipleOfRowToRow(int row1, int row2, double value){
		updateMatrix(ui.getInputMatrix());
		//if the value = 1, change the output text, since the functionality is different
		if(value == 1){
			//ui.printStringToConsole("Add Row " + row1 + " to Row " + row2);
			//ui.toDocx(String.format("R%d + R%d", row1, row2));
		}else{
			//ui.printStringToConsole("Multipy Row " + row1 + " By " + value + " and add it to Row " + row2);
			//ui.printStringToConsole("Multipy Row " + row1 + " By " + (int)value + " and add it to Row " + row2);
			//ui.toDocx(String.format("R%d x R%s + R%d", row1, String.valueOf(value), row2));
		}
		//ui.printMatrixToConsole(matrix);
		ui.update(matrix);
	}
	public void multiplyRow(int row, double value){
		updateMatrix(ui.getInputMatrix());
		if(value % 1 == 0){
			//ui.printStringToConsole("Multipy Row " + row + " By " + (int)value);
		}else{
			//ui.printStringToConsole("Multipy Row " + row + " By " + Fraction.valueOf(value));
		}	
		matrix.multiplyRow(row, value);
		//ui.printMatrixToConsole(matrix);
		//ui.toDocx(String.format("R%d x %s", row, String.valueOf(value)));
		ui.update(matrix);
	}
	public void swapRowWithRow(int row1, int row2){
		updateMatrix(ui.getInputMatrix());
		matrix.swapRowWithRow(row1, row2);
		//ui.printStringToConsole("Swap Row " + row1 + " With Row " + row2);
		//ui.printMatrixToConsole(matrix);
		//ui.toDocx(String.format("R%d <-> R%d", row1, row2));
		ui.update(matrix);
	}
}
