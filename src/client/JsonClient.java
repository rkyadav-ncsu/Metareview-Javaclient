package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
//import lxml.etree
//import urllib
import org.jsoup.select.Elements;

public class JsonClient {

	public static void main(String[] args) {
		System.out.println("start");
		try {
			DbAdapter dbAdapter = DbAdapter.GetInstance();
			
			List<Review> listReview = Get_Review_Object(dbAdapter
					.Get_ResultSet(DBQuery.GetReviewQuery()));
			System.out.println(listReview.size());

			CallServicePost(listReview);

			System.out.println("exit");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static List<Review> Get_Review_Object(ResultSet _resultset)
			throws SQLException, IOException {
		List<Review> listReview = new ArrayList<Review>();
		if (!_resultset.next())
			return listReview;
		Review review = new Review();
		while (_resultset.next()) {
			if (review.ReviewId == _resultset.getInt(1)) {
				review.RubricList.add(Rubric.Get_Rubric(
						_resultset.getString(4), _resultset.getInt(5),
						_resultset.getString(6)));
				continue;
			} else {
				if (review != null && review.ReviewId != 0)
					listReview.add(review);
				review = new Review();
			}

			review.ReviewId = _resultset.getInt(1);
			review.ReviewMapId = _resultset.getInt(3);
			String hyperlink = _resultset.getString(2);
			hyperlink = hyperlink.substring(hyperlink.indexOf("http"));
			hyperlink = hyperlink.replace("wiki/", "");
			review.SubmissionHyperlink = hyperlink;
			Document doc = null;
			try {
				doc = Jsoup.connect(hyperlink).get();
			} catch (Exception e) {

			}
			if (doc != null) {
				Element link = doc.select("body").first();
				Elements newsHeadlines = doc.select("div[id=bodyContent]>p");
				String text = newsHeadlines.text();
				// review.SubmissionText = newsHeadlines.html();
				review.SubmissionText = text;
				review.RubricList.add(Rubric.Get_Rubric(
						_resultset.getString(4), _resultset.getInt(5),
						_resultset.getString(6)));
			}
		}
		listReview.add(review);
		return listReview;
	}

	public static Review Update_Hyperlink(Review review, ResultSet _resultset) {
		try {
			while (_resultset.next()) {
				review.SubmissionHyperlink = _resultset.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();

		}
		return review;
	}

	public static Review Add_Submission(Review review, ResultSet _resultset) {

		return review;
	}

	public static void CallServicePost(List<Review> listReview) {
		for (Review review : listReview) {
			String submission = review.SubmissionText;
			if(submission==null ||  submission.isEmpty())
				submission="We are not considering submission text right now.";
			System.out.println("===============review===========");
			System.out.println(review.ReviewId);
			// iterate for each review rubric
			for (Rubric rubric : review.RubricList) {
				// String reviewText = review.RubricList.get(0).RubricComment;
				String reviewText = rubric.RubricComment;
				String rubricText = rubric.RubricText;

				// create JSON input for webservice

				if (reviewText != null && rubricText != null
						&& submission != null && !reviewText.isEmpty() && !rubricText.isEmpty() && !submission.isEmpty()) {
					String input = "{\"reviews\":\""
							+ reviewText
							+ "\",\"rubric\":\""
							+ rubricText
							+ "\",\"submission\":\""
							+ (submission.length() > 20 ? submission.substring(0, 20) : submission) + "\"}";

					try {
						JSONObject jsonObject = GetJsonObject(input);
						if (jsonObject != null && rubric != null)
							rubric.UpdateMetareview(jsonObject);
					} catch (Exception e) {

					} finally {
						if (rubric.AutomatedMetareview != null) {
							DbAdapter dbAdapter;
							try {
								dbAdapter = DbAdapter.GetInstance();
								boolean sucess=dbAdapter.InsertQuery(DBQuery.GetInsertMetareviewQuery(review.ReviewId,rubric.RubricText,rubric.RubricScore,rubric.AutomatedMetareview.ToneNegative,rubric.AutomatedMetareview.ToneNeutral,rubric.AutomatedMetareview.TonePositive,rubric.AutomatedMetareview.ContentSummative,rubric.AutomatedMetareview.Quantity,rubric.AutomatedMetareview.ContentAdvisory,rubric.AutomatedMetareview.ContentProblem));
								if (!sucess)
									System.out.println("error");
							} catch (InstantiationException
									| IllegalAccessException
									| ClassNotFoundException | SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}							
						}

					}
				}
			}

		}
	}

	private static JSONObject GetJsonObject(String inputText)
			throws IOException {
		// web service url
		URL url = new URL("http://localhost:3000/metareviewgenerator/get_quantity");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		OutputStream os = conn.getOutputStream();
		os.write(inputText.getBytes());
		os.flush();
		JSONObject json = null;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));
			String jsonText = readAll(br);
			System.out.println(inputText);
			System.out.println(jsonText.toString());
			System.out.println();
			json = new JSONObject(jsonText);
		} catch (Exception e) {
			// to-do logging
		}
		conn.disconnect();
		return json;

	}

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public static JSONObject readJsonFromUrl(String url) throws IOException,
			JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is,
					Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
		}
	}

	public static void DatabaseConnect() throws ClassNotFoundException,
			InstantiationException, IllegalAccessException, SQLException {
		Class.forName("com.mysql.jdbc.Driver").newInstance();

		Connection m_Connection = DriverManager
				.getConnection("jdbc:mysql://localhost:3306/expertiza_development?user=root");

		Statement m_Statement = m_Connection.createStatement();

		// to do create the query to pull review, submission and rubrics
		String query = DBQuery.GetSubmissionQuery(1);

		ResultSet m_ResultSet = m_Statement.executeQuery(query);

		while (m_ResultSet.next()) {
			System.out.println(m_ResultSet.getString(1) + ", "
					+ m_ResultSet.getString(2) + ", "
					+ m_ResultSet.getString(3));

		}
	}

}
