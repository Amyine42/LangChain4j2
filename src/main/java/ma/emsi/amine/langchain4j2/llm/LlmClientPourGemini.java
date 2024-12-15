package ma.emsi.amine.langchain4j2.llm;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.enterprise.context.Dependent;


import java.io.Serializable;

/**
 * Gère l'interface avec l'API de Gemini.
 * Son rôle est essentiellement de lancer une requête à chaque nouvelle
 * question qu'on veut envoyer à l'API.
 *
 * De portée dependent pour réinitialiser la conversation à chaque fois que
 * l'instance qui l'utilise est renouvelée.
 * Par exemple, si l'instance qui l'utilise est de portée View, la conversation est
 * réunitialisée à chaque fois que l'utilisateur quitte la page en cours.
 */

@Dependent
public class LlmClientPourGemini implements Serializable {
    // Clé pour l'API du LLM
    private final String key;
    public interface Assistant {
        String chat(String prompt);
    }

    private String systemRole;
    private Assistant assistant;
    private ChatMemory chatMemory;

    public LlmClientPourGemini() {

        this.key = System.getenv("GEMINI_API_KEY");
        ChatLanguageModel modele = GoogleAiGeminiChatModel.builder()
                .temperature(0.5)
                .modelName("gemini-1.5-flash")
                .apiKey(key)
                .build();

        this.chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        this.assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(modele)
                .chatMemory(chatMemory)
                .build();
    }

    public void setSystemRole(String systemRole) {
        this.systemRole = systemRole;
        this.chatMemory.clear();
        this.chatMemory.add(new SystemMessage(this.systemRole));
    }

    public String envoyerMessage(String question) {
        return this.assistant.chat(question);
    }
}

