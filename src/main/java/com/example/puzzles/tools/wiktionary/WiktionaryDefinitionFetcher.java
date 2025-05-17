package com.example.puzzles.tools.wiktionary;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.net.URI;
import java.util.List;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WiktionaryDefinitionFetcher {

    public List<String> fetchDefinition(String word) {
        try {
            String encodedWord = URLEncoder.encode(word, "UTF-8");
            String apiUrl = getProperty("wiktionary.api.url") + encodedWord;
            String json = getUrlContent(apiUrl);
            List<String> definitions = parseDefinitions(json);
            return definitions;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    private List<String> parseDefinitions(String json) {
        List<String> definitions = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject wordObject = jsonArray.getJSONObject(i);
                JSONArray meaningsArray = wordObject.getJSONArray("meanings");
                for (int j = 0; j < meaningsArray.length(); j++) {
                    JSONObject meaningObject = meaningsArray.getJSONObject(j);
                    JSONArray definitionsArray = meaningObject.getJSONArray("definitions");
                    for (int k = 0; k < definitionsArray.length(); k++) {
                        JSONObject definitionObject = definitionsArray.getJSONObject(k);
                        definitions.add(definitionObject.getString("definition"));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return definitions;
    }

    private String getUrlContent(String urlString) throws Exception {
        URL url = new URI(urlString).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder content = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            return content.toString();
        } finally {
            connection.disconnect();
        }
    }

    private String getProperty(String key) throws IOException {
        Properties properties = new Properties();
        try (InputStream input = WiktionaryDefinitionFetcher.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IOException("Unable to find config.properties");
            }
            properties.load(input);
        }
        return properties.getProperty(key);
    }

}
