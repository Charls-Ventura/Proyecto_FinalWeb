let graficaNivel = null;
let graficaSectores = null;
let socketDashboard = null;

document.addEventListener("DOMContentLoaded", () => {
    actualizarEstadoConexion();
    cargarResumenDashboard();
    conectarDashboardSocket();

    window.addEventListener("online", actualizarEstadoConexion);
    window.addEventListener("offline", actualizarEstadoConexion);
});

function actualizarEstadoConexion() {
    const estado = document.getElementById("estadoConexion");
    const punto = document.getElementById("puntoConexion");

    if (!estado || !punto) return;

    if (navigator.onLine) {
        estado.textContent = "En línea";
        punto.style.backgroundColor = "#22c55e";
    } else {
        estado.textContent = "Sin conexión";
        punto.style.backgroundColor = "#ef4444";
    }
}

async function cargarResumenDashboard() {
    try {
        const response = await fetch("/api/dashboard/resumen");
        if (!response.ok) throw new Error("No se pudo cargar el resumen");
        const data = await response.json();
        actualizarTarjetas(data);
        renderizarGraficas(data);
    } catch (error) {
        console.error("Error cargando dashboard:", error);
    }
}

function actualizarTarjetas(data) {
    const totalFormularios = document.getElementById("totalFormularios");
    const totalPendientes  = document.getElementById("totalPendientes");
    const totalSectores    = document.getElementById("totalSectores");
    const totalUsuarios    = document.getElementById("totalUsuarios");

    const pendientes = JSON.parse(localStorage.getItem("formulariosPendientes") || "[]");

    if (totalFormularios) totalFormularios.textContent = data.totalFormularios ?? 0;
    if (totalPendientes)  totalPendientes.textContent  = pendientes.length;
    if (totalSectores)    totalSectores.textContent    = data.totalSectores ?? 0;
    if (totalUsuarios)    totalUsuarios.textContent    = data.totalUsuarios ?? 0;
}

function renderizarGraficas(data) {
    const canvasNivel    = document.getElementById("graficaNivel");
    const canvasSectores = document.getElementById("graficaSectores");

    if (!canvasNivel || !canvasSectores) return;

    const niveles    = data.niveles    || {};
    const topSectores = data.topSectores || [];

    if (graficaNivel)    graficaNivel.destroy();
    if (graficaSectores) graficaSectores.destroy();


    const coloresPastel = [
        "#3b82f6", // Básico - azul
        "#f43f5e", // Medio - rojo
        "#f97316", // Grado Universitario - naranja
        "#eab308", // Postgrado - amarillo
        "#14b8a6"  // Doctorado - teal
    ];

    const valoresNivel = [
        niveles["Básico"]               || niveles["Basico"]               || 0,
        niveles["Medio"]                || 0,
        niveles["Grado Universitario"]  || niveles["GradoUniversitario"]   || 0,
        niveles["Postgrado"]            || 0,
        niveles["Doctorado"]            || 0
    ];

    graficaNivel = new Chart(canvasNivel, {
        type: "doughnut",
        data: {
            labels: ["Básico", "Medio", "Grado Universitario", "Postgrado", "Doctorado"],
            datasets: [{
                data: valoresNivel,
                backgroundColor: coloresPastel,
                borderColor: "#ffffff",
                borderWidth: 3,
                hoverOffset: 8
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            cutout: "60%",
            plugins: {
                legend: {
                    position: "bottom",
                    labels: {
                        padding: 16,
                        usePointStyle: true,
                        pointStyleWidth: 10,
                        font: { size: 12 }
                    }
                },
                tooltip: {
                    callbacks: {
                        label: function(ctx) {
                            const total = ctx.dataset.data.reduce((a, b) => a + b, 0);
                            const pct = total > 0 ? Math.round((ctx.parsed / total) * 100) : 0;
                            return ` ${ctx.label}: ${ctx.parsed} (${pct}%)`;
                        }
                    }
                }
            }
        }
    });


    const coloresBarras = topSectores.map((_, i) => {
        const paleta = ["#3b82f6", "#6366f1", "#8b5cf6", "#ec4899", "#14b8a6", "#f97316"];
        return paleta[i % paleta.length];
    });

    graficaSectores = new Chart(canvasSectores, {
        type: "bar",
        data: {
            labels: topSectores.map(s => s.nombre),
            datasets: [{
                label: "Registros",
                data: topSectores.map(s => s.cantidad),
                backgroundColor: coloresBarras,
                borderRadius: 6,
                borderSkipped: false
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false },
                tooltip: {
                    callbacks: {
                        label: function(ctx) {
                            return ` ${ctx.parsed.y} registro${ctx.parsed.y !== 1 ? "s" : ""}`;
                        }
                    }
                }
            },
            scales: {
                x: {
                    grid: { display: false },
                    ticks: { font: { size: 12 } }
                },
                y: {
                    beginAtZero: true,
                    ticks: {
                        stepSize: 1,
                        font: { size: 12 }
                    },
                    grid: {
                        color: "rgba(0,0,0,0.05)"
                    }
                }
            }
        }
    });
}

function conectarDashboardSocket() {
    const protocolo = window.location.protocol === "https:" ? "wss" : "ws";
    socketDashboard = new WebSocket(`${protocolo}://${window.location.host}/ws/dashboard`);

    socketDashboard.onopen = () => {
        console.log("WebSocket dashboard conectado");
    };

    socketDashboard.onmessage = (event) => {
        try {
            const mensaje = JSON.parse(event.data);
            if (mensaje.tipo === "dashboard-update") {
                actualizarTarjetas(mensaje.payload);
                renderizarGraficas(mensaje.payload);
            }
        } catch (error) {
            console.error("Error procesando mensaje dashboard:", error);
        }
    };

    socketDashboard.onclose = () => {
        setTimeout(conectarDashboardSocket, 3000);
    };

    socketDashboard.onerror = (error) => {
        console.error("Error WebSocket dashboard:", error);
    };
}