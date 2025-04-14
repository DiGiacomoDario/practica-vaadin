package com.gerentes.agenda.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class CustomErrorController implements ErrorController {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomErrorController.class);

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, HttpServletResponse response) {
        Object status = request.getAttribute("javax.servlet.error.status_code");
        Exception exception = (Exception) request.getAttribute("javax.servlet.error.exception");
        
        if (exception != null) {
            logger.error("Error en la aplicación: {}", exception.getMessage(), exception);
        }
        
        logger.error("Error HTTP {}", status);
        
        // Si el error es 500 o 404, redirige al login
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value() || 
                statusCode == HttpStatus.NOT_FOUND.value()) {
                
                // Comprueba si el usuario está autenticado
                if (request.getUserPrincipal() == null) {
                    return "redirect:/login";
                } else {
                    // Si está autenticado, redirige al dashboard
                    return "redirect:/dashboard";
                }
            }
        }
        
        // Para otros errores, muestra la página de error por defecto
        return "error";
    }
}