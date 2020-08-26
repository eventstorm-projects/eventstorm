package eu.eventstorm.sql.model.airport;

public final class AirportMapper implements eu.eventstorm.sql.jdbc.Mapper<Airport> {

    public AirportMapper() {
    }

    public Airport map(eu.eventstorm.sql.Dialect dialect, java.sql.ResultSet rs) throws java.sql.SQLException {
        Airport pojo = new AirportImpl();
        pojo.setId(rs.getString(1));
        pojo.setType(rs.getString(2));
        pojo.setName(rs.getString(3));
        pojo.setElevation(rs.getInt(4));
        pojo.setContinent(rs.getString(5));
        pojo.setCountry(rs.getString(6));
        pojo.setRegion(rs.getString(7));
        return pojo;
    }

    public void insert(eu.eventstorm.sql.Dialect dialect, java.sql.PreparedStatement ps, Airport pojo) throws java.sql.SQLException {
        ps.setString(1,  pojo.getId());
        ps.setString(2,  pojo.getType());
        ps.setString(3,  pojo.getName());
        if (pojo.getElevation() != null) {
        	ps.setInt(4,  pojo.getElevation());	
        }
        
        ps.setString(5,  pojo.getContinent());
        ps.setString(6,  pojo.getCountry());
        ps.setString(7,  pojo.getRegion());
    }

    public void update(eu.eventstorm.sql.Dialect dialect, java.sql.PreparedStatement ps, Airport pojo) throws java.sql.SQLException {
    }

}