package edu.pucmm.icc352.proyectofinalweb.util;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.Map;

public final class VistaUtil {
    private static final TemplateEngine TEMPLATE_ENGINE = crearMotor();

    private VistaUtil() {
    }

    public static String render(String template, Map<String, Object> datos) {
        Context context = new Context();
        context.setVariables(datos);
        return TEMPLATE_ENGINE.process(template, context);
    }

    private static TemplateEngine crearMotor() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("/templates/");
        resolver.setSuffix("");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setTemplateMode("HTML");
        resolver.setCacheable(false);

        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(resolver);
        return engine;
    }
}
