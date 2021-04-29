# Introduction

Welcome to your new Kafka Connect plugin!

# Running in development


The [docker-compose.yml](docker-compose.yml) that is included in this repository is based on the Confluent Platform Docker
images. Take a look at the [quickstart](http://docs.confluent.io/current/cp-docker-images/docs/quickstart.html#getting-started-with-docker-client)
for the Docker images. 

Your development workstation needs to be able to resolve the hostnames that are listed in the `docker-compose.yml` 
file in the root of this repository. If you are using [Docker for Mac](https://docs.docker.com/v17.12/docker-for-mac/install/)
your containers will be available at the ip address `127.0.0.1`. If you are running docker-machine
you will need to determine the ip address of the virtual machine with `docker-machine ip confluent`
to determine the ip address.

```
127.0.0.1 zookeeper
127.0.0.1 kafka
127.0.0.1 schema-registry
```


Start the Confluent platform: `confluent local services start`
Package the project: `mvn clean package`

```
docker-compose up -d
vi confluent-6.1.1/etc/schema-registry/connect-avro-distributed.properties
plugin.path=share/java,/home/felipe/workspace-idea/kafka-connect-twitter/,/home/felipe/confluent-6.1.1/share/confluent-hub-components,/home/felipe/workspace-idea/kafka-connector-github-source/

cp config/GithubSourceConnector.properties /tmp/GithubSourceConnector.properties
vi /tmp/GithubSourceConnector.properties
connect-distributed config/worker.properties config/GithubSourceConnector.properties
```


The debug script assumes that `connect-standalone` is in the path on your local workstation. Download 
the latest version of the [Kafka](https://www.confluent.io/download/) to get started.


Start the connector with debugging enabled.
 
```
./bin/debug.sh
```

Start the connector with debugging enabled. This will wait for a debugger to attach.

```
export SUSPEND='y'
./bin/debug.sh
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
- Pause / Resume a Connector (no response if the call is succesful)
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

