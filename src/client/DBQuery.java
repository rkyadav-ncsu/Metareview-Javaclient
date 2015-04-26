package client;

public class DBQuery {
	public static String GetSubmissionQuery(int review_Id)
	{
		String query="Select submitted_hyperlinks from participants P "+
						"where id in(select  reviewee_id  from response_maps RM "+
						"inner join responses R on R.Map_id=RM.id where R.id="+review_Id+
						") and submitted_hyperlinks is not null";
		return query;
		
	}
	public static String GetReviewQuery()
	{
		String query="select Review.id as ReviewId, P.submitted_hyperlinks as submitted_hyperlink, Review.map_id as submissionId, "+
						" questions.txt as rubric, scores.score, scores.comments as review "+
						" from responses Review "+
						" inner join responses Submission on Review.id=Submission.map_id "+
						" inner join response_maps RM on Review.map_id=RM.reviewed_object_id "+
						" inner join scores on scores.response_id=Review.id "+
						" inner join questions on questions.id=scores.question_id "+
						" inner join participants P on P.id=RM.reviewee_id "+
						" where RM.type='FeedbackResponseMap' and P.submitted_hyperlinks is not null ";
		return query;
	}

}
