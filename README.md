# otus_highload_2024_04

## Домашнее задание 1

для локального запуска проекта в Idea необходимо выполнить следующие действия:

1. Сгенерировать openApi сущности
   ```.\gradlew openApiGenerate```
2. Добавить необходимые env для доступа к базе [application-local.yml](src/main/resources/application-local.yml) к проекту приложен docker-compose.yml с возможностью развернуть базу данных в докере
3. Запустить spring boot приложение с профилем local ```Application.main()```
4. Перейти по ссылке [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) для доступа через swagger
5. Для постмана использовать приложенную коллекцию [postman_collection](otus_highload.postman_collection.json)
