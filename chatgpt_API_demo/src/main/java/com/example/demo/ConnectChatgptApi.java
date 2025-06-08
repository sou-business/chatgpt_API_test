package com.example.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.net.URI.create;

@Component
public class ConnectChatgptApi {

    @Value("${openai.api.key}")
    private String apiKey;

    public ConnectChatgptApi() {
    }

    public void setTextFromChatgpi(ChatgptObj chatgptObj) {
        String textToChatgpt = String.join("要約して「", chatgptObj.getTextFromDisplay(), "」");

        String requestJson = """
                {
                  "model": "gpt-3.5-turbo",
                  "messages": [
                    {"role": "user", "content": "%s"}
                  ]
                }
                """.formatted(textToChatgpt);

        try (HttpClient client = HttpClient.newHttpClient();) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(create("https://api.openai.com/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode responseReadTree = mapper.readTree(response.body());
            JsonNode choicesNode  = responseReadTree.path("choices");
            final String reply;
            if (choicesNode.isArray() && !choicesNode.isEmpty()) {
                reply = choicesNode.get(0).path("message").path("content").asText();
            } else {
                reply = responseReadTree.get("error").get("message").toString();
            }
            chatgptObj.setTextFromChatgpt(reply);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
