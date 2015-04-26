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

import org.json.JSONException;
import org.json.JSONObject;

public class JsonClient {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// CallServicePost();
		// DatabaseConnect();
		System.out.println("start");
		try {
			DbAdapter dbAdapter = DbAdapter.GetInstance();

			Review review = Get_Review_Object(dbAdapter.Get_ResultSet(DBQuery
					.GetReviewQuery()));
			
			System.out.println(review.SubmissionHyperlink);
			for(Rubric rubric:review.RubricList)
			{
				System.out.println(rubric.RubricText);
				System.out.println(rubric.RubricComment);
				System.out.println("----------");
			}
			System.out.println("exit");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static Review Get_Review_Object(ResultSet _resultset)
			throws SQLException {
		Review review = new Review();
		if (!_resultset.next())
			return review;

		review.ReviewId = _resultset.getInt(1);
		review.ReviewMapId = _resultset.getInt(3);
		String hyperlink=_resultset.getString(2);
		hyperlink=hyperlink.substring(hyperlink.indexOf("http"));
		review.SubmissionHyperlink=hyperlink;
		
		while (_resultset.next() && (review.ReviewId == _resultset.getInt(1))) {

			review.RubricList.add(Rubric.Get_Rubric(_resultset.getString(4),
					_resultset.getInt(5), _resultset.getString(6)));
			_resultset.next();
		}
		return review;
	}

	public static Review Update_Hyperlink(Review review, ResultSet _resultset)
	{
		try
		{
		while (_resultset.next()) {
			review.SubmissionHyperlink = _resultset.getString(1);
		}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			
		}
		return review;
	}

	public static Review Add_Submission(Review review, ResultSet _resultset) {

		return review;
	}

	public static void CallServicePost() {
		// JSONObject json =
		// readJsonFromUrl("http://localhost:3000/Metareviewgenerator/");
		// System.out.println(json.toString());
		try {

			// web service url
			URL url = new URL("http://localhost:3000/metareviewgenerator");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");

			// sample inpung
			String review = "They do were necessary  but some of the points don't really lend themselves to being two sided.  Is phishing bad?";
			String submission = "They do were necessary  but some of the points don't really lend themselves to being two sided.  Is phishing bad?";
			String rubric = "describe the organization of the page.";

			// create JSON input for webservice
			String input = "{\"reviews\":\"" + review + "\",\"submission\":\""
					+ submission + "\",\"rubric\":\"" + rubric + "\"}";

			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();

			/*
			 * if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
			 * throw new RuntimeException("Failed : HTTP error code : " +
			 * conn.getResponseCode()); }
			 */

			JSONObject json;
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						(conn.getInputStream())));
				String jsonText = readAll(br);
				json = new JSONObject(jsonText);

			} finally {
				conn.disconnect();
			}
			System.out.println(json.toString());

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}
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

	/*
	 * 
	 * 
	 * 
	 * end
	 */
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
