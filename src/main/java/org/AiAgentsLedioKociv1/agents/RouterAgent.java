package org.AiAgentsLedioKociv1.agents;

import com.google.gson.JsonObject;
import org.AiAgentsLedioKociv1.core.GeminiClient;

public class RouterAgent {
    private final GeminiClient client;

    public RouterAgent(GeminiClient client) {
        this.client = client;
    }

    public String route(String userInput, String currentAgent) {

        String prompt = "You are an Intent Classifier for a Help Desk.\n" +
                "The user is currently talking to the: " + currentAgent + " agent.\n\n" +
                "RULES FOR ROUTING:\n" +
                "1. STICKINESS: If the input is a short answer (like 'pro', 'basic', 'yes', 'no', '12345') or a direct response to a question, YOU MUST KEEP IT IN " + currentAgent + ".\n" +
                "2. ONLY switch agents if the user explicitly changes the topic (e.g., 'I want to switch', 'Help me with something else').\n\n" +
                "CATEGORIES:\n" +
                "TECHNICAL: Router, wifi, red light, errors, firmware, connection.\n" +
                "BILLING: Refunds, prices, plans (pro/basic), upgrades, ID numbers.\n" +
                "CASUAL: Hello, goodbye, jokes, off-topic (ONLY if not related to the above).\n\n" +
                "Reply ONLY with the category name.\n" +
                "Input: " + userInput;

        // same reason as for technical agent
        JsonObject response = client.generateContent(prompt, null);

        // here i extract text from the JSON response
        try {
            String text = response.getAsJsonObject("content")
                    .getAsJsonArray("parts")
                    .get(0).getAsJsonObject()
                    .get("text").getAsString();

            return text.trim().toUpperCase();
        } catch (Exception e) {
            return "CASUAL";
        }
    }
}
