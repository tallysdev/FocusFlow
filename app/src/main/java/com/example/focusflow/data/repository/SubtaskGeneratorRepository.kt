package com.example.focusflow.data.repository

import com.example.focusflow.domain.repository.ISubtaskGeneratorRepository
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubtaskGeneratorRepository
    @Inject
    constructor() : ISubtaskGeneratorRepository {
        override suspend fun generateSubtasks(
            title: String,
            category: String,
            description: String,
        ): Result<List<String>> {
            return withContext(Dispatchers.IO) {
                try {
                    // English prompt for better Gemini performance
                    val prompt =
                        """
                        Generate 5 clear and specific subtasks for completing a $category task titled "$title".
                        ${if (description.isNotBlank()) "Task description: $description" else ""}
                        Each subtask should be a single step toward completing the main task.
                        Return only the list of subtasks, one per line, without numbering or additional text.
                        Make sure each subtask is concise, actionable, and specific.
                        """.trimIndent()

                    // Initialize the Gemini model using Firebase AI
                    val model =
                        com.google.firebase.Firebase.ai(backend = GenerativeBackend.googleAI())
                            .generativeModel(
                                modelName = "gemini-2.0-flash-lite",
//                        generationConfig = GenerationConfig(
//                            temperature = 0.2f,
//                            maxOutputTokens = 256,
//                            topK = 40,
//                            topP = 0.95f
//                        )
                            )

                    // Generate content using the model
                    val response = model.generateContent(prompt)
                    val generatedText = response.text ?: ""

                    // Process the text to extract the subtasks
                    val subtasks =
                        generatedText
                            .split("\n")
                            .filter { it.isNotBlank() }
                            .map { it.trim() }
                            .filter { it.length > 3 }
                            .take(5)

                    Result.success(subtasks)
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }
        }
    }
