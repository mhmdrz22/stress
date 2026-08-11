import android.database.sqlite.SQLiteException

// Inside try block:
            try {
                // ...
            } catch (e: SQLiteException) {
                _state.update { it.copy(isLoading = false, error = "خطا در ذخیره‌سازی اطلاعات. لطفاً دوباره تلاش کنید.") }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "خطای پردازش محلی: ${e.localizedMessage ?: e.message}") }
            }
