# dms-mail-service

This README file outlines the steps to set up and run the `dms-mail-service` project.

## Step 1: Clone the Project
Clone the project repository from GitHub:
```bash
https://github.com/AnkitjavaArena/dms-mail-service.git
```

## Step 2: Place Your Credentials
Add your credentials to the `application.properties` file located at:
```
https://github.com/AnkitjavaArena/dms-mail-service/blob/develop/src/main/resources/application.properties
```

## Step 3: Replace the Resume File
Delete the existing resume file and replace it with your own resume at the following location:
```
https://github.com/AnkitjavaArena/dms-mail-service/blob/develop/src/main/resources/Ankit_Resume_Java.pdf
```

## Step 4: Update the Email Template
Modify the email body in the `email-template.html` file to suit your needs:
```
https://github.com/AnkitjavaArena/dms-mail-service/blob/develop/src/main/resources/email-template.html
```

## Step 5: Run the Project
Start the project using your preferred method (e.g., IDE or command line).

## Step 6: Make API Calls
Use Postman or any other API testing tool to send a POST request to the service.

### Sample Request
**Endpoint:**
```http
POST http://localhost:7880/sendMail
```

**Request Body:**
```json
{
  "recipients": [
    {
      "email": "hr_indigo@gmail.com"
    },
    {
      "email": "ram.vk@gmail.com",
      "salutation": "Ram"
    }
  ]
}
```

You can modify the `recipients` field as needed.

