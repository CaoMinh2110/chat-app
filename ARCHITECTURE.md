# Multi-Module Architecture

## Project Structure

```
├── app/                              # Application module (entry point)
│   ├── src/
│   └── build.gradle
├── core/                             # Core libraries
│   ├── common/                       # Common utilities, constants, extensions
│   │   ├── src/main/java/com/truevibeup/core/common/
│   │   └── build.gradle
│   ├── ui/                          # Shared UI components, theme, resources
│   │   ├── src/main/java/com/truevibeup/core/ui/
│   │   └── build.gradle
│   ├── network/                     # Network layer (Retrofit, Socket.IO, API)
│   │   ├── src/main/java/com/truevibeup/core/network/
│   │   └── build.gradle
│   ├── storage/                     # Local storage (DataStore, cache)
│   │   ├── src/main/java/com/truevibeup/core/storage/
│   │   └── build.gradle
│   └── di/                          # Shared dependency injection
│       ├── src/main/java/com/truevibeup/core/di/
│       └── build.gradle
└── feature/                          # Feature modules
    ├── auth/                        # Authentication feature (login, register)
    │   ├── src/main/java/com/truevibeup/feature/auth/
    │   └── build.gradle
    ├── home/                        # Home/Chat list feature
    │   ├── src/main/java/com/truevibeup/feature/home/
    │   └── build.gradle
    ├── chat/                        # Chat/Messaging feature
    │   ├── src/main/java/com/truevibeup/feature/chat/
    │   └── build.gradle
    └── profile/                     # User profile feature
        ├── src/main/java/com/truevibeup/feature/profile/
        └── build.gradle
```

## Module Dependencies

### Dependency Graph

```
app (Application)
├── feature:auth
├── feature:home
├── feature:chat
├── feature:profile
├── core:common
├── core:ui
├── core:network
├── core:storage
└── core:di

feature:* (Feature modules)
├── core:common
├── core:ui
├── core:network
├── core:storage
└── core:di

core:ui
└── core:common

core:network
├── core:common
└── core:di

core:storage
├── core:common
└── core:di

core:di (No dependencies)

core:common (No dependencies)
```

## Module Responsibilities

### Core Modules

#### `:core:common`

- Common constants, utility functions, and extensions
- Data models that are shared across multiple modules
- No dependencies on other core modules

#### `:core:ui`

- Shared Compose components (buttons, dialogs, cards, etc.)
- Theme configuration and colors
- Common animations and transitions
- Depends on: `core:common`

#### `:core:network`

- Retrofit API setup and configuration
- Socket.IO connection and management
- API service interfaces and implementations
- HTTP interceptors and logging
- Depends on: `core:common`, `core:di`

#### `:core:storage`

- DataStore preferences setup
- Local database configuration
- Cache management
- Data access objects (DAOs) if using Room
- Depends on: `core:common`, `core:di`

#### `:core:di`

- Hilt modules for core dependencies
- Singleton instances configuration
- Depends on: None (pure DI module)

### Feature Modules

#### `:feature:auth`

- Login and registration screens
- Authentication-related UI
- Auth ViewModels
- Dependencies: All core modules

#### `:feature:home`

- Chat list screen
- Chat preview and interactions
- Home screen navigation
- Dependencies: All core modules

#### `:feature:chat`

- Chat detail/message screen
- Message composition and sending
- Real-time message updates
- Dependencies: All core modules

#### `:feature:profile`

- User profile screen
- Profile editing
- User settings
- Dependencies: All core modules

### App Module

- Application entry point
- Main navigation graph
- Global app configuration
- Depends on: All feature modules and core modules

## Migration Steps

### How to Move Existing Code

1. **Data Layer (API, Models, Socket)**
   - Move API services → `core:network`
   - Move data models → `core:common` or `feature:*` (if feature-specific)
   - Move Socket.IO client → `core:network`

2. **UI Components & Theme**
   - Move theme config → `core:ui`
   - Move reusable components → `core:ui`
   - Move screen-specific components → respective `feature:*`

3. **ViewModels**
   - Move shared ViewModels → `core:di` (as providers)
   - Move feature ViewModels → respective `feature:*`

4. **DI & Repository**
   - Move Hilt modules → `core:di`
   - Move repositories → `core:network` or `core:storage` (by domain)
   - Move UseCases → respective `feature:*`

5. **Navigation**
   - Keep navigation graph in app module
   - Create graph composables in each feature module
   - Each feature exports a public route function

## Gradle Dependencies

Add module dependencies in your build.gradle files:

```gradle
// In feature module
dependencies {
    implementation project(':core:common')
    implementation project(':core:ui')
    implementation project(':core:network')
    implementation project(':core:storage')
    implementation project(':core:di')
}

// In app module
dependencies {
    implementation project(':feature:auth')
    implementation project(':feature:home')
    implementation project(':feature:chat')
    implementation project(':feature:profile')
    // ... and core modules
}
```

## Best Practices

1. **Minimal External Exposure**
   - Each module should export only essential interfaces
   - Use internal keyword to hide implementation details
   - Create public API files (e.g., `AuthApi.kt`)

2. **No Circular Dependencies**
   - Feature modules cannot depend on each other
   - Use shared models in core:common for inter-feature communication

3. **Hilt Usage**
   - Define Hilt modules in core:di
   - Features can use @Provides and @Binds from core:di
   - Each feature module can have its own @HiltViewModel

4. **Navigation**
   - Use sealed classes for navigation routes
   - Store navigation routes in core:common
   - Each feature provides its navigation composable

5. **Resource Sharing**
   - Strings, colors, drawables used by multiple features → core:ui
   - Feature-specific resources → feature:\*/res/

## Building & Running

```bash
# Build entire project
./gradlew build

# Build specific module
./gradlew :core:common:build
./gradlew :feature:auth:build

# Run app
./gradlew :app:installDebug
```

## Testing

```bash
# Run all tests
./gradlew test

# Run tests for specific module
./gradlew :feature:auth:test
./gradlew :core:network:test
```

## Advantages of This Architecture

✅ **Scalability** - Easy to add new features without affecting existing code
✅ **Maintainability** - Clear separation of concerns
✅ **Testability** - Each module can be tested independently
✅ **Build Time** - Only changed modules need to be rebuilt
✅ **Team Collaboration** - Different teams can work on different features
✅ **Code Reusability** - Core modules can be shared across multiple features
✅ **Offline Support** - Easier to implement caching strategies
