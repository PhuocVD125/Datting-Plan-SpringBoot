// Fetch data and populate div with ID 'listPost'
const MAX_CONTENT_LENGTH = 50;  // Set the character limit for content

// Fetch data and populate div with ID 'listPost'
console.log(timeAgo("2023-10-10T12:30:00"));
fetch('/api/v1/post/most-liked')
  .then(response => response.json())
  .then(data => {
    // Check if data is an array before mapping
    if (Array.isArray(data)) {
      // Proceed if data is valid
      document.getElementById('listPost').innerHTML = data.map(post => {
        const truncatedContent = truncateContent(post.content, MAX_CONTENT_LENGTH);
        
        return `
        
          <div class="bg-white p-8 rounded-lg shadow-md max-w-md">
            <a href="/social/posts/${post.id}" class="text-blue-600 font-bold text-lg">
            <!-- User Info with Three-Dot Menu -->
            <div class="flex items-center justify-between mb-4">
              <div class="flex items-center space-x-2">
                <img src="${post.userImage}" alt="User Avatar" class="w-8 h-8 rounded-full"> <!-- Placeholder for user avatar -->
                <div>
                  <p class="text-gray-800 font-semibold">${post.username}</p>
                  <p class="text-gray-500 text-sm">Posted on ${timeAgo(post.time)}</p>
                </div>
              </div>
            </div>
            <!-- Message -->
            <div class="mb-4">
              <a href="/social/posts/${post.id}" class="text-blue-600 font-bold text-lg">${post.title}</a>
              <p class="text-gray-800">${truncatedContent}</p> <!-- Display truncated content -->
              <div class="mt-2">
                ${post.tags
                  .split(' ') // Split tags into individual items
                  .map(tag => `<a href="/social/posts/tag/${tag.slice(1).trim()}" class="text-blue-600"> ${tag.trim()} </a>`)
                  .join('')} <!-- Render each tag as a separate <a> -->
              </div>
            </div>
            <!-- Image -->
            <div class="h-100 mb-4">
              <a href="/social/posts/${post.id}" ><img src="${post.image}" alt="Post Image" class="w-full h-full object-cover rounded-md"></a>
            </div>
            <!-- Like and Comment Section -->
            <div class="flex items-center justify-between text-gray-500">
              <div class="flex items-center space-x-2">
                <button class="flex justify-center items-center gap-2 px-2 hover:bg-gray-50 rounded-full p-1">
                  <svg class="w-5 h-5 fill-current" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
                    <path d="M12 21.35l-1.45-1.32C6.11 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-4.11 6.86-8.55 11.54L12 21.35z" />
                  </svg>
                  <span>${post.likeCount} Like${post.likeCount > 1 ? "s" : ""} </span>
                </button>
              </div>
              <button class="flex justify-center items-center gap-2 px-2 hover:bg-gray-50 rounded-full p-1">
                <svg width="22px" height="22px" viewBox="0 0 24 24" class="w-5 h-5 fill-current" xmlns="http://www.w3.org/2000/svg">
                  <path fill-rule="evenodd" clip-rule="evenodd" d="M12 22C17.5228 22 22 17.5228 22 12C22 6.47715 17.5228 2 12 2C6.47715 2 2 6.47715 2 12C2 13.5997 2.37562 15.1116 3.04346 16.4525C3.22094 16.8088 3.28001 17.2161 3.17712 17.6006L2.58151 19.8267C2.32295 20.793 3.20701 21.677 4.17335 21.4185L6.39939 20.8229C6.78393 20.72 7.19121 20.7791 7.54753 20.9565C8.88837 21.6244 10.4003 22 12 22ZM8 13.25C7.58579 13.25 7.25 13.5858 7.25 14C7.25 14.4142 7.58579 14.75 8 14.75H13.5C13.9142 14.75 14.25 14.4142 14.25 14C14.25 13.5858 13.9142 13.25 13.5 13.25H8ZM7.25 10.5C7.25 10.0858 7.58579 9.75 8 9.75H16C16.4142 9.75 16.75 10.0858 16.75 10.5C16.75 10.9142 16.4142 11.25 16 11.25H8C7.58579 11.25 7.25 10.9142 7.25 10.5Z"></path>
                </svg>
                <span>${post.commentCount} Comment${post.commentCount > 1 ? "s" : ""} </span>
              </button>
            </div>
            </a>
          </div>
        `;
      }).join('');
    } else {
      console.error('Data is not an array', data);
    }
  })
  .catch(error => {
    console.error('Error fetching data:', error);
  });

  // Function to truncate content
  function truncateContent(content, maxLength) {
    if (content.length > maxLength) {
      return content.slice(0, maxLength) + '...';  // Truncate and add ellipsis
    }
    return content;
  }
  function timeAgo(dateTime) {
    const now = new Date();
    const targetDate = new Date(dateTime);
    
    // Calculate the difference in years, months, and days
    const years = now.getFullYear() - targetDate.getFullYear();
    const months = now.getMonth() - targetDate.getMonth() + (years * 12);  // Total number of months
    const days = Math.floor((now - targetDate) / (1000 * 60 * 60 * 24)); // Difference in days
  
    // If the difference is more than a month but less than a year, show the local date
    if (months > 1) {
      return targetDate.toLocaleString();  // Return localized date and time
    }
  
    // If the difference is more than 1 year
    if (years > 0) {
      return `${years} year${years > 1 ? "s" : ""} ago`;
    }
  
    // If the difference is more than 30 days but less than 1 month, show months
    if (months > 0) {
      return `${months} month${months > 1 ? "s" : ""} ago`;
    }
  
    // If the difference is more than 1 day, show days
    if (days >= 1) {
      return `${days} day${days > 1 ? "s" : ""} ago`;
    }
  
    // For periods less than a day, use the standard time intervals (hour, minute, second)
    const diff = Math.floor((now - targetDate) / 1000); // Difference in seconds
    const timeIntervals = [
      { label: "hour", seconds: 3600 },
      { label: "minute", seconds: 60 },
      { label: "second", seconds: 1 },
    ];
    
    const timeInterval = timeIntervals.find(i => diff >= i.seconds);
    const count = Math.floor(diff / timeInterval.seconds);
    
    return `${count} ${timeInterval.label}${count !== 1 ? "s" : ""} ago`;
  }
  