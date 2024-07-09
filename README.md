# Fraud Transaction Detector Service

This microservice is designed to detect potential fraud in real-time by analyzing transaction patterns, customer behavior, and anomalies across multiple data sources. It leverages Spring Boot for the microservice architecture and Spring Kafka for handling message-driven events.

## Features

- **Anomaly Detection Event**: Emits events when potential anomalies are detected. Other microservices can subscribe to these events to enhance security measures or notify relevant stakeholders.
- **Rule Set Caching**: Implements caching of frequently used fraud detection rules to improve response times. This allows for quick retrieval and application of predefined rules during real-time analysis.

## Getting Started

### Prerequisites

- Java JDK 11 or later
- Maven 3.6 or later
- Redis server v=7.0.0 or later
- Spring-Kafka 3.0.0
- Jacoco 0.8.7


### Setup

1. **Clone the repository**
   ```bash
   git clone https://pscode.lioncloud.net/vanvandn/FraudTransactionDetector.git
   


### **Build the project**
    ```
    mvn clean install



### **Execution steps for local**

1. Start redis server using command 
    ```
   redis-server
   
2. Start Kafka Zookeeper using command
    ```
   zookeeper-server-start /opt/homebrew/etc/kafka/zookeeper.properties

3. After zookeeper starts successfully, start Kafka Server using command
    ```
   kafka-server-start /opt/homebrew/etc/kafka/server.properties
   
4. Start main Application
    ```
   mvn spring-boot:run
   
5. Running Unit Test Cases
    ```
   mvn clean test
   
To check coverage report open ***"target/site/jacococ/index.html"***
