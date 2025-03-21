document.getElementById("loginForm").addEventListener("submit", async function(event){
    event.preventDefault();

    const formData = new URLSearchParams();
    formData.append("username", document.getElementById("username").value)
    formData.append("password", document.getElementById("password").value)

    const response = await fetch("http://localhost:8080/login", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded"},
        body: formData
    })

    if(response.ok){
        alert("Login Success");
        window.location.href = "/inicio";
    } else {
        alert("credential Incorrect");
    }
});