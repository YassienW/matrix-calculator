

import java.awt.Cursor;
import java.io.File;
import java.util.ArrayList;

import javax.xml.bind.JAXBElement;

import org.docx4j.Docx4J;
import org.docx4j.math.CTChar;
import org.docx4j.math.CTD;
import org.docx4j.math.CTDPr;
import org.docx4j.math.CTM;
import org.docx4j.math.CTMR;
import org.docx4j.math.CTOMath;
import org.docx4j.math.CTOMathArg;
import org.docx4j.math.CTOMathPara;
import org.docx4j.math.CTR;
import org.docx4j.math.CTText;
import org.docx4j.math.ObjectFactory;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.RFonts;
import org.docx4j.wml.RPr;
import org.docx4j.wml.Text;


public class DocXManager {
	private WordprocessingMLPackage wordMLPackage;
	private MainDocumentPart mainDocument;
	private File targetFile;
	private UI ui;
	//private OperationsManager operations = new OperationsManager(null);
	
	public DocXManager(UI ui){
		this.ui = ui;
	}
	
	public void setDocxFile(File targetFile){
		this.targetFile = targetFile;
		try{
			ui.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			wordMLPackage = Docx4J.load(targetFile);
			ui.printStringToConsole("Document Loaded!");
		}catch(Docx4JException e){
			e.printStackTrace();
			ui.printStringToConsole("Unable to load document :(");	
		}finally{
			ui.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}
	
	//adds text to the end of the word document
	public void addText(String text){	
		mainDocument = wordMLPackage.getMainDocumentPart();
		mainDocument.addParagraphOfText(text);
		try{
			wordMLPackage.save(targetFile);
		}catch(Docx4JException e){
			e.printStackTrace();
		}
	}
	
	public void addMatrix(ArrayList<ArrayList<Double>> matrix, String s){
		mainDocument = wordMLPackage.getMainDocumentPart();
		ObjectFactory mathObjFactory = new ObjectFactory();
		P paragraph = new P();
		try{
			mainDocument.getContents().getBody().getContent().add(paragraph);
		}catch(Docx4JException e){
			e.printStackTrace();
		}
		
		//add the text that describes the operation, before the matrix;
		R r = new R();
		paragraph.getContent().add(r);
		Text t = new Text();
		t.setSpace("preserve");
		t.setValue(s + "   ");
		r.getContent().add(t);
				
		//set up and add OMathPara and it's properties
		CTOMathPara oMathPara = mathObjFactory.createCTOMathPara();
		//wrapper to fix the XmlRootElement issue
		JAXBElement<CTOMathPara> oMathParaWrapper = mathObjFactory.createOMathPara(oMathPara);
		paragraph.getContent().add(oMathParaWrapper);
		
		//Pr section is automatically added
		/*CTOMathParaPr oMathParaPr = mathObjFactory.createCTOMathParaPr();
		oMathPara.setOMathParaPr(oMathParaPr);
		CTOMathJc jc = new CTOMathJc();
		jc.setVal(STJc.CENTER);
		oMathParaPr.setJc(jc);*/
		
		//set up and add OMath
		CTOMath oMath = mathObjFactory.createCTOMath();
		oMathPara.getOMath().add(oMath);
		
		//set up and add brackets, with their properties(d object with an e object inside)
		CTD d = new CTD();
		JAXBElement<CTD> dWrapper = mathObjFactory.createCTOMathD(d);
		oMath.getEGOMathElements().add(dWrapper);
		CTDPr dPr = new CTDPr();
		CTChar char1 = new CTChar();
		char1.setVal("[");
		dPr.setBegChr(char1);
		CTChar char2 = new CTChar();
		char2.setVal("]");
		dPr.setEndChr(char2);
		d.setDPr(dPr);
		CTOMathArg dE = mathObjFactory.createCTOMathArg();
		d.getE().add(dE);
		
		//matrix(m), and its properties
		CTM m = mathObjFactory.createCTM();
		//wrapper to fix the XmlRootElement issue
		JAXBElement<CTM> matrixWrapper = mathObjFactory.createCTOMathM(m);
		dE.getEGOMathElements().add(matrixWrapper);
		
		//somehow it automatically adds the matrixPr section, good4me
		/*CTMPr matrixPr = null;
		try{
			matrixPr = (CTMPr)XmlUtils.unmarshalString(mPr, org.docx4j.jaxb.Context.jc, org.docx4j.math.CTMR.class);;
		}catch(JAXBException e){
			e.printStackTrace();
		}
		matrix.setMPr(matrixPr);*/
		
		//each loop adds a matrix row (m:mr element)
		for(int i = 0; i < matrix.size(); i++){
			CTMR matrixRow = mathObjFactory.createCTMR();
			m.getMr().add(matrixRow);
			for(int j = 0; j < matrix.get(i).size(); j++){
				//for future fraction support in the word document
			/*	if(fraction){
					
				}else{
					
				}*/
				CTOMathArg e = mathObjFactory.createCTOMathArg();
				matrixRow.getE().add(e); 
				CTR mR = mathObjFactory.createCTR(); 
				JAXBElement<CTR> mrWrapper = mathObjFactory.createCTOMathArgR(mR); 
				e.getEGOMathElements().add(mrWrapper);
				RPr rpr = new RPr(); 
				mR.getContent().add(rpr); 
				RFonts rfonts = new RFonts(); 
	            rfonts.setAscii("Cambria Math"); 
	            rfonts.setHAnsi("Cambria Math"); 
	            rpr.setRFonts(rfonts); 
				CTText text = mathObjFactory.createCTText();
				JAXBElement<CTText> textWrapper = mathObjFactory.createCTRTMath(text);
				if(matrix.get(i).get(j) % 1 != 0){
					text.setValue(new Fraction(matrix.get(i).get(j)).toString()); 
				}else{
					text.setValue(String.valueOf(matrix.get(i).get(j).intValue())); 
				}	 
			    mR.getContent().add(textWrapper); 	   
			}
		}
		try{
			wordMLPackage.save(targetFile);
			ui.printStringToConsole("Saved to Docx!");
		}catch(Docx4JException e){
			e.printStackTrace();
		}
	}
	
	//adds a fraction to the given e object, given the numerator and the denominator
	public void addFraction(int numerator, int denominator, CTOMathArg e){
		
	}
}
