package org.AiAgentsLedioKociv1.agents;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.AiAgentsLedioKociv1.core.GeminiClient;
import com.google.gson.JsonArray;

public class BillingAgent {
    private final GeminiClient client;
    private final Gson gson;

    public BillingAgent(GeminiClient client) {
        this.client = client;
        this.gson = new Gson();
    }

    public String handle(String input, String history) {

        // this is where we declare the functions gemini can call
        JsonObject toolsConfig = createToolDefinitions();

        String prompt = "You are a billing agent. Use the provided tools to help the user.\n" +
                "History:\n" + history + "\n" +
                "User: " + input;

        JsonObject candidate = client.generateContent(prompt, toolsConfig);

        // this is where gemini sees if it id a function call or text
        JsonObject content = candidate.getAsJsonObject("content");
        JsonArray parts = content.getAsJsonArray("parts");
        JsonObject firstPart = parts.get(0).getAsJsonObject();

        if (firstPart.has("functionCall")) {
            return executeNativeTool(firstPart.getAsJsonObject("functionCall"));
        } else {
            // text response
            return firstPart.get("text").getAsString();
        }
    }

    // this is to execute the function gemini asked for
    private String executeNativeTool(JsonObject functionCall) {
        String name = functionCall.get("name").getAsString();
        JsonObject args = functionCall.getAsJsonObject("args");

        if ("refund".equals(name)) {
            String plan = args.get("plan_name").getAsString();
            double amount = plan.toLowerCase().contains("pro") ? 50.00 : 15.00;
            return "Refund processed for " + plan + " amount: " + amount;
        }
        else if ("upgrade".equals(name)) {
            String userId = args.get("user_id").getAsString();
            return "User " + userId + " upgraded to pro";
        }
        return "Error: i dont recognize this tool";
    }

    // this is where i build the schema
    private JsonObject createToolDefinitions() {

        // the refund tool
        JsonObject refund = new JsonObject();
        refund.addProperty("name", "refund");
        refund.addProperty("description", "Process a refund based on plan name");
        JsonObject refundParams = new JsonObject();
        refundParams.addProperty("type", "OBJECT");
        JsonObject refundProps = new JsonObject();
        JsonObject planArg = new JsonObject();
        planArg.addProperty("type", "STRING");
        refundProps.add("plan_name", planArg);
        refundParams.add("properties", refundProps);
        refundParams.add("required", new JsonArray());
        refund.add("parameters", refundParams);

        // the upgrade tool
        JsonObject upgrade = new JsonObject();
        upgrade.addProperty("name", "upgrade");
        upgrade.addProperty("description", "Upgrade a user account");
        JsonObject upgradeParams = new JsonObject();
        upgradeParams.addProperty("type", "OBJECT");
        JsonObject upgradeProps = new JsonObject();
        JsonObject idArg = new JsonObject();
        idArg.addProperty("type", "STRING");
        upgradeProps.add("user_id", idArg);
        upgradeParams.add("properties", upgradeProps);
        upgrade.add("parameters", upgradeParams);

        // here is where i wrap them in a "function_declarations" structure
        JsonArray funcs = new JsonArray();
        funcs.add(refund);
        funcs.add(upgrade);

        JsonObject toolDecl = new JsonObject();
        toolDecl.add("function_declarations", funcs);

        JsonArray tools = new JsonArray();
        tools.add(toolDecl);

        JsonObject root = new JsonObject();
        root.add("function_declarations", funcs);
        return root;
    }
}

