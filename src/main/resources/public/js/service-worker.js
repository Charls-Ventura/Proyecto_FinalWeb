const CACHE = 'proyecto-final-web-v1';
const ARCHIVOS = [
    '/login',
    '/css/estilos.css',
    '/js/login.js',
    '/js/encuesta.js',
    '/js/sync-worker.js',
    '/logo.svg'
];

self.addEventListener('install', function (event) {
    event.waitUntil(
        caches.open(CACHE).then(function (cache) {
            return cache.addAll(ARCHIVOS);
        })
    );
});

self.addEventListener('fetch', function (event) {
    event.respondWith(
        caches.match(event.request).then(function (response) {
            return response || fetch(event.request);
        })
    );
});
