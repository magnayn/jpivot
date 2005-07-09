rem erzeugt schablone aus *.tld
rem in tld datei muss der DOCTYPE entfernt werden!
rem parameter ist tld-name ohne extension
rem ergebnis ist %1-tags.xml

rem set JAVA_HOME=C:\java\j2sdk1.4.1
rem set XALAN_HOME=C:\java\xalan-j_2_5_D1

java -Duser.language=en -Duser.region=US -jar %XALAN_HOME%\bin\xalan.jar -in %1.tld -xsl tld2tags.xsl -out %1-tags.xml
tidy -wrap 999 -m -i -xml %1-tags.xml

