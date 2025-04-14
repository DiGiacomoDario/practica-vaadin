// Script para desactivar el comportamiento offline y limpiar cachés
(function() {
    console.log('Ejecutando limpieza de caché y Service Workers...');
    
    // Función para desregistrar todos los Service Workers
    function unregisterAllServiceWorkers() {
        if ('serviceWorker' in navigator) {
            navigator.serviceWorker.getRegistrations()
                .then(registrations => {
                    for (const registration of registrations) {
                        console.log('Desregistrando Service Worker:', registration.scope);
                        registration.unregister();
                    }
                    console.log('Todos los Service Workers han sido desregistrados');
                })
                .catch(error => {
                    console.error('Error al desregistrar Service Workers:', error);
                });
        }
    }
    
    // Función para eliminar todas las cachés
    function clearAllCaches() {
        if ('caches' in window) {
            caches.keys()
                .then(cacheNames => {
                    for (const cacheName of cacheNames) {
                        console.log('Eliminando caché:', cacheName);
                        caches.delete(cacheName);
                    }
                    console.log('Todas las cachés han sido eliminadas');
                })
                .catch(error => {
                    console.error('Error al eliminar cachés:', error);
                });
        }
    }
    
    // Función para limpiar localStorage y sessionStorage
    function clearStorages() {
        try {
            localStorage.clear();
            console.log('localStorage limpiado');
            
            sessionStorage.clear();
            console.log('sessionStorage limpiado');
        } catch (error) {
            console.error('Error limpiando storages:', error);
        }
    }
    
    // Función para recargar la página si estamos en la página offline
    function checkAndReloadIfOffline() {
        const path = window.location.pathname;
        if (path === '/offline.html' || document.title.includes('offline') || 
            document.body.textContent.includes('You are offline')) {
            console.log('Detectada página offline, recargando...');
            window.location.href = '/';
        }
    }
    
    // Ejecutar la limpieza al cargar
    window.addEventListener('load', () => {
        unregisterAllServiceWorkers();
        clearAllCaches();
        clearStorages();
        
        // Verificar si estamos en una página offline
        checkAndReloadIfOffline();
    });
    
    // Si el navegador detecta que está online, recargar para asegurar conexión fresca
    window.addEventListener('online', () => {
        console.log('Conexión detectada, recargando...');
        window.location.reload();
    });
    
    // Comprobar regularmente si estamos en la página offline
    setInterval(checkAndReloadIfOffline, 3000);
    
    // Reimplementar función para evitar la detección incorrecta de modo offline
    if (typeof window.navigator.onLine !== 'undefined') {
        Object.defineProperty(window.navigator, 'onLine', {
            get: function() {
                return true;
            }
        });
    }
    
    // Redefinir la API de Service Worker para prevenir registros automáticos
    if ('serviceWorker' in navigator) {
        const originalRegister = navigator.serviceWorker.register;
        
        // Sobrescribir el método register para prevenir registros de Service Workers
        navigator.serviceWorker.register = function(scriptURL, options) {
            console.warn('Intento de registro de Service Worker interceptado:', scriptURL);
            console.warn('Las funcionaliades PWA están deshabilitadas para evitar problemas de conexión');
            
            // Retornar una promesa que se resuelve con un registro falso
            return Promise.resolve({
                scope: '/',
                active: { state: 'activated' },
                installing: null,
                waiting: null,
                unregister: function() { return Promise.resolve(true); },
                update: function() { return Promise.resolve(); }
            });
        };
    }
})();