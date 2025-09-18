package com.example.todo

// ==================== RESULT MONAD IN KOTLIN ====================

// ==================== PART 1: CORE RESULT TYPE ====================

sealed class Result<out T> {
    data class Success<T>(val value: T) : Result<T>()
    data class Failure(val error: Throwable) : Result<Nothing>()

    companion object {
        // Create success result
        fun <T> success(value: T): Result<T> = Result.Success(value)

        // Create failure result
        fun failure(error: Throwable): Result<Nothing> = Result.Failure(error)
        fun failure(message: String): Result<Nothing> = Result.Failure(Exception(message))

        // Wrap a potentially throwing operation
        inline fun <T> runCatching(block: () -> T): Result<T> {
            return try {
                success(block())
            } catch (e: Throwable) {
                failure(e)
            }
        }
    }

    // Check if result is success/failure
    val isSuccess: Boolean get() = this is Success
    val isFailure: Boolean get() = this is Failure

    // Get value or null
    fun getOrNull(): T? = when (this) {
        is Success -> value
        is Failure -> null
    }

    // Get error or null
    fun errorOrNull(): Throwable? = when (this) {
        is Success -> null
        is Failure -> error
    }
}

// ==================== PART 2: FACTORY FUNCTIONS ====================

// Create success result
fun <T> success(value: T): Result<T> = Result.Success(value)

// Create failure result
fun failure(error: Throwable): Result<Nothing> = Result.Failure(error)
fun failure(message: String): Result<Nothing> = Result.Failure(Exception(message))

// Wrap a potentially throwing operation
inline fun <T> runCatching(block: () -> T): Result<T> {
    return try {
        success(block())
    } catch (e: Throwable) {
        failure(e)
    }
}

// Convert nullable to Result
fun <T : Any> T?.toResult(errorMessage: String = "Value is null"): Result<T> {
    return this?.let { success(it) } ?: failure(errorMessage)
}

// Convert boolean to Result
fun Boolean.toResult(errorMessage: String = "Condition failed"): Result<Unit> {
    return if (this) success(Unit) else failure(errorMessage)
}

// Convert with success value if condition is true
fun <T> Boolean.toResult(value: T, errorMessage: String = "Condition failed"): Result<T> {
    return if (this) success(value) else failure(errorMessage)
}

// Convert any value to success
fun <T> T.toSuccess(): Result<T> = success(this)

// Convert exception to failure
fun Throwable.toFailure(): Result<Nothing> = failure(this)

// ==================== PART 3: CORE MONAD OPERATIONS ====================

// MAP: Transform success value, keep failure as-is
inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> {
    return when (this) {
        is Result.Success -> success(transform(value))
        is Result.Failure -> this
    }
}

// FLAT MAP: Transform success value to another Result, flatten nested Results
inline fun <T, R> Result<T>.flatMap(transform: (T) -> Result<R>): Result<R> {
    return when (this) {
        is Result.Success -> transform(value)
        is Result.Failure -> this
    }
}

// INFIX version of flatMap for better chaining
inline infix fun <T, R> Result<T>.then(transform: (T) -> Result<R>): Result<R> = flatMap(transform)

// ==================== PART 4: ERROR HANDLING ====================

// Map errors to different types
inline fun <T> Result<T>.mapError(transform: (Throwable) -> Throwable): Result<T> {
    return when (this) {
        is Result.Success -> this
        is Result.Failure -> failure(transform(error))
    }
}

// Recover from failure with a default value
inline fun <T> Result<T>.recover(recovery: (Throwable) -> T): Result<T> {
    return when (this) {
        is Result.Success -> this
        is Result.Failure -> success(recovery(error))
    }
}

// Recover from failure with another Result
inline fun <T> Result<T>.recoverWith(recovery: (Throwable) -> Result<T>): Result<T> {
    return when (this) {
        is Result.Success -> this
        is Result.Failure -> recovery(error)
    }
}

// ==================== PART 5: INFIX OPERATIONS FOR CHAINING ====================

