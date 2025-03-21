const API_URL = "http://localhost:8080"; // Asegúrate de que la URL es correcta

// Obtener todos los tweets principales (hilos)
async function getThreads() {
    try {
        const response = await fetch(`${API_URL}/threads`);
        if (!response.ok) throw new Error("Error al obtener los hilos");
        return await response.json();
    } catch (error) {
        console.error(error);
        return [];
    }
}

// Obtener respuestas de un tweet
async function getReplies(postId) {
    try {
        const response = await fetch(`${API_URL}/replies?parentPostId=${postId}`);
        if (!response.ok) throw new Error("Error al obtener respuestas");
        return await response.json();
    } catch (error) {
        console.error(error);
        return [];
    }
}

// Crear un nuevo tweet
async function createPost(content, parentPostId = null) {
    try {
        const response = await fetch(`${API_URL}/create`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ userId, content, parentPostId })
        });
        if (!response.ok) throw new Error("Error al crear el post");
        return await response.json();
    } catch (error) {
        console.error(error);
        return null;
    }
}

// Cargar tweets en la interfaz
async function loadThreads() {
    const tweetsContainer = document.querySelector(".tweets");
    tweetsContainer.innerHTML = "";

    const threads = await getThreads();

    threads.forEach(thread => {
        const tweetElement = document.createElement("div");
        tweetElement.classList.add("tweet");
        tweetElement.innerHTML = `
            <div class="tweet-header">
                <img src="user.jpg" alt="User" class="avatar">
                <span class="username">Usuario ${thread.userId}</span>
                <span class="time">Hace un momento</span>
            </div>
            <p class="tweet-content">${thread.content}</p>
        `;
        tweetElement.addEventListener("click", () => toggleThread(tweetElement, thread.id));
        tweetsContainer.appendChild(tweetElement);
    });
}

// Mostrar respuestas de un tweet
async function toggleThread(tweetElement, postId) {
    let thread = tweetElement.nextElementSibling;

    if (!thread) {
        thread = document.createElement("div");
        thread.classList.add("tweet-thread");
        tweetElement.insertAdjacentElement("afterend", thread);
    }

    if (thread.style.display === "none" || thread.innerHTML === "") {
        thread.style.display = "block";
        const replies = await getReplies(postId);
        thread.innerHTML = "";
        replies.forEach(reply => {
            const replyElement = document.createElement("div");
            replyElement.classList.add("tweet");
            replyElement.innerHTML = `
                <div class="tweet-header">
                    <img src="user.jpg" alt="User" class="avatar">
                    <span class="username">Usuario ${reply.userId}</span>
                    <span class="time">Hace un momento</span>
                </div>
                <p class="tweet-content">${reply.content}</p>
            `;
            thread.appendChild(replyElement);
        });
    } else {
        thread.style.display = "none";
    }
}

// Enviar un nuevo tweet desde el input
const postButton = document.querySelector(".post-btn");
if (postButton) {
    postButton.addEventListener("click", async function() {
        const input = document.querySelector(".tweet-input");
        const content = input.value.trim();
        if (content === "") return;
        await createPost("1", content);
        input.value = "";
        loadThreads();
    });
}

// Cargar hilos al iniciar
document.addEventListener("DOMContentLoaded", loadThreads);
