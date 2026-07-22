CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(180) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE hosting_requests (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(180) NOT NULL,
    project_type VARCHAR(30) NOT NULL,
    project_name VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    hosting_type VARCHAR(150) NOT NULL,
    needs_database BOOLEAN NOT NULL DEFAULT FALSE,
    database_type VARCHAR(30) NOT NULL DEFAULT 'aucune',
    git_repo VARCHAR(300),
    expected_traffic VARCHAR(150),
    message TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'EN_ATTENTE',
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE password_reset_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    token VARCHAR(200) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_hosting_requests_user_id ON hosting_requests(user_id);
CREATE INDEX idx_hosting_requests_status ON hosting_requests(status);

INSERT INTO users (full_name, email, password_hash, role)
VALUES ('Admin WebSGI', 'admin@websgi.fr', '$2a$10$qYD9bgF3QXOzt4QZWHyUsuW06i6JlrQ306RPOPcQDZzv2.ysC5.22', 'ADMIN');
