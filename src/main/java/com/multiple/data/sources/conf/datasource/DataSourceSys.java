package com.multiple.data.sources.conf.datasource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ResourceLoader;

import javax.sql.DataSource;

/**
 * @author admin
 */
@Configuration
@MapperScan(basePackages = {"com.multiple.data.sources.mapper.sys"},sqlSessionFactoryRef = "dataSysSqlSessionFactory")
public class DataSourceSys {
    /**
     * 返回data1数据库的会话工厂
     * @param ds
     * @return
     * @throws Exception
     */
    @Bean(name = "dataSysSqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory(
            @Qualifier("sysDataSource") DataSource ds,
            @Qualifier("mybatisData") MybatisProperties properties,
            ResourceLoader resourceLoader) throws Exception{
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(ds);
        bean.setTypeAliasesPackage(properties.getTypeAliasesPackage());
        bean.setConfigLocation(resourceLoader.getResource(properties.getConfigLocation()));
        bean.setMapperLocations(properties.resolveMapperLocations());
        return bean.getObject();
    }
    /**
     * 返回data1数据库的会话模板
     * @param sessionFactory
     * @return
     */
    @Bean(name = "dataSysSqlSessionTemplate")
    @Primary
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("dataSysSqlSessionFactory") SqlSessionFactory sessionFactory) {
        return  new SqlSessionTemplate(sessionFactory);
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
