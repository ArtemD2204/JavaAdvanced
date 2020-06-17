Задача 1:
0) Сначала выполнить Задачу 2
1) Создать новый maven проект
2) Задать: 
   	groupId: ru.progwards.java2.lessons.builders
   	artifactId: Calculator
   	version: 1.0-SNAPSHOT
3) Скопировать в проект папку src и файл pom.xml из ru.progwards.java2.lessons.builders.calculator
4) В файле pom.xml у плагина MailPlugin задать конфигурационные параметры
		<configuration>
                    <emailFrom>aaa@yandex.ru</emailFrom>
                    <emailTo>aaa@yandex.ru</emailTo>
                    <authServ>smtp.yandex.ru</authServ>
                    <authPass>aaa</authPass>
                </configuration>
5) собрать проект

Задача 2:
1) Создать новый maven проект с помощью архетипа maven-archetype-plugin
2) Задать:
	groupId: ru.progwards.java2.lessons.builders
  	artifactId: MailPlugin
  	version: 1.0
3) Скопировать в проект папку src и файл pom.xml из ru.progwards.java2.lessons.builders.mailplugin
4) Собрать проект и поместить упакованный код в локальный репозиторий: install