package fr.boudonpierre.myyoutube;

/**
 * Created by Pierre BOUDON on 06/06/2016.
 */
public class Video {

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
}
