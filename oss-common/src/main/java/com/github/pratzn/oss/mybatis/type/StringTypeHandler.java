package com.github.pratzn.oss.mybatis.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes({String.class})
@MappedJdbcTypes({JdbcType.NVARCHAR, JdbcType.NCHAR})
public class StringTypeHandler
  extends BaseTypeHandler<String>
{
  public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType)
    throws SQLException
  {
    ps.setNString(i, parameter);
  }
  
  public String getNullableResult(ResultSet rs, String columnName)
    throws SQLException
  {
    return rs.getNString(columnName);
  }
  
  public String getNullableResult(ResultSet rs, int columnIndex)
    throws SQLException
  {
    return rs.getNString(columnIndex);
  }
  
  public String getNullableResult(CallableStatement cs, int columnIndex)
    throws SQLException
  {
    return cs.getNString(columnIndex);
  }
}
