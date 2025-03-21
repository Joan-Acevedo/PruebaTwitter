async function handleRegister() {
    const username = document.getElementById("register-username").value;
    const password = document.getElementById("register-password").value;
    const messageElement = document.getElementById("message");

    if (!username || !password) {
        messageElement.textContent = "Usuario y contraseña son obligatorios.";
        messageElement.style.color = "red";
        return;
    }

    const requestData = {
        username: username,
        password: password
    };

    try {
        const response = await fetch("http://localhost:8080/api/users/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(requestData)
        });

        const data = await response.json();

        if (response.ok) {
            messageElement.textContent = "Registro exitoso. Redirigiendo...";
            messageElement.style.color = "green";

            setTimeout(() => {
                window.location.href = "login.html"; // Redirigir al login después de 2 segundos
            }, 2000);
        } else {
            messageElement.textContent = `Error: ${data.message || "No se pudo registrar"}`;
            messageElement.style.color = "red";
        }
    } catch (error) {
        messageElement.textContent = "Error de conexión. Intenta de nuevo.";
        messageElement.style.color = "red";
    }
}
