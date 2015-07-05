package com.github.pratzn.oss.mybatis.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.joda.time.DateTime;

@MappedTypes({DateTime.class})
public class DateTimeTypeHandler
  extends BaseTypeHandler<DateTime>
{
  public void setNonNullParameter(PreparedStatement ps, int i, DateTime parameter, JdbcType jdbcType)
    throws SQLException
  {
    ps.setTimestamp(i, new Timestamp(parameter.getMillis()));
  }
  
  public DateTime getNullableResult(ResultSet rs, String columnName)
    throws SQLException
  {
    return convertToDateTime(rs.getTimestamp(columnName));
  }
  
  public DateTime getNullableResult(ResultSet rs, int columnIndex)
    throws SQLException
  {
    return convertToDateTime(rs.getTimestamp(columnIndex));
  }
  
  public DateTime getNullableResult(CallableStatement cs, int columnIndex)
    throws SQLException
  {
    return convertToDateTime(cs.getTimestamp(columnIndex));
  }
  
  private DateTime convertToDateTime(Timestamp timestamp)
  {
    if (timestamp != null) {
      return new DateTime(timestamp.getTime());
    }
    return null;
  }
}
