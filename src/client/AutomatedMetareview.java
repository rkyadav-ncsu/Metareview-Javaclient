package client;

public class AutomatedMetareview {

	public double Coverage=0;
	public double ContentProblem=0;
	public double ToneNegative=0;
	public double TonePositive=0;
	public double ToneNeutral=0;
	public double Quantity=0;
	public boolean Plagiarism=false;
	public double ContentSummative=0;
	public double ContentAdvisory=0;
	public double Relevance=0;
	public AutomatedMetareview(double coverage, double contentProblem, double toneNegative, double tonePositive, double toneNeutral, double quantity, double contentSummative, double contentAdvisory, double relevance, boolean plagiarism)
	{
		Coverage=coverage;
		ContentProblem=contentProblem;
		ToneNegative=toneNegative;
		TonePositive=tonePositive;
		ToneNeutral=toneNeutral;
		Quantity=quantity;
		Plagiarism=plagiarism;
		ContentSummative=contentSummative;
		ContentAdvisory=contentAdvisory;
		Relevance=relevance;
		
	}
}
