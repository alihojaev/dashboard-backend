--liquibase formatted sql

--changeset author:system:001-create-clients-table
CREATE TABLE clients (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20),
    password VARCHAR(255),
    google_id VARCHAR(255) UNIQUE,
    google_email VARCHAR(255),
    apple_id VARCHAR(255) UNIQUE,
    apple_email VARCHAR(255),
    blocked BOOLEAN DEFAULT FALSE,
    auth_type VARCHAR(20) NOT NULL,
    last_activity TIMESTAMP,
    created_by UUID,
    modified_by UUID,
    cdt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    mdt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    rdt TIMESTAMP
);

-- Индексы для улучшения производительности
CREATE INDEX idx_clients_email ON clients(email);
CREATE INDEX idx_clients_username ON clients(username);
CREATE INDEX idx_clients_google_id ON clients(google_id);
CREATE INDEX idx_clients_apple_id ON clients(apple_id);
CREATE INDEX idx_clients_phone ON clients(phone);
CREATE INDEX idx_clients_auth_type ON clients(auth_type);
CREATE INDEX idx_clients_blocked ON clients(blocked);
CREATE INDEX idx_clients_last_activity ON clients(last_activity);
CREATE INDEX idx_clients_cdt ON clients(cdt);

-- Комментарии к таблице и колонкам
COMMENT ON TABLE clients IS 'Таблица для хранения информации о клиентах';
COMMENT ON COLUMN clients.id IS 'Уникальный идентификатор клиента';
COMMENT ON COLUMN clients.email IS 'Основной email клиента';
COMMENT ON COLUMN clients.username IS 'Уникальное имя пользователя';
COMMENT ON COLUMN clients.phone IS 'Номер телефона клиента';
COMMENT ON COLUMN clients.password IS 'Хешированный пароль (для email авторизации)';
COMMENT ON COLUMN clients.google_id IS 'ID пользователя в Google OAuth';
COMMENT ON COLUMN clients.google_email IS 'Email пользователя в Google OAuth';
COMMENT ON COLUMN clients.apple_id IS 'ID пользователя в Apple OAuth';
COMMENT ON COLUMN clients.apple_email IS 'Email пользователя в Apple OAuth';
COMMENT ON COLUMN clients.blocked IS 'Флаг блокировки аккаунта';
COMMENT ON COLUMN clients.auth_type IS 'Тип авторизации: EMAIL, GOOGLE, APPLE';
COMMENT ON COLUMN clients.last_activity IS 'Время последней активности';
COMMENT ON COLUMN clients.created_by IS 'ID пользователя, создавшего запись';
COMMENT ON COLUMN clients.modified_by IS 'ID пользователя, изменившего запись';
COMMENT ON COLUMN clients.cdt IS 'Время создания записи';
COMMENT ON COLUMN clients.mdt IS 'Время последнего изменения записи';
COMMENT ON COLUMN clients.rdt IS 'Время удаления записи (soft delete)'; 