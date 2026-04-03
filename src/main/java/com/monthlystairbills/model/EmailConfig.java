package com.monthlystairbills.model;

public class EmailConfig {

    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final boolean auth;
    private final boolean starttls;
    private final String subjectTemplate;
    private final String bodyTemplate;

    public EmailConfig(String host, int port, String username, String password,
                       boolean auth, boolean starttls, String subjectTemplate, String bodyTemplate) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.auth = auth;
        this.starttls = starttls;
        this.subjectTemplate = subjectTemplate;
        this.bodyTemplate = bodyTemplate;
    }

    public String getHost()             { return host; }
    public int getPort()                { return port; }
    public String getUsername()         { return username; }
    public String getPassword()         { return password; }
    public boolean isAuth()             { return auth; }
    public boolean isStarttls()         { return starttls; }
    public String getSubjectTemplate()  { return subjectTemplate; }
    public String getBodyTemplate()     { return bodyTemplate; }
}
