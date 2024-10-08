package com.example.rag.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rag/chatbot")
public class ChatbotController {

	private static final Logger methIDquery = LoggerFactory.getLogger(ChatbotController.class.getName() + ".query()");

	private final ChatClient chatClient;

	private final VectorStore vectorStore;

	public ChatbotController(VectorStore vectorStore, ChatClient.Builder builder) {
		this.vectorStore = vectorStore;
		this.chatClient = builder.build();
	}

	@GetMapping
	public String query(
			@RequestParam(value = "question", defaultValue = "What is the purpose of Carina?") String question,
			@RequestParam(value = "version", defaultValue = "1") String version) {

		Logger logger = methIDquery;

		String returnValue = null;

		logger.debug("Begins...");

		logger.info("question: " + question);
		logger.info("version: " + version);

		var filterExpression = "version == " + version; // portable across all vector stores

 		logger.info("filterExpression: " + filterExpression);

		returnValue = this.chatClient
				.prompt()
				.advisors(new QuestionAnswerAdvisor(vectorStore, SearchRequest.defaults().withFilterExpression(filterExpression)))
				.user(question)
				.call()
				.content();

		logger.debug("returnValue: " + returnValue);

		logger.debug("Ends...");

		return (returnValue);

	}


}
