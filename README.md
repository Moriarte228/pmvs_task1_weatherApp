# Weather App (Compose Multiplatform)

Кроссплатформенное приложение для отображения погоды в реальном времени.
Поддерживаемые таргеты: **Android**, **iOS**, **Linux/Desktop (JVM)**, **Web (wasmJs)**.

Реализовано на Compose Multiplatform на основе задания 9 лабораторной работы 5.

## Стек

| Категория          | Технология                                  |
| ------------------ | ------------------------------------------- |
| Язык               | Kotlin 2.1.21                               |
| UI                 | Compose Multiplatform 1.7.3 (Material 3)    |
| HTTP-клиент        | Ktor 3.0.3 (OkHttp / Darwin / CIO / JS)     |
| Сериализация       | kotlinx.serialization 1.7.3                 |
| Асинхронность      | Kotlin Coroutines 1.9.0                     |
| Кэш / Settings     | multiplatform-settings 1.2.0                |
| Сборка             | Gradle 8.9 + AGP 8.7.3                      |

## Функциональность

- Ввод названия города и добавление в список.
- Получение текущей погоды через **OpenWeatherMap API**.
- Отображение: температура, ощущается, описание, иконка-эмодзи, влажность,
  скорость ветра, давление, видимость.
- **Кэширование** для офлайн-просмотра (SharedPreferences / NSUserDefaults /
  Properties / localStorage в зависимости от платформы).
- Адаптивный UI: своя верстка под каждую платформу.

## Адаптация UI

| Платформа | Особенности                                                                                       |
| --------- | ------------------------------------------------------------------------------------------------- |
| Android   | Material 3 карточки с тенями и скруглёнными углами, `OutlinedTextField` с иконкой поиска         |
| iOS       | Плоские карточки без теней, `SegmentedButton` для переключения между городами, пилюля-`SearchBar` |
| Linux     | Минималистично, чёткие границы (`border`) у элементов, изменяемое окно                            |
| Web       | Отзывчивая сетка: 1 колонка (`<600dp`), 2 (`<1024dp`), 3 (`>=1024dp`) на основе `BoxWithConstraints` |

## Запуск

### Android
```bash
./gradlew :composeApp:installDebug
```

### Desktop (Linux)
```bash
./gradlew :composeApp:run
# или сборка дистрибутива:
./gradlew :composeApp:packageDeb
```

### Web (wasmJs)
```bash
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
# или production-билд:
./gradlew :composeApp:wasmJsBrowserDistribution
```

### iOS
Открыть `iosApp/iosApp.xcodeproj` в Xcode и запустить схему `iosApp`.

## Тесты

Реализованы три категории тестов:

- **Unit-тесты** (`commonTest/unit`) — `WeatherEmojiTest`, `FormattingTest`.
- **Интеграционные тесты** (`commonTest/integration`) — `WeatherApiIntegrationTest`,
  `WeatherRepositoryIntegrationTest` с использованием Ktor MockEngine и in-memory кэша.
- **UI-тесты виджетов** (`desktopTest/ui`) — `WeatherDetailsBlockTest` на
  Compose UI Test API.

Запуск тестов:
```bash
./gradlew :composeApp:desktopTest
```

## CI

GitHub Actions автоматически собирает все четыре платформы при push/PR в `main`:

- `test-common` — unit и integration тесты на JVM.
- `build-android` — APK debug.
- `build-desktop-linux` — `.deb` пакет.
- `build-web` — wasmJs production-дистрибутив.
- `build-ios` — сборка для iPhone-симулятора (macos-14 runner).

См. [`.github/workflows/build.yml`](.github/workflows/build.yml).

## API ключ

В проекте используется демо-ключ OpenWeatherMap из исходной лабораторной.
Для production стоит вынести его в `local.properties` или переменную окружения.

## Структура проекта

```
composeApp/
  src/
    commonMain/kotlin/com/example/weatherapp/
      data/       # API, кэш, репозиторий, DTO
      domain/     # доменные модели
      platform/   # expect-объявления для платформ
      ui/         # Composable, тема, ViewModel
    commonTest/   # unit + integration
    androidMain/  # Android entry-point + actuals
    iosMain/      # iOS entry-point + actuals
    desktopMain/  # JVM entry-point + actuals
    desktopTest/  # UI-тесты
    wasmJsMain/   # Web entry-point + actuals
iosApp/           # Xcode-проект
.github/workflows/build.yml
```
