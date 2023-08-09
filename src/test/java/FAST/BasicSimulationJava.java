package FAST;

// 2
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Iterator;
import java.time.format.DateTimeFormatter;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import java.util.NoSuchElementException;
import java.util.function.Function;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;

public class BasicSimulationJava extends Simulation {

    private static final Iterator<String> numCaisse = new Iterator<String>() {
        private int i = 1;

        @Override
        public boolean hasNext() {
            return true; // Ou ajustez la condition selon vos besoins
        }

        @Override
        public String next() {
            String formattedValue = String.format("%03d", (i - 1) % 999 + 1);
            i++;
            return formattedValue;
        }
    };

    private static final Iterator<String> numSequence = new Iterator<String>() {
        private int i = 1;

        @Override
        public boolean hasNext() {
            return true; // Ou ajustez la condition selon vos besoins
        }

        @Override
        public String next() {
            String formattedValue = String.format("%03d", (i - 1) % 999 + 1);
            i++;
            return formattedValue;
        }
    };

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private static final LocalDateTime startDateTime = LocalDateTime.parse("2023-04-03T07:00:01");
   // private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private static final Supplier<LocalDateTime> incrementFunction = () -> startDateTime.plusSeconds(1);
    private static final DateTimeIterator dateTimeIterator = new DateTimeIterator(startDateTime, incrementFunction);



    private static final String filePath = "data/file.xml";

    HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://esbpprd1.galerieslafayette.ggl.inet:1609")
            .header("Authorization","Basic c2FsZXNjaGVja2VyOjlONEo9PylrSHlyYThMMw==");

    ScenarioBuilder scn = scenario("FAST API ")
            .exec(flushSessionCookies())
            .exec(flushHttpCache())
            .exec(flushCookieJar())
            .exec(session -> session.set("code_magasin", 3050))
            .exec(session -> {
                System.out.println("code_magasin :" + session("code_magasin").as(String.class));
                return session;
            })
            .exec(session -> {
                String num_caisse = numCaisse.next();
                return session.set("numero_caisse", num_caisse);
            })
            .exec(session -> {
                System.out.println("numero_caisse : " + session("numero_caisse").as(String.class));
                return session;
            })
            .exec(session -> {
                String num_seq = numSequence.next();
                return session.set("numero_sequence", num_seq);
            })
            .exec(session -> {
                System.out.println("numero_sequence : " + session("numero_sequence").as(String.class));
                return session;
            })
            .exec(session -> {
                LocalDateTime nextDateTime = dateTimeIterator.next();
                String formattedNextDateTime = dateTimeFormatter.format(nextDateTime);
                return session.set("nextDateTime", formattedNextDateTime);
            })

            .exec(session -> {
                System.out.println("nextDateTime : " + session("nextDateTime").as(String.class));
                return session;
            })
            .exec(session -> session.set("filePath", filePath))
            .exec(session -> {
                String xmlContenu = session("filePath").as(String.class)
                        .replace("${code_magasin}", session("code_magasin").as(String.class))
                        .replace("${numero_caisse}", session("numero_caisse").as(String.class))
                        .replace("${numero_sequence}", session("numero_sequence").as(String.class))
                        .replace("${nextDateTime}", session("nextDateTime").as(String.class));
                return session.set("xmlContenu", xmlContenu);
            })
            .exec(http("Upload POSLog XML")
                    .post("/ws/fast/tx6")
                    .header("Content-Type", "multipart/form-data")
                    .asMultipartForm()
                    .body(ElFileBody("${filePath}"))

                    .check(status().is(200))
                    .check(
                            bodyString().saveAs("response")
                    ));

    {
        setUp(
                scn.injectOpen(atOnceUsers(1))
        ).protocols(httpProtocol);
    }
}
