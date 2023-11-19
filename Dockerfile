ARG IMAGE=intersystemsdc/irishealth-community:2020.3.0.200.0-zpm
ARG IMAGE=intersystemsdc/iris-community:2020.4.0.547.0-zpm
ARG IMAGE=containers.intersystems.com/intersystems/iris:2021.1.0.215.0
ARG IMAGE=intersystemsdc/irishealth-community
ARG IMAGE=intersystemsdc/iris-community
ARG IMAGE=intersystemsdc/iris-community:preview
FROM $IMAGE as builder


WORKDIR /home/irisowner/dev

ARG TESTS=0
ARG MODULE="dc-sample"
ARG NAMESPACE="IRISAPP"

## Embedded Python environment
ENV IRISUSERNAME "_SYSTEM"
ENV IRISPASSWORD "SYS"
ENV IRISNAMESPACE $NAMESPACE
ENV PYTHON_PATH=/usr/irissys/bin/
ENV PATH "/usr/irissys/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/home/irisowner/bin"

RUN --mount=type=bind,src=.,dst=. \
    pip3 install -r requirements.txt && \
    iris start IRIS && \
	iris session IRIS < iris.script && \
    ([ $TESTS -eq 0 ] || iris session iris -U $NAMESPACE "##class(%ZPM.PackageManager).Shell(\"test $MODULE -v -only\",1,1)") && \
    iris stop IRIS quietly


FROM $IMAGE as final

#USER root

USER ${ISC_PACKAGE_MGRUSER}

RUN wget https://dlcdn.apache.org/hadoop/common/hadoop-3.3.6/hadoop-3.3.6.tar.gz && \
    tar -xzf hadoop-3.3.6.tar.gz && \
    echo "export HADOOP_HOME=/opt/irisapp/hadoop-3.3.6"

COPY iris-parquet.jar /usr/irissys/bin/

ADD --chown=${ISC_PACKAGE_MGRUSER}:${ISC_PACKAGE_IRISGROUP} https://github.com/grongierisc/iris-docker-multi-stage-script/releases/latest/download/copy-data.py /home/irisowner/dev/copy-data.py
#ADD https://github.com/grongierisc/iris-docker-multi-stage-script/releases/latest/download/copy-data.py /home/irisowner/dev/copy-data.py

RUN --mount=type=bind,source=/,target=/builder/root,from=builder \
    cp -f /builder/root/usr/irissys/iris.cpf /usr/irissys/iris.cpf && \
    python3 /home/irisowner/dev/copy-data.py -c /usr/irissys/iris.cpf -d /builder/root/