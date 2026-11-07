package com.monthlystairbills.config;

import com.monthlystairbills.model.BillConfig;
import com.monthlystairbills.model.EmailConfig;
import com.monthlystairbills.model.Recipient;
import com.monthlystairbills.util.CatalanNumberWords;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PropertiesLoader {

    private static final String PROPERTIES_FILE = "bills.properties";
    private static final String BILL_NUMBER_KEY = "montly.stair.bill.number";

    private final Path propertiesPath;
    private final Properties props;

    public PropertiesLoader() throws IOException {
        this.propertiesPath = Paths.get(System.getProperty("user.dir"), PROPERTIES_FILE);
        this.props = new Properties();
        load();
    }

    private void load() throws IOException {
        if (!Files.exists(propertiesPath)) {
            throw new FileNotFoundException("No se ha encontrado el fichero " + propertiesPath.toAbsolutePath());
        }
        try (InputStream in = Files.newInputStream(propertiesPath)) {
            props.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        }
    }

    public BillConfig buildConfig() {
        int billNumber = Integer.parseInt(require(BILL_NUMBER_KEY));
        int year       = Integer.parseInt(require("montly.stair.bill.year"));
        int month      = Integer.parseInt(require("montly.stair.bill.month"));
        int day        = Integer.parseInt(require("montly.stair.bill.day"));
        String communityName = require("montly.stair.bill.community.name");
        String communityCity = require("montly.stair.bill.community.city");

        List<Recipient> recipients = parseRecipients(month);

        return new BillConfig(billNumber, year, month, day, communityName, communityCity, recipients);
    }

    private List<Recipient> parseRecipients(int month) {
        String monthName = CatalanNumberWords.monthName(month);
        List<Recipient> result = new ArrayList<>();
        int index = 1;
        while (true) {
            String prefix = "montly.stair.bill.recipient." + index + ".";
            String name = props.getProperty(prefix + "name");
            if (name == null) break;

            String activeStr = props.getProperty(prefix + "active", "false");
            if (!Boolean.parseBoolean(activeStr)) {
                index++;
                continue;
            }

            int floor      = Integer.parseInt(require(prefix + "floor"));
            String email   = require(prefix + "email");
            String concept = require(prefix + "concept").replace("{month}", monthName);
            BigDecimal quantity = new BigDecimal(require(prefix + "quantity.in.euros").replace(',', '.'));

            result.add(new Recipient(name.trim(), floor, email.trim(), concept.trim(), quantity));
            index++;
        }
        return result;
    }

    public EmailConfig buildEmailConfig() {
        String host      = require("spring.mail.host");
        int port         = Integer.parseInt(require("spring.mail.port"));
        String username  = require("spring.mail.username");
        String password  = require("spring.mail.password");
        boolean auth     = Boolean.parseBoolean(props.getProperty("spring.mail.properties.mail.smtp.auth", "true"));
        boolean starttls = Boolean.parseBoolean(props.getProperty("spring.mail.properties.mail.smtp.starttls.enable", "true"));
        String subject   = require("spring.mail.subject");
        String body      = require("spring.mail.body");
        return new EmailConfig(host, port, username, password, auth, starttls, subject, body);
    }

    /** Increments the bill number by {@code count} and persists it to the file. */
    public void incrementBillNumber(int count) throws IOException {
        int current = Integer.parseInt(props.getProperty(BILL_NUMBER_KEY));
        props.setProperty(BILL_NUMBER_KEY, String.valueOf(current + count));
        saveProperties();
    }

    private void saveProperties() throws IOException {
        // Read raw lines to preserve comments and order, only replacing the bill number line
        List<String> lines = Files.readAllLines(propertiesPath, StandardCharsets.UTF_8);
        List<String> updated = new ArrayList<>();
        String newValue = props.getProperty(BILL_NUMBER_KEY);
        for (String line : lines) {
            if (line.startsWith(BILL_NUMBER_KEY + "=")) {
                updated.add(BILL_NUMBER_KEY + "=" + newValue);
            } else {
                updated.add(line);
            }
        }
        Files.write(propertiesPath, updated, StandardCharsets.UTF_8);
    }

    private String require(String key) {
        String value = props.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Propiedad obligatoria no encontrada: " + key);
        }
        return value.trim();
    }
}
