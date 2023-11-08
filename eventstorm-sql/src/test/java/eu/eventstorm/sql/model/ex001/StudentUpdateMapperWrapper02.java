package eu.eventstorm.sql.model.ex001;

import com.google.common.collect.ImmutableList;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.SqlQuery;
import eu.eventstorm.sql.builder.UpdateBuilder;
import eu.eventstorm.sql.desc.SqlColumn;
import eu.eventstorm.sql.expression.Expressions;
import eu.eventstorm.sql.jdbc.UpdateMapperWrapper;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StudentUpdateMapperWrapper02 implements UpdateMapperWrapper<StudentAdapter> {

    @Override
    public void update(Dialect dialect, PreparedStatement ps, StudentAdapter pojo) throws SQLException {
        ps.setInt(1, pojo.getAge());
        ps.setInt(2, pojo.getId());
    }


    @Override
    public SqlQuery createSqlQuery(UpdateBuilder updateBuilder) {
        return updateBuilder.where(Expressions.eq(StudentDescriptor.ID)).build();
    }

    @Override
    public ImmutableList<SqlColumn> getColumns() {
        return ImmutableList.of(StudentDescriptor.AGE);
    }

}