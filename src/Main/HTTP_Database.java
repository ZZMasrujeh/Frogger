package Main;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

class HTTP_Database {
    private HttpsURLConnection con;
    private final String USER_AGENT = "Mozilla/67.0";
    private String KEY = "apikey=1d7c89b20b359452ca00ad18c41b3fa4c4800";

    HTTP_Database() {}

    ArrayList<ScoreModel> getHighscores() throws Exception {
        URL u = new URL(
                "https://frogger-0dda.restdb.io/rest/highscores?" + KEY
                        + "&h={\"$fields\":{\"Name\":1,\"score\":1}}"
                        + "&sort=score"
                        + "&dir=-1"
                        + "&max=" + Highscores.maxRecords
        );
        con = (HttpsURLConnection) u.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        return addToList(getResponse());
    }

    /**
     * Adds a new row to the database.
     * @param name Row's name value.
     * @param score Row's score value.
     */
    void post(String name, int score){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                String url = "https://frogger-0dda.restdb.io/rest/highscores?" +KEY;
                URL obj = new URL(url);
                con = (HttpsURLConnection) obj.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("User-Agent", USER_AGENT);
                con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                con.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes("Name="+name+"&score="+score);
                wr.flush();
                wr.close();

                getResponse();
                }catch (Exception ignored){}
            }
        });
        t.start();
    }

    private String getResponse() throws Exception {
        int responseCode = con.getResponseCode();
        if (responseCode < 200 || responseCode > 299) {
            return "error";
        }

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //no contents in the database.
        if (response.toString().equals("[]"))
            return "error";

        return response.toString();
    }

    /**
     * Parse request response and add the contents to a list of ScoreModels.
     * @param json The response
     * @return An ArrayList with the response's contents.
     */
    private ArrayList<ScoreModel> addToList(String json) throws Exception {
        if (json.equals("error"))
            return null;
        String patternSquareBrS = "\\[\\u0020*";
        String patternSquareBrE = "\\u0020*]";
        String patterCurlyBrS = "\\{\\u0020*";
        String patterCurlyBrE = "\\u0020*}";

        json = json.replaceAll(patternSquareBrS, "");
        json = json.replaceAll(patternSquareBrE, "");
        String[] contents = json.split(", ");

        ArrayList<ScoreModel> list = new ArrayList<ScoreModel>();
        for (String content : contents) {
            content = content.replaceAll(patterCurlyBrS, "");
            content = content.replaceAll(patterCurlyBrE, "");
            String[] subcontents = content.split(",");
            String name = "";
            int score = 0;

            for (String s : subcontents) {
                s = s.replaceAll("\"", "");
                s = s.replaceAll(":", "");
                if (s.contains("Name")){
                    name = s.replace("Name", "");
                }
                if (s.contains("score")){
                    score = Integer.parseInt(s.replace("score", ""));
                }
            }
            list.add(new ScoreModel(name, score));
        }
            deleteAllBut(list.toString());

        return list;
    }

    /**
     * Delete everyting from the database except whatever was retrieved as top 10.
     * @param array The string representation of the array of values not to be deleted.
     */
    private void deleteAllBut(String array){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                URL u = new URL(
                        "https://frogger-0dda.restdb.io/rest/highscores/*?"+KEY
                                +"&q={\"score\":{\"$nin\":"+array.replaceAll(", ",",")+"}}"
                );
                con = (HttpsURLConnection) u.openConnection();
                con.setRequestMethod("DELETE");
                con.setRequestProperty("User-Agent", USER_AGENT);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                }catch (Exception ignored) { }
            }
        });
        t.start();
    }
}
