package com.baykin.jobAggregator.html;

import com.baykin.jobAggregator.dao.PsqlStore;
import com.baykin.jobAggregator.entity.Post;
import com.baykin.jobAggregator.utils.DateTimeParser;
import com.baykin.jobAggregator.utils.HabrCareerDateTimeParser;
import com.baykin.jobAggregator.utils.Parse;
import com.baykin.jobAggregator.utils.PropertiesUtil;
import org.jsoup.Jsoup;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HabrCareerParse implements Parse  {
    private final DateTimeParser dateTimeParser;
    private static final String SOURCE_LINK = "https://career.habr.com";
    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer", SOURCE_LINK);

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    public static void main(String[] args) throws IOException {
        HabrCareerDateTimeParser habrCareerDateTimeParser = new HabrCareerDateTimeParser();
        HabrCareerParse habrCareerParse = new HabrCareerParse(habrCareerDateTimeParser);
        List<Post> list = habrCareerParse.list(PAGE_LINK, 5);
        PsqlStore psqlStore = new PsqlStore(new PropertiesUtil("application.properties"));
        for (Post post : list) {
            psqlStore.save(post);
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

    @Override
    public List<Post> list(String link, int amountPage) {
        List<Post> posts = new ArrayList<>();
        for (int i = 1; i <= amountPage; i++) {
            Connection connection = Jsoup.connect(String.format("%s?page=%d", link, i));
            try {
                Document document = connection.get();
                Elements rows = document.select(".vacancy-card__inner");
                rows.forEach(row -> {
                    Element titleElement = row.select(".vacancy-card__title").first();
                    Element linkElement = titleElement.child(0);
                    String vacancyName = titleElement.text();
                    Element dateTimeElement = row.select(".vacancy-card__date").first();
                    Element dataTime = dateTimeElement.child(0);
                    LocalDateTime created = this.dateTimeParser.parse(dataTime.attr("datetime"));
                    String vacancyLink = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                    String description = retrieveDescription(vacancyLink);
                    posts.add(new Post(vacancyName, vacancyLink, description, created));
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return posts;
    }
}
