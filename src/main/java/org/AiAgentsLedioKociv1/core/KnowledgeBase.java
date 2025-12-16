package org.AiAgentsLedioKociv1.core;

import java.util.ArrayList;
import java.util.List;

public class KnowledgeBase {

    // this is to hold text
    private List<String> documentSegments = new ArrayList<>();

    // this is to hold the vector of the text
    private List<float[]> documentVectors = new ArrayList<>();

    private final GeminiClient client;

    public KnowledgeBase(GeminiClient client){
        this.client = client;
    }

    public void addDocument(String text) {

        if (text == null) return;

        // i store the text in the list of segments
        documentSegments.add(text);

        // here i get the mathematical value of the text
        float[] vector = client.getEmbedding(text);

        //and then i add it to the list of vectors
        documentVectors.add(vector);
    }
    // this method helps to search the user's prompt within the document, by its actual context
    public String search(String prompt){

            float[] promptVector = client.getEmbedding(prompt);

            String bestMatch = "";
            double maxScore = -1.0;

            for(int i = 0; i < documentVectors.size(); i++){

                float[] docVector = documentVectors.get(i);

                double score = cosineSimilarity(promptVector, docVector);

                if(score > maxScore){
                    maxScore = score;
                    bestMatch = documentSegments.get(i);
                }
            }

        return (maxScore > 0.5) ? bestMatch : "";
    }

    // the math implementation of the vector similarity
    private double cosineSimilarity(float[] vecA, float[] vecB){

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for(int i = 0; i < vecA.length; i++){
            dotProduct += vecA[i]*vecB[i];

            normA += Math.pow(vecA[i], 2);
            normB += Math.pow(vecB[i], 2);

        }

        return dotProduct/(Math.sqrt(normA)*Math.sqrt(normB));

    }
}
