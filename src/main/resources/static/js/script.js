const API_URL = "http://localhost:8080";

// Get user info from JWT stored in session cookie
function getUserInfo() {
	// Function to get a cookie value by name
	function getCookie(name) {
		const value = `; ${document.cookie}`;
		const parts = value.split(`; ${name}=`);
		if (parts.length === 2) return parts.pop().split(";").shift();
		return null;
	}

	// Get the session cookie
	const sessionCookie = getCookie("session");

	// If no session cookie exists, redirect to root and return null
	if (!sessionCookie) {
		alert("Please log in to post.");
		window.location.href = "/";
		return null;
	}

	try {
		// Parse the JWT
		const parts = sessionCookie.split(".");
		if (parts.length !== 3) throw new Error("Invalid JWT format");

		// Decode the payload (middle part)
		const payload = JSON.parse(
			atob(parts[1].replace(/-/g, "+").replace(/_/g, "/")),
		);

		// Extract and return both userID and username
		if (!payload.userID) throw new Error("No userID in token");
		
		return {
			userID: payload.userID,
			username: payload.username || `User ${payload.userID}` // Use username if available, otherwise fall back to user ID
		};
	} catch (error) {
		console.error("Error parsing JWT:", error);
		// Redirect to root on invalid token
		window.location.href = "/";
		return null;
	}
}

// Create a new post or reply
async function createPost(content, parentPostId = null) {
	const userInfo = getUserInfo();
	if (!userInfo) return null;

	try {
		const response = await fetch(`${API_URL}/posts/create`, {
			method: "POST",
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify({
				userId: userInfo.userID.toString(),
				content,
				parentPostId,
			}),
		});

		if (!response.ok) throw new Error("Error creating post");
		return await response.json();
	} catch (error) {
		console.error("Error creating post:", error);
		return null;
	}
}

// Fetch replies for a specific post
async function getReplies(postId) {
	try {
		const response = await fetch(`${API_URL}/posts/${postId}/replies`);
		if (!response.ok)
			throw new Error(`Error fetching replies for post ${postId}`);
		return await response.json();
	} catch (error) {
		console.error(error);
		return [];
	}
}

// Create a reply UI element
function createReplyElement(reply) {
	const replyElement = document.createElement("div");
	replyElement.classList.add("reply");
	replyElement.innerHTML = `
    <div class="reply-header">
      <div class="avatar small"></div>
      <span class="username">${reply.username || `User ${reply.userId}`}</span>
      <span class="time">Just now</span>
    </div>
    <p class="reply-content">${reply.content}</p>
  `;
	return replyElement;
}

// Create a reply form
function createReplyForm(postId) {
	const replyForm = document.createElement("div");
	replyForm.classList.add("reply-form");
	replyForm.innerHTML = `
    <textarea placeholder="Write a reply..." class="reply-input" rows="2"></textarea>
    <button class="reply-btn">Reply</button>
  `;

	// Add event listener to the reply button
	replyForm
		.querySelector(".reply-btn")
		.addEventListener("click", async function () {
			const input = replyForm.querySelector(".reply-input");
			const content = input.value.trim();

			if (content === "") return;

			this.disabled = true;
			this.textContent = "Sending...";

			const result = await createPost(content, postId);

			this.disabled = false;
			this.textContent = "Reply";

			if (result) {
				input.value = "";
				// Refresh replies for this post
				loadRepliesForPost(postId);
			} else {
				alert("Failed to post reply. Please try again.");
			}
		});

	return replyForm;
}

// Toggle replies visibility and load them if needed
async function toggleReplies(postElement, postId) {
	// Check if replies container already exists
	let repliesContainer = postElement.querySelector(".replies-container");

	if (repliesContainer) {
		// Toggle visibility if container already exists
		if (repliesContainer.style.display === "none") {
			repliesContainer.style.display = "block";
		} else {
			repliesContainer.style.display = "none";
		}
		return;
	}

	// Create replies container if it doesn't exist
	repliesContainer = document.createElement("div");
	repliesContainer.classList.add("replies-container");
	repliesContainer.innerHTML = "<p>Loading replies...</p>";
	postElement.appendChild(repliesContainer);

	// Load replies
	await loadRepliesForPost(postId, repliesContainer);

	// Add reply form
	repliesContainer.appendChild(createReplyForm(postId));
}

