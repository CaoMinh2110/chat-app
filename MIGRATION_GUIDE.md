# Migration Guide: Single Module → Multi-Module

## Step-by-Step Migration Instructions

### Phase 1: Core Data Layer

#### Step 1.1: Move Network Layer (API clients, Services)

```
Current:   app/src/main/java/.../data/api/
Target:    core/network/src/main/java/com/truevibeup/core/network/api/

Files to move:
- APIClient.kt / RetrofitClient.kt
- AuthService.kt
- ChatService.kt
- UserService.kt
- API interfaces and models
```

**Action:**

1. Create subdirectories in `core:network`
2. Move API service files
3. Update package declarations
4. Update imports in moved files

#### Step 1.2: Move Socket.IO Client

```
Current:   app/src/main/java/.../data/socket/
Target:    core/network/src/main/java/com/truevibeup/core/network/socket/

Files to move:
- SocketIOClient.kt
- SocketIOManager.kt
- Socket event handlers
```

#### Step 1.3: Move Data Models

```
Current:   app/src/main/java/.../data/model/
Target:    core/common/src/main/java/com/truevibeup/core/common/model/

OR keep feature-specific models in respective feature modules:

Target:    feature/{feature}/src/main/java/com/truevibeup/feature/{feature}/data/model/

Files to move:
- User.kt
- Chat.kt
- Message.kt (if shared, move to core:common)
- Message specific models (if feature-specific, move to feature:chat)
```

#### Step 1.4: Move Local Storage/Data Access

```
Current:   app/src/main/java/.../data/local/
Target:    core/storage/src/main/java/com/truevibeup/core/storage/

Files to move:
- DataStore setup
- Preferences objects
- Room database (if used)
- DAOs (Data Access Objects)
```

### Phase 2: Domain & Repository Layer

#### Step 2.1: Move Repositories

Repositories should be in the module that owns the data:

```
Chat-related repository:
Target: core/network/src/main/java/com/truevibeup/core/network/repository/ChatRepository.kt

User/Auth repository:
Target: core/network/src/main/java/com/truevibeup/core/network/repository/AuthRepository.kt

Local data repository:
Target: core/storage/src/main/java/com/truevibeup/core/storage/repository/PreferencesRepository.kt
```

#### Step 2.2: Move Use Cases (if applicable)

```
Move to respective feature modules:

feature/auth/src/main/java/com/truevibeup/feature/auth/domain/usecase/LoginUseCase.kt
feature/chat/src/main/java/com/truevibeup/feature/chat/domain/usecase/SendMessageUseCase.kt
feature/home/src/main/java/com/truevibeup/feature/home/domain/usecase/GetChatsUseCase.kt
```

### Phase 3: UI Layer

#### Step 3.1: Move Theme & Common UI Components

```
Current:   app/src/main/java/.../ui/theme/
Target:    core/ui/src/main/java/com/truevibeup/core/ui/theme/

Current:   app/src/main/java/.../ui/components/
Target:    core/ui/src/main/java/com/truevibeup/core/ui/components/

Files to move:
- Color.kt / Theme.kt
- Typography.kt
- Common Button.kt, Card.kt, etc.
- Shared composable utilities
```

#### Step 3.2: Move Feature Screens & ViewModels

**Auth Feature:**

```
Current:   app/src/main/java/.../ui/screens/{Login,Register}/
Target:    feature/auth/src/main/java/com/truevibeup/feature/auth/presentation/

Current:   app/src/main/java/.../viewmodel/AuthViewModel.kt
Target:    feature/auth/src/main/java/com/truevibeup/feature/auth/presentation/viewmodel/AuthViewModel.kt
```

**Home Feature:**

```
Current:   app/src/main/java/.../ui/screens/Home/
Target:    feature/home/src/main/java/com/truevibeup/feature/home/presentation/

Current:   app/src/main/java/.../viewmodel/HomeViewModel.kt
Target:    feature/home/src/main/java/com/truevibeup/feature/home/presentation/viewmodel/HomeViewModel.kt
```

**Chat Feature:**

```
Current:   app/src/main/java/.../ui/screens/Chat/
Target:    feature/chat/src/main/java/com/truevibeup/feature/chat/presentation/

Current:   app/src/main/java/.../viewmodel/ChatViewModel.kt
Target:    feature/chat/src/main/java/com/truevibeup/feature/chat/presentation/viewmodel/ChatViewModel.kt
```

**Profile Feature:**

```
Current:   app/src/main/java/.../ui/screens/Profile/
Target:    feature/profile/src/main/java/com/truevibeup/feature/profile/presentation/

Current:   app/src/main/java/.../viewmodel/ProfileViewModel.kt
Target:    feature/profile/src/main/java/com/truevibeup/feature/profile/presentation/viewmodel/ProfileViewModel.kt
```

#### Step 3.3: Move Navigation

```
Keep in app module:
app/src/main/java/com/truevibeup/mobile/navigation/NavGraph.kt
app/src/main/java/com/truevibeup/mobile/navigation/NavRoutes.kt (or move to core:common)

Or create navigation per feature:
feature/{feature}/src/main/java/com/truevibeup/feature/{feature}/navigation/
```

### Phase 4: Dependency Injection

#### Step 4.1: Move Hilt Modules

