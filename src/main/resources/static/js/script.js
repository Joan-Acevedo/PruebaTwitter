const API_URL = "http://localhost:8080/posts";

async function getThreads(){
    const response = await fetch(`${API_URL}/stream`);
    return await response.json();
}

async function createPost(userId, content, parentPostId = null){
    const response = await fetch(`${API_URL}/create`, {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({userId, content, parentPostId})
    });
    return await response.json();
}

async function getReplies(postId){
    const response = await fetch(`${API_URL}/${postId}/replies`);
    return await response.json();
}

function toggleThread(tweetElement) {
    let thread = tweetElement.nextElementSibling;
    let replyBox = thread.querySelector(".reply-box");
    if (thread.style.display === "none" || thread.style.display === "") {
        thread.style.display = "block";
        replyBox.style.display = "flex";
    } else {
        thread.style.display = "none";
        replyBox.style.display = "none";
    }
}

function toggleMenu() {
    let dropdown = document.getElementById("userDropdown");
    dropdown.style.display = dropdown.style.display === "block" ? "none" : "block";
}

window.onclick = function(event) {
    if (!event.target.closest('.user-menu')) {
        document.getElementById("userDropdown").style.display = "none";
    }
}