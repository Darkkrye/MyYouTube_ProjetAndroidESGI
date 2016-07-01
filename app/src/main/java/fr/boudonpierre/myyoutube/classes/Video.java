package fr.boudonpierre.myyoutube.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nicolas KERVOERN.
 */
public class Video implements Parcelable {

    /* VARIABLES */
    String id;
    String name;
    String description;
    String imageUrl;
    String videoUrl;


    /* CONSTRUCTOR */
    public Video(String id, String name, String description, String imageUrl, String videoUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
    }




    /* GETTERS & SETTERS */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }






    /* PARCELLING PART */
    public Video(Parcel in){
        String[] data = new String[5];

        in.readStringArray(data);
        this.id = data[0];
        this.name = data[1];
        this.description = data[2];
        this.imageUrl = data[3];
        this.videoUrl = data[4];
    }

    //@Оverride
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                this.id,
                this.name,
                this.description,
                this.imageUrl,
                this.videoUrl
        });
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        public Video[] newArray(int size) {
            return new Video[size];
        }
    };
}
