# AI Agents Help Desk System

A Java-based CLI help desk application that utilizes a multi-agent architecture to handle user queries. It routes requests to specialized agents using the Google Gemini API, featuring Retrieval-Augmented Generation (RAG) and simulated tool execution.

## Architecture

* **Router Agent:** Dynamically classifies user input into `TECHNICAL`, `BILLING`, or `CASUAL` categories based on conversation history.
* **Technical Agent (RAG):** Uses a local Vector Knowledge Base. It calculates cosine similarity between user queries and loaded document embeddings to provide context-aware answers.
* **Billing Agent (Tool Usage):** Simulates business logic by instructing the AI to output structured JSON commands (e.g., `refund`, `upgrade`) which are parsed and executed.

## Tech Stack

* **Java:** JDK 11+
* **AI Model:** Google Gemini (`gemini-2.5-flash-lite` for text, `text-embedding-004` for vectors).
* **Dependencies:** `com.google.code.gson` (JSON handling), `io.github.cdimascio.dotenv` (Environment configuration).

## Setup & Usage

1.  **Environment Variables:**
    Create a `.env` file in the project root containing your API key:
    ```properties
    GEMINI_API_KEY=your_actual_api_key_here
    ```

2.  **Knowledge Base:**
    Create a folder named `documents` in the project root. Add `.txt` files containing support documentation. These are ingested and vectorized on startup.

3.  **Run:**
    Execute `App.java`. The system will initialize the agents and start the chat loop.
    * Type your query (e.g., "I need a refund" or "Wifi is down").
    * Type `exit` to close the application.

## Demo & Capabilities

The system handles various scenarios including technical support (RAG), account management (Tool execution), and context retention.

**Example Session:**
```text
Help desk system online (type 'exit' if you want to leave)

User: i wanna have an upgrade
Routing to: BILLING
Agent: Could you please provide me with your user ID so I can process your upgrade?

User: 8475894499030
Routing to: BILLING
Agent: User 8475894499030 has been upgraded to Pro.

User: when is the internal configuratioon database backed up?
Routing to: TECHNICAL
Agent: The ADB's internal configuration database is automatically backed up daily at midnight.
```

## Project Structure

* `org.AiAgentsLedioKociv1.core`: Contains `GeminiClient` (API wrapper) and `KnowledgeBase` (Vector Store).
* `org.AiAgentsLedioKociv1.agents`: Contains logic for `RouterAgent`, `TechnicalAgent`, and `BillingAgent`.
* `App.java`: Main entry point handling the chat loop and file loading.