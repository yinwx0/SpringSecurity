package top.wxyin.utils;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

/**
 * @description MyBatis Plus 生成工具类
 **/
public class AutoGeneratorUtils {

    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/spring_security?zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&autoReconnect=true",
                        "root",
                        "ywx041108")
                .globalConfig(builder -> {
                    // 设置作者
                    builder.author("wxyin")
                            // 覆盖已生成文件
                            .fileOverride()
                            // 指定 Java 代码输出目录
                            .outputDir("C:/Users/Lenovo/Desktop/SpringSecurity/02-auth-database/src/main/java");
                })
                .packageConfig(builder -> {
                    // 设置父包名
                    builder.parent("top.wxyin")
                            .pathInfo(Collections.singletonMap(OutputFile.xml, "C:/Users/Lenovo/Desktop/SpringSecurity/02-auth-database/src/main/resources/mapper")); // 设置 mapperXml 生成路径
                })
                .strategyConfig(builder -> {
                    // 设置需要生成的表名
                    builder.addInclude("user")
                            // 设置过滤表前缀
                            .addTablePrefix("t_", "c_");
                })
                // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}