import re

with open('app/src/main/java/com/aistudio/detected/stress/viewmodel/StressViewModel.kt', 'r', encoding='utf-8') as f:
    content = f.read()

old_catch = """            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        isLoading = false, 
                        error = "خطا در پردازش اطلاعات. لطفاً دوباره تلاش کنید."
                    ) 
                }
            }"""

new_catch = """            } catch (e: Exception) {
                val isNetworkError = e is java.net.UnknownHostException || e is java.net.SocketTimeoutException || e.message?.contains("timeout") == true
                
                if (isNetworkError || e is Exception) {
                    kotlinx.coroutines.delay(1500)
                    
                    val bestOfflineMatch = localJsonRagEngine.findBestMatch(currentText)

                    val fallbackResult = if (bestOfflineMatch != null) {
                        GeminiResponse(
                            has_stress = bestOfflineMatch.is_stress,
                            category_tag = bestOfflineMatch.category,
                            empathy_message = bestOfflineMatch.empathy,
                            search_keywords = listOf(bestOfflineMatch.query)
                        )
                    } else {
                        GeminiResponse(
                            has_stress = true,
                            category_tag = "anxiety",
                            empathy_message = "به نظر میاد شرایط پرفشاری رو طی می‌کنی. من نتونستم به اینترنت وصل بشم، اما می‌تونی از این راهکارهای آفلاین استفاده کنی.",
                            search_keywords = listOf("موسیقی آرامش بخش اعصاب")
                        )
                    }

                    withContext(Dispatchers.IO) {
                        moodDao.insertMood(MoodEntry(
                            dateMillis = System.currentTimeMillis(),
                            userInput = currentText,
                            categoryTag = fallbackResult.category_tag,
                            hasStress = fallbackResult.has_stress
                        ))
                    }

                    val advice = LocalAdviceGraph.getAdviceForCategory(fallbackResult.category_tag, likedAdviceTitles.value)

                    _state.update {
                        it.copy(
                            isLoading = false,
                            result = fallbackResult,
                            adviceList = advice,
                            isOffline = true
                        )
                    }
                } else {
                    _state.update { 
                        it.copy(
                            isLoading = false, 
                            error = "خطا در پردازش اطلاعات. لطفاً دوباره تلاش کنید."
                        ) 
                    }
                }
            }"""

content = content.replace(old_catch, new_catch)

with open('app/src/main/java/com/aistudio/detected/stress/viewmodel/StressViewModel.kt', 'w', encoding='utf-8') as f:
    f.write(content)

