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
    const contenedor =
        document.getElementById('mensajeEncuesta') ||
        document.getElementById('mensajeDashboard');

    if (contenedor) {
        contenedor.textContent = texto;
        contenedor.classList.remove('oculto');
    }
}

function actualizarConexion() {
    document.getElementById('estadoConexion').textContent = navigator.onLine ? 'En línea' : 'Sin conexión';
}

function limpiarFormulario() {
    document.getElementById('formularioEncuesta').reset();
    document.getElementById('latitud').value = '';
    document.getElementById('longitud').value = '';

    const contenedorUbicacion = document.getElementById('contenedorUbicacion');
    if (contenedorUbicacion) {
        contenedorUbicacion.classList.add('oculto');
    }

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
    document.getElementById('nombre').value = item.nombre || '';
    document.getElementById('sector').value = item.sector || '';
    document.getElementById('nivelEscolar').value = item.nivelEscolar || '';
    document.getElementById('latitud').value = item.latitud ?? '';
    document.getElementById('longitud').value = item.longitud ?? '';
    fotoBase64Actual = item.fotoBase64 || '';

    const contenedorUbicacion = document.getElementById('contenedorUbicacion');
    if ((item.latitud || item.longitud) && contenedorUbicacion) {
        contenedorUbicacion.classList.remove('oculto');
    }

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
            resolve({ latitud: 0, longitud: 0 });
            return;
        }

        navigator.geolocation.getCurrentPosition(
            (position) => resolve({
                latitud: position.coords.latitude,
                longitud: position.coords.longitude
            }),
            () => resolve({ latitud: 0, longitud: 0 }),
            {
                enableHighAccuracy: true,
                timeout: 10000,
                maximumAge: 0
            }
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

    const latitudInput = document.getElementById('latitud').value.trim();
    const longitudInput = document.getElementById('longitud').value.trim();

    let latitud = latitudInput;
    let longitud = longitudInput;

    if (!latitud || !longitud) {
        const ubicacion = await obtenerUbicacion();
        latitud = ubicacion.latitud;
        longitud = ubicacion.longitud;

        document.getElementById('latitud').value = latitud;
        document.getElementById('longitud').value = longitud;
    }

    const fotoBase64 = await leerFotoComoBase64(foto);
    const lista = leerPendientes();

    const registro = {
        nombre,
        sector,
        nivelEscolar,
        latitud,
        longitud,
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
    const btn = document.getElementById('btnSincronizar');
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

    if (btn) {
        btn.disabled = true;
        btn.textContent = 'Sincronizando...';
    }

    mostrarMensaje('Sincronizando registros...');

    const timeout = setTimeout(() => {
        worker.terminate();
        if (btn) {
            btn.disabled = false;
            btn.textContent = 'Sincronizar';
        }
        mostrarMensaje('La sincronización tardó demasiado o no recibió respuesta.');
    }, 10000);

    worker.onmessage = function (event) {
        clearTimeout(timeout);

        const respuesta = event.data;

        if (respuesta.ok) {
            guardarPendientes([]);
            renderPendientes();

            const actual = parseInt(localStorage.getItem('misFormulariosSincronizados') || '0', 10);
            localStorage.setItem('misFormulariosSincronizados', String(actual + pendientes.length));
        }

        if (btn) {
            btn.disabled = false;
            btn.textContent = 'Sincronizar';
        }

        mostrarMensaje(respuesta.mensaje || 'Sincronización terminada.');
        worker.terminate();
    };

    worker.onerror = function (error) {
        clearTimeout(timeout);
        console.error('Error en worker:', error);

        if (btn) {
            btn.disabled = false;
            btn.textContent = 'Sincronizar';
        }

        mostrarMensaje('Error en el proceso de sincronización.');
        worker.terminate();
    };

    worker.postMessage({
        socketUrl,
        token,
        formularios: pendientes
    });
}

function obtenerUbicacionGPS() {
    const latitudInput = document.getElementById('latitud');
    const longitudInput = document.getElementById('longitud');
    const contenedorUbicacion = document.getElementById('contenedorUbicacion');

    if (!navigator.geolocation) {
        alert('Tu navegador no soporta geolocalización.');
        return;
    }

    navigator.geolocation.getCurrentPosition(
        (posicion) => {
            const lat = posicion.coords.latitude;
            const lng = posicion.coords.longitude;

            if (latitudInput) latitudInput.value = lat;
            if (longitudInput) longitudInput.value = lng;
            if (contenedorUbicacion) contenedorUbicacion.classList.remove('oculto');

            mostrarMensaje('Ubicación GPS obtenida correctamente.');
        },
        (error) => {
            console.error('Error obteniendo ubicación:', error);
            alert('No se pudo obtener la ubicación.');
        },
        {
            enableHighAccuracy: true,
            timeout: 10000,
            maximumAge: 0
        }
    );
}

document.addEventListener('DOMContentLoaded', function () {
    actualizarConexion();
    renderPendientes();

    window.addEventListener('online', actualizarConexion);
    window.addEventListener('offline', actualizarConexion);

    document.getElementById('formularioEncuesta').addEventListener('submit', guardarLocal);
    document.getElementById('btnSincronizar').addEventListener('click', sincronizar);

    const btnUbicacion = document.getElementById('btnUbicacion');
    if (btnUbicacion) {
        btnUbicacion.addEventListener('click', obtenerUbicacionGPS);
    }
});