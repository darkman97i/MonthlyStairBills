package com.monthlystairbills;

import com.monthlystairbills.config.PropertiesLoader;
import com.monthlystairbills.email.EmailSender;
import com.monthlystairbills.model.BillConfig;
import com.monthlystairbills.model.EmailConfig;
import com.monthlystairbills.model.Recipient;
import com.monthlystairbills.pdf.PdfGenerator;

import jakarta.mail.MessagingException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    private static final String BILLS_DIR = "bills";

    public static void main(String[] args) {
        try {
            PropertiesLoader loader = new PropertiesLoader();
            BillConfig config = loader.buildConfig();

            List<Recipient> recipients = config.getRecipients();
            if (recipients.isEmpty()) {
                System.out.println("No active recipients found in bills.properties.");
                return;
            }

            Path billsDir = Paths.get(System.getProperty("user.dir"), BILLS_DIR);
            if (!Files.exists(billsDir)) {
                Files.createDirectories(billsDir);
                System.out.println("Directory created: " + billsDir.toAbsolutePath());
            }

            EmailConfig emailConfig = loader.buildEmailConfig();
            EmailSender emailSender = new EmailSender(emailConfig);
            PdfGenerator generator = new PdfGenerator();

            int currentBillNumber = config.getBillNumber();
            int generated = 0;

            for (Recipient recipient : recipients) {
                String filename = String.format("%d_%02d_%02d_%s.pdf",
                        config.getYear(),
                        config.getMonth(),
                        config.getDay(),
                        recipient.getFilenameName());

                Path outputPath = billsDir.resolve(filename);

                // Generate PDF
                generator.generate(outputPath, currentBillNumber, config, recipient);
                System.out.printf("PDF generated : %s  (Rebut N\u00BA %d / %d)%n",
                        filename, currentBillNumber, config.getYear());

                // Send email
                try {
                    emailSender.send(outputPath, currentBillNumber, config, recipient);
                    System.out.printf("Email sent    : %s%n", recipient.getEmail());
                } catch (MessagingException e) {
                    System.err.printf("Email failed  : %s -> %s%n", recipient.getEmail(), e.getMessage());
                }

                currentBillNumber++;
                generated++;
            }

            loader.incrementBillNumber(generated);
            System.out.printf("%nDone: %d bill(s) generated. Next bill number: %d%n",
                    generated, currentBillNumber);

        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
            System.exit(1);
        } catch (IllegalStateException e) {
            System.err.println("Configuration error: " + e.getMessage());
            System.exit(1);
        }
    }
}
