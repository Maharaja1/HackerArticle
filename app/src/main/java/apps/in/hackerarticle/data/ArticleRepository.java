package apps.in.hackerarticle.data;

import java.util.List;

import apps.in.hackerarticle.data.entity.Discussion;
import apps.in.hackerarticle.data.entity.Story;
import apps.in.hackerarticle.data.remote.ApiService;
import io.reactivex.Observable;

public class ArticleRepository {
    ApiService service;

    public ArticleRepository(ApiService service) {
        this.service = service;
    }

    public Observable<Story> getStory(String itemId) {
        return service.getStory(itemId);
    }

    public Observable<List<Long>> getStories(String storyType) {
        return service.getStories(storyType);
    }

    public Observable<Discussion> getComment(long itemId) {
        return service.getComment(itemId);
    }
}
