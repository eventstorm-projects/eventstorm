package eu.eventstorm.sql.model.ex001;

public final class StudentMapper implements eu.eventstorm.sql.jdbc.Mapper<Student> {

    public StudentMapper() {
    }

    public Student map(eu.eventstorm.sql.Dialect dialect, java.sql.ResultSet rs) throws java.sql.SQLException {
        Student pojo = new StudentImpl();
        pojo.setId(rs.getInt(1));
        pojo.setCode(rs.getString(2));
        pojo.setAge(rs.getInt(3));
        pojo.setOverallRating(rs.getLong(4));
        if (rs.wasNull()) {
            pojo.setOverallRating(null);
        }
        pojo.setCreatedAt(rs.getTimestamp(5));
        pojo.setReadonly(rs.getString(6));
        return pojo;
    }

    public void insert(eu.eventstorm.sql.Dialect dialect, java.sql.PreparedStatement ps, Student pojo) throws java.sql.SQLException {
        ps.setInt(1,  pojo.getId());
        ps.setString(2,  pojo.getCode());
        ps.setInt(3,  pojo.getAge());
        if (pojo.getOverallRating() != null) {
            ps.setLong(4,  pojo.getOverallRating());
        } else {
            ps.setNull(4, java.sql.Types.BIGINT);
        }
        ps.setTimestamp(5,  pojo.getCreatedAt());
    }

    public void update(eu.eventstorm.sql.Dialect dialect, java.sql.PreparedStatement ps, Student pojo) throws java.sql.SQLException {
        ps.setString(1,  pojo.getCode());
        ps.setInt(2,  pojo.getAge());
        if (pojo.getOverallRating() != null) {
            ps.setLong(3,  pojo.getOverallRating());
        } else {
            ps.setNull(3, java.sql.Types.BIGINT);
        }
        //set primary key
        ps.setInt(4,  pojo.getId());

    }

}