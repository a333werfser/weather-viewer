package edu.example.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;

import org.hibernate.SessionFactory;
import org.hibernate.jpa.HibernatePersistenceConfiguration;

@Configuration
@ComponentScan("edu.example")
@EnableWebMvc
@PropertySource("classpath:application.properties")
public class WebConfig implements WebMvcConfigurer {

    @Bean(initMethod = "migrate")
    public Flyway flyway(
            @Value("${flyway.url}") String url,
            @Value("${flyway.username}") String username,
            @Value("${flyway.password}") String password
    ) {
        return Flyway.configure().dataSource(url, username, password).cleanDisabled(false).load();
    }

    @Bean
    public SessionFactory sessionFactory() {
        return new HibernatePersistenceConfiguration("postgresql-configuration")
                .showSql(true, true, true)
                .createEntityManagerFactory();
    }

    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");

        return templateResolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine(){
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        templateEngine.setEnableSpringELCompiler(true);
        return templateEngine;
    }

    @Bean
    public ThymeleafViewResolver viewResolver(){
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine());
        return viewResolver;
    }
}
