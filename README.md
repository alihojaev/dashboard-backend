## Dashboard Backend

Короткое описание: многомодульный бэкенд на Spring Boot для админки и публичного API с готовыми блоками (аутентификация/авторизация, отчеты, работа с файлами, MinIO, документация, миграции БД).

### Технологии
- Spring Boot 3 (Web, Security, Data JPA, Mail, Validation)
- Springdoc OpenAPI 2 (Swagger UI)
- Liquibase (миграции БД)
- PostgreSQL (JDBC-драйвер)
- MapStruct (маппинг DTO ↔ Entity)
- Lombok
- MinIO Java SDK (хранилище файлов)
- Caffeine Cache
- Apache POI (Excel), Apache HttpClient, Jackson/Gson
- RSQL (фильтры для запросов)
- Java 22, Maven

### Структура проекта
- Корень (`dashboard-backend`): многомодульный Maven-проект
  - `core`: доменные модели, сервисы, утилиты, конфиги, миграции
    - `config`: конфигурации (например, `MinioConfig`, Gson, security)
    - `auth`, `admin`, `client`: доменные подсистемы (роли, права, пользователи)
    - `files`: работа с файлами (MinIO) — `FileService`, `MinioService`
    - `report`: CSV-отчеты, утилиты отражений и репозиториев
    - `translate`: интеграция Yandex Translate
    - `util`: общие утилиты (валидация, транзакции, SQL-билдер, REST и др.)
    - `resources`: `application.yml`, Liquibase (`db/`), статические файлы
  - `admin-api`: REST API для админки
  - `client-api`: публичное REST API для клиента

### Чем полезен
- Быстрый старт для продакшн-ready бэкенда: слои, безопасность, миграции, документация.
- Готовые блоки: аутентификация/авторизация, выгрузка отчетов, загрузка/скачивание файлов в MinIO.
- Паттерны: чистая валидация DTO, транзакционная работа с JDBC, гибкая фильтрация RSQL.

### Утилиты и полезные компоненты
- Валидация:
  - `ChainValidator`, `ChainElement`, `Validatable`: декларативная валидация DTO цепочками правил.
  - Аннотация `@UniqueNameWithRdt` и валидатор `UniqueNameWithRdtValidator`: проверка уникальности с учетом soft-delete-поля `rdt`.
- Транзакции и SQL:
  - `TransactionUtil`: обертки для транзакций JDBC (с возвратом результата и без), безопасный rollback.
  - `SqlBuilder` и DML-утилиты (`Insert`, `InsertNoReturning`, `Update`, `Delete`, `SqlSelectExecutable`): безопасная сборка SQL, маппинг `PreparedStatement`, батчи.
  - Базовые DAO: `BaseDao`, `BaseEntityDao` — унифицированные CRUD-операции через `TransactionUtil` и `SqlBuilder`.
- REST-утилиты:
  - `RestUtil`/`RestUtilImpl` и статический фасад `Rest`: POST/GET с заголовками, токенами и сериализацией.
- Файлы:
  - `FileService`/`FileServiceImpl` и `MinioService`/`MinioServiceImpl`: загрузка по SHA-256 имени, скачивание, создание бакета при необходимости.
  - `MinioConfig`: бин `MinioClient` из env-конфигурации.
- Отчеты:
  - `ReportServiceImpl`: формирование CSV с BOM, заголовками и полями.
  - `RepositoryUtil`: получение данных (в т.ч. RSQL-спецификации через `RsqlSpecificationFactory`).
- Прочее:
  - Конвертеры Gson (`EnumAdapterFactory`, `HibernateProxyAdapterFactory`, `GsonAdapterFactory`).
  - OpenAPI/Swagger UI подключены для документации.
  - Кэширование через Caffeine.

### Конфигурация и секреты
- Все секреты и пароли вынесены в переменные окружения (см. `.env.example`).
- `core/src/main/resources/application.yml` содержит плейсхолдеры для:
  - Базы данных: `DB_PASSWORD`
  - Почты: `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`
  - MinIO: `MINIO_URL`, `MINIO_ACCESS_KEY`, `MINIO_SECRET_KEY`
  - Yandex Translate: `YANDEX_TRANSLATE_API_KEY`, `YANDEX_TRANSLATE_FOLDER_ID`, `YANDEX_TRANSLATE_URL`
  - JWT: `JWT_SECRET`
- В корне присутствуют настроенные `
.gitignore` и `.gitattributes`.


