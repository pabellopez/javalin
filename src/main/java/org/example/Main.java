package org.example;

import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinThymeleaf;
import org.h2.tools.Server;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Main {

    static void aplicarConfiguracion(JavalinConfig config) {
        //AQUI CONGIGURAMOS QUE EL PROYECTO EN VEZ DE INICIAR EN http://localhost:7071/ inicie en http://localhost:7071/my-app ASI TENEMOS UNA IDENTIDAD A NUESTRA APP
        config.router.contextPath = "/my-app";
        config.staticFiles.add("static/", Location.CLASSPATH);

        //AQUI APLICAMOS LA CONFIGURACION PARA QUE JAVALIN ENTIENDA QUE SU MANEJADOR DE PLATILLA SERA THYMELEAF Y QUE SU RUTA ES EN resources/templates Y QUE SUS ARCHIVOS .html
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/"); // Carpeta donde se encuentran las plantillas
        templateResolver.setSuffix(".html"); // Extensión de archivo
        templateResolver.setTemplateMode("HTML"); // Modo de plantilla (HTML5)

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        config.fileRenderer(new JavalinThymeleaf(templateEngine));//LE DECIMOS A JAVALIN QUE SU MANEJADOR DE PLANTILLA ES THYMELEAF
    }


    public static void main(String[] args) throws SQLException {
        //INICIAR SERVIDOR WEB H2
        Server server = Server.createWebServer(args).start();
        System.out.println("Url del servidor de h2 : " + server.getURL());//IMPRIMIR URL DE ACCESO
        H2JDBCUtils.init();//INICIAMOS LA BASE DE DATOS Y CREAMOS LAS TABLAS E INSERTAMOS EL USUARIO INICIAL CON PERMISO ADMINISTRADOR
        var app = Javalin.create(config -> {
                    aplicarConfiguracion(config);
                }).get("/", ctx -> {
                    ctx.redirect("home");
                })
                .start(7071);

        app.get("/home", context -> {

            Map mapaSession = (Map)context.req().getSession().getAttribute("usuario");
            Map model = new HashMap();
            model.put("nombre",mapaSession.get("nombre"));
            model.put("id",mapaSession.get("id"));

            context.render("home.html",model);
        });

        //FILTRO
        app.before(context -> {
            if (context.req().getSession().getAttribute("usuario") == null && !context.fullUrl().contains("login") && !context.fullUrl().contains("assets")) {
                context.redirect("login");
            } else if (context.req().getSession().getAttribute("usuario") != null && context.fullUrl().contains("login")) {
                context.redirect("home");
            }
        });

        app.get("/login", context -> {
            context.render("login.html");
        });

        app.post("/login/iniciar", context -> {

            String sql = "select * from usuario where username = ? and password = ?";
            PreparedStatement preparedStatement = H2JDBCUtils.connection.prepareStatement(sql);
            preparedStatement.setString(1, context.formParam("usuario"));
            preparedStatement.setString(2, context.formParam("password"));
            ResultSet rs = preparedStatement.executeQuery();
            Map<String, Object> usuario = null;
            while (rs.next()) {
                usuario = new HashMap<>();
                usuario.put("username", rs.getString("username"));
                usuario.put("nombre", rs.getString("nombre"));
                usuario.put("administrator", rs.getBoolean("administrator"));
                usuario.put("autor", rs.getBoolean("autor"));
                usuario.put("id", rs.getInt("id"));

            }
            if (usuario != null) {
                context.req().getSession().setAttribute("usuario", usuario);
                context.result("true");
            } else {
                context.result("false");
            }
        });

    }
}