// Infix version of map
inline infix fun <T, R> Result<T>.mapTo(transform: (T) -> R): Result<R> = map(transform)

// Infix version of recover
infix fun <T> Result<T>.orElse(defaultValue: T): T {
    return when (this) {
        is Result.Success -> value
        is Result.Failure -> defaultValue
    }
}

// Infix version of recoverWith
inline infix fun <T> Result<T>.orTry(recovery: (Throwable) -> Result<T>): Result<T> = recoverWith(recovery)

// ==================== PART 6: SIDE EFFECTS ====================

// Execute side effect on success
inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) action(value)
    return this
}

// Execute side effect on failure
inline fun <T> Result<T>.onFailure(action: (Throwable) -> Unit): Result<T> {
    if (this is Result.Failure) action(error)
    return this
}

// Infix versions for natural language
inline infix fun <T> Result<T>.ifSuccess(action: (T) -> Unit): Result<T> = onSuccess(action)
inline infix fun <T> Result<T>.ifFailure(action: (Throwable) -> Unit): Result<T> = onFailure(action)

// ==================== PART 7: FOLD AND TERMINAL OPERATIONS ====================

// Fold: Handle both success and failure cases
inline fun <T, R> Result<T>.fold(
    onSuccess: (T) -> R,
    onFailure: (Throwable) -> R
): R {
    return when (this) {
        is Result.Success -> onSuccess(value)
        is Result.Failure -> onFailure(error)
    }
}

// Get value or throw exception
fun <T> Result<T>.getOrThrow(): T {
    return when (this) {
        is Result.Success -> value
        is Result.Failure -> throw error
    }
}

// Get value or return default
fun <T> Result<T>.getOrDefault(default: T): T {
    return when (this) {
        is Result.Success -> value
        is Result.Failure -> default
    }
}

// ==================== PART 8: COLLECTION OPERATIONS ====================

// Transform list of Results to Result of list (fails fast)
fun <T> List<Result<T>>.sequence(): Result<List<T>> {
    val values = mutableListOf<T>()
    for (result in this) {
        when (result) {
            is Result.Success -> values.add(result.value)
            is Result.Failure -> return result
        }
    }
    return success(values)
}

// Transform list with a function that returns Result
inline fun <T, R> List<T>.mapResult(transform: (T) -> Result<R>): Result<List<R>> {
    return map(transform).sequence()
}

// ==================== PART 9: DSL FOR RESULT BUILDING ====================

class ResultBuilder<T> {
    private var result: Result<T>? = null

    infix fun tryOperation(operation: () -> T) {
        result = runCatching(operation)
    }

    infix fun <R> thenMap(transform: (T) -> R): ResultBuilder<R> {
        val newBuilder = ResultBuilder<R>()
        newBuilder.result = result?.map(transform)
        return newBuilder
    }

    infix fun <R> thenFlatMap(transform: (T) -> Result<R>): ResultBuilder<R> {
        val newBuilder = ResultBuilder<R>()
        newBuilder.result = result?.flatMap(transform)
        return newBuilder
    }

    fun build(): Result<T> = result ?: failure("No operation specified")
}

fun <T> buildResult(init: ResultBuilder<T>.() -> Unit): Result<T> {
    return ResultBuilder<T>().apply(init).build()
}

// ==================== PART 10: PRACTICAL EXAMPLES ====================

// Example domain objects
data class User(val id: Int, val email: String, val name: String)
data class Profile(val userId: Int, val bio: String, val avatarUrl: String)
data class UserWithProfile(val user: User, val profile: Profile)

// Simulated service functions that can fail
object UserService {
    private val users = mapOf(
        1 to User(1, "john@example.com", "John Doe"),
        2 to User(2, "jane@example.com", "Jane Smith")
    )

    fun findUser(id: Int): Result<User> {
        return users[id]?.let { success(it) }
            ?: failure("User not found: $id")
    }

