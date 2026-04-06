self.onmessage = function (event) {
    const data = event.data;
    const socket = new WebSocket(data.socketUrl);

    socket.onopen = function () {
        socket.send(JSON.stringify({
            token: data.token,
            formularios: data.formularios
        }));
    };

    socket.onmessage = function (message) {
        self.postMessage(JSON.parse(message.data));
        socket.close();
    };

    socket.onerror = function () {
        self.postMessage({ok: false, mensaje: 'No se pudo sincronizar con el servidor.'});
    };
};
