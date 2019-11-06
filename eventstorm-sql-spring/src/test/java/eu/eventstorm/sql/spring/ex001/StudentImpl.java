package eu.eventstorm.sql.spring.ex001;

public final class StudentImpl implements Student {

    public StudentImpl(){}

    private int id;
    private java.lang.String code;
    private java.lang.Integer age;
    private java.lang.Long overallRating;
    private java.sql.Timestamp createdAt;
    private java.lang.String readonly;


    /** {@inheritDoc} */
    @Override
    public int getId() {
        return this.id;
    }

    /** {@inheritDoc} */
    @Override
    public void setId(int id) {
        this.id = id;
    }

    /** {@inheritDoc} */
    @Override
    public java.lang.String getCode() {
        return this.code;
    }

    /** {@inheritDoc} */
    @Override
    public void setCode(java.lang.String code) {
        this.code = code;
    }

    /** {@inheritDoc} */
    @Override
    public java.lang.Integer getAge() {
        return this.age;
    }

    /** {@inheritDoc} */
    @Override
    public void setAge(java.lang.Integer age) {
        this.age = age;
    }

    /** {@inheritDoc} */
    @Override
    public java.lang.Long getOverallRating() {
        return this.overallRating;
    }

    /** {@inheritDoc} */
    @Override
    public void setOverallRating(java.lang.Long overallRating) {
        this.overallRating = overallRating;
    }

    /** {@inheritDoc} */
    @Override
    public java.sql.Timestamp getCreatedAt() {
        return this.createdAt;
    }

    /** {@inheritDoc} */
    @Override
    public void setCreatedAt(java.sql.Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    /** {@inheritDoc} */
    @Override
    public java.lang.String getReadonly() {
        return this.readonly;
    }

    /** {@inheritDoc} */
    @Override
    public void setReadonly(java.lang.String readonly) {
        this.readonly = readonly;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        eu.eventstorm.util.ToStringBuilder builder = new eu.eventstorm.util.ToStringBuilder(this);
        builder.append("id",id);
        builder.append("code", this.code);
        builder.append("age", this.age);
        builder.append("overallRating", this.overallRating);
        builder.append("createdAt", this.createdAt);
        builder.append("readonly", this.readonly);
        return builder.toString();
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if ((object == null) || (!(object instanceof Student))) {
            return false;
        }
        Student other = (Student) object;
        // 1 business key on property : code
        return code.equals(other.getCode());
    }
}