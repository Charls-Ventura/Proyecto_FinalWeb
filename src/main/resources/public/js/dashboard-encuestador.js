document.addEventListener("DOMContentLoaded", () => {
    actualizarConexion();
    cargarDatosEncuestador();

    window.addEventListener("online", actualizarConexion);
    window.addEventListener("offline", actualizarConexion);
});

function actualizarConexion() {
    const punto = document.getElementById("puntoConexion");
    const estado = document.getElementById("estadoConexion");

    if (!punto || !estado) return;

    if (navigator.onLine) {
        punto.style.backgroundColor = "#9ca3af";
        estado.textContent = "En línea";
    } else {
        punto.style.backgroundColor = "#ef4444";
        estado.textContent = "Sin conexión";
    }
}

function cargarDatosEncuestador() {
    const pendientes = JSON.parse(localStorage.getItem("formulariosPendientes") || "[]");

    const misPendientes = document.getElementById("misPendientes");
    const misSincronizados = document.getElementById("misSincronizados");
    const ultimoSector = document.getElementById("ultimoSector");

    if (misPendientes) {
        misPendientes.textContent = pendientes.length;
    }

    const sincronizados = parseInt(localStorage.getItem("misFormulariosSincronizados") || "0", 10);
    if (misSincronizados) {
        misSincronizados.textContent = sincronizados;
    }

    if (ultimoSector) {
        if (pendientes.length > 0) {
            const ultimo = pendientes[pendientes.length - 1];
            ultimoSector.textContent = ultimo.sector || "-";
        } else {
            ultimoSector.textContent = "-";
        }
    }
}