    fun validateEmail(email: String): Result<String> {
        return if (email.contains("@") && email.contains(".")) {
            success(email)
        } else {
            failure("Invalid email format: $email")
        }
    }

    fun updateUserName(user: User, newName: String): Result<User> {
        return if (newName.isNotBlank()) {
            success(user.copy(name = newName))
        } else {
            failure("Name cannot be blank")
        }
    }
}

object ProfileService {
    private val profiles = mapOf(
        1 to Profile(1, "Software developer", "avatar1.jpg"),
        2 to Profile(2, "Designer", "avatar2.jpg")
    )

    fun findProfile(userId: Int): Result<Profile> {
        return profiles[userId]?.let { success(it) }
            ?: failure("Profile not found for user: $userId")
    }

    fun createProfile(userId: Int, bio: String): Result<Profile> {
        return if (bio.length >= 10) {
            success(Profile(userId, bio, "default.jpg"))
        } else {
            failure("Bio must be at least 10 characters")
        }
    }
}

// ==================== PART 11: DEMONSTRATION ====================

fun demonstrateBasicChaining() {
    println("=== Basic Result Chaining ===")

    // Simple success chain
    val result1 = success(5)
        .map { it * 2 }
        .map { it + 3 }
        .map { "Result: $it" }

    println("Success chain: ${result1.getOrNull()}")

    // Chain that fails
    val result2 = success(10)
        .map { it / 2 }
        .flatMap { if (it > 3) success(it) else failure("Too small") }
        .map { "Final: $it" }

    println("Success result: ${result2.getOrNull()}")

    // Chain that fails early
    val result3 = success(2)
        .map { it / 2 }
        .flatMap { if (it > 3) success(it) else failure("Too small") }
        .map { "Final: $it" }  // This won't execute

    println("Failed result: ${result3.errorOrNull()?.message}")
}

fun demonstrateInfixChaining() {
    println("=== Infix Result Chaining ===")

    val userId = 1

    val result = UserService.findUser(userId)
        .then { user -> UserService.validateEmail(user.email) mapTo { user } }
        .then { user -> ProfileService.findProfile(user.id) mapTo { profile -> UserWithProfile(user, profile) } }
        .ifSuccess { println("Found user with profile: ${it.user.name}") }
        .ifFailure { println("Error: ${it.message}") }

    println("Result: ${result.fold({ "Success" }, { "Failed: ${it.message}" })}")
}

fun demonstrateErrorHandling() {
    println("=== Error Handling ===")

    val result1 = UserService.findUser(999) // Will fail
        .recover { failure -> User(-1, "unknown@example.com", "Unknown User") }
        .map { "User: ${it.name}" }

    println("Recovered result: ${result1.getOrNull()}")

    val result2 = UserService.findUser(999) // Will fail
        .orTry { failure ->
            println("First attempt failed: ${failure.message}")
            UserService.findUser(1) // Try with different ID
        }
        .map { "Found user: ${it.name}" }

    println("Recovery result: ${result2.getOrNull()}")

    val defaultValue = UserService.findUser(999) orElse User(0, "default", "Default")
    println("Default value: $defaultValue")
}

fun demonstrateCollectionOperations() {
    println("=== Collection Operations ===")

    val userIds = listOf(1, 2, 999) // Last one will fail

    // This will fail fast on the invalid ID
    val allUsersResult = userIds.mapResult { UserService.findUser(it) }
    println("All users result: ${allUsersResult.fold({ "Success: ${it.size} users" }, { "Failed: ${it.message}" })}")

    // Get only successful results
    val validUsers = userIds
        .map { UserService.findUser(it) }
        .mapNotNull { it.getOrNull() }

    println("Valid users: ${validUsers.map { it.name }}")
}

fun demonstrateResultBuilder() {
    println("=== Result Builder DSL ===")

    val result = buildResult {
        tryOperation { "Hello" }
        thenMap { "$it World" }
        thenMap { it.uppercase() }
        thenFlatMap {
            if (it.length > 5) success(it)
            else failure("Too short")
        }
    }

    println("Builder result: ${result.getOrNull()}")
}

