const API_URL = "http://localhost:8080";

// Get random user ID (simplified for demo)
function getUserId() {
  return Math.floor(Math.random() * 1000) + 1;
}

// Create a new post
async function createPost(content) {
  const userId = getUserId();
  
  try {
    const response = await fetch(`${API_URL}/posts/create`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ 
        userId: userId.toString(), 
        content, 
        parentPostId: null 
      })
    });
    
    if (!response.ok) throw new Error("Error creating post");
    return await response.json();
  } catch (error) {
    console.error("Error creating post:", error);
    return null;
  }
}

// Load feed (5 random posts)
async function loadFeed() {
  const tweetsContainer = document.querySelector(".tweets");
  tweetsContainer.innerHTML = "<p>Loading posts...</p>";

  try {
    const response = await fetch(`${API_URL}/posts/feed`);
    
    if (!response.ok) throw new Error("Error fetching feed");
    
    const posts = await response.json();
    tweetsContainer.innerHTML = "";
    
    // Display up to 5 posts
    const postsToShow = posts.slice(0, 5);
    
    if (postsToShow.length === 0) {
      tweetsContainer.innerHTML = "<p>No posts yet. Be the first to post!</p>";
      return;
    }
    
    postsToShow.forEach(post => {
      const tweetElement = document.createElement("div");
      tweetElement.classList.add("tweet");
      tweetElement.innerHTML = `
        <div class="tweet-header">
          <div class="avatar"></div>
          <span class="username">User ${post.userId}</span>
          <span class="time">Just now</span>
        </div>
        <p class="tweet-content">${post.content}</p>
      `;
      tweetsContainer.appendChild(tweetElement);
    });
  } catch (error) {
    console.error("Error loading feed:", error);
    tweetsContainer.innerHTML = "<p>Failed to load posts. Please try again.</p>";
  }
}

// Handle posting a new tweet
document.querySelector(".post-btn").addEventListener("click", async function() {
  const input = document.querySelector(".tweet-input");
  const content = input.value.trim();
  
  if (content === "") return;
  
  const loader = document.createElement("span");
  loader.textContent = " Posting...";
  this.appendChild(loader);
  this.disabled = true;
  
  const result = await createPost(content);
  
  this.removeChild(loader);
  this.disabled = false;
  
  if (result) {
    input.value = "";
    loadFeed(); // Reload the feed to show the new post
  } else {
    alert("Failed to create post. Please try again.");
  }
});

// Load feed when page loads
document.addEventListener("DOMContentLoaded", loadFeed);