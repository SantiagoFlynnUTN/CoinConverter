# Coin Converter App

An Android application for converting currency, with support for both offline and online modes
through product flavors.

## Features

* **Offline & Online Modes:** Leverages product flavors and Dependency Injection (DI) to provide
  distinct offline and online versions of the app. This approach ensures that the production
  codebase remains clean and free of flavor-specific code.
* **Testable without API Key:** The offline flavor allows for complete testing of the app's core
  functionality and user interface without requiring an API key, ideal for quick evaluations and
  development.
* **API Key Management:** The API key is securely stored in `local.properties`.
* **Modern UI:** The user interface is built with Jetpack Compose, incorporating AI-assisted design
  and manual refinements to adhere to the following best practices:
  * Granular composable methods
  * Single Responsibility Principle
  * Low complexity and small method sizes
  * Descriptive and idiomatic Kotlin code
  * State hoisting for better state management
  * Non-reusable modifiers
  * Optimized recomposition
  * Lazy loading for history items
* **Navigation:** Implements Compose Navigation with a bottom bar and grouped navigation graphs.
* **Architecture:** Follows MVVM (Model-View-ViewModel) with MVI (Model-View-Intent) guidelines for
  a unidirectional data flow.
  * Utilizes Kotlin Flows for handling requests with back pressure.
  * Employs Moshi for efficient JSON serialization.
  * Uses Kotlin Result for managing the status of repository requests.
* **Testability:** The presentation layer is designed to be testable, with support for
  `CoroutineTest`, JUnit, and MockK.
* **Data Sources:** Abstracts network operations with remote and local data source layers.
* **Dark Mode:** Compatible with dark mode.
* **Modular Design:** Packages are structured in a way that allows for easy migration to separate
  modules, promoting separation of concerns.

## Setup

**Note:** For testing the app's general behavior and user interface, you can use the 'offline'
flavor, which does not require an API key. The following steps are for enabling the 'online'
features with real-time currency data.

To use the online features of the Coin Converter App, you need to obtain an API key
from [FreeCurrencyAPI](https://freecurrencyapi.com/).

1. **Get your API Key:**
  * Go to [https://freecurrencyapi.com/](https://freecurrencyapi.com/) and sign up for a free
    account.
  * Once registered, you will find your API key on your dashboard.

2. **Add the API Key to the project:**
  * Open the `local.properties` file in the root of the project. If this file doesn't exist,
    create it.
  * Add the following line to `local.properties`, replacing `YOUR_API_KEY` with the actual key you
    obtained:
    ```properties
    API_KEY=YOUR_API_KEY
    ```
  * **Important:** Ensure that `local.properties` is listed in your project's `.gitignore` file to
    prevent your API key from being committed to version control.

3. **Build and Run:**
  * You can now build and run the application. The online flavor will use the provided API key to
    fetch the latest currency exchange rates.

## Requirements

* **Android Studio:** Meerkat (2024.3.1) or newer
* **Android Gradle Plugin:** 8.9.0 or newer
* **Gradle Version:** 8.11.1 or newer
* **`compileSdk`:** 35
* **`minSdk`:** 24 (Android 7.0 Nougat)
* **`targetSdk`:** 35

## Disclaimer

Due to time constraints for this project, some dependencies are directly injected without an
interface. In a production environment, using interfaces would be mandatory to facilitate
modularization and maintain a clean architecture.

## Enhancements Backlog

* Improve UI colors, styles, and top bar details.
* Implement a debounce strategy for previews.
* Add a date formatter.
* Migrate strings to `strings.xml` or a localizable provider and encapsulate them.