fun demonstrateComplexChain() {
    println("=== Complex Real-World Chain ===")

    fun processUser(userId: Int, newName: String, newBio: String): Result<String> {
        return UserService.findUser(userId)
            .then { user -> UserService.updateUserName(user, newName) }
            .then { updatedUser ->
                ProfileService.findProfile(updatedUser.id)
                    .orTry { ProfileService.createProfile(updatedUser.id, newBio) }
                    .mapTo { profile -> UserWithProfile(updatedUser, profile) }
            }
            .mapTo { userWithProfile ->
                "Successfully processed ${userWithProfile.user.name} with bio: ${userWithProfile.profile.bio}"
            }
            .ifSuccess { println("✓ $it") }
            .ifFailure { println("✗ Error: ${it.message}") }
    }

    // Success case
    processUser(1, "John Updated", "I am a software developer with 5 years experience")

    // Failure case - invalid name
    processUser(2, "", "Short bio")

    // Mixed case - user exists but profile creation needed
    processUser(1, "John Again", "This is a longer bio that meets the requirements")
}

// ==================== PART 12: ADVANCED PATTERNS ====================

// Result with context (like Either with Left/Right but more specific)
sealed class ResultWithContext<out T, out C> {
    data class Success<T, C>(val value: T, val context: C) : ResultWithContext<T, C>()
    data class Failure<C>(val error: Throwable, val context: C) : ResultWithContext<Nothing, C>()
}

// Parallel execution (simplified - would use coroutines in real code)
fun <T1, T2> Result<T1>.zip(other: Result<T2>): Result<Pair<T1, T2>> {
    return this.flatMap { value1 ->
        other.map { value2 -> Pair(value1, value2) }
    }
}

fun demonstrateAdvancedPatterns() {
    println("=== Advanced Patterns ===")

    // Combining multiple results
    val user = UserService.findUser(1)
    val profile = ProfileService.findProfile(1)

    val combined = user.zip(profile)
        .map { (u, p) -> UserWithProfile(u, p) }
        .map { "Combined: ${it.user.name} - ${it.profile.bio}" }

    println("Combined result: ${combined.getOrNull()}")

    // Pipeline with multiple validations
    val pipeline = success("john.doe@example.com")
        .flatMap { UserService.validateEmail(it) }
        .map { email -> User(100, email, "John Doe") }
        .flatMap { user -> UserService.updateUserName(user, "Updated John") }
        .map { "Pipeline result: ${it.name}" }

    println("Pipeline: ${pipeline.getOrNull()}")
}

// ==================== MAIN DEMONSTRATION ====================

fun main() {
    demonstrateBasicChaining()
    println()

    demonstrateInfixChaining()
    println()

    demonstrateErrorHandling()
    println()

    demonstrateCollectionOperations()
    println()

    demonstrateResultBuilder()
    println()

    demonstrateComplexChain()
    println()

    demonstrateAdvancedPatterns()
}

// ==================== KEY FEATURES SUMMARY ====================

/*
RESULT MONAD FEATURES:

1. **Core Operations**:
   - map: Transform success values
   - flatMap/then: Chain operations that return Results
   - recover: Handle failures with default values
   - fold: Handle both success and failure cases

2. **Infix Functions for Readability**:
   - result then { ... }
   - result mapTo { ... }
   - result orElse defaultValue
   - result ifSuccess { ... } ifFailure { ... }

3. **Chaining Benefits**:
   - Operations only execute on success
   - First failure stops the chain
   - Clean, functional pipeline style
   - Type-safe error handling

4. **DSL Features**:
   - Natural language-like syntax
   - Lambda with receivers for builders
   - Infix functions for better readability
   - Composable operations

5. **Real-World Usage**:
   - Database operations
   - API calls
   - Validation pipelines
   - File I/O operations
   - Any operation that can fail

The Result monad eliminates nested try-catch blocks and null checks,
making error handling explicit and chainable while maintaining type safety.
*/