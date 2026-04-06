document.addEventListener('DOMContentLoaded', async function () {
    const mapa = L.map('mapaLeaflet').setView([19.4517, -70.6970], 8);

    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '&copy; OpenStreetMap'
    }).addTo(mapa);

    try {
        const response = await fetch('/api/formularios/mapa');
        const json = await response.json();
        const formularios = json.datos || [];

        formularios.forEach(function (item) {
            const popup = `
                <strong>${item.nombre}</strong><br>
                Sector: ${item.sector}<br>
                Nivel: ${item.nivelEscolar}<br>
                Usuario: ${item.usuario}
            `;
            L.marker([item.latitud, item.longitud]).addTo(mapa).bindPopup(popup);
        });
    } catch (error) {
        console.error(error);
    }
});
