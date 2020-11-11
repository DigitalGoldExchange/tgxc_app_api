package com.dgex.backend.config;

import freemarker.template.TemplateModel;
import kr.pe.kwonnam.freemarker.inheritance.BlockDirective;
import kr.pe.kwonnam.freemarker.inheritance.ExtendsDirective;
import kr.pe.kwonnam.freemarker.inheritance.PutDirective;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final Logger log = LoggerFactory.getLogger(WebMvcConfig.class);

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/");
    }

    @Bean
    public Map<String, TemplateModel> freemarkerLayoutDirectives() {

        Map<String, TemplateModel> freemarkerLayoutDirectives = new HashMap<String, TemplateModel>();
        freemarkerLayoutDirectives.put("extends", new ExtendsDirective());
        freemarkerLayoutDirectives.put("block", new BlockDirective());
        freemarkerLayoutDirectives.put("put", new PutDirective());
        return freemarkerLayoutDirectives;
    }

    @Bean
    public FreeMarkerConfigurer freemarkerConfig() {

        FreeMarkerConfigurer freemarkerConfig = new FreeMarkerConfigurer();
        freemarkerConfig.setTemplateLoaderPath("classpath:/templates");
        freemarkerConfig.setDefaultEncoding("utf-8");


        Map<String, Object> freemarkerVariables = new HashMap<String, Object>();
        freemarkerVariables.put("layout", freemarkerLayoutDirectives());


        freemarkerConfig.setFreemarkerVariables(freemarkerVariables);
        return freemarkerConfig;
    }


    @Bean
    public ViewResolver viewResolver() {

        FreeMarkerViewResolver viewResolver = new FreeMarkerViewResolver();
        viewResolver.setCache(false);
        viewResolver.setPrefix("");
        viewResolver.setExposeSpringMacroHelpers(true);
        viewResolver.setSuffix(".ftl");
        viewResolver.setContentType("text/html; charset=utf-8");
        return viewResolver;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new HandlerInterceptor() {

            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
                    throws Exception {

                try{
                    long startTime = new Date().getTime();

                    StringBuffer logBf = new StringBuffer();

                    logBf.append("\n" + request.getRequestURI() + " ===============================================> start1 \n");

                    Enumeration<String> params = request.getParameterNames();

                    while(params.hasMoreElements()) {
                        String name = params.nextElement();
                        logBf.append("     parameter ====> " + name + " ====>" + request.getParameter(name) + "\n");
                    }

                    logBf.append("     request.getMethod() ===>" + request.getMethod() + "\n");
                    logBf.append("     request.getRequestURL() ===>" + request.getRequestURL() + "\n");

                    request.setAttribute("startTime", startTime);
                    request.setAttribute("logBf", logBf);

                }catch (Exception e){
                    log.error(e.getMessage(),e);
                }
                return true;
            }

            @Override
            public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                        Exception ex) throws Exception {

                if(ex != null) log.error(ex.getMessage(),ex);
                try {

                    long startTime = (long) request.getAttribute("startTime");

                    StringBuffer logBf = (StringBuffer) request.getAttribute("logBf");

                    Exception controllerError = (Exception) request.getAttribute("controllerError");

                    if(controllerError != null) {

                        logBf.append(" \t Error 발생 ==>  \n");

                        StringWriter errors = new StringWriter();
                        controllerError.printStackTrace(new PrintWriter(errors));


                        logBf.append(" \t " + errors.toString());
                    }

                    logBf.append(
                            request.getRequestURI() + " ===============================================> end1 " + "[" +
                                    (new Date().getTime() - startTime) / 1000.0 + " Sec] \n");

                    log.info(logBf.toString());

                } catch(Exception e) {
                    log.error(e.getMessage(),e);
                }

            }
        });

    }

}
