package client;

public class Rubric {

	public String RubricText;
	public int RubricScore=0;
	public String RubricComment="";
	public AutomatedMetareview AutomatedMetareview;
	public static Rubric Get_Rubric(String _rubric_text, int _rubric_score, String _rubric_comment)
	{
		Rubric rubric=new Rubric();
		rubric.RubricText=_rubric_text;
		rubric.RubricScore=_rubric_score;
		rubric.RubricComment=_rubric_comment;
		return rubric;
	}
	public void UpdateMetareview(org.json.JSONObject jsonObject)
	{
		
		AutomatedMetareview=new AutomatedMetareview(jsonObject.getDouble("coverage"),jsonObject.getDouble("content_problem"),jsonObject.getDouble("tone_negative"),jsonObject.getDouble("tone_positive"),jsonObject.getDouble("tone_neutral"),jsonObject.getDouble("quantity"),jsonObject.getDouble("content_summative"),jsonObject.getDouble("content_advisory"),jsonObject.getDouble("relevance"),jsonObject.getBoolean("plagiarism"));
	}
}
