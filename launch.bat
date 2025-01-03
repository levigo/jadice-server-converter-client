@echo off
mvn exec:java -Dexec.mainClass="ConverterClient" -Dexec.args="-gui" --add-opens=javafx.graphics/javafx.css=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED -e