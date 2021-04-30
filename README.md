# GitHub source connector to Kafka

Welcome to your new GitHub Kafka Connect source created using the archtype [https://github.com/jcustenborder/kafka-connect-archtype](https://github.com/jcustenborder/kafka-connect-archtype).

## Package the project
```
mvn clean package
cd target
tar -xvf kafka-connector-github-source-1.0-SNAPSHOT.tar.gz
$ ll
-rw-rw-r--  1 felipe felipe 10430300 Apr 30 11:08 kafka-connector-github-source-1.0-SNAPSHOT.tar.gz
drwxrwxr-x  3 felipe felipe     4096 Apr 30 11:10 etc/
drwxrwxr-x  3 felipe felipe     4096 Apr 30 11:10 usr/
...
```
Configure the GitHub Source Kafka source connector on the confluent platform
```
vi confluent-6.1.1/etc/schema-registry/connect-avro-distributed.properties
plugin.path=share/java,/home/felipe/workspace-idea/kafka-connector-github-source/
```
## Running
Start the Confluent platform `confluent local services start` and access it on [http://127.0.0.1:9021/](http://127.0.0.1:9021/) and create a GitHub source connector using:
```
{
  "name": "GitHubSourceConnectorConnectorDemo",
  "connector.class": "com.github.felipegutierrez.kafka.connector.github.GitHubSourceConnector",
  "tasks.max": "1",
  "key.converter": "org.apache.kafka.connect.json.JsonConverter",
  "value.converter": "org.apache.kafka.connect.json.JsonConverter",
  "topic": "github-issues",
  "github.owner": "kubernetes",
  "github.repo": "kubernetes",
  "batch.size": "2",
  "since.timestamp": "2021-01-01T00:00:00Z",
  "auth.username": "USERNAME",
  "auth.password": "PASSWORD"
}
```
Consume data using:
```
kafka-console-consumer --bootstrap-server localhost:9092 --from-beginning --topic github-issues | jq
```


### Rest API:
Examples are taken from here: http://docs.confluent.io/3.2.0/connect/managing.html#common-rest-examples . Replace 127.0.0.1 by 192.168.99.100 if you're using docker toolbox
- Get Worker information
  `curl -s 127.0.0.1:8083/ | jq`
- List Connectors available on a Worker
  `curl -s 127.0.0.1:8083/connector-plugins | jq`
- Ask about Active Connectors
  `curl -s 127.0.0.1:8083/connectors | jq`
  Get information about a Connector Tasks and Config
  `curl -s 127.0.0.1:8083/connectors/source-twitter-distributed/tasks | jq`
- Get Connector Status
  `curl -s 127.0.0.1:8083/connectors/file-stream-demo-distributed/status | jq`
- Pause / Resume a Connector (no response if the call is successful)
  `curl -s -X PUT 127.0.0.1:8083/connectors/file-stream-demo-distributed/pause`
  `curl -s -X PUT 127.0.0.1:8083/connectors/file-stream-demo-distributed/resume`
- Get Connector Configuration
  `curl -s 127.0.0.1:8083/connectors/file-stream-demo-distributed | jq`
- Delete our Connector
  `curl -s -X DELETE 127.0.0.1:8083/connectors/file-stream-demo-distributed`
- Create a new Connector
  `curl -s -X POST -H "Content-Type: application/json" --data '{"name": "file-stream-demo-distributed", "config":{"connector.class":"org.apache.kafka.connect.file.FileStreamSourceConnector","key.converter.schemas.enable":"true","file":"demo-file.txt","tasks.max":"1","value.converter.schemas.enable":"true","name":"file-stream-demo-distributed","topic":"demo-2-distributed","value.converter":"org.apache.kafka.connect.json.JsonConverter","key.converter":"org.apache.kafka.connect.json.JsonConverter"}}' http://127.0.0.1:8083/connectors | jq`
  `curl -s -X POST -H "Content-Type: application/json" --data '{"name": "GitHubSourceConnectorDemo", "config":{"connector.class":"com.github.felipegutierrez.kafka.connector.connectors.GitHubSourceConnector","key.converter.schemas.enable":"true","tasks.max":"1","value.converter.schemas.enable":"true","topic":"github-issues","value.converter":"org.apache.kafka.connect.json.JsonConverter","key.converter":"org.apache.kafka.connect.json.JsonConverter"}}' http://127.0.0.1:8083/connectors | jq`
- Update Connector configuration
  `curl -s -X PUT -H "Content-Type: application/json" --data '{"connector.class":"org.apache.kafka.connect.file.FileStreamSourceConnector","key.converter.schemas.enable":"true","file":"demo-file.txt","tasks.max":"2","value.converter.schemas.enable":"true","name":"file-stream-demo-distributed","topic":"demo-2-distributed","value.converter":"org.apache.kafka.connect.json.JsonConverter","key.converter":"org.apache.kafka.connect.json.JsonConverter"}' 127.0.0.1:8083/connectors/file-stream-demo-distributed/config | jq`


### References:
- [https://www.confluent.io/blog/using-ksql-to-analyse-query-and-transform-data-in-kafka/](https://www.confluent.io/blog/using-ksql-to-analyse-query-and-transform-data-in-kafka/)
- [https://github.com/riferrei/kafka-source-connector](https://github.com/riferrei/kafka-source-connector)
- [https://github.com/jcustenborder/kafka-connect-archtype](https://github.com/jcustenborder/kafka-connect-archtype)
- [https://www.confluent.io/blog/create-dynamic-kafka-connect-source-connectors/](https://www.confluent.io/blog/create-dynamic-kafka-connect-source-connectors/)
- [https://medium.com/@gowdasunil15/building-custom-connector-for-kafka-connect-c163a7ed84c2](https://medium.com/@gowdasunil15/building-custom-connector-for-kafka-connect-c163a7ed84c2)
- [https://docs.confluent.io/home/connect/userguide.html](https://docs.confluent.io/home/connect/userguide.html)
- [https://docs.github.com/en/rest/reference/issues#list-repository-issues](https://docs.github.com/en/rest/reference/issues#list-repository-issues)
- [https://api.github.com/repos/kubernetes/kubernetes/issues](https://api.github.com/repos/kubernetes/kubernetes/issues)
- [https://www.confluent.io/blog/using-ksql-to-analyse-query-and-transform-data-in-kafka/](https://www.confluent.io/blog/using-ksql-to-analyse-query-and-transform-data-in-kafka/)
- [https://github.com/jcustenborder/kafka-connect-twitter](https://github.com/jcustenborder/kafka-connect-twitter)


