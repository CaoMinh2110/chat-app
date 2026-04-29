# Multi-Module Quick Reference

## Module Naming Convention

### Core Modules

```
:core:di          → Dependency Injection
:core:common      → Common utilities, constants, extensions
:core:ui          → UI components, theme, resources
:core:network     → Network, API, Socket.IO
:core:storage     → DataStore, cache, local database
```

### Feature Modules

```
:feature:auth     → Authentication (login, register)
:feature:home     → Home screen (chat list)
:feature:chat     → Chat detail screen (messaging)
:feature:profile  → User profile screens
```

## Package Structure

### Core Modules

```
core/
├── common/src/main/java/com/truevibeup/core/common/
│   ├── Constants.kt
│   ├── extension/
│   ├── model/
│   └── util/
├── ui/src/main/java/com/truevibeup/core/ui/
│   ├── UIConstants.kt
│   ├── component/
│   ├── theme/
│   └── util/
├── network/src/main/java/com/truevibeup/core/network/
│   ├── api/
│   ├── model/
│   ├── socket/
│   ├── di/
│   └── repository/
├── storage/src/main/java/com/truevibeup/core/storage/
│   ├── datastore/
│   ├── model/
│   ├── di/
│   └── repository/
└── di/src/main/java/com/truevibeup/core/di/
    ├── NetworkModule.kt
    ├── StorageModule.kt
    └── RepositoryModule.kt
```

### Feature Modules

```
feature/{feature}/src/main/java/com/truevibeup/feature/{feature}/
├── presentation/
│   ├── viewmodel/
│   ├── screen/
│   └── component/
├── data/
│   ├── model/
│   ├── source/
│   └── repository/
├── domain/
│   ├── model/
│   └── usecase/
└── navigation/
    └── {Feature}NavGraph.kt
```

## How to Add New Code

### Adding a New Utility to core:common

```kotlin
// core/common/src/main/java/com/truevibeup/core/common/extension/StringExt.kt
package com.truevibeup.core.common.extension

fun String.isValidEmail(): Boolean {
    return this.contains("@")
}
```

### Adding a New Reusable Component to core:ui

```kotlin
// core/ui/src/main/java/com/truevibeup/core/ui/component/CustomButton.kt
package com.truevibeup.core.ui.component

@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(onClick = onClick, modifier = modifier) {
        Text(text)
    }
}
```

### Adding a New API Service to core:network

```kotlin
// core/network/src/main/java/com/truevibeup/core/network/api/UserService.kt
package com.truevibeup.core.network.api

import retrofit2.http.GET

interface UserService {
    @GET("users/profile")
    suspend fun getProfile(): UserResponse
}
```

### Adding a New Screen to a Feature Module

```kotlin
// feature/home/src/main/java/com/truevibeup/feature/home/presentation/screen/HomeScreen.kt
package com.truevibeup.feature.home.presentation.screen

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    // Implementation
}
```

### Adding a New ViewModel

```kotlin
// feature/chat/src/main/java/com/truevibeup/feature/chat/presentation/viewmodel/ChatViewModel.kt
package com.truevibeup.feature.chat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {
    // Implementation
}
```

### Adding Dependencies to a Module

```gradle
// feature/chat/build.gradle
dependencies {
    // This module depends on:
    implementation project(':core:common')
    implementation project(':core:ui')
    implementation project(':core:network')
    implementation project(':core:di')

    // And external libraries
    implementation 'com.squareup.okhttp3:okhttp:4.12.0'
}
```

## Module Dependencies Rules

✅ **ALLOWED:**

```
feature:* → core:*
core:ui → core:common
core:network → core:common, core:di
core:storage → core:common, core:di
app → feature:*, core:*
```

❌ **NOT ALLOWED:**

```
core:common → anything else
core:di → core:network, core:storage (can't have circular deps)
feature:auth → feature:chat (features can't depend on each other)
```

## Building & Running Commands

```bash
# Build entire project
./gradlew build

# Build specific module
./gradlew :core:network:build
./gradlew :feature:auth:build

# Run app
./gradlew :app:installDebug
./gradlew :app:run

# Run tests
./gradlew test
./gradlew :feature:auth:test

# Clean build
./gradlew clean build

# Check dependencies
./gradlew :app:dependencies
```

