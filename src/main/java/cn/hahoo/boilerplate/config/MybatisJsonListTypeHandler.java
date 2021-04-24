package cn.hahoo.boilerplate.config;

import cn.hahoo.boilerplate.utils.JsonUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MybatisJsonListTypeHandler<T> extends BaseTypeHandler<List<T>> {
    private final Class<T> clazz;

    public MybatisJsonListTypeHandler(Class<T> clazz) {
        if (clazz == null) throw new IllegalArgumentException("Type argument cannot be null");
        this.clazz = clazz;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<T> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, JsonUtil.toJson(parameter));
    }

    @Override
    public List<T> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return JsonUtil.toObjectList(rs.getString(columnName), clazz);
    }

    @Override
    public List<T> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return JsonUtil.toObjectList(rs.getString(columnIndex), clazz);
    }

    @Override
    public List<T> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return JsonUtil.toObjectList(cs.getString(columnIndex), clazz);
    }
}
