@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  edu.uci.ics.gamendo1.service.api_gateway startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and EDU_UCI_ICS_GAMENDO1_SERVICE_API_GATEWAY_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\edu.uci.ics.gamendo1.service.api_gateway.jar;%APP_HOME%\lib\project-2.28.pom;%APP_HOME%\lib\jersey-container-grizzly2-http-2.28.jar;%APP_HOME%\lib\jersey-server-2.28.jar;%APP_HOME%\lib\jersey-client-2.28.jar;%APP_HOME%\lib\jersey-media-jaxb-2.28.jar;%APP_HOME%\lib\jersey-hk2-2.28.jar;%APP_HOME%\lib\jersey-media-json-jackson-2.28.jar;%APP_HOME%\lib\jersey-common-2.28.jar;%APP_HOME%\lib\jersey-entity-filtering-2.28.jar;%APP_HOME%\lib\grizzly-http-servlet-2.4.4.jar;%APP_HOME%\lib\grizzly-http-server-2.4.4.jar;%APP_HOME%\lib\jackson-jaxrs-json-provider-2.9.8.jar;%APP_HOME%\lib\jackson-dataformat-yaml-2.9.8.jar;%APP_HOME%\lib\javax.ws.rs-api-2.1.1.jar;%APP_HOME%\lib\commons-codec-1.12.jar;%APP_HOME%\lib\mysql-connector-java-8.0.15.jar;%APP_HOME%\lib\guava-27.0.1-jre.jar;%APP_HOME%\lib\spring-boot-starter-data-jpa-2.2.2.RELEASE.jar;%APP_HOME%\lib\spring-boot-starter-data-rest-2.2.2.RELEASE.jar;%APP_HOME%\lib\spring-boot-starter-web-2.2.2.RELEASE.jar;%APP_HOME%\lib\spring-boot-starter-aop-2.2.2.RELEASE.jar;%APP_HOME%\lib\jakarta.ws.rs-api-2.1.6.jar;%APP_HOME%\lib\hk2-locator-2.5.0.jar;%APP_HOME%\lib\spring-boot-starter-jdbc-2.2.2.RELEASE.jar;%APP_HOME%\lib\spring-boot-starter-json-2.2.2.RELEASE.jar;%APP_HOME%\lib\spring-boot-starter-validation-2.2.2.RELEASE.jar;%APP_HOME%\lib\spring-boot-starter-2.2.2.RELEASE.jar;%APP_HOME%\lib\spring-boot-starter-tomcat-2.2.2.RELEASE.jar;%APP_HOME%\lib\hk2-api-2.5.0.jar;%APP_HOME%\lib\hk2-utils-2.5.0.jar;%APP_HOME%\lib\jakarta.annotation-api-1.3.5.jar;%APP_HOME%\lib\jakarta.inject-2.5.0.jar;%APP_HOME%\lib\validation-api-2.0.1.Final.jar;%APP_HOME%\lib\osgi-resource-locator-1.0.1.jar;%APP_HOME%\lib\jackson-module-jaxb-annotations-2.10.1.jar;%APP_HOME%\lib\jackson-jaxrs-base-2.10.1.jar;%APP_HOME%\lib\spring-data-rest-webmvc-3.2.3.RELEASE.jar;%APP_HOME%\lib\spring-data-rest-core-3.2.3.RELEASE.jar;%APP_HOME%\lib\jackson-datatype-jdk8-2.10.1.jar;%APP_HOME%\lib\jackson-datatype-jsr310-2.10.1.jar;%APP_HOME%\lib\jackson-module-parameter-names-2.10.1.jar;%APP_HOME%\lib\jackson-databind-2.10.1.jar;%APP_HOME%\lib\jackson-annotations-2.10.1.jar;%APP_HOME%\lib\grizzly-http-2.4.4.jar;%APP_HOME%\lib\snakeyaml-1.25.jar;%APP_HOME%\lib\jackson-core-2.10.1.jar;%APP_HOME%\lib\failureaccess-1.0.1.jar;%APP_HOME%\lib\listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar;%APP_HOME%\lib\jsr305-3.0.2.jar;%APP_HOME%\lib\checker-qual-2.5.2.jar;%APP_HOME%\lib\error_prone_annotations-2.2.0.jar;%APP_HOME%\lib\j2objc-annotations-1.1.jar;%APP_HOME%\lib\animal-sniffer-annotations-1.17.jar;%APP_HOME%\lib\hibernate-core-5.4.9.Final.jar;%APP_HOME%\lib\jaxb-runtime-2.3.2.jar;%APP_HOME%\lib\stax-ex-1.8.1.jar;%APP_HOME%\lib\jakarta.xml.bind-api-2.3.2.jar;%APP_HOME%\lib\istack-commons-runtime-3.0.8.jar;%APP_HOME%\lib\jakarta.activation-api-1.2.1.jar;%APP_HOME%\lib\jakarta.persistence-api-2.2.3.jar;%APP_HOME%\lib\jakarta.transaction-api-1.3.3.jar;%APP_HOME%\lib\spring-data-jpa-2.2.3.RELEASE.jar;%APP_HOME%\lib\spring-aspects-5.2.2.RELEASE.jar;%APP_HOME%\lib\spring-webmvc-5.2.2.RELEASE.jar;%APP_HOME%\lib\spring-hateoas-1.0.2.RELEASE.jar;%APP_HOME%\lib\spring-web-5.2.2.RELEASE.jar;%APP_HOME%\lib\spring-boot-autoconfigure-2.2.2.RELEASE.jar;%APP_HOME%\lib\spring-boot-2.2.2.RELEASE.jar;%APP_HOME%\lib\spring-plugin-core-2.0.0.RELEASE.jar;%APP_HOME%\lib\spring-context-5.2.2.RELEASE.jar;%APP_HOME%\lib\spring-aop-5.2.2.RELEASE.jar;%APP_HOME%\lib\aspectjweaver-1.9.5.jar;%APP_HOME%\lib\aopalliance-repackaged-2.5.0.jar;%APP_HOME%\lib\javassist-3.24.0-GA.jar;%APP_HOME%\lib\grizzly-framework-2.4.4.jar;%APP_HOME%\lib\HikariCP-3.4.1.jar;%APP_HOME%\lib\spring-orm-5.2.2.RELEASE.jar;%APP_HOME%\lib\spring-jdbc-5.2.2.RELEASE.jar;%APP_HOME%\lib\hibernate-commons-annotations-5.1.0.Final.jar;%APP_HOME%\lib\hibernate-validator-6.0.18.Final.jar;%APP_HOME%\lib\jboss-logging-3.4.1.Final.jar;%APP_HOME%\lib\byte-buddy-1.10.4.jar;%APP_HOME%\lib\antlr-2.7.7.jar;%APP_HOME%\lib\jandex-2.1.1.Final.jar;%APP_HOME%\lib\classmate-1.5.1.jar;%APP_HOME%\lib\dom4j-2.1.1.jar;%APP_HOME%\lib\spring-data-commons-2.2.3.RELEASE.jar;%APP_HOME%\lib\spring-tx-5.2.2.RELEASE.jar;%APP_HOME%\lib\spring-beans-5.2.2.RELEASE.jar;%APP_HOME%\lib\spring-expression-5.2.2.RELEASE.jar;%APP_HOME%\lib\spring-core-5.2.2.RELEASE.jar;%APP_HOME%\lib\spring-boot-starter-logging-2.2.2.RELEASE.jar;%APP_HOME%\lib\logback-classic-1.2.3.jar;%APP_HOME%\lib\log4j-to-slf4j-2.12.1.jar;%APP_HOME%\lib\jul-to-slf4j-1.7.29.jar;%APP_HOME%\lib\json-path-2.4.0.jar;%APP_HOME%\lib\slf4j-api-1.7.29.jar;%APP_HOME%\lib\tomcat-embed-websocket-9.0.29.jar;%APP_HOME%\lib\tomcat-embed-core-9.0.29.jar;%APP_HOME%\lib\tomcat-embed-el-9.0.29.jar;%APP_HOME%\lib\jakarta.validation-api-2.0.1.jar;%APP_HOME%\lib\txw2-2.3.2.jar;%APP_HOME%\lib\FastInfoset-1.2.16.jar;%APP_HOME%\lib\spring-jcl-5.2.2.RELEASE.jar;%APP_HOME%\lib\evo-inflector-1.2.2.jar;%APP_HOME%\lib\logback-core-1.2.3.jar;%APP_HOME%\lib\log4j-api-2.12.1.jar;%APP_HOME%\lib\json-smart-2.3.jar;%APP_HOME%\lib\accessors-smart-1.2.jar;%APP_HOME%\lib\asm-5.0.4.jar

@rem Execute edu.uci.ics.gamendo1.service.api_gateway
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %EDU_UCI_ICS_GAMENDO1_SERVICE_API_GATEWAY_OPTS%  -classpath "%CLASSPATH%" edu.uci.ics.gamendo1.service.api_gateway.GatewayService %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable EDU_UCI_ICS_GAMENDO1_SERVICE_API_GATEWAY_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%EDU_UCI_ICS_GAMENDO1_SERVICE_API_GATEWAY_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
