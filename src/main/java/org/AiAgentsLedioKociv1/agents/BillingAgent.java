package org.AiAgentsLedioKociv1.agents;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.AiAgentsLedioKociv1.core.GeminiClient;

public class BillingAgent {

    private final GeminiClient client;
    private final Gson gson;

    public BillingAgent(GeminiClient client) {
        this.client = client;
        this.gson = new Gson();
    }

    // in the handle method, i tell gemini to generate a json kind of text that i will then manage in the executeTool method
    public String handle(String input, String history) {
        String prompt = "You are a billing agent\n" +
                "You have access to these tools:\n" +
                " - refund(plan_name): returns the refund amount\n" +
                " - upgrade(user_id): upgrades a user\n\n" +
                "HISTORY:\n" + history + "\n\n" +
                "INSTRUCTIONS:\n" +
                "Look at the HISTORY. If the user provided a plan name for a refund, output JSON: { \"tool\": \"refund\", \"arg\": \"plan_name\" }\n" +
                "If the user provided an ID for an upgrade (like '1234'), output JSON: { \"tool\": \"upgrade\", \"arg\": \"user_id\" }\n" +
                "Otherwise, reply with text clarifying what you need.\n" +
                "User Input: " + input;

        String response = client.generateContent(prompt);

        // i need to implement this, in case gemini adds some markdown
        String cleanedResponse = response.replace("```json", "")
                .replace("```", "")
                .trim();

        if (cleanedResponse.trim().startsWith("{")) {
            return executeTool(cleanedResponse);
        } else {
            return response;
        }
    }

    // like mentioned above, not only i handle the logic here, but first i use gson to convert the string into an actual json object
    private String executeTool(String jsonResponse) {
        try {
            // here is where i store the values i need from the json string gemini generated from the user's input
            JsonObject cmd = gson.fromJson(jsonResponse, JsonObject.class);
            String tool = cmd.get("tool").getAsString();
            String arg = cmd.get("arg").getAsString();

            if ("refund".equals(tool)) {
                double amount = arg.toLowerCase().contains("pro") ? 50.00 : 15.00;
                return "refund processed. amount: " + amount;
            }
            else if ("upgrade".equals(tool)) {
                return "User " + arg + " has been upgraded to Pro.";
            }
            return "error: Unknown tool.";
        } catch (Exception e) {
            return "error executing tool.";
        }
    }
}

