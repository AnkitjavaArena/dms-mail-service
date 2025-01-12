CREATE TABLE "Enroll" (
    "Id" BIGINT PRIMARY KEY AUTO_INCREMENT,
    "Recipient" VARCHAR(255) NOT NULL,
    "Salutation" VARCHAR(255),
    "Time" TIMESTAMP NOT NULL,
    "Status" VARCHAR(255) NOT NULL,
    "Count" INT,
    "Subscribe" BOOLEAN DEFAULT TRUE
);

CREATE TABLE "EmailHistory" (
    "Id" BIGINT PRIMARY KEY AUTO_INCREMENT,
    "EnrollId" BIGINT,
    "Sender" VARCHAR(255) NOT NULL,
    "Recipient" VARCHAR(255) NOT NULL,
    "Timestamp" TIMESTAMP NOT NULL,
    "Status" VARCHAR(255) NOT NULL,
    "Subscribe" BOOLEAN DEFAULT TRUE,
    FOREIGN KEY ("EnrollId") REFERENCES "Enroll"("Id")
);
