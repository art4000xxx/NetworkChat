NetworkChat

Чат на Java с TCP и WebSocket. Консольные и браузерные клиенты обмениваются сообщениями в реальном времени.

Что реализовано





TCP-чат: Сервер (порт 8081), консольные клиенты (Client.java), ввод имени, команды (/exit).



WebSocket-чат: Сервер (порт 8082, WebSocketChatServer.java), браузерный интерфейс (http://localhost:8080/chat.html), имена через conn.setAttachment(), сообщения с временем.



HTTP-сервер: Порт 8080, отдаёт chat.html.



Синхронизация: Сообщения между TCP и WebSocket.



Логи: В file.log через Logback.



Фильтр: TCP-сервер блокирует HTTP-запросы.

Технологии





Java 21, Gradle 8.12



org.java-websocket:Java-WebSocket:1.5.4, ch.qos.logback:logback-classic:1.4.14

Запуск





Клонировать: git clone https://github.com/art4000xxx/NetworkChat.git



Собрать: ./gradlew clean build



Сервер: ./gradlew run --args="server"



Браузер: http://localhost:8080/chat.html



Консоль: ./gradlew run --args="client"

Пример логов

2025-04-26 11:39:42 [WebSocketWorker-21] INFO  o.e.networkchat.WebSocketChatServer - WebSocket пользователь art4000 подключился
2025-04-26 11:39:49 [WebSocketWorker-21] INFO  o.e.networkchat.WebSocketChatServer - WebSocket сообщение: [2025-04-26 11:39:49] art4000: привет

Автор

art4000xxx

