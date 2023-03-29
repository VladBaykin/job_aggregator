package com.baykin.aggregator.html;

import org.jsoup.Jsoup;
import java.io.IOException;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HabrCareerParse {
    private static final String SOURCE_LINK = "https://career.habr.com";
    private static final int AMOUNT_PAGE = 1;
    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer", SOURCE_LINK);

    public static void main(String[] args) throws IOException {
        for (int i = 1; i <= AMOUNT_PAGE; i++) {
            Connection connection = Jsoup.connect(String.format("%s?page=%d", PAGE_LINK, i));
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                String vacancyName = titleElement.text();
                Element dateTimeElement = row.select(".vacancy-card__date").first();
                Element dataTime = dateTimeElement.child(0);
                String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                String description = retrieveDescription(link);
                System.out.printf(
                        "%s, дата публикации: %s, %s%n%s%n", vacancyName, dataTime.attr("datetime"), link, description);
            });
        }
    }
    static private String retrieveDescription(String link) {
        StringBuilder description = new StringBuilder();
        try {
            var connection = Jsoup.connect(link);
            Document document = connection.get();
            Elements rows = document.select(".style-ugc");
            for (Element row : rows) {
                description.append(row.text());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return description.toString();
    }
}
