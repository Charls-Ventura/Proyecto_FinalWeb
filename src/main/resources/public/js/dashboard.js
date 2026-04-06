const CLAVE_FORMULARIOS = 'formulariosPendientes';
let indiceEdicion = -1;
let fotoBase64Actual = '';

function leerPendientes() {
    const data = localStorage.getItem(CLAVE_FORMULARIOS);
    return data ? JSON.parse(data) : [];
}

function guardarPendientes(lista) {
    localStorage.setItem(CLAVE_FORMULARIOS, JSON.stringify(lista));
}

function mostrarMensaje(texto) {
    document.getElementById('mensajeDashboard').textContent = texto;
}

function actualizarConexion() {
    document.getElementById('estadoConexion').textContent = navigator.onLine ? 'En línea' : 'Sin conexión';
}

function limpiarFormulario() {
    document.getElementById('formularioEncuesta').reset();
    fotoBase64Actual = '';
    indiceEdicion = -1;
}

function renderPendientes() {
    const tbody = document.getElementById('tablaPendientes');
    const lista = leerPendientes();
    tbody.innerHTML = '';

    if (lista.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4">No hay registros pendientes.</td></tr>';
        return;
    }

    lista.forEach((item, index) => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${item.nombre}</td>
            <td>${item.sector}</td>
            <td>${item.nivelEscolar}</td>
            <td>
                <button class="boton boton-secundario" type="button" onclick="editarRegistro(${index})">Editar</button>
                <button class="boton boton-peligro" type="button" onclick="eliminarRegistro(${index})">Borrar</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function editarRegistro(index) {
    const lista = leerPendientes();
    const item = lista[index];
    if (!item) {
        return;
    }

    indiceEdicion = index;
    document.getElementById('nombre').value = item.nombre;
    document.getElementById('sector').value = item.sector;
    document.getElementById('nivelEscolar').value = item.nivelEscolar;
    fotoBase64Actual = item.fotoBase64 || '';
    mostrarMensaje('Registro cargado para editar.');
}

function eliminarRegistro(index) {
    const lista = leerPendientes();
    lista.splice(index, 1);
    guardarPendientes(lista);
    renderPendientes();
    mostrarMensaje('Registro eliminado del almacenamiento local.');
}

function obtenerUbicacion() {
    return new Promise((resolve) => {
        if (!navigator.geolocation) {
            resolve({latitud: 0, longitud: 0});
            return;
        }

        navigator.geolocation.getCurrentPosition(
            (position) => resolve({
                latitud: position.coords.latitude,
                longitud: position.coords.longitude
            }),
            () => resolve({latitud: 0, longitud: 0})
        );
    });
}

function leerFotoComoBase64(file) {
    return new Promise((resolve) => {
        if (!file) {
            resolve(fotoBase64Actual || '');
            return;
        }
        const reader = new FileReader();
        reader.onload = () => resolve(reader.result || '');
        reader.readAsDataURL(file);
    });
}

async function guardarLocal(event) {
    event.preventDefault();

    const nombre = document.getElementById('nombre').value.trim();
    const sector = document.getElementById('sector').value.trim();
    const nivelEscolar = document.getElementById('nivelEscolar').value.trim();
    const foto = document.getElementById('foto').files[0];

    if (!nombre || !sector || !nivelEscolar) {
        mostrarMensaje('Complete todos los campos.');
        return;
    }

    const ubicacion = await obtenerUbicacion();
    const fotoBase64 = await leerFotoComoBase64(foto);
    const lista = leerPendientes();

    const registro = {
        nombre,
        sector,
        nivelEscolar,
        latitud: ubicacion.latitud,
        longitud: ubicacion.longitud,
        fotoBase64
    };

    if (indiceEdicion >= 0) {
        lista[indiceEdicion] = registro;
    } else {
        lista.push(registro);
    }

    guardarPendientes(lista);
    renderPendientes();
    limpiarFormulario();
    mostrarMensaje('Formulario guardado en el almacenamiento local.');
}

function sincronizar() {
    const token = localStorage.getItem('jwt') || '';
    const pendientes = leerPendientes();

    if (!token) {
        mostrarMensaje('No hay token guardado. Debe iniciar sesión otra vez.');
        return;
    }

    if (pendientes.length === 0) {
        mostrarMensaje('No hay registros pendientes para sincronizar.');
        return;
    }

    if (!window.Worker) {
        mostrarMensaje('El navegador no soporta Web Worker.');
        return;
    }

    const protocolo = location.protocol === 'https:' ? 'wss:' : 'ws:';
    const socketUrl = `${protocolo}//${location.host}/ws/sync`;
    const worker = new Worker('/js/sync-worker.js');

    worker.onmessage = function (event) {
        const respuesta = event.data;
        if (respuesta.ok) {
            guardarPendientes([]);
            renderPendientes();
        }
        mostrarMensaje(respuesta.mensaje || 'Sincronización terminada.');
        worker.terminate();
    };

    worker.postMessage({
        socketUrl,
        token,
        formularios: pendientes
    });
}

document.addEventListener('DOMContentLoaded', function () {
    actualizarConexion();
    renderPendientes();

    window.addEventListener('online', actualizarConexion);
    window.addEventListener('offline', actualizarConexion);

    document.getElementById('formularioEncuesta').addEventListener('submit', guardarLocal);
    document.getElementById('btnSincronizar').addEventListener('click', sincronizar);
});
