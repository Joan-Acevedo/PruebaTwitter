document.getElementById("loginForm").addEventListener("submit", async function(event) {
    event.preventDefault();

    document.cookie = "session=; path=/; expires=Thu, 01 Jan 1970 00:00:00 UTC;";

    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    const response = await fetch("http://localhost:8080/log-in", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Accept": "application/json"
        },
        body: JSON.stringify({ username, password }),
        credentials: "include"
    });

    if (response.ok) {
        const data = await response.json();
        
        document.cookie = `session=${data.session};`;
        
        console.log("Cookies establecidas:", document.cookie);
        
        alert("Login Success");
        window.location.assign("/inicio");
    } else {
        alert("Credential Incorrect");
    }
});