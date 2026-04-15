document.addEventListener("DOMContentLoaded", () => {
    const formLogin = document.getElementById("formRestLogin");
    const btnListar = document.getElementById("btnRestListar");
    const formCrear = document.getElementById("formRestCrear");

    formLogin.addEventListener("submit", loginRest);
    btnListar.addEventListener("click", listarFormulariosRest);
    formCrear.addEventListener("submit", crearFormularioRest);

    const tokenGuardado = localStorage.getItem("rest_jwt") || "";
    document.getElementById("restToken").value = tokenGuardado;
});

async function loginRest(event) {
    event.preventDefault();

    const username = document.getElementById("restUsername").value.trim();
    const password = document.getElementById("restPassword").value.trim();
    const mensaje = document.getElementById("mensajeRestLogin");
    const tokenArea = document.getElementById("restToken");

    try {
        const response = await fetch("/api/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ username, password })
        });

        const data = await response.json();

        if (data.ok && data.token) {
            localStorage.setItem("rest_jwt", data.token);
            tokenArea.value = data.token;
            mensaje.textContent = "Token obtenido correctamente.";
            mensaje.classList.remove("oculto");
        } else {
            mensaje.textContent = data.mensaje || "No se pudo obtener el token.";
            mensaje.classList.remove("oculto");
        }
    } catch (error) {
        mensaje.textContent = "Error al iniciar sesión REST.";
        mensaje.classList.remove("oculto");
    }
}

async function listarFormulariosRest() {
    const token = localStorage.getItem("rest_jwt") || "";
    const tbody = document.querySelector("#restTabla tbody");

    if (!token) {
        tbody.innerHTML = "<tr><td colspan='5'>Debe autenticarse primero</td></tr>";
        return;
    }

    try {
        const response = await fetch("/api/formularios/mios", {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        const data = await response.json();

        if (!data.ok || !data.datos) {
            tbody.innerHTML = "<tr><td colspan='5'>No hay datos</td></tr>";
            return;
        }

        tbody.innerHTML = "";

        data.datos.forEach(f => {
            const fila = `
                <tr>
                    <td>${f.nombre || "-"}</td>
                    <td>${f.sector || "-"}</td>
                    <td>${f.nivelEscolar || "-"}</td>
                    <td><span class="badge-verde">Sincronizado</span></td>
                    <td>${f.latitud}, ${f.longitud}</td>
                </tr>
            `;
            tbody.innerHTML += fila;
        });

    } catch (error) {
        tbody.innerHTML = "<tr><td colspan='5'>Error al cargar datos</td></tr>";
    }
}

async function crearFormularioRest(event) {
    event.preventDefault();

    const token = localStorage.getItem("rest_jwt") || "";
    const salida = document.getElementById("restRespuestaCrear");

    if (!token) {
        salida.value = "No hay token guardado. Primero debe autenticarse.";
        return;
    }

    const fotoFile = document.getElementById("restFoto").files[0];
    const fotoBase64 = await leerFotoComoBase64(fotoFile);

    const payload = {
        nombre: document.getElementById("restNombre").value.trim(),
        sector: document.getElementById("restSector").value.trim(),
        nivelEscolar: document.getElementById("restNivelEscolar").value.trim(),
        latitud: parseFloat(document.getElementById("restLatitud").value || "0"),
        longitud: parseFloat(document.getElementById("restLongitud").value || "0"),
        fotoBase64: fotoBase64
    };

    try {
        const response = await fetch("/api/formularios", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify(payload)
        });

        const data = await response.json();
        salida.value = JSON.stringify(data, null, 2);
    } catch (error) {
        salida.value = "Error al crear formulario por REST.";
    }
}

function leerFotoComoBase64(file) {
    return new Promise((resolve) => {
        if (!file) {
            resolve("");
            return;
        }

        const reader = new FileReader();
        reader.onload = () => resolve(reader.result || "");
        reader.readAsDataURL(file);
    });
}