package eu.eventstorm.sql.model.airport;

import eu.eventstorm.sql.annotation.Column;
import eu.eventstorm.sql.annotation.PrimaryKey;
import eu.eventstorm.sql.annotation.Table;

@Table(value = "airport")
public interface Airport {

    @PrimaryKey("ident")
    String getId();

    void setId(String id);

    @Column("type")
    String getType();

    void setType(String Type);

    @Column("name")
    String getName();

    void setName(String name);

    @Column(value = "elevation_ft", nullable = true)
    Integer getElevation();

    void setElevation(Integer elevation);

    @Column("continent")
    String getContinent();

    void setContinent(String continent);

    @Column("country")
    String getCountry();

    void setCountry(String country);

    @Column("region")
    String getRegion();

    void setRegion(String region);

}
