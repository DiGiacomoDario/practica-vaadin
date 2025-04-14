package com.gerentes.agenda.config;

import org.springframework.context.annotation.Configuration;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.server.communication.IndexHtmlRequestListener;
import com.vaadin.flow.server.communication.IndexHtmlResponse;

/**
 * Configuración personalizada para el Service Worker de Vaadin.
 * Esta clase controla cómo se comporta la aplicación cuando está desconectada.
 */
@Configuration
public class CustomServiceWorkerConfiguration implements VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent event) {
        VaadinService service = event.getSource();

        // Registrar listener para modificar la respuesta HTML inicial
        event.addIndexHtmlRequestListener(new CustomIndexHtmlRequestListener());

        // Configuración para manejar eventos de conexión/desconexión en cada UI
        service.addUIInitListener(uiInitEvent -> {
            uiInitEvent.getUI().getPage().executeJs(
                "window.addEventListener('online', function() { window.location.reload(); });" +
                "window.addEventListener('offline', function() { " +
                "   if (window.location.pathname !== '/offline.html') {" +
                "       console.log('Offline detected - potentially redirecting');" + // Adjusted log message
                "       // Consider adding the fetch check here as well or relying on the IndexHtmlRequestListener script" +
                "   }" +
                "});"
            );
        });
    } // End of serviceInit method

    // Make the nested class public or package-private if needed, but private static should be fine here.
    private static class CustomIndexHtmlRequestListener implements IndexHtmlRequestListener {
        @Override
        public void modifyIndexHtmlResponse(IndexHtmlResponse response) {
            // Append the script to the body of the HTML document
            response.getDocument().body().append(
                "<script>" +
                "function updateOnlineStatus() {" +
                "   if (!navigator.onLine && window.location.pathname !== '/offline.html' && " +
                "       !window.location.pathname.includes('/VAADIN/')) {" +
                "       console.log('App is offline, checking connection to server');" +
                "       fetch(location.origin + '/', { method: 'HEAD', cache: 'no-store', mode: 'no-cors' })" + // Use location.origin and no-cors
                "       .then(function(response) { " +
                "           if (!response.ok && response.status === 0) { throw new Error('Network error'); }" + // Handle opaque response for no-cors
                "           console.log('Server seems reachable despite navigator.onLine being false.'); " +
                "       })" +
                "       .catch(function(error) {" +
                "           console.log('Server unreachable, redirecting to offline page. Error: ', error);" +
                "           window.location.href = '/offline.html';" +
                "       });" +
                "   }" +
                "}" +
                "window.addEventListener('load', updateOnlineStatus);" +
                "window.addEventListener('online', function() { window.location.reload(); });" +
                "window.addEventListener('offline', updateOnlineStatus);" +
                "setInterval(updateOnlineStatus, 30000);" + // Check every 30 seconds
                "</script>"
            );
        }
    }
} // End of CustomServiceWorkerConfiguration class