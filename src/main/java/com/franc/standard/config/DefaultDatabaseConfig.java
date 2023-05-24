package com.franc.standard.config;


import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@MapperScan(basePackages = "com.franc.standard.repository", sqlSessionFactoryRef = "defaultSqlSessionFactory")
@EnableTransactionManagement
public class DefaultDatabaseConfig {

    @Autowired
    private ApplicationContext applicationContext;

    @Primary
    @Bean(name = "defaultDataSource", destroyMethod = "close")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource defaultDataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class).build();
    }

    @Primary
    @Bean(name = "defaultSqlSessionFactory")
    public SqlSessionFactory defaultSqlSessionFactory(
            @Qualifier("defaultDataSource") DataSource defaultDataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(defaultDataSource);
        sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:mapper/*.xml"));
        sqlSessionFactoryBean.setTypeAliasesPackage("com.franc.standard.vo");
        Objects.requireNonNull(sqlSessionFactoryBean.getObject()).getConfiguration().setMapUnderscoreToCamelCase(true);
        return sqlSessionFactoryBean.getObject();
    }

    @Primary
    @Bean(name = "defaultSqlSessionTemplate")
    public SqlSession defaultSqlSessionTemplate(SqlSessionFactory defaultSqlSessionFactory) throws Exception {
        SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(defaultSqlSessionFactory);
        return sqlSessionTemplate;
    }
}
