document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('loginForm');
    const mensaje = document.getElementById('mensajeLogin');

    if ('serviceWorker' in navigator) {
        navigator.serviceWorker.register('/js/service-worker.js').catch(() => {});
    }

    form.addEventListener('submit', async function (event) {
        event.preventDefault();

        const payload = {
            username: document.getElementById('username').value.trim(),
            password: document.getElementById('password').value.trim()
        };

        try {
            const response = await fetch('/api/auth/login', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(payload)
            });

            const data = await response.json();

            if (!data.ok) {
                mensaje.textContent = data.mensaje;
                return;
            }

            localStorage.setItem('jwt', data.token || '');
            localStorage.setItem('username', data.username || '');
            localStorage.setItem('rol', data.rol || '');
            localStorage.setItem('loginOffline', 'true');
            window.location.href = '/dashboard';
        } catch (error) {
            if (localStorage.getItem('loginOffline') === 'true') {
                mensaje.textContent = 'Sin conexión. Puede entrar al panel cuando el navegador ya tenga la sesión activa.';
            } else {
                mensaje.textContent = 'No se pudo iniciar sesión.';
            }
        }
    });
});
