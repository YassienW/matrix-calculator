import java.io.File;
import java.util.ArrayList;

import javax.swing.JFormattedTextField;

public class OperationsManager{
	private UI ui = new UI(this);
	private DocXManager docX = new DocXManager();
	private Matrix matrix = new Matrix();
	private boolean docXOn = false;
	
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
		matrix.addMultipleOfRowToRow(row1, row2, value);
		//if the value = 1, change the output text, since the functionality is different
		if(value == 1){
			ui.printStringToConsole("Add Row " + row1 + " to Row " + row2);
			toDocx(String.format("R%d + R%d", row1, row2));
		}else{
			if(value % 1 == 0){
				ui.printStringToConsole("Multipy Row " + row1 + " By " + (int)value + " and add it to Row " + row2);
				toDocx(String.format("R%d x %d + R%d", row1, (int)value, row2));
			}else{
				ui.printStringToConsole("Multipy Row " + row1 + " By " + Fraction.valueOf(value) + " and add it to Row " + row2);
				toDocx(String.format("R%d x %s + R%d", row1, Fraction.valueOf(value), row2));
			}
			
		}
		ui.printMatrixToConsole(matrix);
		ui.update(matrix);
	}
	public void multiplyRow(int row, double value){
		updateMatrix(ui.getInputMatrix());
		if(value % 1 == 0){
			ui.printStringToConsole("Multipy Row " + row + " By " + (int)value);
			toDocx(String.format("R%d x %d", row, (int)value));
		}else{
			ui.printStringToConsole("Multipy Row " + row + " By " + Fraction.valueOf(value));
			toDocx(String.format("R%d x %s", row, Fraction.valueOf(value)));
		}	
		matrix.multiplyRow(row, value);
		ui.printMatrixToConsole(matrix);
		ui.update(matrix);
	}
	public void swapRowWithRow(int row1, int row2){
		updateMatrix(ui.getInputMatrix());
		matrix.swapRowWithRow(row1, row2);
		ui.printStringToConsole("Swap Row " + row1 + " With Row " + row2);
		ui.printMatrixToConsole(matrix);
		toDocx(String.format("R%d <-> R%d", row1, row2));
		ui.update(matrix);
	}
	//automatically reduces a double matrix to reduced row form
	public void reduce(){
		updateMatrix(ui.getInputMatrix());
		ui.printStringToConsole("Original Matrix");
		ui.printMatrixToConsole(matrix);
		toDocx("Original Matrix");
		//add a condition for matrices that have more rows than columns, so that the loop doesn't exceed the smallest value
		
		//loops diagonally through the pivot points and operates on them to be == 1
		for(int i = 0; i < matrix.getRows(); i++){
			//if diagonal entry is 0 AND the row under it isn't an all zero row(because all zero rows have to stay in the bottom),
			//switch that row with the row under it
			//added a check to make sure the row under current row isn't out of bounds
			if(i+2 < matrix.getRows()){
				if(matrix.getValue(i, i) == 0 && !matrix.isZeroRow(i+2)){
					swapRowWithRow(i+1, i+2);
				}
			}
			if(matrix.getValue(i, i) != 1 && matrix.getValue(i, i) != 0){	
				//before changing the pivot value to 1, check if there are any other rows under it with 1 or -1 in the current column,
				//IF there is, switch that row with the current row
				for(int j = i+1; j < matrix.getRows(); j++){
					if(matrix.getValue(j, i) == 1 || matrix.getValue(j, i) == -1){
						swapRowWithRow(i+1, j+1);
						break;
					}
				}
				//add one because the function takes row number NOT the array index (row number > 0)
				//add a condition to only do this if it isn't == 1 (since the goal is to change it to 1)
				//we add this because sometimes the previous operation switches the pivot row to one that already has a 1
				//therefore operating on it would be a useless operation
				if(matrix.getValue(i, i) != 1){
					multiplyRow(i+1, 1 / matrix.getValue(i, i));
				}
				
			}
			//for each pivot point, we zero the values above/under it, after turning it to 1 (previous loop)
			for(int k = 0; k < matrix.getRows(); k++){
				if(matrix.getValue(k, i) != 0 && k != i && matrix.getValue(i, i) != 0){	
					addMultipleOfRowToRow(i+1, k+1, matrix.getValue(k, i) * -1);
				}		
			}	
		}
		//ui.printStringToConsole("Running Back Check..");
		//run checks to make sure matrix form is correct
		//iterate through the rows from bottom to top, if the row above is a zero row, switch current row with it
		for(int i = matrix.getRows(); i > 1; i--){
			if(matrix.isZeroRow(i-1)){
				swapRowWithRow(i, i-1);
			}
		}
		//For each non zero row, if the first entry in it isn't one, change it to one and zero the values under/above it using it
		for(int i = 0; i < matrix.getRows(); i++){
			for(int j = 0; j < matrix.getColumns(); j++){
				if(matrix.getValue(i, j) == 1){
					break;
				}else if(!matrix.isZeroRow(i+1) && matrix.getValue(i, j) != 0){
					multiplyRow(i+1, 1 / matrix.getValue(i, j));
					for(int k = 0; k < matrix.getRows(); k++){
						if(matrix.getValue(k, j) != 0 && matrix.getValue(k, j) != 1){	
							addMultipleOfRowToRow(i+1, k+1, matrix.getValue(k, j) * -1);
						}		
					}
				}		
			}
		}
		ui.printStringToConsole("Done!");
	}
	public void setDocxFile(File f){
		new Thread("Non EDT"){
			public void run(){
				ui.printStringToConsole(docX.setDocxFile(f));
			}
		}.start();
	}
	public void setDocxOn(boolean isOn){
		this.docXOn = isOn;
	}
	public void toDocx(String s){
		if(docXOn){
			docX.addMatrix(matrix, s);
		}
	}
}
