package org.AiAgentsLedioKociv1;

import com.google.gson.JsonObject;
import org.AiAgentsLedioKociv1.agents.BillingAgent;
import org.AiAgentsLedioKociv1.agents.RouterAgent;
import org.AiAgentsLedioKociv1.agents.TechnicalAgent;
import org.AiAgentsLedioKociv1.core.GeminiClient;
import org.AiAgentsLedioKociv1.core.KnowledgeBase;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class App
{
    public static void main( String[] args ){

        GeminiClient client = new GeminiClient();
        KnowledgeBase kb = new KnowledgeBase(client);

        // here is where i load the documents to the knowledge base class
        loadDocuments(kb);

        // i construct each agent
        RouterAgent router = new RouterAgent(client);
        TechnicalAgent techAgent = new TechnicalAgent(client, kb);
        BillingAgent billingAgent = new BillingAgent(client);

        Scanner scanner = new Scanner(System.in);
        System.out.println("Help desk system online (type 'exit' if you want to leave)");

        // this i need to keep track of the current agent
        String currentAgent = "CASUAL";

        // and this for the chat history, so that the router agent doesn't lose "memory"
        StringBuilder chatHistory = new StringBuilder();
        while (true) {
            System.out.print("\nUser: ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit")) break;

            String route = router.route(input, currentAgent);

            // the memory keeps getting built
            chatHistory.append("user: ").append(input).append("\n");

            currentAgent = route;

            System.out.println("Routing to: " + route);

            // this is where the agents are being "called by the router"
            String response = switch (route) {
                // Ensure this matches your TechnicalAgent method name (I used 'handle' in previous steps)
                case "TECHNICAL" -> techAgent.handleRequest(input, chatHistory.toString());
                case "BILLING" -> billingAgent.handle(input, chatHistory.toString());
                default -> {
                    // FIX: Pass 'null' for tools, and extract text from the JSON object
                    JsonObject json = client.generateContent("You are an assistant. Reply to: " + input, null);
                    try {
                        yield json.getAsJsonObject("content")
                                .getAsJsonArray("parts")
                                .get(0).getAsJsonObject()
                                .get("text").getAsString();
                    } catch (Exception e) {
                        yield "I didn't catch that.";
                    }
                }
            };

            System.out.println("Agent: " + response);
        }
    }

    // load the files from the documents folder in the root, and then i store all the strings in the knowledge base
    private static void loadDocuments(KnowledgeBase kb) {
        try {
            Path dir = Paths.get("documents");
            if (Files.exists(dir)) {
                Files.list(dir).forEach(path -> {
                    try {
                        String text = Files.readString(path);
                        for (String p : text.split("\\n\\n")) {
                            kb.addDocument(p.trim());
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}