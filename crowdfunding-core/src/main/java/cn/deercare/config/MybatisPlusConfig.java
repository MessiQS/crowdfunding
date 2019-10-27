package cn.deercare.config;

import com.github.pagehelper.PageHelper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Properties;

// import com.baomidou.mybatisplus.entity.GlobalConfiguration;

@EnableTransactionManagement
@Configuration
@MapperScan("cn.deercare.mapper")
public class MybatisPlusConfig {
/*

    @Bean
    public GlobalConfiguration globalConfiguration() {
        GlobalConfiguration global = new GlobalConfiguration();
        global.setDbType("mysql");
        return global;
    }
*/

    /**
     * 配置mybatis的分页插件pageHelper
     * @return
     */
    @Bean
    public PageHelper pageHelper(){
        PageHelper pageHelper = new PageHelper();
        Properties properties = new Properties();
        properties.setProperty("offsetAsPageNum","true");
        properties.setProperty("rowBoundsWithCount","true");
        properties.setProperty("reasonable","true");
        //配置mysql数据库的方言
        properties.setProperty("dialect","postgresql");
        pageHelper.setProperties(properties);
        return pageHelper;
    }

}
