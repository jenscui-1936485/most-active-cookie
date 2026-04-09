import org.example.MostActiveCookie;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MostActiveCookieTest {

    @Test
    void returnsSingleMostActiveCookie() throws IOException {
        Path tempFile = Files.createTempFile("cookie_log", ".csv");
        Files.writeString(tempFile,
                "cookie,timestamp\n" +
                        "AtY0laUfhglK3lC7,2018-12-09T14:19:00+00:00\n" +
                        "SAZuXPGUrfbcn5UA,2018-12-09T10:13:00+00:00\n" +
                        "5UAVanZf6UtGyKVS,2018-12-09T07:25:00+00:00\n" +
                        "AtY0laUfhglK3lC7,2018-12-09T06:19:00+00:00\n" +
                        "SAZuXPGUrfbcn5UA,2018-12-08T22:03:00+00:00\n"
        );

        List<String> result = MostActiveCookie.findMostActiveCookies(
                tempFile.toString(),
                LocalDate.parse("2018-12-09")
        );

        assertEquals(List.of("AtY0laUfhglK3lC7"), result);
    }

    @Test
    void returnsAllCookiesWhenThereIsATieInMostRecentOrder() throws IOException {
        Path tempFile = Files.createTempFile("cookie_log", ".csv");
        Files.writeString(tempFile,
                "cookie,timestamp\n" +
                        "A,2018-12-08T10:00:00+00:00\n" +
                        "B,2018-12-08T09:00:00+00:00\n" +
                        "C,2018-12-08T08:00:00+00:00\n" +
                        "D,2018-12-07T08:00:00+00:00\n"
        );

        List<String> result = MostActiveCookie.findMostActiveCookies(
                tempFile.toString(),
                LocalDate.parse("2018-12-08")
        );

        assertEquals(List.of("A", "B", "C"), result);
    }

    @Test
    void returnsEmptyListWhenFileHasOnlyHeader() throws IOException {
        Path tempFile = Files.createTempFile("cookie_log", ".csv");
        Files.writeString(tempFile, "cookie,timestamp\n");

        List<String> result = MostActiveCookie.findMostActiveCookies(
                tempFile.toString(),
                LocalDate.parse("2018-12-09")
        );

        assertEquals(List.of(), result);
    }

    @Test
    void returnsEmptyListWhenTargetDateDoesNotExistInFile() throws IOException {
        Path tempFile = Files.createTempFile("cookie_log", ".csv");
        Files.writeString(tempFile,
                "cookie,timestamp\n" +
                        "A,2018-12-09T10:00:00+00:00\n" +
                        "B,2018-12-09T09:00:00+00:00\n" +
                        "C,2018-12-07T08:00:00+00:00\n"
        );

        List<String> result = MostActiveCookie.findMostActiveCookies(
                tempFile.toString(),
                LocalDate.parse("2018-12-08")
        );

        assertEquals(List.of(), result);
    }

    @Test
    void ignoresDatesAfterTargetDateAndCountsOnlyTargetDate() throws IOException {
        Path tempFile = Files.createTempFile("cookie_log", ".csv");
        Files.writeString(tempFile,
                "cookie,timestamp\n" +
                        "X,2018-12-10T11:00:00+00:00\n" +
                        "A,2018-12-09T10:00:00+00:00\n" +
                        "B,2018-12-09T09:00:00+00:00\n" +
                        "A,2018-12-09T08:00:00+00:00\n" +
                        "C,2018-12-08T07:00:00+00:00\n"
        );

        List<String> result = MostActiveCookie.findMostActiveCookies(
                tempFile.toString(),
                LocalDate.parse("2018-12-09")
        );

        assertEquals(List.of("A"), result);
    }

    @Test
    void stopsReadingWhenDateBecomesOlderThanTargetDate() throws IOException {
        Path tempFile = Files.createTempFile("cookie_log", ".csv");
        Files.writeString(tempFile,
                "cookie,timestamp\n" +
                        "A,2018-12-09T12:00:00+00:00\n" +
                        "B,2018-12-09T11:00:00+00:00\n" +
                        "A,2018-12-09T10:00:00+00:00\n" +
                        "Z,2018-12-08T23:59:59+00:00\n" +
                        "Z,2018-12-08T20:00:00+00:00\n"
        );

        List<String> result = MostActiveCookie.findMostActiveCookies(
                tempFile.toString(),
                LocalDate.parse("2018-12-09")
        );

        assertEquals(List.of("A"), result);
    }

    @Test
    void skipsBlankLines() throws IOException {
        Path tempFile = Files.createTempFile("cookie_log", ".csv");
        Files.writeString(tempFile,
                "cookie,timestamp\n" +
                        "\n" +
                        "A,2018-12-09T12:00:00+00:00\n" +
                        "\n" +
                        "B,2018-12-09T11:00:00+00:00\n" +
                        "A,2018-12-09T10:00:00+00:00\n" +
                        "\n"
        );

        List<String> result = MostActiveCookie.findMostActiveCookies(
                tempFile.toString(),
                LocalDate.parse("2018-12-09")
        );

        assertEquals(List.of("A"), result);
    }

    @Test
    void throwsIllegalArgumentExceptionForInvalidCsvLineWithoutComma() throws IOException {
        Path tempFile = Files.createTempFile("cookie_log", ".csv");
        Files.writeString(tempFile,
                "cookie,timestamp\n" +
                        "INVALID_LINE\n"
        );

        assertThrows(IllegalArgumentException.class, () ->
                MostActiveCookie.findMostActiveCookies(
                        tempFile.toString(),
                        LocalDate.parse("2018-12-09")
                )
        );
    }

    @Test
    void throwsIllegalArgumentExceptionForInvalidTimestamp() throws IOException {
        Path tempFile = Files.createTempFile("cookie_log", ".csv");
        Files.writeString(tempFile,
                "cookie,timestamp\n" +
                        "A,not-a-timestamp\n"
        );

        assertThrows(Exception.class, () ->
                MostActiveCookie.findMostActiveCookies(
                        tempFile.toString(),
                        LocalDate.parse("2018-12-09")
                )
        );
    }

    @Test
    void parseArgsThrowsWhenNoArgumentsProvided() {
        assertThrows(IllegalArgumentException.class, () ->
                MostActiveCookie.parseArgs(new String[]{})
        );
    }

    @Test
    void parseArgsThrowsWhenTooFewArgumentsProvided() {
        assertThrows(IllegalArgumentException.class, () ->
                MostActiveCookie.parseArgs(new String[]{"cookie_log.csv", "-d"})
        );
    }

    @Test
    void parseArgsThrowsWhenTooManyArgumentsProvided() {
        assertThrows(IllegalArgumentException.class, () ->
                MostActiveCookie.parseArgs(new String[]{"cookie_log.csv", "-d", "2018-12-09", "extra"})
        );
    }

    @Test
    void parseArgsThrowsWhenFlagIsMissing() {
        assertThrows(IllegalArgumentException.class, () ->
                MostActiveCookie.parseArgs(new String[]{"cookie_log.csv", "--date", "2018-12-09"})
        );
    }

    @Test
    void parseArgsThrowsWhenDateFormatIsInvalid() {
        assertThrows(IllegalArgumentException.class, () ->
                MostActiveCookie.parseArgs(new String[]{"cookie_log.csv", "-d", "12-09-2018"})
        );
    }

    @Test
    void parseArgsThrowsWhenDateValueIsInvalid() {
        assertThrows(IllegalArgumentException.class, () ->
                MostActiveCookie.parseArgs(new String[]{"cookie_log.csv", "-d", "2018-02-30"})
        );
    }
}