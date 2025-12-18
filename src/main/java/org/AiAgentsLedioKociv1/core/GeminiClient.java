package org.AiAgentsLedioKociv1.core;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.cdimascio.dotenv.Dotenv;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GeminiClient {

        private final String apiKey;
        private final String baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/";
        private final HttpClient client;
        private final Gson gson;

        public GeminiClient(){
            Dotenv dotenv = Dotenv.load();
            this.apiKey = dotenv.get("GEMINI_API_KEY");
            this.client = HttpClient.newHttpClient();
            this.gson = new Gson();
        }

        // this method is used to generate the text content from gemini
        public JsonObject generateContent(String prompt, JsonObject toolsConfig) {

            String url = baseUrl + "gemini-2.5-flash-lite:generateContent?key=" + apiKey;

            JsonObject part = new JsonObject();
            part.addProperty("text", prompt);

            JsonArray parts = new JsonArray();
            parts.add(part);

            JsonObject content = new JsonObject();
            content.add("parts", parts);
            JsonArray contents = new JsonArray();
            contents.add(content);

            JsonObject payload = new JsonObject();
            payload.add("contents", contents);

            // by implementing toolsConfig, i am contacting directly the google API
            if (toolsConfig != null) {
                payload.add("tools", toolsConfig);
            }
            String responseBody = sendPost(url, payload.toString());
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);

            return jsonResponse.getAsJsonArray("candidates").get(0).getAsJsonObject();
        }

        // and this method is used to get the vector value of a text, from the gemini api
        public float[] getEmbedding(String text){

            String url = baseUrl + "text-embedding-004:embedContent?key=" + apiKey;

            JsonObject part = new JsonObject();
            part.addProperty("text", text);

            JsonObject content = new JsonObject();
            content.add("parts", part);

            JsonObject payload = new JsonObject();
            payload.add("content", content);
            payload.addProperty("model", "models/text-embedding-004");

            try{

                HttpRequest  request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(payload)))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);

                JsonArray values = jsonResponse.getAsJsonObject("embedding").getAsJsonArray("values");

                float[] vector = new float[values.size()];
                for (int i = 0; i < values.size(); i++) {
                    vector[i] = values.get(i).getAsFloat();
                }
                return vector;

            } catch (Exception e) {
                System.err.println("Embedding Failed: " + e.getMessage());
                return new float[0];
            }
        }

        // i created this method to manage better the whole request procedure, instead of putting it all together which makes it look messy
    private String sendPost(String url, String jsonBody){
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (Exception e) {
            return "{}";
        }
    }


}
