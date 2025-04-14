package com.gerentes.agenda.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controlador para manejar solicitudes relacionadas con Service Workers
 */
@Controller
public class ServiceWorkerController {

    /**
     * Proporciona un Service Worker vacío para desregistrar cualquier Service Worker previo
     * que pueda estar mostrando la pantalla "offline"
     */
    @GetMapping(value = "/sw.js", produces = "application/javascript")
    @ResponseBody
    public String serviceWorker() {
        return "// Este Service Worker está diseñado para desactivar funcionalidad offline\n" +
               "self.addEventListener('install', function(event) {\n" +
               "  self.skipWaiting();\n" +
               "  console.log('Service Worker de reemplazo instalado');\n" +
               "});\n\n" +
               "self.addEventListener('activate', function(event) {\n" +
               "  event.waitUntil(\n" +
               "    caches.keys().then(function(cacheNames) {\n" +
               "      return Promise.all(\n" +
               "        cacheNames.map(function(cacheName) {\n" +
               "          console.log('Eliminando caché:', cacheName);\n" +
               "          return caches.delete(cacheName);\n" +
               "        })\n" +
               "      );\n" +
               "    }).then(function() {\n" +
               "      return clients.claim();\n" +
               "    })\n" +
               "  );\n" +
               "});\n\n" +
               "// No interceptar ninguna petición de red\n" +
               "self.addEventListener('fetch', function(event) {\n" +
               "  // Comportamiento normal del navegador\n" +
               "});";
    }
    
    /**
     * Proporciona un Service Worker vacío para limpiar cachés
     */
    @GetMapping(value = "/empty-sw.js", produces = "application/javascript")
    @ResponseBody
    public String emptyServiceWorker() {
        return "// Este archivo es un Service Worker vacío que anula cualquier Service Worker previo\n" +
               "self.addEventListener('install', function(event) {\n" +
               "  self.skipWaiting(); // Activa inmediatamente este Service Worker\n" +
               "  console.log('Service Worker nulo instalado para desregistrar cualquier Service Worker anterior');\n" +
               "});\n\n" +
               "self.addEventListener('activate', function(event) {\n" +
               "  // Eliminar cualquier caché almacenada por Service Workers previos\n" +
               "  event.waitUntil(\n" +
               "    caches.keys().then(function(cacheNames) {\n" +
               "      return Promise.all(\n" +
               "        cacheNames.map(function(cacheName) {\n" +
               "          console.log('Eliminando caché:', cacheName);\n" +
               "          return caches.delete(cacheName);\n" +
               "        })\n" +
               "      );\n" +
               "    })\n" +
               "  );\n\n" +
               "  console.log('Service Worker nulo activado, cachés eliminados');\n" +
               "  \n" +
               "  // Tomar control de todas las páginas inmediatamente\n" +
               "  event.waitUntil(clients.claim());\n" +
               "});\n\n" +
               "// No hay manejo de fetch - esto deja que el navegador maneje todas las solicitudes normalmente\n" +
               "self.addEventListener('fetch', function(event) {\n" +
               "  // No interceptamos ninguna petición - comportamiento normal del navegador\n" +
               "  return;\n" +
               "});";
    }
    
    /**
     * Proporciona un manifest.webmanifest vacío para evitar que se intente
     * utilizar la funcionalidad PWA
     */
    @GetMapping(value = "/manifest.webmanifest", produces = "application/manifest+json")
    @ResponseBody
    public String manifest() {
        return "{\n" +
               "  \"name\": \"Agenda de Gerentes\",\n" +
               "  \"short_name\": \"Agenda\",\n" +
               "  \"start_url\": \".\",\n" +
               "  \"display\": \"browser\",\n" +
               "  \"background_color\": \"#ffffff\",\n" +
               "  \"theme_color\": \"#007bff\",\n" +
               "  \"icons\": []\n" +
               "}";
    }
}