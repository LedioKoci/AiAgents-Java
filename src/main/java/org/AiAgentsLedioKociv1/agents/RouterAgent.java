package org.AiAgentsLedioKociv1.agents;

import org.AiAgentsLedioKociv1.core.GeminiClient;

public class RouterAgent {

    private  final GeminiClient client;

    public RouterAgent(GeminiClient client){
        this.client = client;
    }

    // the router agent needs to keep track of what was the earlier agent, to avoid going out of context
    public String route(String userInput, String currentAgent){

        String prompt = "Analyze the input and choose the best agent.\n" +
                "Categories:\n" +
                "TECHNICAL: for errors, equipment, troubleshooting, wifi.\n" +
                "BILLING: for refunds, prices, account upgrades, ID numbers.\n" +
                "CASUAL: for hello, goodbye, or off-topic.\n\n" +
                "CONTEXT: The user is currently talking to the " + currentAgent + " agent.\n" +
                "INSTRUCTION: If the input looks like a direct answer to the " + currentAgent + " agent (like a number, ID, or 'yes', or a subscription plan), keep it in that category.\n" +
                "otherwise, classify based on the text.\n\n" +
                "Reply ONLY with the category name.\n" +
                "Input: " + userInput;

        String response = client.generateContent(prompt);

        // trimming the response to remove white spaces
        return response.trim();
    }
}
