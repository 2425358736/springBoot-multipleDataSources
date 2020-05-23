### 第一步 [上源码Demo地址](https://github.com/2425358736/springBoot-multipleDataSources)


### 首先查看我们mysql是否开启了xa事务的支持，innodb_support_xa 为 ON 则开启了

```
show variables like 'innodb_support_xa';
```

### springBoot 引入 spring-boot-starter-jta-atomikos 开发包

```
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jta-atomikos</artifactId>
        </dependency>
```

### 引入 druid 和 mysql开发包，版本要匹配。因为不匹配，我深入源码多次查看错误

```
        <!--mysql-->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.11</version>
        </dependency>

        <!--druid(数据库连接池)-->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid</artifactId>
            <version>1.1.21</version>
        </dependency>
```

### application.yml 配置

```yml
spring:
  application:
    name: springBoot-multipleDataSources
  #druid数据源相关配置配置
  datasource:
    sys:
      url: jdbc:mysql://123.206.19.217:3306/system?useUnicode=true&characterEncoding=UTF-8&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&serverTimezone=Asia/Shanghai&allowMultiQueries=true&useSSL=false
      username: root
      password: lzq199528
      driverClassName: com.mysql.cj.jdbc.Driver
    base:
      url: jdbc:mysql://123.206.19.217:3306/base_data?useUnicode=true&characterEncoding=UTF-8&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&serverTimezone=Asia/Shanghai&allowMultiQueries=true&useSSL=false
      username: root
      password: lzq199528
      driverClassName: com.mysql.cj.jdbc.Driver

    #连接池的配置信息
    pool:
      initialSize: 10
      minIdle: 5
      maxActive: 20
      maxWait: 600
      timeBetweenEvictionRunsMillis: 60000
      minEvictableIdleTimeMillis: 300000
      validationQuery: SELECT 1 FROM DUAL
      validationQueryTimeout: 1000
      testWhileIdle: true
      testOnBorrow: false
      testOnReturn: false
      poolPreparedStatements: true
      maxPoolPreparedStatementPerConnectionSize: 20
      filters: stat,wall
      connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000
      useGlobalDataSourceStat: true
#mybatis 配置信息
mybatis:
  type-aliases-package: com.multiple.data.sources.domain.**
  config-location: classpath:mybatis/mybatis-config.xml
  mapper-locations: classpath:mappers/**/*.xml
```

### 配置 Jta 事务管理配置

```java
package com.multiple.data.sources.conf.datasource;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.transaction.UserTransaction;

/**
 * @author admin
 */
@Configuration
public class JtaTransactionManagerConfig {
    @Bean(name = "xatx")
    public JtaTransactionManager regTransactionManager () {
        UserTransactionManager userTransactionManager = new UserTransactionManager();
        UserTransaction userTransaction = new UserTransactionImp();
        return new JtaTransactionManager(userTransaction, userTransactionManager);
    }
}

```

### 配置数据源

```java
package com.multiple.data.sources.conf.datasource;

import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

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
```

### 配置 数据源上下文

```java
package com.multiple.data.sources.conf.datasource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ResourceLoader;

import javax.sql.DataSource;

/**
 * @author admin
 * com.multiple.data.sources.mapper.sys 目录下的 走sys
 */
@Configuration
@MapperScan(basePackages = {"com.multiple.data.sources.mapper.sys"},sqlSessionFactoryRef = "dataSysSqlSessionFactory")
public class SessionSys {
    /**
     * 返回sys数据库的会话工厂
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
     * 返回sys数据库的会话模板
     * @param sessionFactory
     * @return
     */
    @Bean(name = "dataSysSqlSessionTemplate")
    @Primary
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("dataSysSqlSessionFactory") SqlSessionFactory sessionFactory) {
        return  new SqlSessionTemplate(sessionFactory);
    }


}


```

```java
package com.multiple.data.sources.conf.datasource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import javax.sql.DataSource;

/**
 * @author admin
 * com.multiple.data.sources.mapper.base 目录下的走base数据源
 */
@Configuration
@MapperScan(basePackages = {"com.multiple.data.sources.mapper.base"},sqlSessionFactoryRef = "dataBaseSqlSessionFactory")
public class SessionBase {

    /**
     * 返回base数据库的会话工厂
     * @param ds
     * @return
     * @throws Exception
     */
    @Bean(name = "dataBaseSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(
            @Qualifier("baseDataSource") DataSource ds,
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
     * 返回base数据库的会话模板
     * @param sessionFactory
     * @return
     */
    @Bean(name = "dataBaseSqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("dataBaseSqlSessionFactory") SqlSessionFactory sessionFactory) {
        return  new SqlSessionTemplate(sessionFactory);
    }
}

```

### 配置 Druid 数据库连接池


```java
package com.multiple.data.sources.conf.druid;


import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author admin
 * @author admin
 * @ServletComponentScan // 用于扫描所有的Servlet、filter、listener
 */
@Configuration
public class DruidConfig {

    @Bean
    public ServletRegistrationBean<StatViewServlet> druidServlet() {
        ServletRegistrationBean<StatViewServlet> servletRegistrationBean = new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");

        servletRegistrationBean.addInitParameter("loginUsername", "root");
        servletRegistrationBean.addInitParameter("loginPassword", "1234");
        return servletRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean<WebStatFilter> filterRegistrationBean() {
        FilterRegistrationBean<WebStatFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        filterRegistrationBean.addInitParameter("profileEnable", "true");
        return filterRegistrationBean;
    }

    @Bean
    public StatFilter statFilter(){
        StatFilter statFilter = new StatFilter();
        //slowSqlMillis用来配置SQL慢的标准，执行时间超过slowSqlMillis的就是慢。
        statFilter.setLogSlowSql(true);
        //SQL合并配置
        statFilter.setMergeSql(true);
        //slowSqlMillis的缺省值为3000，也就是3秒。
        statFilter.setSlowSqlMillis(1000);
        return statFilter;
    }

    @Bean
    public WallFilter wallFilter(){
        WallFilter wallFilter = new WallFilter();
        //允许执行多条SQL
        WallConfig config = new WallConfig();
        config.setMultiStatementAllow(true);
        wallFilter.setConfig(config);
        return wallFilter;
    }
}

```

### 使用  @Transactional(rollbackFor = Exception.class) 声明事务

### http://localhost:8080/druid 进入druid可视化web网页查看sql及数据源信息
