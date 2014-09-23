@echo off
mvn exec:java -Dexec.mainClass="ConverterClient" -Dexec.args="-gui" -e