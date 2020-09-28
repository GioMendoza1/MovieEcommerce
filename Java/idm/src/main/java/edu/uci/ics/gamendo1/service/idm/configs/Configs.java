package edu.uci.ics.gamendo1.service.idm.configs;

import edu.uci.ics.gamendo1.service.idm.logger.ServiceLogger;
import edu.uci.ics.gamendo1.service.idm.models.ConfigsModel;

public class Configs {
    // Default service configs. These configurations are for use by the Grizzly HTTP server. We can set default params
    // for the HTTP server because the HTTP server does its job independent of the host it is actually running on. That
    // is to say, it doesn't matter what host the HTTP server runs on. By setting default values, we are assured that
    // even if there is a critical failure in reading a configuration file or assigning custom parameters, the service
    // will always be able to revert to a pre-determined, working state. You should only revert to using defaults if
    // there is some critical error with your application and you need to return it to a working state.
    private final String DEFAULT_SCHEME = "http://";
    private final String DEFAULT_HOSTNAME = "0.0.0.0";
    private final int    DEFAULT_PORT = 6243;
    private final String DEFAULT_PATH = "/api/basicService";
    // Default logger configs
    private final String DEFAULT_OUTPUTDIR = "./logs/";
    private final String DEFAULT_OUTPUTFILE = "basicService.log";

    // Service configs
    private String scheme;
    private String hostName;
    private int    port;
    private String path;

    // Logger configs
    private String outputDir;
    private String outputFile;


    // Database configs
    private String dbUsername;
    private String dbPassword;
    private String dbHostname;
    private int dbPort;
    private String dbDriver;
    private String dbName;
    private String dbSettings;

    // Session configs
    private long timeout;
    private long expiration;

    public Configs() {
        scheme = DEFAULT_SCHEME;
        hostName = DEFAULT_HOSTNAME;
        port = DEFAULT_PORT;
        path = DEFAULT_PATH;
        outputDir = DEFAULT_OUTPUTDIR;
        outputFile = DEFAULT_OUTPUTFILE;
    }

    /*
        If a configuration file is located, then each key within that config file must now be retrieved from it's
        corresponding Map<String,String> within the CongfigsModel, and assigned to a variable for use in other places
        of your microservice. Extensive error checking in this process is very important. Note that all data members
        are private, with only getters supplied. This is because we do not want any outside class to be able to change
        the configurations loaded from the file once they exists in our application.
     */
    public Configs(ConfigsModel cm) throws NullPointerException {
        if (cm == null) {
            throw new NullPointerException("Unable to create Configs from ConfigsModel.");
        } else {

            // Set service configs
            scheme = cm.getServiceConfig().get("scheme");
            if (scheme == null) {
                scheme = DEFAULT_SCHEME;
                System.err.println("Scheme not found in configuration file. Using default.");
            } else {
                System.err.println("Scheme: " + scheme);
            }

            hostName = cm.getServiceConfig().get("hostName");
            if (hostName == null) {
                hostName = DEFAULT_HOSTNAME;
                System.err.println("Hostname not found in configuration file. Using default.");
            } else {
                System.err.println("Hostname: " + hostName);
            }

            port = Integer.parseInt(cm.getServiceConfig().get("port"));
            if (port == 0) {
                port = DEFAULT_PORT;
                System.err.println("Port not found in configuration file. Using default.");
            } else if (port < 1024 || port > 65536) {
                port = DEFAULT_PORT;
                System.err.println("Port is not within valid range. Using default.");
            } else {
                System.err.println("Port: " + port);
            }

            path = cm.getServiceConfig().get("path");
            if (path == null) {
                path = DEFAULT_PATH;
                System.err.println("Path not found in configuration file. Using default.");
            } else {
                System.err.println("Path: " + path);
            }

            // Set logger configs
            outputDir = cm.getLoggerConfig().get("outputDir");
            if (outputDir == null) {
                outputDir = DEFAULT_OUTPUTDIR;
                System.err.println("Logging output directory not found in configuration file. Using default.");
            } else {
                System.err.println("Logging output directory: " + outputDir);
            }

            outputFile = cm.getLoggerConfig().get("outputFile");
            if (outputFile == null) {
                outputFile = DEFAULT_OUTPUTFILE;
                System.err.println("Logging output file not found in configuration file. Using default.");
            } else {
                System.err.println("Logging output file: " + outputFile);
            }

            dbUsername = cm.getDatabaseConfig().get("dbUsername");
            if (dbUsername == null)
            {
                System.err.println("Logging dbUsername not found");
            }
            else
            {
                System.err.println("Logging dbUsername: " + dbUsername);
            }

            dbPassword = cm.getDatabaseConfig().get("dbPassword");
            if (dbPassword == null)
            {
                System.err.println("Logging dbPassword not found");
            }
            else
            {
                System.err.println("Logging dbPassword: " + dbPassword);
            }

            dbHostname = cm.getDatabaseConfig().get("dbHostname");
            if (dbHostname == null)
            {
                System.err.println("Logging dbHostname not found");
            }
            else
            {
                System.err.println("Logging dbHostname: " + dbHostname);
            }

            dbPort = Integer.parseInt(cm.getDatabaseConfig().get("dbPort"));
            if (dbPort == 0)
            {
                System.err.println("Logging dbPort not found");
            }
            else
            {
                System.err.println("Logging dbPort: " + dbPort);
            }

            dbDriver = cm.getDatabaseConfig().get("dbDriver");
            if (dbDriver == null)
            {
                System.err.println("Logging dbDriver not found");
            }
            else
            {
                System.err.println("Logging dbDriver: " + dbDriver);
            }

            dbName = cm.getDatabaseConfig().get("dbName");
            if (dbName == null)
            {
                System.err.println("Logging dbName not found");
            }
            else
            {
                System.err.println("Logging dbName: " + dbName);
            }

            dbSettings = cm.getDatabaseConfig().get("dbSettings");
            if (dbSettings == null)
            {
                System.err.println("Logging dbSettings not found");
            }
            else
            {
                System.err.println("Logging dbSettings: " + dbSettings);
            }

            timeout = Long.parseLong(cm.getSessionConfig().get("timeout"));
            if (timeout == 0)
            {
                System.err.println("Logging timeout not found");
            }
            else
            {
                System.err.println("Logging timeout: " + timeout);
            }

            expiration = Long.parseLong(cm.getSessionConfig().get("expiration"));
            if (expiration == 0)
            {
                System.err.println("Logging expiration not found");
            }
            else
            {
                System.err.println("Logging expiration: " + expiration);
            }
        }
    }

    public void currentConfigs() {
        ServiceLogger.LOGGER.config("Scheme: " + scheme);
        ServiceLogger.LOGGER.config("Hostname: " + hostName);
        ServiceLogger.LOGGER.config("Port: " + port);
        ServiceLogger.LOGGER.config("Path: " + path);
        ServiceLogger.LOGGER.config("Logger output directory: " + outputDir);
        ServiceLogger.LOGGER.config("Logger output file: " + outputFile);
    }

    public String getScheme() {
        return scheme;
    }

    public String getHostName() {
        return hostName;
    }

    public int getPort() {
        return port;
    }

    public String getPath() {
        return path;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public String getDbHostname() {
        return dbHostname;
    }

    public int getDbPort() {
        return dbPort;
    }

    public String getDbDriver() {
        return dbDriver;
    }

    public String getDbName() {
        return dbName;
    }

    public String getDbSettings() {
        return dbSettings;
    }

    public long getTimeout() {
        return timeout;
    }

    public long getExpiration() {
        return expiration;
    }
}