CREATE TABLE "Enroll" (
    "Id" BIGINT PRIMARY KEY AUTO_INCREMENT,
    "Recipient" VARCHAR(255) NOT NULL,
    "Salutation" VARCHAR(255),
    "Time" TIMESTAMP NOT NULL,
    "Status" VARCHAR(255) NOT NULL,
    "Count" INT,
    "Subscribe" BOOLEAN DEFAULT TRUE
);
