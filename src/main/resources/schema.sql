CREATE TABLE "EmailTrack" (
    "Id" BIGINT PRIMARY KEY AUTO_INCREMENT,
    "Sender" VARCHAR(255) NOT NULL,
    "Receipient" VARCHAR(255) NOT NULL,
    "Salutation" VARCHAR(255),
    "Time" TIMESTAMP NOT NULL,
    "Status" VARCHAR(255) NOT NULL,
    "Count" INT,
    "Subscribe" BOOLEAN DEFAULT TRUE
);
