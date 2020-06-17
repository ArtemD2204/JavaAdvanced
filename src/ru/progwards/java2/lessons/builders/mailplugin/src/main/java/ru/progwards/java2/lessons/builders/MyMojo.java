package ru.progwards.java2.lessons.builders;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.Properties;

/**
 * Goal which touches a timestamp file.
 */
@Mojo( name = "touch", defaultPhase = LifecyclePhase.PROCESS_SOURCES )
public class MyMojo
    extends AbstractMojo
{
    /**
     * Location of the file.
     */
    @Parameter( defaultValue = "${project.build.directory}", /*property = "outputDir",*/ required = true )
    private String outputDirectory;
    @Parameter(/*property = "emailTo", */required = true)
    private String emailTo;
    @Parameter(/*property = "emailFrom", */required = true)
    private String emailFrom;
    @Parameter(/*property = "authServ", */required = true)
    private String authServ;
    @Parameter(/*property = "authPass", */required = true)
    private String authPass;

    public void execute() {
        final String password = authPass;
        final String to = emailTo;
        final String from = emailFrom;
        String host = authServ;
        String port = "25"; // TLS: 25, 587;  SSL: 465
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.port", port);

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("Отчёт о сформированном jar с метаинформацией по нему");
            // прикрепляем текст сообщения
            Multipart multipart = new MimeMultipart();
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("Метаинформация по jar\n");
            multipart.addBodyPart(messageBodyPart);
            // прикрепляем файл
            messageBodyPart = new MimeBodyPart();
            String jarName = findJarPath();
            DataSource source = new FileDataSource(jarName);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(jarName);
            multipart.addBodyPart(messageBodyPart);

            message.setContent(multipart);
            Transport.send(message);
        } catch (MessagingException e){
            e.printStackTrace();
        }
    }

    private String findJarPath() {
        Path outputDir = Paths.get(outputDirectory);
        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:*jar-with-dependencies.jar");
        final String[] filename = {null};
        try {
            Files.walkFileTree(outputDir, Collections.emptySet(), 1, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
                    if (pathMatcher.matches(outputDir.relativize(path))) {
                        filename[0] = path.toString();
                    }
                    return FileVisitResult.CONTINUE;
                }
                @Override
                public FileVisitResult visitFileFailed(Path file, IOException e) {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filename[0];
    }
}
