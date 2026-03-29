package com.spring.ollama.agent;

import com.spring.ollama.tools.CommunicationTool;
import com.spring.ollama.tools.DataTool;
import com.spring.ollama.tools.DateTimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MultiAgentService {

    @Autowired
    private DataTool dataTool;

    @Autowired
    private CommunicationTool communicationTool;

    //@Autowired
    //private SchedulingTool schedulingTool;


    private final ChatClient chatClient;

    public MultiAgentService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String process(String username, String message) {

        String systemPrompt = """
                    You are a coordinator AI.
                
                    You have access to multiple tools:
                    - Database queries
                    - Email sending
                    - Scheduling meetings
                
                    Your job:
                    1. Understand user intent
                    2. Decide which tool to call
                    3. Execute step by step
                    4. Respond clearly
                
                    Always use tools when actions are required.
                """;

        return chatClient.prompt()
                .system(systemPrompt)
                .user("User: " + username + "\n" + message)
                .tools(new DateTimeTools(),dataTool,communicationTool)
                .call()
                .content();
    }
}
