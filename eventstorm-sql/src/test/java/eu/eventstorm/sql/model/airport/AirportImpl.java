package eu.eventstorm.sql.model.airport;

public final class AirportImpl implements Airport {

    public AirportImpl(){}

    private java.lang.String id;
    private java.lang.String type;
    private java.lang.String name;
    private java.lang.Integer elevation;
    private java.lang.String continent;
    private java.lang.String country;
    private java.lang.String region;

    @Override
	public java.lang.String getId() {
		return id;
	}

    @Override
	public void setId(java.lang.String id) {
		this.id = id;
	}

    @Override
	public java.lang.String getType() {
		return type;
	}

    @Override
	public void setType(java.lang.String type) {
		this.type = type;
	}

    @Override
	public java.lang.String getName() {
		return name;
	}

    @Override
	public void setName(java.lang.String name) {
		this.name = name;
	}

    @Override
	public java.lang.Integer getElevation() {
		return elevation;
	}

    @Override
	public void setElevation(java.lang.Integer elevation) {
		this.elevation = elevation;
	}

    @Override
	public java.lang.String getContinent() {
		return continent;
	}

    @Override
	public void setContinent(java.lang.String continent) {
		this.continent = continent;
	}

    @Override
	public java.lang.String getCountry() {
		return country;
	}

    @Override
	public void setCountry(java.lang.String country) {
		this.country = country;
	}

    @Override
	public java.lang.String getRegion() {
		return region;
	}

    @Override
	public void setRegion(java.lang.String region) {
		this.region = region;
	}

	/** {@inheritDoc} */
    @Override
    public String toString() {
        eu.eventstorm.util.ToStringBuilder builder = new eu.eventstorm.util.ToStringBuilder(this);
        builder.append("id",id);
        builder.append("type", this.type);
        builder.append("name", this.name);
        builder.append("elevation", this.elevation);
        builder.append("continent", this.continent);
        builder.append("country", this.country);
        builder.append("region", this.region);
        return builder.toString();
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if ((object == null) || (!(object instanceof Airport))) {
            return false;
        }
        Airport other = (Airport) object;
        // 1 business key on property : code
        return id.equals(other.getId());
    }
}