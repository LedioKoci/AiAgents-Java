package org.AiAgentsLedioKociv1.agents;

import com.google.gson.JsonObject;
import org.AiAgentsLedioKociv1.core.GeminiClient;

public class TechnicalAgent {

    private final GeminiClient client;
    private final org.AiAgentsLedioKociv1.core.KnowledgeBase knowledgeBase;

    public TechnicalAgent(GeminiClient client, org.AiAgentsLedioKociv1.core.KnowledgeBase knowledgeBase){
        this.client = client;
        this.knowledgeBase = knowledgeBase;
    }

    // in this method the technical agent uses the string gathered from the vector similarity to gain context and reply to the user
    public String handleRequest(String input, String history){

        String context = knowledgeBase.search(input);

        String prompt = "You are a technical support agent\n" +
                "Answer STRICTLY based on the context below\n" +
                "If the answer is not in the context, say 'i don't know'\n\n" +
                "This is the chat history so far: " + history +
                "Context: " + context + "\n\n" +
                "User Question: " + input;

        // this is because teh tools are not needed
        JsonObject response = client.generateContent(prompt, null);

        // this is where i extract text manually
        try {
            return response.getAsJsonObject("content")
                    .getAsJsonArray("parts")
                    .get(0).getAsJsonObject()
                    .get("text").getAsString();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