// Load replies for a specific post
async function loadRepliesForPost(postId, containerParam = null) {
	// If no container is provided, find it in the DOM
	let repliesContainer = containerParam;
	if (!repliesContainer) {
		repliesContainer = document.querySelector(
			`.tweet[data-post-id="${postId}"] .replies-container`,
		);
		if (!repliesContainer) return;
	}

	// Clear loading message
	repliesContainer.innerHTML = "";

	// Fetch replies
	const replies = await getReplies(postId);

	if (replies.length === 0) {
		const noRepliesMsg = document.createElement("p");
		noRepliesMsg.classList.add("no-replies");
		noRepliesMsg.textContent = "No replies yet. Be the first to reply!";
		repliesContainer.appendChild(noRepliesMsg);
	} else {
		// Create a wrapper for all replies
		const repliesList = document.createElement("div");
		repliesList.classList.add("replies-list");

		// Add each reply
		for (const reply of replies) {
			repliesList.appendChild(createReplyElement(reply));
		}

		repliesContainer.appendChild(repliesList);
	}

	// Find existing reply form or create new one
	let replyForm = repliesContainer.querySelector(".reply-form");
	if (!replyForm) {
		replyForm = createReplyForm(postId);
		repliesContainer.appendChild(replyForm);
	}
}

// Create a tweet element with reply functionality
function createTweetElement(post, isUserPost = false) {
	const tweetElement = document.createElement("div");
	tweetElement.classList.add("tweet");
	if (isUserPost) tweetElement.classList.add("my-tweet");

	// Set data attribute for post ID to use later
	tweetElement.setAttribute("data-post-id", post.id);

	// Display username if available, otherwise fall back to userId
	const displayName = isUserPost ? "You" : (post.username || `User ${post.userId}`);

	tweetElement.innerHTML = `
    <div class="tweet-header">
      <div class="avatar"></div>
	  <span class="username">${displayName}</span>
      <span class="time">Just now</span>
    </div>
    <p class="tweet-content">${post.content}</p>
    <div class="tweet-actions">
      <button class="reply-toggle-btn">Show replies</button>
    </div>
  `;

	// Add event listener to toggle replies
	tweetElement
		.querySelector(".reply-toggle-btn")
		.addEventListener("click", function () {
			const isShowingReplies = this.textContent === "Hide replies";

			// Toggle button text
			this.textContent = isShowingReplies ? "Show replies" : "Hide replies";

			// Toggle replies visibility
			toggleReplies(tweetElement, post.id);
		});

	return tweetElement;
}

// Load feed (5 random posts)
async function loadFeed() {
	const tweetsContainer = document.querySelector(".tweets");
	tweetsContainer.innerHTML = "<p>Loading posts...</p>";

	try {
		function getCookie(name) {
			const value = `; ${document.cookie}`;
			const parts = value.split(`; ${name}=`);
			if (parts.length === 2) return parts.pop().split(";").shift();
			return null;
		}

		const sessionCookie = getCookie("session");

		if (!sessionCookie) {
			alert("Please log in to view feed.");
			window.location.href = "/";
			return;
		}

		const response = await fetch(`${API_URL}/posts/feed`, {
			method: "GET",
			headers: {
				"Authorization": `Bearer ${sessionCookie}`
			}
		});

		if (!response.ok) throw new Error("Error fetching feed");

		const posts = await response.json();
		tweetsContainer.innerHTML = "";

		// Display up to 5 posts
		const postsToShow = posts.slice(0, 5);

		if (postsToShow.length === 0) {
			tweetsContainer.innerHTML = "<p>No posts yet. Be the first to post!</p>";
			return;
		}

		for (const post of postsToShow) {
			const tweetElement = createTweetElement(post);
			tweetsContainer.appendChild(tweetElement);
		}
	} catch (error) {
		console.error("Error loading feed:", error);
		tweetsContainer.innerHTML =
			"<p>Failed to load posts. Please try again.</p>";
	}
}

async function loadUserPosts() {
	const myTweetsContainer = document.querySelector(".my-tweets");
	myTweetsContainer.innerHTML = "<p>Loading your posts...</p>";

	const userInfo = getUserInfo();
	if (!userInfo) return;

	try {
		const response = await fetch(`${API_URL}/posts/user?userId=${userInfo.userID}`);

		if (!response.ok) throw new Error("Error fetching user posts");

		const posts = await response.json();
		myTweetsContainer.innerHTML = "";

		if (posts.length === 0) {
			myTweetsContainer.innerHTML = "<p>You haven't posted anything yet.</p>";
			return;
		}

		for (const post of posts) {
			const tweetElement = createTweetElement(post, true);
			myTweetsContainer.appendChild(tweetElement);
		}
	} catch (error) {
		console.error("Error loading user posts:", error);
		myTweetsContainer.innerHTML =
			"<p>Failed to load your posts. Please try again.</p>";
	}
}

// Handle posting a new tweet
document
	.querySelector(".post-btn")
	.addEventListener("click", async function () {
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
			loadFeed(); // Reload the feed
			loadUserPosts(); // Also reload user posts
		} else {
			alert("Failed to create post. Please try again.");
		}
	});

// Load feed when page loads
document.addEventListener("DOMContentLoaded", () => {
	loadFeed();
	loadUserPosts(); // Load the user's posts when the page loads
});