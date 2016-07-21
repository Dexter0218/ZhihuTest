package com.dexter0218.zhihu.observable;

import android.util.Pair;

import com.annimon.stream.Optional;
import com.dexter0218.zhihu.bean.DailyNews;
import com.dexter0218.zhihu.bean.Story;
import com.dexter0218.zhihu.support.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;

import rx.Observable;

import static com.dexter0218.zhihu.observable.Helper.getHtml;

/**
 * Created by Dexter0218 on 2016/7/21.
 */
public class NewsListFromZhihuObservable {

    private static final String QUESTION_SELECTOR = "div.question";
    private static final String QUESTION_TITLES_SELECTOR = "h2.question-title";
    private static final String QUESTION_LINKS_SELECTOR = "div.view-more a";

    public static Observable<List<DailyNews>> ofDate(String date) {
        Observable<Story> stories = getHtml(Constants.Urls.ZHIHU_DAILY_BEFORE, date)
                .flatMap(NewsListFromZhihuObservable::getStoriesJsonArrayObservable)
                .flatMap(NewsListFromZhihuObservable::getStoriesObservable);

        Observable<Document> documents = stories.flatMap(NewsListFromZhihuObservable::getDocumentObservable);

        Observable<Optional<Pair<Story, Document>>> optionalStoryNDocuments = Observable.zip(stories, documents, NewsListFromZhihuObservable::createPair);
        return null;
    }

    private static Optional<Pair<Story, Document>> createPair(Story story, Document document) {
        return Optional.empty().ofNullable(document == null ? null : Pair.create(story, document));
    }

    private static Observable<Document> getDocumentObservable(Story news) {
        return getHtml(Constants.Urls.ZHIHU_DAILY_OFFLINE_NEWS, news.getStoryId())
                .map(NewsListFromZhihuObservable::getstoryDocument);
    }

    private static Document getstoryDocument(String json) {
        JSONObject newsJson = null;
        try {
            newsJson = new JSONObject(json);
            return newsJson.has("body") ? Jsoup.parse(newsJson.getString("body")) : null;
        } catch (JSONException e) {
            return null;
        }

    }


    private static Observable<JSONArray> getStoriesJsonArrayObservable(String html) {
        return Observable.create(subscriber -> {
            try {
                subscriber.onNext(new JSONObject(html).getJSONArray("stories"));
                subscriber.onCompleted();
            } catch (JSONException e) {
                subscriber.onError(e);
            }
        });
    }

    private static Observable<Story> getStoriesObservable(JSONArray newsArray) {
        return Observable.create(subscriber -> {
            try {
                for (int i = 0; i < newsArray.length(); i++) {
                    JSONObject newsJson = newsArray.getJSONObject(i);
                    subscriber.onNext(getStoryFromJSON(newsJson));
                }
            } catch (JSONException e) {
                subscriber.onError(e);
            }
        });
    }

    private static Story getStoryFromJSON(JSONObject jsonStory) throws JSONException {
        Story story = new Story();
        story.setStoryId(jsonStory.getInt("id"));
        story.setDailyTitle(jsonStory.getString("title"));
        story.setThumbnailUrl(getThumbnailUrlForStory(jsonStory));
        return story;
    }

    private static String getThumbnailUrlForStory(JSONObject jsonStory) throws JSONException {
        if (jsonStory.has("images")) {
            return (String) jsonStory.getJSONArray("image").get(0);
        } else {
            return null;
        }
    }
}
