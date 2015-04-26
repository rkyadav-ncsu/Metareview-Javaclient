package client;

public class Rubric {

	public String RubricText;
	public int RubricScore=0;
	public String RubricComment="";
	public static Rubric Get_Rubric(String _rubric_text, int _rubric_score, String _rubric_comment)
	{
		Rubric rubric=new Rubric();
		rubric.RubricText=_rubric_text;
		rubric.RubricScore=_rubric_score;
		rubric.RubricComment=_rubric_comment;
		return rubric;
	}
}
