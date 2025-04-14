// Este archivo es un Service Worker vacío que anula cualquier Service Worker previo
self.addEventListener('install', function(event) {
  self.skipWaiting(); // Activa inmediatamente este Service Worker
  console.log('Service Worker nulo instalado para desregistrar cualquier Service Worker anterior');
});

self.addEventListener('activate', function(event) {
  // Eliminar cualquier caché almacenada por Service Workers previos
  event.waitUntil(
    caches.keys().then(function(cacheNames) {
      return Promise.all(
        cacheNames.map(function(cacheName) {
          console.log('Eliminando caché:', cacheName);
          return caches.delete(cacheName);
        })
      );
    })
  );

  console.log('Service Worker nulo activado, cachés eliminados');
  
  // Tomar control de todas las páginas inmediatamente
  event.waitUntil(clients.claim());
});

// No hay manejo de fetch - esto deja que el navegador maneje todas las solicitudes normalmente
self.addEventListener('fetch', function(event) {
  // No interceptamos ninguna petición - comportamiento normal del navegador
  return;
});