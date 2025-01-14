/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/javascript.js to edit this template
 */
document.getElementById('searchForm').addEventListener('submit', function (e) {
                e.preventDefault(); // Prevent the default form submission

                const query = document.getElementById('searchInput').value.trim(); // Get the search query

                if (query) {
                    // Redirect to the search results page with the query parameter
                    window.location.href = `/social/posts/search?keyword=${encodeURIComponent(query)}`;
                } else {
                    alert('Please enter a search query'); // Optional alert for empty search query
                }
            });

// Optional: Trigger form submission on Enter key press
            document.getElementById('searchInput').addEventListener('keydown', function (e) {
                if (e.key === 'Enter') {
                    document.getElementById('searchForm').submit(); // Trigger the form submission
                }
            });

