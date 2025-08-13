package edu.example;

import edu.example.interceptor.AuthSessionInterceptor;
import edu.example.model.AuthSession;
import edu.example.model.Location;
import edu.example.model.User;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;

import org.hibernate.SessionFactory;
import org.hibernate.jpa.HibernatePersistenceConfiguration;

@EnableWebMvc
@Configuration
@Profile("default")
@ComponentScan("edu.example")
@PropertySource("classpath:application.properties")
public class WebConfig implements WebMvcConfigurer {

    private AuthSessionInterceptor authSessionInterceptor;

    @Autowired
    public void setAuthSessionInterceptor(AuthSessionInterceptor authSessionInterceptor) {
        this.authSessionInterceptor = authSessionInterceptor;
    }

    @Bean(initMethod = "migrate")
    public Flyway flyway(
            @Value("${flyway.url}") String url,
            @Value("${flyway.username}") String username,
            @Value("${flyway.password}") String password
    ) {
        return Flyway.configure().dataSource(url, username, password).cleanDisabled(false).load();
    }

    @Bean
    @Scope("prototype")
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public SessionFactory sessionFactory() {
        return new HibernatePersistenceConfiguration("postgresql")
                .managedClass(User.class)
                .managedClass(AuthSession.class)
                .managedClass(Location.class)
                .jdbcDriver("org.postgresql.Driver")
                .jdbcUrl("jdbc:postgresql://localhost:5432/weather-viewer-db")
                .jdbcCredentials("postgres", "1234")
                .showSql(true, true, true)
                .createEntityManagerFactory();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/img/**").addResourceLocations("/img/");
        registry.addResourceHandler("/css/**").addResourceLocations("/css/");
        registry.addResourceHandler("/js/**").addResourceLocations("/js/");
        registry.addResourceHandler("/favicon.ico").addResourceLocations("/favicon/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authSessionInterceptor).addPathPatterns("/location", "/location/delete");
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
