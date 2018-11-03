import java.util.ArrayList;

public class Matrix{
	private double[][] matrix;
	
	public Matrix(){
		matrix = new double[1][1];
	}
	public Matrix(int rows, int columns){
		setDimensions(rows, columns);
	}
	public void setDimensions(int rows, int columns){
		matrix = new double[rows][columns];
	}
	public int getRows(){
		return matrix.length;
	}
	public int getColumns(){
		return matrix[0].length;
	}
	public void setValue(int row, int column, double value){
		matrix[row][column] = value;
	}
	public double getValue(int row, int column){
		return matrix[row][column];
	}
	public void multiplyRow(int row, double value){	
		int i = 0;
		for(double d : matrix[row-1]){
			matrix[row-1][i] = d * value;
			i++;
		}
	}
	//adds value * row 1 to row 2
	public void addMultipleOfRowToRow(int row1, int row2, double value){
		int i = 0;
		for(double d : matrix[row2-1]){
			matrix[row2-1][i] = d + (matrix[row1-1][i] * value);
			i++;
		}
	}	
	//swaps row1 with row2
	public void swapRowWithRow(int row1, int row2){
		ArrayList<Double> temp = new ArrayList<Double>();
		int i = 0;
		for(double d : matrix[row1-1]){
			temp.add(d);
			matrix[row1-1][i] = matrix[row2-1][i];
			matrix[row2-1][i] = temp.get(i);
			i++;
		}
	}
	//checks if that row is an all zero row
	public boolean isZeroRow(int row){
		for(double d : matrix[row-1]){
			if(d != 0){
				 return false;
			}
		}
		return true;
	}
}
