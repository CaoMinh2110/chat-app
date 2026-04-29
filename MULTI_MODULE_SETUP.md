# TrueVibeDate - Multi-Module Architecture

## 📋 Overview

This project has been refactored to follow a **multi-module architecture**, which provides better scalability, maintainability, and team collaboration.

## 🏗️ Project Structure

```
TrueVibeDate (Root)
├── app/                           # Application module (entry point)
├── core/                          # Core libraries (shared code)
│   ├── common/                   # Utilities, constants, extensions
│   ├── ui/                       # UI components, theme
│   ├── network/                  # API, Socket.IO, networking
│   ├── storage/                  # DataStore, caching
│   └── di/                       # Dependency injection
└── feature/                       # Feature modules (independent features)
    ├── auth/                     # Authentication feature
    ├── home/                     # Chat list feature
    ├── chat/                     # Messaging feature
    └── profile/                  # User profile feature
```

## 📚 Documentation Files

1. **[ARCHITECTURE.md](ARCHITECTURE.md)** - Complete architecture documentation
   - Module responsibilities
   - Dependencies graph
   - Best practices

2. **[MIGRATION_GUIDE.md](MIGRATION_GUIDE.md)** - Step-by-step migration from single to multi-module
   - How to move existing code
   - Common issues and solutions

3. **[MODULE_GUIDE.md](MODULE_GUIDE.md)** - Quick reference for developers
   - Naming conventions
   - How to add new code
   - Common commands

## 🚀 Getting Started

### Prerequisites

- Android Studio 2024.1 or later
- Gradle 8.x
- Kotlin 2.0.0
- Minimum SDK: 26
- Target SDK: 35

### Build & Run

```bash
# Sync Gradle
./gradlew build

# Run the app
./gradlew :app:installDebug

# Run tests
./gradlew test
```

## 📦 Module Description

### Core Modules

#### `:core:common`

- Shared utilities and extensions
- Common constants and models
- No external dependencies

#### `:core:ui`

- Reusable Compose components
- Theme configuration (colors, typography)
- Animations and transitions

#### `:core:network`

- Retrofit API client setup
- Socket.IO connection management
- API services and models
- HTTP interceptors

#### `:core:storage`

- DataStore preferences
- Local database (if applicable)
- Caching strategies

#### `:core:di`

- Hilt modules for dependency injection
- Singleton instances
- Factory functions

### Feature Modules

#### `:feature:auth`

- Login and registration screens
- Authentication logic
- Session management

#### `:feature:home`

- Chat list screen
- Chat preview
- List pagination

#### `:feature:chat`

- Message screen
- Message sending/receiving
- Real-time updates via Socket.IO

#### `:feature:profile`

- User profile management
- Profile editing
- User settings

## 🔄 Next Steps for Migration

1. **Move existing code** following [MIGRATION_GUIDE.md](MIGRATION_GUIDE.md)
2. **Organize packages** according to module structure
3. **Update imports** to match new module paths
4. **Test build** with `./gradlew clean build`
5. **Verify app runs** without errors

## ✅ Architecture Benefits

- ✅ **Better Organization** - Clear separation of concerns
- ✅ **Faster Builds** - Only changed modules rebuild
- ✅ **Easier Testing** - Test modules independently
- ✅ **Team Collaboration** - Different teams → different features
- ✅ **Code Reusability** - Core modules shared across features
- ✅ **Scalability** - Easy to add new features
- ✅ **Offline Support** - Better caching strategies

## 🛠️ Useful Commands

```bash
# Clean and build
./gradlew clean build

# Build specific module
./gradlew :core:network:build

# Run app
./gradlew :app:run

# Run tests
./gradlew test

# See module dependencies
./gradlew :app:dependencies

# Check for dependency issues
./gradlew :app:dependencyInsight
```

## 📖 Conventions

### Package Naming

- `com.truevibeup.core.common` - Core common module
- `com.truevibeup.core.ui` - Core UI module
- `com.truevibeup.core.network` - Core network module
- `com.truevibeup.feature.auth` - Auth feature module

### File Organization

- **Screens**: `presentation/screen/`
- **ViewModels**: `presentation/viewmodel/`
- **Components**: `presentation/component/`
- **Models**: `data/model/` or `domain/model/`
- **Repositories**: `data/repository/`
- **Use Cases**: `domain/usecase/`

## ⚙️ Gradle Configuration

Each module has its own `build.gradle` file with:

- Appropriate plugins (application vs library)
- Module-specific dependencies
- Build configuration
- KSP annotation processor setup

Common dependencies are declared in:

- Root `build.gradle` - Plugin versions
- Module `build.gradle` - Actual dependencies

## 🔗 Dependencies

Current major dependencies:

- Jetpack Compose 2024.06.00
- Hilt 2.51.1
- Retrofit 2.11.0
- Socket.IO 2.1.0
- Kotlin Coroutines 1.9.0
- Android Material3

## 🐛 Troubleshooting

### Build Errors

1. Run `./gradlew clean build` to clean all build cache
2. Invalidate caches in Android Studio: File → Invalidate Cache

### Dependency Issues

1. Check module dependencies in build.gradle
2. Verify no circular dependencies exist
3. Run `./gradlew --refresh-dependencies`

### Compilation Errors

1. Verify package names match directory structure
2. Check imports are pointing to correct modules
3. Ensure `implementation project(':module:name')` declarations exist

## 📞 Getting Help

Refer to the documentation files:

- **Quick questions?** → [MODULE_GUIDE.md](MODULE_GUIDE.md)
- **Migrating code?** → [MIGRATION_GUIDE.md](MIGRATION_GUIDE.md)
- **Architecture details?** → [ARCHITECTURE.md](ARCHITECTURE.md)

## 🎯 Future Enhancements

- [ ] Add core:analytics module
- [ ] Create core:design (design system)
- [ ] Implement core:model (type-safe models)
- [ ] Add dynamic feature modules
- [ ] Create shared test fixtures

---

**Last Updated**: April 2026
**Architecture Version**: 1.0
