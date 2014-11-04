package org.rembx.jeeshop.catalog.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.*;

import static org.rembx.jeeshop.util.DateUtil.dateToLocalDateTime;
import static org.rembx.jeeshop.util.LocaleUtil.FALLBACK;
import static org.rembx.jeeshop.util.LocaleUtil.getLocaleCode;

/**
 * Parent class of all catalog node types
 * Group common properties used by catalog node elements
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class CatalogItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    /**
     * Technical name
     */
    @Column(nullable = false, length = 50)
    @NotNull
    @Size(max = 50)
    protected String name;

    /**
     * Technical description
     */
    @Size(max = 255)
    @Column(length = 255)
    protected String description;

    /**
     * When true, entity is disabled, and not visible by end customer
     */
    protected Boolean disabled;

    /**
     * When defined, entity is not visible when current date is lower than it
     */
    @Temporal(TemporalType.TIMESTAMP)
    protected Date startDate;

    /**
     * When defined, entity is not visible when current date is greater than it
     */
    @Temporal(TemporalType.TIMESTAMP)
    protected Date endDate;

    /**
     * True when entity is visible
     */
    @Transient
    protected boolean visible;

    @ManyToMany(cascade = {CascadeType.PERSIST,CascadeType.REMOVE, CascadeType.MERGE})
    @JoinTable(joinColumns = @JoinColumn(name = "catalogItemId", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "presentationId"))
    @MapKey(name = "locale")
    @XmlTransient
    private Map<String, Presentation> presentationByLocale;

    @Transient
    private Presentation localizedPresentation;

    public CatalogItem() {
    }

    @PrePersist
    @PreUpdate
    protected void computeDisabled() {
        if (disabled == null) {
            disabled = false;
        }
    }

    @PostLoad
    @PostPersist
    @PostUpdate
    protected void computeIsVisible() {

        ZonedDateTime now = ZonedDateTime.now();

        if (this.isDisabled() || (endDate != null && dateToLocalDateTime(endDate).isBefore(now))
                || (startDate != null && dateToLocalDateTime(startDate).isAfter(now))) {
            visible = false;
        } else {
            visible = true;
        }
    }

    public void setLocalizedPresentation(String locale) { // TODO missing test!! and Fallback
        Presentation localizedPresentation = null;

        if (presentationByLocale == null || presentationByLocale.size()==0){
            return;
        }

        localizedPresentation = presentationByLocale.get(getLocaleCode(locale));

        if (localizedPresentation == null && locale != null){
            String [] splitted = locale.split("_");
            if (splitted.length > 1){
                localizedPresentation = presentationByLocale.get(getLocaleCode(splitted[0]));
            }
        }

        if (localizedPresentation == null && presentationByLocale.get(FALLBACK.toString()) != null) {
            localizedPresentation = presentationByLocale.get(FALLBACK.toString());
        }
        setLocalizedPresentation(localizedPresentation);
    }

    public Long getId() {
        return id;
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

    public Boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isVisible() {
        return visible;
    }

    public Presentation getLocalizedPresentation() {
        return localizedPresentation;
    }

    public void setLocalizedPresentation(Presentation localizedPresentation) {
        this.localizedPresentation = localizedPresentation;
    }

    public Map<String, Presentation> getPresentationByLocale() {
        return presentationByLocale;
    }

    public void setPresentationByLocale(Map<String, Presentation> presentationByLocale) {
        this.presentationByLocale = presentationByLocale;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CatalogItem that = (CatalogItem) o;

        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (disabled != null ? !disabled.equals(that.disabled) : that.disabled != null) return false;
        if (endDate != null ? !endDate.equals(that.endDate) : that.endDate != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (startDate != null ? !startDate.equals(that.startDate) : that.startDate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (disabled != null ? disabled.hashCode() : 0);
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CatalogItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", disabled=" + disabled +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }

        static public void main(String[] args) {

            Locale[] availableLocales = SimpleDateFormat.getAvailableLocales();
            List<Locale> list = new ArrayList<Locale>(Arrays.asList(availableLocales));
            list.remove(0);

            TreeSet<String> set = new TreeSet<>();
            int localeLength = 0;
            for (Locale a : list) {
                if (a.toString().length()>localeLength)
                    localeLength = a.toString().length();

                set.add("{displayName:\"" + a.getDisplayName(Locale.ENGLISH) + "\"," + "name:\"" + a.toString() + "\"},");
            }

            System.out.println(localeLength);
            String buff = "";
            for (String i : set){
;               buff += i;
                if (buff.length()>160){
                    System.out.println(buff);
                    buff ="";
                }

            }


        }

}
