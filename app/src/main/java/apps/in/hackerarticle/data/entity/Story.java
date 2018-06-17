package apps.in.hackerarticle.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Story extends RealmObject implements Parcelable {
    public static final Parcelable.Creator<Story> CREATOR =
            new Parcelable.Creator<Story>() {
                @Override
                public Story createFromParcel(Parcel source) {
                    return new Story(source);
                }

                @Override
                public Story[] newArray(int size) {
                    return new Story[size];
                }
            };
    private String by;
    private Integer descendants;
    @PrimaryKey
    private Long id;
    private RealmList<Long> kids;
    private Integer score;
    private Integer time;
    private String title;
    private String type;
    private String url;

    public Story() {
    }

    protected Story(Parcel in) {
        this.by = in.readString();
        this.descendants = (Integer) in.readValue(Integer.class.getClassLoader());
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.kids = new RealmList<>();
        in.readList(this.kids, Long.class.getClassLoader());
        this.score = (Integer) in.readValue(Integer.class.getClassLoader());
        this.time = (Integer) in.readValue(Integer.class.getClassLoader());
        this.title = in.readString();
        this.type = in.readString();
        this.url = in.readString();
    }

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public Integer getDescendants() {
        return descendants;
    }

    public void setDescendants(Integer descendants) {
        this.descendants = descendants;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getKids() {
        return kids;
    }

    public void setKids(RealmList<Long> kids) {
        this.kids = kids;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.by);
        dest.writeValue(this.descendants);
        dest.writeValue(this.id);
        dest.writeList(this.kids);
        dest.writeValue(this.score);
        dest.writeValue(this.time);
        dest.writeString(this.title);
        dest.writeString(this.type);
        dest.writeString(this.url);
    }
}
