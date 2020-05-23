package com.multiple.data.sources.conf.datasource;

import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author admin
 */
@Configuration
public class DataSourceConfig {
    @Autowired
    private Environment env;

    @Bean(name = "sysDataSource")
    @Primary
    public DataSource sysDataSource() {
        AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
        ds.setXaDataSourceClassName("com.alibaba.druid.pool.xa.DruidXADataSource");
        ds.setUniqueResourceName("sys");
        ds.setPoolSize(5);
        ds.setXaProperties(build("spring.datasource.sys."));
        return ds;
    }

    @Bean(name = "baseDataSource")
    public DataSource baseDataSource() {
        AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
        ds.setXaDataSourceClassName("com.alibaba.druid.pool.xa.DruidXADataSource");
        ds.setUniqueResourceName("base");
        ds.setPoolSize(5);
        ds.setXaProperties(build("spring.datasource.base."));
        return ds;
    }



    private Properties build(String prefix) {
        String prefixPool = "spring.datasource.pool.";
        Properties prop = new Properties();
        prop.put("url", env.getProperty(prefix + "url"));
        prop.put("username", env.getProperty(prefix + "username"));
        prop.put("password", env.getProperty(prefix + "password"));
        prop.put("driverClassName", env.getProperty(prefix + "driverClassName"));

        prop.put("initialSize", env.getProperty(prefixPool + "initialSize", Integer.class));

        prop.put("maxActive", env.getProperty(prefixPool + "maxActive", Integer.class));
        prop.put("minIdle", env.getProperty(prefixPool + "minIdle", Integer.class));
        prop.put("maxWait", env.getProperty(prefixPool + "maxWait", Integer.class));
        prop.put("poolPreparedStatements", env.getProperty(prefixPool + "poolPreparedStatements", Boolean.class));
        prop.put("maxPoolPreparedStatementPerConnectionSize", env.getProperty(prefixPool + "maxPoolPreparedStatementPerConnectionSize", Integer.class));
        prop.put("validationQuery", env.getProperty(prefixPool + "validationQuery"));
        prop.put("validationQueryTimeout", env.getProperty(prefixPool + "validationQueryTimeout", Integer.class));
        prop.put("testOnBorrow", env.getProperty(prefixPool + "testOnBorrow", Boolean.class));
        prop.put("testOnReturn", env.getProperty(prefixPool + "testOnReturn", Boolean.class));
        prop.put("testWhileIdle", env.getProperty(prefixPool + "testWhileIdle", Boolean.class));
        prop.put("timeBetweenEvictionRunsMillis", env.getProperty(prefixPool + "timeBetweenEvictionRunsMillis", Integer.class));
        prop.put("minEvictableIdleTimeMillis", env.getProperty(prefixPool + "minEvictableIdleTimeMillis", Integer.class));
        prop.put("filters", env.getProperty(prefixPool + "filters"));
        return prop;
    }

    /**
     * +定义配置文件
     * @return MybatisProperties
     */
    @Bean(name = "mybatisData")
    @ConfigurationProperties(prefix = "mybatis")
    @Primary
    public MybatisProperties mybatisProperties() {
        MybatisProperties mybatisProperties = new MybatisProperties();
        return mybatisProperties;
    }
}