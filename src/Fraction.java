public class Fraction{
	private int numerator = 0, denominator = 0;
	private boolean isNegative = false;
	
	//takes a string representing the fraction. i.e -1/3
	public Fraction(String fractionString){
		//check if string contains "-" sign and negate accordingly
		if(fractionString.contains("-")){
			fractionString = fractionString.replace("-", "");
			isNegative = true;
		}
		numerator = Integer.valueOf(fractionString.split("/")[0]);
		denominator = Integer.valueOf(fractionString.split("/")[1]);	
	}
	
	public Fraction(double d){
		if(d < 0){
			d *= -1;
			isNegative = true;
		}
		doubleToFraction(d);
	}
	
	public Fraction(int numerator, int denominator, boolean isNegative){
		this.isNegative = isNegative;
		this.numerator = numerator;
		this.denominator = denominator;
	}
	
	public String toString(){
		if(isNegative){
			return "-" + numerator + "/" + denominator;	
		}else{
			return numerator + "/" + denominator;	
		}
	}
	
	public double toDecimal(){
		if(isNegative){
			return (double)numerator/denominator * -1 ;	
		}else{	
			return (double)numerator/denominator;	
		}	
	}
	
	public static String valueOf(double decimal){
		return new Fraction(decimal).toString();
	}
	
	public int getNumerator(){
		return numerator;
	}
	
	public int getDenominator(){
		return denominator;
	}
	
	public void doubleToFraction(double num){
		double n = Math.floor(num);
		double error = 0.000001;
		
		num -= n;
		
		int lower_n = 0;
		int lower_d = 1;
		
		int upper_n = 1;
		int upper_d = 1;
		
		while(true){
			//System.out.println(num);
			
			int middle_n = lower_n + upper_n;
			int middle_d = lower_d + upper_d;
			
			//System.out.println(middle_n + "/" + middle_d);
			//System.out.println((double)middle_n/middle_d);
			//System.out.println();
			
			//if num < middle
			if(num + error < (double)middle_n/middle_d){
				upper_n = middle_n;
				upper_d = middle_d;
			//if num > middle
			}else if(num - error > (double)middle_n/middle_d){
				lower_n = middle_n;
				lower_d = middle_d;
			}else{
				numerator = (int) n * middle_d + middle_n;
				denominator =  middle_d;
				//System.out.println("done");
				break;
			}		
		}	
	}
	
/*	public void bigDecimalToFraction(BigDecimal num){
		boolean isNegative = false;
		
		num = num.round(new MathContext(6, RoundingMode.HALF_UP));
		//numbers after the decimal point
		int countAfterPoint = String.valueOf(num).length() - (String.valueOf(num).indexOf(".")+1);
		long numerator = num.multiply(BigDecimal.valueOf(Math.pow(10, countAfterPoint))).longValue();
		long denominator = (long)Math.pow(10, countAfterPoint);
		if(numerator < 0){
			numerator *= -1;
			isNegative = true;
		}
		if(denominator < 0){
			denominator *= -1;
			isNegative = true;
		}
		System.out.println("Count after point " + countAfterPoint);
		System.out.println(numerator+ "   " + denominator);
		long gcd = getGCD(numerator, denominator);	
		
		if(isNegative){
			//return "-" + numerator/gcd + "/" + denominator/gcd;	
		}else{
			//return numerator/gcd + "/" + denominator/gcd;	
		}		
	}*/
	
	public long getGCD(long val1, long val2){
		long gcd = 0;
		if(val1 > val2){
			System.out.println(val1 +"<"+ val2);
			for(long i = val2; i > 0; i--){
				//System.out.println(val1 % i + "  " + val2 % i);
				if(val1 % i == 0 && val2 % i == 0){
					gcd = i;
					break;
				}
			}
		}else{
			System.out.println(val2 + ">"+ val1);
			for(long i = val1; i > 0; i--){
				//System.out.println(val1 % i + "  " + val2 % i);
				if(val1 % i == 0 && val2 % i == 0){
					gcd = i;
					break;
				}
			}
		}
		System.out.println("GCD: " + gcd);
		return gcd;
	}
}
