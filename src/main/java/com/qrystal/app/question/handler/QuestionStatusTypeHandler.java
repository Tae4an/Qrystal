package com.qrystal.app.question.handler;

import com.qrystal.app.question.domain.QuestionStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(QuestionStatus.class)
public class QuestionStatusTypeHandler extends BaseTypeHandler<QuestionStatus> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, QuestionStatus parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.name());
    }

    @Override
    public QuestionStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : QuestionStatus.valueOf(value);
    }

    @Override
    public QuestionStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : QuestionStatus.valueOf(value);
    }

    @Override
    public QuestionStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : QuestionStatus.valueOf(value);
    }
}