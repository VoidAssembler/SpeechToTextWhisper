# Speech to Text API with Whisper

REST API сервис для преобразования речи в текст с использованием OpenAI Whisper.

## Требования

Для локальной разработки:
- Java 17+
- FFmpeg
- OpenAI Whisper (`pip install -U openai-whisper`)
- Maven

Для Docker:
- Docker
- Docker Compose (опционально)

## Установка

### Локальная установка

1. Клонируйте репозиторий:
```bash
git clone <repository-url>
cd speech-to-text
```

2. Установите Whisper:
```bash
pip install -U openai-whisper
```

3. Установите FFmpeg (если не установлен):
- Windows: Скачайте с [официального сайта](https://ffmpeg.org/download.html) и добавьте в PATH
- Linux: `sudo apt-get install ffmpeg`
- macOS: `brew install ffmpeg`

4. Соберите Java приложение:
```bash
mvn clean package
```

### Docker установка

1. Сборка и запуск с помощью Docker Compose:
```bash
docker-compose up --build
```

2. Или используйте готовый образ:
```bash
docker pull ghcr.io/[username]/speech-to-text:latest
docker run -p 8080:8080 ghcr.io/[username]/speech-to-text:latest
```

## Конфигурация

Настройки приложения находятся в `src/main/resources/application.yml`:

- `server.port`: порт сервера (по умолчанию 8080)
- `whisper.timeout-seconds`: таймаут обработки в секундах
- `server.servlet.multipart.max-file-size`: максимальный размер файла
- `server.servlet.multipart.max-request-size`: максимальный размер запроса

## Запуск

### Локальный запуск
```bash
java -jar target/speech-to-text-1.0.0.jar
```

### Docker запуск
```bash
docker-compose up
```

## API Documentation

API документация доступна через Swagger UI по адресу:
```
http://localhost:8080/swagger-ui.html
```

Также доступна OpenAPI спецификация в формате JSON:
```
http://localhost:8080/v3/api-docs
```

## API Endpoints

### POST /api/v1/transcribe

Преобразует аудиофайл в текст.

**Параметры запроса:**

- `file` (обязательный) - аудиофайл (WAV, MP3, FLAC, AAC, OGG)
- `model` (опционально) - модель Whisper (tiny, base, small, medium, large)
- `language` (опционально) - язык аудио (auto, en, ru, es, fr, de, it, pt, nl, pl, tr, zh)

**Пример запроса:**

```bash
curl -X POST "http://localhost:8080/api/v1/transcribe" \
     -H "Content-Type: multipart/form-data" \
     -F "file=@audio.mp3" \
     -F "model=base" \
     -F "language=ru"
```

**Успешный ответ (200 OK):**

```json
{
  "status": "success",
  "data": {
    "text": "Распознанный текст из аудио",
    "language": "ru",
    "duration": 45.23,
    "model_used": "base"
  }
}
```

## Поддерживаемые форматы

API поддерживает следующие форматы аудио файлов:
- WAV (audio/wav, audio/x-wav)
- MP3 (audio/mp3, audio/mpeg)
- FLAC (audio/flac)
- AAC (audio/aac, audio/x-aac)
- OGG (audio/ogg, application/ogg, video/ogg)

## Обработка ошибок

- 400 Bad Request: Неверный формат файла или отсутствует файл
- 500 Internal Server Error: Ошибка обработки файла

## Безопасность

- Проверка формата файла
- Ограничение размера файла
- Очистка временных файлов
- Таймаут обработки

## Разработка

### Структура проекта
```
.
├── src/                    # Исходный код Java
├── Dockerfile             # Конфигурация Docker
├── docker-compose.yml     # Docker Compose конфигурация
├── pom.xml                # Maven конфигурация
└── README.md              # Документация
