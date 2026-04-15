document.addEventListener("DOMContentLoaded", () => {
    const btnListar = document.getElementById("btnGrpcListar");
    const formCrear = document.getElementById("formGrpcCrear");

    btnListar.addEventListener("click", listarFormulariosGrpc);
    formCrear.addEventListener("submit", crearFormularioGrpc);
});

async function listarFormulariosGrpc() {
    const tbody = document.querySelector("#grpcTabla tbody");

    try {
        const response = await fetch("/api/grpc/formularios/listar", {
            method: "GET"
        });

        const data = await response.json();

        const lista = data.datos || [];

        if (!data.ok || !lista || lista.length === 0) {
            tbody.innerHTML = "<tr><td colspan='5'>No hay formularios para mostrar.</td></tr>";
            return;
        }

        tbody.innerHTML = "";

        lista.forEach(f => {
            const fila = `
                <tr>
                    <td>${f.nombre || "-"}</td>
                    <td>${f.sector || "-"}</td>
                    <td>${f.nivelEscolar || "-"}</td>
                    <td>${f.usuario || f.username || "-"}</td>
                    <td>${f.latitud ?? "-"}, ${f.longitud ?? "-"}</td>
                </tr>
            `;
            tbody.innerHTML += fila;
        });
    } catch (error) {
        tbody.innerHTML = "<tr><td colspan='5'>Error al ejecutar el listado gRPC.</td></tr>";
    }
}

async function crearFormularioGrpc(event) {
    event.preventDefault();

    const salida = document.getElementById("grpcRespuestaCrear");
    const fotoFile = document.getElementById("grpcFoto").files[0];
    const fotoBase64 = await leerFotoComoBase64(fotoFile);

    const payload = {
        nombre: document.getElementById("grpcNombre").value.trim(),
        sector: document.getElementById("grpcSector").value.trim(),
        nivelEscolar: document.getElementById("grpcNivelEscolar").value.trim(),
        username: document.getElementById("grpcUsername").value.trim(),
        latitud: parseFloat(document.getElementById("grpcLatitud").value || "0"),
        longitud: parseFloat(document.getElementById("grpcLongitud").value || "0"),
        fotoBase64: fotoBase64
    };

    try {
        const response = await fetch("/api/grpc/formularios/crear", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        const data = await response.json();
        salida.value = JSON.stringify(data, null, 2);
    } catch (error) {
        salida.value = "Error al crear formulario vía gRPC.";
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