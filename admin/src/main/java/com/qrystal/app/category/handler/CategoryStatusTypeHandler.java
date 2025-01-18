package com.qrystal.app.category.handler;

import com.qrystal.app.category.domain.CategoryStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CategoryStatusTypeHandler extends BaseTypeHandler<CategoryStatus> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, CategoryStatus parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.name());
    }

    @Override
    public CategoryStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : CategoryStatus.fromString(value);
    }

    @Override
    public CategoryStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : CategoryStatus.fromString(value);
    }

    @Override
    public CategoryStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : CategoryStatus.fromString(value);
    }
}