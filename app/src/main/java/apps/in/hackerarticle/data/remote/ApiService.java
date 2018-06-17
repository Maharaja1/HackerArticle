package apps.in.hackerarticle.data.remote;

import java.util.List;

import apps.in.hackerarticle.data.entity.Discussion;
import apps.in.hackerarticle.data.entity.Story;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {

    @GET("/v0/{story_type}.json")
    Observable<List<Long>> getStories(@Path("story_type") String storyType);

    @GET("/v0/item/{itemId}.json")
    Observable<Story> getStory(@Path("itemId") String itemId);

    @GET("/v0/item/{itemId}.json")
    Observable<Discussion> getComment(@Path("itemId") long itemId);
}
