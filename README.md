# Bare Metal AI Agents Help Desk

A low-level, Java-based CLI help desk application that implements a Multi-Agent architecture **without using high-level AI frameworks** (like LangChain or Spring AI).

This project demonstrates a deep understanding of Large Language Model (LLM) mechanics by manually implementing HTTP communication, Vector Retrieval (RAG), and Native Function Calling (Tool Use) using standard Java libraries.

## Core Concepts & Implementation

### 1. Zero-Framework Architecture
Instead of relying on abstracted SDKs, this project uses a custom `GeminiClient` built on Java's `java.net.http.HttpClient`. It constructs raw JSON payloads, manages headers, and parses complex API responses manually.

### 2. Native Function Calling (The "Billing Agent")
Unlike simple prompt engineering, the Billing Agent utilizes the Gemini API's **Native Tool Use** capabilities.
* **Schema Definition:** Tools (`refund`, `upgrade`) are rigorously defined using a JSON Schema (`function_declarations`) passed to the model's `tools` parameter.
* **Structured Execution:** The system parses the model's `functionCall` response object directly, ensuring deterministic execution of business logic rather than relying on regex or string parsing.

### 3. Manual RAG Implementation (The "Technical Agent")
Retrieval-Augmented Generation is implemented from scratch:
* **Ingestion:** Documents are read and chunked.
* **Embedding:** Text is converted to `float[]` vectors using the `text-embedding-004` model.
* **Retrieval:** A custom **Cosine Similarity** algorithm (Euclidean dot product logic) runs against the in-memory vector store to find the most relevant context for user queries.

### 4. Stateful "Sticky" Routing
The `RouterAgent` solves the "Agent Amnesia" problem by maintaining a **State Machine**. It analyzes the conversation history and enforces a "stickiness" rule, ensuring short follow-up answers (e.g., "12345", "Pro") remain with the active agent rather than being misclassified.

## Tech Stack

* **Language:** Java 11+
* **LLM Provider:** Google Gemini API (REST)
    * *Chat:* `gemini-1.5-flash`
    * *Embeddings:* `text-embedding-004`
* **Dependencies:**
    * `com.google.code.gson` (JSON Parsing)
    * `io.github.cdimascio.dotenv` (Environment Config)

## Project Structure

* **`org.AiAgentsLedioKociv1.core`**
    * `GeminiClient.java`: The raw HTTP engine handling API requests, tool definitions, and error handling.
    * `KnowledgeBase.java`: The custom vector database and math logic for similarity search.
* **`org.AiAgentsLedioKociv1.agents`**
    * `BillingAgent.java`: Defines JSON tool schemas and executes native function calls.
    * `RouterAgent.java`: Implements the stateful classification logic.
    * `TechnicalAgent.java`: Handles context injection and RAG responses.
* **`App.java`**: The main event loop and dependency injection root.

## Setup & Usage

1.  **Environment Variables:**
    Create a `.env` file in the project root containing your API key:
    ```properties
    GEMINI_API_KEY=your_actual_api_key_here
    ```

2.  **Knowledge Base:**
    Create a folder named `documents` in the project root. Add `.txt` files containing support documentation (e.g., troubleshooting guides). These are ingested on startup.

3.  **Run:**
    Execute `App.java`. The system will initialize the vector store and start the chat loop.

## Example Session

```text
Help desk system online (type 'exit' if you want to leave)

User: i wanna have an upgrade
Routing to: BILLING
Agent: I can definitely help with that. Could you please provide your User ID?

User: 8475894499030
Routing to: BILLING   <-- "Sticky" routing correctly identifies this number belongs to Billing
Agent: SUCCESS: User 8475894499030 has been upgraded to Pro.

User: router has a red light
Routing to: TECHNICAL
Agent: A solid red light on the physical unit indicates a hardware fault. Please perform a hard reset.
