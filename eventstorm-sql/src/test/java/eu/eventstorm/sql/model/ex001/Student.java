package eu.eventstorm.sql.model.ex001;

import eu.eventstorm.sql.annotation.BusinessKey;
import eu.eventstorm.sql.annotation.Column;
import eu.eventstorm.sql.annotation.CreateTimestamp;
import eu.eventstorm.sql.annotation.PrimaryKey;
import eu.eventstorm.sql.annotation.Table;
import eu.eventstorm.sql.annotation.UpdatableBy;

import java.sql.Timestamp;

@Table(value = "student")
@UpdatableBy(updateMapper = {StudentUpdateMapperWrapper01.class, StudentUpdateMapperWrapper02.class})
public interface Student {

    @PrimaryKey("id")
    int getId();

    void setId(int id);

    @Column("code")
    @BusinessKey
    String getCode();

    void setCode(String code);

    @Column("age")
    Integer getAge();

    void setAge(Integer integer);

    @Column(value = "overall_rating", nullable = true)
    Long getOverallRating();

    void setOverallRating(Long value);

    @Column(value = "created_at", updatable = false)
    @CreateTimestamp
    Timestamp getCreatedAt();

    void setCreatedAt(Timestamp value);

    @Column(value = "readonly", nullable = true, insertable = false, updatable = false)
    String getReadonly();

    void setReadonly(String value);

}
