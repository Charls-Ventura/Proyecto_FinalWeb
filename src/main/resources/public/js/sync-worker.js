self.onmessage = function (event) {
    const { socketUrl, token, formularios } = event.data;

    try {
        const socket = new WebSocket(socketUrl);

        socket.onopen = function () {
            socket.send(JSON.stringify({
                token: token,
                formularios: formularios
            }));
        };

        socket.onmessage = function (event) {
            try {
                const respuesta = JSON.parse(event.data);
                self.postMessage(respuesta);
            } catch (e) {
                self.postMessage({
                    ok: false,
                    mensaje: 'Respuesta inválida del servidor.'
                });
            }
            socket.close();
        };

        socket.onerror = function () {
            self.postMessage({
                ok: false,
                mensaje: 'Error conectando al WebSocket de sincronización.'
            });
        };

        socket.onclose = function () {
            // no hacer nada aquí para no duplicar mensajes
        };

    } catch (e) {
        self.postMessage({
            ok: false,
            mensaje: 'No se pudo iniciar la sincronización.'
        });
    }
};