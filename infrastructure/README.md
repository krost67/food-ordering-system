# README #

### How to run Kafka cluster locally? ###
You'll need to have Docker installed.

1. Start Zookeeper
    ```
    $ docker-compose -f common.yml -f zookeeper.yml up
    ```
    You can check if service is running in a non error state
    ```
    $ echo ruok | nc localhost 2181
    ```

2. Start Kafka Cluster
    ```
    $ docker-compose -f common.yml -f kafka-cluster.yml up
    ```
3. Initialize Kafka Topics
    ```
    $ docker-compose -f common.yml -f kafka-init.yml up
    ```
4. Go to kafka-manager `localhost:9000`\
   Cluster -> Add cluster\
   Cluster Name: `food-ordering-system-cluster`\
   Cluster Zookeeper Hosts: `zookeeper:2181`
   
