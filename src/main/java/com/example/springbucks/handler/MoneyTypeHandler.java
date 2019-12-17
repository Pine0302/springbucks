package com.example.springbucks.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.springframework.lang.Nullable;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MoneyTypeHandler extends BaseTypeHandler<Money> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Money parameter, JdbcType jdbcType) throws SQLException{
        ps.setLong(i,parameter.getAmountMinorLong());
    }

    @Override
    public Money getNullableResult(ResultSet rs,String columnName) throws SQLException{
        return paraseMoney(rs.getLong(columnName));
    }

    @Override
    public Money getNullableResult(ResultSet rs,int columnName) throws SQLException{
        return paraseMoney(rs.getLong(columnName));
    }

    @Override
    public Money getNullableResult(CallableStatement cs, int columnName) throws SQLException{
        return paraseMoney(cs.getLong(columnName));
    }

    private Money paraseMoney(Long value){
        return Money.ofMinor(CurrencyUnit.of("CNY"),value);
    }
}
