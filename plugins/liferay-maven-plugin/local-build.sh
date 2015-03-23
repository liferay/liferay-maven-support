#!/bin/bash

mvn clean install -DskipTests
mvn verify
