Notification Microservice 
(currently for push notifications)

A robust, asynchronous, and priority-aware push notification service built using **Spring Boot**, **Redis Streams**, and **Firebase Cloud Messaging (FCM)**. This system ensures reliable delivery with support for retry mechanisms, scheduled notifications, and smart failure handling — designed for scalability and production-readiness.

---

## 🚀 Features

- 🔔 **Priority-Based Notification Handling**  
  Sends notifications with `HIGH`, `MEDIUM`, and `LOW` priority levels, each mapped to configurable Time-To-Live (TTL) durations.

- 🔁 **Retry Mechanism with Exponential Backoff**  
  Retries failed notifications up to a configurable limit using exponential delay to avoid spamming and ensure effective delivery.
Triggers the mechanism for atmost 3 retries.

- 🗓️ **Scheduled Notifications**  
  Supports future-dated delivery by storing requests in the database and periodically dispatching them through a scheduler.
Currently scheduled after every 60 seconds/

- ⚙️ **System ,API and Event-Triggered Events**  
  Accommodates both programmatically generated (event) and externally triggered (API-based and system) notifications.
For Event triggered notification different events can be programatically added to trigger the notification.
For system notifications api can be integrated for each system triggered event .

- 📲 **Multi-Platform Delivery via FCM**  
  Sends notifications to Android apps and web clients using Firebase Cloud Messaging.

- 🧠 **Smart Failure Detection**  
  Maintains delivery status and retry attempts in the database for post-analysis, visibility, and reliability improvements.

- 🧠 **Handless Invalid FCM token**
   If notifications fails due to invalid fcm token than that token is deleted from the device table and waits for new token . 

---

## 🛠 Tech Stack

| Component          | Technology                        |
|--------------------|-----------------------------------|
| Backend            | Java, Spring Boot                 |
| Messaging Queue    | Redis Streams                     |
| Notification Engine| Firebase Cloud Messaging (FCM)    |
| Database           | MySQL                             |
| Build Tools        | Gradle                     |

---

## 🧱 Architecture Overview

+--------------------------+
|   User Registration      |
|  - Register user & devices|
+--------------------------+
            |
            v
+-----------------------------+
| Notification Triggered      | 
|  - API or Event Triggered   |
|  - Validate user & devices  |
+-----------------------------+
            |
            v
+-------------------------------+
| Redis Producer (Event Queue)  |
+-------------------------------+
            |
            v
+-------------------------------+
| Notification Consumer         |
|  - Listen to Redis Stream     |
|  - Sort events by priority    |
|  - Convert events to notifications |
|  - Save to DB                 |
+-------------------------------+
            |
            v
+-------------------------------+
| FCM Service Call              |
|  - Fetch FCM Tokens           |
|  - Send notifications         |
|  - Handle success/failure     |
+-------------------------------+
            |
            v
+-------------------------------+
| Retry Mechanism               |
|  - Exponential backoff        |
|  - Retry up to 3 attempts     |
|  - Mark as failed if needed   |
+-------------------------------+
           
+-------------------------------+
| Scheduled Notifications       |
|  - Poll DB every 60 seconds   | <-- Periodically polls DB
|  - Push future notifications  |
+-------------------------------+


------
## Future Enhancements
-RateLimiting to avoid spam and overload
-Batching notifications user wise
-Bulk notifications
-Api authentication and Authorisation
