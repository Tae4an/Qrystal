package com.qrystal.app.exam.handler;

import com.qrystal.app.exam.domain.ExamAttemptStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(ExamAttemptStatus.class)
public class ExamAttemptStatusTypeHandler extends BaseTypeHandler<ExamAttemptStatus> {
    
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ExamAttemptStatus parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, parameter.name());
    }

    @Override
    public ExamAttemptStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : ExamAttemptStatus.valueOf(value);
    }

    @Override
    public ExamAttemptStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : ExamAttemptStatus.valueOf(value);
    }

    @Override
    public ExamAttemptStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : ExamAttemptStatus.valueOf(value);
    }
}