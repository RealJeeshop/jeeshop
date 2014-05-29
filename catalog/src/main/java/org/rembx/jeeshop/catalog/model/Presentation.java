package org.rembx.jeeshop.catalog.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.HashMap;
import java.util.List;

/**
 * Hold presentation data associated to catalog items such as descriptions,
 * media...
 *
 */
@Entity
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class Presentation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @XmlTransient
    private Long id;

    @NotNull
    @Size(min = 5, max = 5)
    @Column(length = 5)
    private String locale;

    @Size(max = 255)
    @Column(length = 255)
    private String displayName;

    @Size(max = 2000)
    @Column(length =2000)
    private String shortDescription;

    @Size(max = 10000 )
    @Column(length = 10000)
    private String description;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private Media thumbnail;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private Media smallImage;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private Media largeImage;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private Media video;

    @OneToMany
    @OrderColumn(name = "orderIdx")
    @XmlTransient
    private List<Media> otherMedia;

    private HashMap<String,String> features;

    public Presentation() {
    }

    public Presentation(String locale, String displayName, String shortDescription, String description) {
        this.locale = locale;
        this.displayName = displayName;
        this.shortDescription = shortDescription;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Media getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Media thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Media getSmallImage() {
        return smallImage;
    }

    public void setSmallImage(Media smallImage) {
        this.smallImage = smallImage;
    }

    public Media getLargeImage() {
        return largeImage;
    }

    public void setLargeImage(Media largeImage) {
        this.largeImage = largeImage;
    }

    public Media getVideo() {
        return video;
    }

    public void setVideo(Media video) {
        this.video = video;
    }

    public List<Media> getOtherMedia() {
        return otherMedia;
    }

    public void setOtherMedia(List<Media> otherMedia) {
        this.otherMedia = otherMedia;
    }

    public HashMap<String, String> getFeatures() {
        return features;
    }

    public void setFeatures(HashMap<String, String> features) {
        this.features = features;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Presentation that = (Presentation) o;

        if (displayName != null ? !displayName.equals(that.displayName) : that.displayName != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (locale != null ? !locale.equals(that.locale) : that.locale != null) return false;
        if (shortDescription != null ? !shortDescription.equals(that.shortDescription) : that.shortDescription != null)
            return false;

        return true;
    }



    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (locale != null ? locale.hashCode() : 0);
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        result = 31 * result + (shortDescription != null ? shortDescription.hashCode() : 0);
        return result;
    }
}
