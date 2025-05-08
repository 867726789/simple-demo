package org.example.MiniBatis;

import org.example.MiniBatis.annotation.Param;
import org.example.MiniBatis.annotation.Table;
import org.example.MiniBatis.entity.User;

import java.lang.reflect.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MySqlSessionFactory {

    private static final String JDBCURL = "jdbc:mysql://localhost:3306/mybatis_db";
    private static final String DBUSER = "root";
    private static final String DBPASS = "root";

    @SuppressWarnings("all")
    public <T> T getMapper(Class<T> mapperClass) {
        return (T)Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{mapperClass}, new MapperInvocationHandler());
    }

    static class MapperInvocationHandler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            if (method.getName().startsWith("select")) {
                return invokeSelect(proxy, method, args);
            }
            return null;
        }

        private Object invokeSelect(Object proxy, Method method, Object[] args) {
            String sql = createSelectSql(method);

            try (Connection conn = DriverManager.getConnection(JDBCURL, DBUSER, DBPASS);
                 PreparedStatement ps = conn.prepareStatement(sql)){
                for (int i = 1; i <= args.length; i++) {
                    Object arg = args[i - 1];
                    if (arg instanceof String) {
                        ps.setString(i, (String) arg);
                    } else if (arg instanceof Integer) {
                        ps.setInt(i, (Integer) arg);
                    }
                }
                ResultSet rs = ps.executeQuery();
                if (rs.next()){
                    return parseResult(rs,method);
                }
            } catch (Exception e) {
                System.out.println(e);
            }
            return null;
        }

        private Object parseResult(ResultSet rs,Method method) throws Exception {

            List<Object> result = new ArrayList<>();
            Class<?> resultType = extractRealType(method);


             do {
                Object object = resultType.getConstructor().newInstance();
                Field[] fields = resultType.getDeclaredFields();
                for (Field field : fields) {
                    Object columnValue = null;
                    String fieldName = field.getName();
                    if (field.getType() == String.class) {
                        columnValue = rs.getString(fieldName);
                    } else if (field.getType() == Integer.class) {
                        columnValue = rs.getInt(fieldName);
                    }
                    field.setAccessible(true);
                    field.set(object, columnValue);
                }
                result.add(object);
            } while (rs.next());
            if (method.getGenericReturnType() instanceof ParameterizedType) {
                return result;
            } else {
                return result.get(0);
            }
        }

        private String createSelectSql(Method method) {
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("SELECT ");

            List<String> selectCols = getSelectCols(extractRealType(method));
            sqlBuilder.append(String.join(",",selectCols));

            sqlBuilder.append(" FROM ");
            String tableName = getSelectTableName(extractRealType(method));
            sqlBuilder.append(tableName);

            sqlBuilder.append(" WHERE ");
            String where = getWhere(method);
            sqlBuilder.append(where);

             return sqlBuilder.toString();
        }

        private String getWhere(Method method) {
            return Arrays.stream(method.getParameters())
                    .map((parameter)->{
                        Param param = parameter.getAnnotation(Param.class);
                        String column = param.value();
                        return column + " = ?";
                    }).collect(Collectors.joining(" and "));
        }

        private String getSelectTableName(Class<?> returnType) {
            Table table = returnType.getAnnotation(Table.class);
            if (table != null) {
                return table.tableName();
            }
            return null;
        }

        private List<String> getSelectCols(Class<?> returnType) {
            Field[] fields = returnType.getDeclaredFields();
            return Arrays.stream(fields).map(Field::getName).toList();
        }

        private Class<?> extractRealType(Method method) {
            Type returnType = method.getGenericReturnType();

            // 如果返回类型是 ParameterizedType（如 List<User>）
            if (returnType instanceof ParameterizedType) {
                ParameterizedType paramType = (ParameterizedType) returnType;
                Type[] actualTypeArgs = paramType.getActualTypeArguments();
                if (actualTypeArgs.length > 0) {
                    Type firstType = actualTypeArgs[0];
                    if (firstType instanceof Class) {
                        return (Class<?>) firstType;
                    }
                }
            }
            else if (returnType instanceof Class) {
                return (Class<?>) returnType;
            }
            return null;
        }
    }
}
