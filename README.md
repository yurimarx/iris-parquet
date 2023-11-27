 [![Gitter](https://img.shields.io/badge/Available%20on-Intersystems%20Open%20Exchange-00b2a9.svg)](https://openexchange.intersystems.com/package/iris-parquet)
 [![Quality Gate Status](https://community.objectscriptquality.com/api/project_badges/measure?project=intersystems_iris_community%2Fjirisreport&metric=alert_status)](https://community.objectscriptquality.com/dashboard?id=intersystems_iris_community%2Firis-parquet)
 [![Reliability Rating](https://community.objectscriptquality.com/api/project_badges/measure?project=intersystems_iris_community%2Fjirisreport&metric=reliability_rating)](https://community.objectscriptquality.com/dashboard?id=intersystems_iris_community%2Firis-parquet)

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg?style=flat&logo=AdGuard)](LICENSE)
# Iris-Parquet
This is a tool to generate parquet files from IRIS data or load parquet data on IRIS data.

## Description
The Iris-Parquet allows you:
* Generate parquet files from IRIS SQL instruction
* Generate JSON from Parquet file to allow you save it on IRIS SQL tables or JSON Documents


## Prerequisites
* HADOOP_HOME configured to Hadoop folder

## Installation with Docker

Clone/git pull the repo into any local directory

```
$ git clone https://github.com/yurimarx/iris-parquet.git
```

Open the terminal in this directory and call the command to build and run InterSystems IRIS in container:
*Note: Users running containers on a Linux CLI, should use "docker compose" instead of "docker-compose"*
*See [Install the Compose plugin](https://docs.docker.com/compose/install/linux/)*


```
$ docker-compose build
$ docker-compose up -d
```

## Installation with ZPM

```
USER> zpm install iris-parquet
```

Install hadoop files and set ENV variable to HADOOP_HOME:

```
wget https://dlcdn.apache.org/hadoop/common/hadoop-3.3.6/hadoop-3.3.6.tar.gz && \
    tar -xzf hadoop-3.3.6.tar.gz && \
    echo "export HADOOP_HOME=/<unzipped folder>/hadoop-3.3.6"
```

## Testing
1. Open the file IRISParquet.postman_collection.json (or download from [iris parquet postman](https://github.com/yurimarx/iris-parquet/raw/main/IRISParquet.postman_collection.json)) 
2. Set the variables server (iris webserver host) and port (iris webserver port) on Variables tab of the collection
3. Run the method /generate-persons one or more to generate sample person fake data
4. Run the method /sql2parquet with this query on body: select * from dc_irisparquet.SamplePerson
5. Download the parquet file on the link Download file
6. Run the method /parquet2json to the parquet file generated on the past step and the results
7. You can also open the Parquet file on VSCode (install the parquet-viewer extension to see the parquet content from VSCode - https://marketplace.visualstudio.com/items?itemName=dvirtz.parquet-viewer)