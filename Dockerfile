# Copyright 2024 Paion Data
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
FROM ubuntu:22.04

LABEL maintainer="Wentao (Peter) Pan"
LABEL maintainer-email="Doom9527@gmail.com"

ARG WS_VERSION=1.0-SNAPSHOT

ENV JAVA_HOME /usr/lib/jvm/java-17-openjdk-amd64

RUN apt update && \
    apt upgrade -y && \
    apt install -y software-properties-common wget openjdk-17-jdk

COPY ./target/Aristotle-$WS_VERSION.jar /tmp/app.jar

ENTRYPOINT ["java", "-jar", "/tmp/app.jar"]
