# # /usr/bin/env sh
# export DISPLAY=:0
# rem set javafxexepath=C:\Java\zulu11.50.19-ca-fx-jdk11.0.12-win_x64
export javafxexepath=/home/tk/.sdkman/candidates/java/11.0.22.fx-librca
export JAVA_HOME=$javafxexepath
export PATH=$JAVA_HOME/bin:$PATH
# echo JAVA_HOME=$JAVA_HOME
# echo PATH=$PATH
# rem C:\Java\zulu11.50.19-ca-fx-jdk11.0.12-win_x64\jmods
#rem %JAVA_HOME%\bin\java -version
#rem %JAVA_HOME%\bin\java -cp .\lib\javax.mail.jar;%CLASSPATH% --module-path %javafxexepath% C:\Java\project\javafx\javafxemailclient\lib --add-modules javafx.controls.javafx.fxml,java.mail -jar .\javafxemailclient.jar  com.metait.javafxemailclient.imap.gmail.CountAndListEmailsOfAllGmailMessages
export username=xxxxx.yyyyy
export password="xxxxx"
# echo "uname=$username"
# echo "pwd=$password"
java -cp ./lib/javax.mail.jar:$CLASSPATH  -jar ./javafxemailclient.jar -gui -donotuse_user_home  $username "$password"
#rem java -cp .\lib\javax.mail.jar;%CLASSPATH%  -jar .\javafxemailclient.jar -commandline %upsername% %password%
