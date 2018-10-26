import java.util.ArrayList;

public class Matrix{
	private int rows, columns;
	private ArrayList<ArrayList<Double>> matrix = new ArrayList<ArrayList<Double>>();
	
	public Matrix(int rows, int columns){
		setDimensions(rows, columns);
	}
	public void setDimensions(int rows, int columns){
		this.rows = rows;
		this.columns = columns;
	}
	public int getRows(){
		return rows;
	}
	public int getColumns(){
		return columns;
	}
	public void getRowValues(int index){
		
	}
	public void getColumnValues(int index){
		
	}
	public void multiplyRow(int row, double value){
		if(value % 1 == 0){
			//ui.printStringToConsole("Multipy Row " + row + " By " + (int)value);
		}else{
			//ui.printStringToConsole("Multipy Row " + row + " By " + Fraction.valueOf(value));
		}	
		int i = 0;
		for(double d : matrix.get(row-1)){
			matrix.get(row-1).set(i, d * value);
			i++;
		}
		//ui.printMatrixToConsole(matrix);
		//ui.toDocx(String.format("R%d x %s", row, String.valueOf(value)));
	}
	//adds value * row 1 to row 2 in a matrix of type double
		public void addMultipleOfRowToRow(int row1, int row2, double value){
			int i = 0;
			for(double d : matrix.get(row2-1)){
				matrix.get(row2-1).set(i, d + (matrix.get(row1-1).get(i) * value));
				i++;
			}
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
		}
		
		//swaps row1 with row2 in a matrix of type BigDecimal
		public void swapRowWithRow(int row1, int row2){
			ArrayList<Double> temp = new ArrayList<Double>();
			//ui.printStringToConsole("Swap Row " + row1 + " With Row " + row2);
			int i = 0;
			for(double d : matrix.get(row1-1)){
				temp.add(d);
				matrix.get(row1-1).set(i, matrix.get(row2-1).get(i));
				matrix.get(row2-1).set(i, temp.get(i));
				i++;
			}
			//ui.printMatrixToConsole(matrix);
			//ui.toDocx(String.format("R%d <-> R%d", row1, row2));
		}
		
		//checks if that row is an all zero row
		public boolean isAllZero(int row){
			for(double d : matrix.get(row-1)){
				if(d != 0){
					 return false;
				}
			}
			return true;
		}
		
		//automatically reduces a double matrix to reduced row form
		public void reduce(){
			//ui.printStringToConsole("Original Matrix");
			//ui.printMatrixToConsole(matrix);
			//add a condition for matrices that have more rows than columns, so that the loop doesn't exceed the smallest value
			
			
			//loops diagonally through the pivot points and operates on them to be == 1
			for(int i = 0; i < matrix.size(); i++){
				//if diagonal entry is 0 AND the row under it isn't an all zero row(because all zero rows have to stay in the bottom),
				//switch that row with the row under it
				//added a check to make sure the row under current row isn't out of bounds
				if(i+2 < matrix.size()){
					if(matrix.get(i).get(i) == 0 && !isAllZero(i+2)){
						swapRowWithRow(i+1, i+2);
					}
				}
				//System.out.println(i);
				if(matrix.get(i).get(i) != 1 && matrix.get(i).get(i) != 0){	
					//before changing the pivot value to 1, check if there are any other rows under it with 1 or -1 in the current column,
					//IF there is, switch that row with the current row
					for(int j = i+1; j < matrix.size(); j++){
						if(matrix.get(j).get(i) == 1 || matrix.get(j).get(i) == -1){
							swapRowWithRow(i+1, j+1);
							break;
						}
					}
					//add one because the function takes row number NOT the array index (row number > 0)
					//add a condition to only do this if it isn't == 1 (since the goal is to change it to 1)
					//we add this because sometimes the previous operation switches the pivot row to one that already has a 1
					//therefore operating on it would be a useless operation
					if(matrix.get(i).get(i) != 1){
						multiplyRow(i+1, 1 / matrix.get(i).get(i));
					}
					
				}
				//for each pivot point, we zero the values above/under it, after turning it to 1 (previous loop)
				for(int k = 0; k < matrix.size(); k++){
					if(matrix.get(k).get(i) != 0 && k != i && matrix.get(i).get(i) != 0){	
						addMultipleOfRowToRow(i+1, k+1, matrix.get(k).get(i) * -1);
					}		
				}	
			}
			//ui.printStringToConsole("Running Back Check..");
			//run checks to make sure matrix form is correct
			//iterate through the rows from bottom to top, if the row above is a zero row, switch current row with it
			for(int i = matrix.size(); i > 1; i--){
				if(isAllZero(i-1)){
					swapRowWithRow(i, i-1);
				}
			}
			//For each non zero row, if the first entry in it isn't one, change it to one and zero the values under/above it using it
			for(int i = 0; i < matrix.size(); i++){
				for(int j = 0; j < matrix.get(i).size(); j++){
					if(matrix.get(i).get(j) == 1){
						break;
					}else if(!isAllZero(i+1) && matrix.get(i).get(j) != 0){
						multiplyRow(i+1, 1 / matrix.get(i).get(j));
						for(int k = 0; k < matrix.size(); k++){
							if(matrix.get(k).get(j) != 0 && matrix.get(k).get(j) != 1){	
								addMultipleOfRowToRow(i+1, k+1, matrix.get(k).get(j) * -1);
							}		
						}
					}		
				}
			}
			//ui.printStringToConsole("Done!");
		}
}
