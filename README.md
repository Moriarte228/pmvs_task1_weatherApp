# Weather CMP — Compose Multiplatform Weather App

Кроссплатформенное приложение погоды на **Compose Multiplatform** (Android, iOS, Desktop/Linux, Web).  
Основано на проекте задания 9 лабораторной работы 5.

## Поддерживаемые платформы

| Платформа | Статус |
|-----------|--------|
| Android   | ✅ Material 3, карточки с тенями |
| iOS       | ✅ Плоские карточки, SearchBar-стиль |
| Linux Desktop | ✅ Минималистичный дизайн, изменяемый размер окна |
| Web (WasmJS) | ✅ Адаптивная сетка 1/2/3 колонки |

## Функциональность

- **Поиск города** — TextField с иконкой поиска
- **Текущая погода** — температура, ощущаемая, описание, эмодзи-иконка
- **Детали** — влажность, скорость ветра, давление
- **Кеширование** — офлайн-просмотр последних загруженных данных
- **Несколько городов** — добавление и удаление городов
- **Адаптивная верстка** — одно/двух/трёхколоночная сетка на Web

## Технологии

- **Ktor** — HTTP-клиент (OkHttp на Android, Darwin на iOS, CIO на Desktop, JS на Web)
- **kotlinx.serialization** — десериализация JSON-ответов OpenWeatherMap
- **Coroutines + Flow** — асинхронные запросы через ViewModel
- **Compose Multiplatform 1.7.3** — общий UI-код
- **Material 3** — дизайн-система

## Структура проекта

```
composeApp/
├── src/
│   ├── commonMain/kotlin/com/example/weather/
│   │   ├── App.kt              — общий UI (Composable)
│   │   ├── WeatherData.kt      — модели данных + сериализация
│   │   ├── WeatherRepository.kt — API + кеш
│   │   ├── WeatherViewModel.kt  — бизнес-логика, StateFlow
│   │   └── Platform.kt         — expect-объявления
│   ├── androidMain/    — MainActivity, actual Platform
│   ├── iosMain/        — MainViewController, actual Platform
│   ├── desktopMain/    — main.kt (Window), actual Platform
│   ├── wasmJsMain/     — main.kt (ComposeViewport), actual Platform
│   └── commonTest/     — unit + integration тесты
```

## Тесты

### Модульные тесты (`WeatherUnitTests.kt`) — 8 тестов
- `testGetWeatherEmojiThunderstorm` — эмодзи для грозы
- `testGetWeatherEmojiRain` — эмодзи для дождя
- `testGetWeatherEmojiClear` — эмодзи для ясной погоды
- `testGetWeatherEmojiCloudy` — эмодзи для пасмурной погоды
- `testGetWeatherEmojiUnknown` — эмодзи для неизвестного ID
- `testWeatherResponseToWeatherData` — маппинг API-ответа в модель
- `testWeatherResponseEmptyWeatherList` — обработка пустого массива
- `testGetCurrentWeatherSuccess` — успешный запрос через mock
- `testGetCurrentWeatherCachesResult` — кеширование результата
- `testGetCurrentWeatherNetworkError` — обработка ошибки сети
- `testGetAllCachedCitiesEmpty` — пустой кеш
- `testCacheKeyIsCaseInsensitive` — кеш без учёта регистра

### Интеграционные тесты (`WeatherIntegrationTests.kt`) — 7 тестов
- `testLoadWeatherUpdatesState` — загрузка обновляет кеш
- `testSearchQueryUpdatesState` — обновление поисковой строки
- `testClearErrorClearsState` — очистка ошибки
- `testRemoveCityRemovesFromList` — кеш после загрузки
- `testWeatherEmojiIntegrationWithRealId` — эмодзи из реальных данных
- `testMultipleCitiesCached` — несколько городов в кеше
- `testWeatherDataFieldMapping` — маппинг всех полей

## Запуск через GitHub Actions (рекомендуется)

1. Форкните/создайте репозиторий на GitHub
2. Загрузите файлы проекта
3. GitHub Actions автоматически запустит:
   - `test` — unit + integration тесты (Ubuntu)
   - `android` — сборка APK + тесты (Ubuntu)
   - `desktop` — сборка JAR для Linux (Ubuntu)
   - `web` — сборка WasmJS (Ubuntu)
   - `ios` — сборка framework (macOS)
4. Артефакты сборки доступны во вкладке **Actions → Artifacts**

## Локальный запуск (опционально)

```bash
# Android (нужен Android SDK)
./gradlew :composeApp:assembleDebug

# Desktop
./gradlew :composeApp:run

# Web
./gradlew :composeApp:wasmJsBrowserDevelopmentRun

# Тесты
./gradlew :composeApp:desktopTest
```

## API Key

В `WeatherRepository.kt` вшит ключ из задания 9.  
При необходимости замените на собственный ключ [OpenWeatherMap](https://openweathermap.org/api).

## Подключение как submodule

```bash
# В репозитории лабораторной работы:
git submodule add https://github.com/<your-username>/weather-cmp
git commit -m "Add weather-cmp submodule"
```