## Import Organization

Always use these imports in the correct order:

```kotlin
// 1. Android & Jetpack
import android.content.*
import androidx.compose.*
import androidx.lifecycle.*

// 2. Project core modules
import com.truevibeup.core.common.*
import com.truevibeup.core.ui.*
import com.truevibeup.core.network.*
import com.truevibeup.core.storage.*
import com.truevibeup.core.di.*

// 3. Project feature modules
import com.truevibeup.feature.auth.*
import com.truevibeup.feature.home.*

// 4. External libraries
import com.squareup.retrofit2.*
import kotlinx.coroutines.*
import dagger.hilt.*

// 5. Java standard library
import java.io.*
import kotlin.collections.*
```

## File Naming Conventions

| Type                | Convention              | Example                                  |
| ------------------- | ----------------------- | ---------------------------------------- |
| Composable Screen   | `[Feature]Screen.kt`    | `LoginScreen.kt`, `ChatScreen.kt`        |
| ViewModel           | `[Feature]ViewModel.kt` | `AuthViewModel.kt`, `ChatViewModel.kt`   |
| Repository          | `[Domain]Repository.kt` | `AuthRepository.kt`, `ChatRepository.kt` |
| Service             | `[Domain]Service.kt`    | `AuthService.kt`, `UserService.kt`       |
| Hilt Module         | `[Domain]Module.kt`     | `NetworkModule.kt`, `StorageModule.kt`   |
| Sealed Class Routes | `[Feature]Route.kt`     | `AuthRoute.kt`, `ChatRoute.kt`           |
| Component           | `[Name].kt`             | `CustomButton.kt`, `ChatCard.kt`         |
| Extension           | `[Type]Ext.kt`          | `StringExt.kt`, `ContextExt.kt`          |
| Constants           | `[Domain]Constants.kt`  | `UIConstants.kt`, `NetworkConstants.kt`  |

## Visibility Modifiers

```kotlin
// Core modules should hide internal implementation
internal class InternalClass  // Not visible outside module
internal fun internalFunction()

internal object InternalObject {
    fun publicMethod() { }  // Accessible via the object
}

// Export public API
class PublicClass : PublicInterface {
    // This is visible module-wide
}

// Hilt modules must be public
object PublicModule {
    @Provides
    fun provideService(): Service = ...
}
```

## Common Mistakes

❌ **Wrong:**

```kotlin
// Feature module depending on another feature
implementation project(':feature:home')  // DON'T DO THIS

// Circular dependencies
// core:common depends on core:network

// Direct activity references in libraries
startActivity(Intent(this, MainActivity::class.java))  // Use nav graph instead
```

✅ **Correct:**

```kotlin
// Only depend on core modules
implementation project(':core:common')

// Keep dependencies unidirectional
// core:common has no dependencies

// Use navigation graph
navController.navigate(route)
```

## Troubleshooting

### Build fails with "Unresolved reference"

→ Add missing `implementation project(':core:*')` in build.gradle

### "Cannot find symbol" errors

→ Check package name matches directory structure
→ Rebuild project: `./gradlew clean build`

### Navigation not working

→ Verify NavGraph is in app module
→ Check route strings match sealed class

### Hilt errors

→ Ensure `@HiltAndroidApp` in app's Application class
→ Check all @Provides/@Binds in modules
→ Run `./gradlew clean build`

### Module can't see other module's code

→ Check if `internal` modifier is hiding it
→ Verify module is listed in dependencies
→ Check package names are correct

## Resources

- Full Architecture Documentation: [ARCHITECTURE.md](ARCHITECTURE.md)
- Migration Guide: [MIGRATION_GUIDE.md](MIGRATION_GUIDE.md)
- Android Multi-Module Best Practices: https://developer.android.com/guide/architecture/modularization
- Jetpack Compose: https://developer.android.com/jetpack/compose
- Hilt Documentation: https://developer.android.com/training/dependency-injection/hilt-android
