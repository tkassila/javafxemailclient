@echo off
rem set javafxexepath=C:\Java\zulu11.50.19-ca-fx-jdk11.0.12-win_x64
set javafxexepath=C:\Java\zulu11.50.19-ca-fx-jdk11.0.12-win_x64
set JAVA_HOME=%javafxexepath%
set PATH=%JAVA_HOME%\bin;%PATH%
echo JAVA_HOME=%JAVA_HOME%
echo PATH=%PATH%
rem C:\Java\zulu11.50.19-ca-fx-jdk11.0.12-win_x64\jmods
rem %JAVA_HOME%\bin\java -version
rem %JAVA_HOME%\bin\java -cp .\lib\javax.mail.jar;%CLASSPATH% --module-path %javafxexepath% C:\Java\project\javafx\javafxemailclient\lib --add-modules javafx.controls.javafx.fxml,java.mail -jar .\javafxemailclient.jar  com.metait.javafxemailclient.imap.gmail.CountAndListEmailsOfAllGmailMessages
set upsername=
set password=

java -cp .\lib\javax.mail.jar;%CLASSPATH%  -jar .\javafxemailclient.jar -gui %upsername% %password%
rem java -cp .\lib\javax.mail.jar;%CLASSPATH%  -jar .\javafxemailclient.jar -commandline %upsername% %password%