```
Current:   app/src/main/java/.../di/
Target:    core/di/src/main/java/com/truevibeup/core/di/

Files to move:
- NetworkModule.kt (provides Retrofit, OkHttp, etc.)
- StorageModule.kt (provides DataStore, Room, etc.)
- RepositoryModule.kt (provides repositories)
- UseCaseModule.kt (if using use cases)

New files to create:
- AppModule.kt (for app-specific dependencies - in app module)
```

#### Step 4.2: Update Hilt Annotations

**In core/di modules:**

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit { ... }

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService { ... }
}
```

**In feature modules (for ViewModels):**

```kotlin
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() { ... }
```

### Phase 5: Update Main App Files

#### Step 5.1: Update TrueVibeUpApp.kt

Keep in app module:

```kotlin
@HiltAndroidApp
class TrueVibeUpApp : Application() {
    // Firebase init, logging, etc.
}
```

#### Step 5.2: Update MainActivity.kt

Keep in app module, update navigation:

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrueVibeUpTheme {
                NavGraph()  // From navigation module
            }
        }
    }
}
```

### Phase 6: Update Navigation

#### Step 6.1: Create Navigation Routes in core:common

```kotlin
// core/common/src/main/java/com/truevibeup/core/common/navigation/NavRoute.kt
sealed class NavRoute(val route: String) {
    object Auth : NavRoute("auth")
    object Home : NavRoute("home")
    object Chat : NavRoute("chat/{chatId}")
    object Profile : NavRoute("profile")
}
```

#### Step 6.2: Create Feature Navigation Graphs

Each feature module exports a navigation extension:

**feature/auth:**

```kotlin
fun NavGraphBuilder.authGraph(navController: NavController) {
    composable(AuthRoute.Login.route) { LoginScreen(navController) }
    composable(AuthRoute.Register.route) { RegisterScreen(navController) }
}
```

**feature/home:**

```kotlin
fun NavGraphBuilder.homeGraph(navController: NavController) {
    composable(HomeRoute.Chat.route) { HomeScreen(navController) }
}
```

#### Step 6.3: Update Main NavGraph in app module

```kotlin
fun NavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = NavRoute.Auth.route) {
        authGraph(navController)
        homeGraph(navController)
        chatGraph(navController)
        profileGraph(navController)
    }
}
```

### Phase 7: Update Imports & Build

#### Step 7.1: Update Package Names

1. Right-click on each class that was moved
2. Run `Refactor → Rename` if using IDE
3. Or manually update `package` declaration at top of file

#### Step 7.2: Update Import Statements

Search and replace old imports:

```
// Old
import com.truevibeup.mobile.data.api.*
import com.truevibeup.mobile.data.socket.*

// New
import com.truevibeup.core.network.api.*
import com.truevibeup.core.network.socket.*
```

#### Step 7.3: Fix Build Errors

```bash
# Clean build
./gradlew clean

# Sync Gradle files
./gradlew build --refresh-dependencies

# Rebuild
./gradlew build
```

### Phase 8: Testing

#### Step 8.1: Create Test Structure

```
core/common/src/test/java/com/truevibeup/core/common/
core/network/src/test/java/com/truevibeup/core/network/
feature/auth/src/test/java/com/truevibeup/feature/auth/
```

#### Step 8.2: Move Unit Tests

- API tests → core/network/src/test
- Storage tests → core/storage/src/test
- ViewModel tests → feature/\*/src/test
- Utility tests → core/common/src/test

#### Step 8.3: Run Tests

```bash
./gradlew test
./gradlew connectedAndroidTest
```

## Common Issues & Solutions

### Issue 1: Circular Dependencies

**Problem:** Feature A depends on Feature B and vice versa
**Solution:** Move common models to core:common, use callback/listener pattern

### Issue 2: Missing Dependencies

**Problem:** "Cannot find symbol" errors after moving files
**Solution:** Check build.gradle files, ensure all `implementation project()` declarations exist

### Issue 3: Hilt Issues

**Problem:** "Hilt component does not exist"
**Solution:** Ensure `@HiltAndroidApp` is in app module, rebuild project

### Issue 4: Navigation Issues

**Problem:** "NavController not found" or navigation crashes
**Solution:** Verify NavGraph is properly set up in main activity, check route strings

### Issue 5: Resource Not Found

**Problem:** Can't find drawable/string resources
**Solution:** Check if resources were moved with code, update resource references

## Verification Checklist

- [ ] All compilation errors resolved
- [ ] All classes properly moved to correct modules
- [ ] All imports updated
- [ ] All module dependencies declared in build.gradle
- [ ] Navigation graph updated with all routes
- [ ] Hilt modules properly configured
- [ ] App builds successfully
- [ ] App runs without runtime errors
- [ ] All tests pass
- [ ] No circular dependencies

## Next Steps After Migration

1. **Optimize Module Structure**
   - Profile build times
   - Remove unused dependencies

2. **Implement Clean Architecture**
   - Create proper domain layer
   - Implement repository pattern

3. **Add More Core Modules**
   - core:model (type-safe models)
   - core:design (design system)
   - core:analytics (analytics wrapper)

4. **Feature Testing**
   - Add unit tests per module
   - Add integration tests per feature
   - Add UI tests for screens

5. **Documentation**
   - Document each module's API
   - Create developer guides
   - Update README with new structure
