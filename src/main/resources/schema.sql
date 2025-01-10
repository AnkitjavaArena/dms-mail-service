CREATE TABLE "EmailTrack" (
    "Id" VARCHAR(255) PRIMARY KEY,         
    "Sender" VARCHAR(255) NOT NULL,         
    "Receipient" VARCHAR(255) NOT NULL,           
    "Time" TIMESTAMP NOT NULL,            
    "Status" VARCHAR(255) NOT NULL,       
    "Count" INT,                          
    "Subscribe" BOOLEAN DEFAULT TRUE     -- Subscription status (True/False), defaults to true, if set to false, user unsubscribed, dont mail them
);
