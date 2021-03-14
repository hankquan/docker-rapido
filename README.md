# docker-rapido
## Overview
Docker-rapido is a container compose engine like docker-compose. Most part of its yaml template are inherited from docker-compose.  
It provide remotely docker image build functionality and rolling update for a service on a single docker server.

## Yaml template
```yaml
version: 1.0
delivery_type: official
owner: master
remote_docker: tcp://<remote docker host>
repository:
  url: <repo url>
  username: xxx
  password: xxx
services:
  vmm:
    image: <image name>
    build: <dockerfile relative path>
    ports:
    - 8080:8080
    network: bridge
    extra_hosts:
    - <host name>:<host ip>
    volumes:
    - <local storage>:<container storage>
    environment:
    - <env name>:<env value>
    links:
    - <host name>:<container name>
    commands:
    - "cmd param1"
    - "cmd param2"
    depends_on:
    - <dependent container name>
    deploy:
      deploy_policy: rolling-update #force-update,on-absent
      placement:
        constraints:
        - node.name == node1
      replicas: 1
      healthcheck:
        disable: true
      servicecheck:
        disable: false
        uri: <health check uri>
nodes:
  node1:
    ip: <remote docker ip>
    docker_port: <docker remote port>
```

## Usage
```shell
java -jar docker-rapido.jar -t template.yaml
```
