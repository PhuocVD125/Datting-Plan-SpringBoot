/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/javascript.js to edit this template
 */

document.addEventListener("DOMContentLoaded", () => {
    const tagContainer = document.getElementById("tagContainer");
    const searchInput = document.getElementById("searchTagInput");
    const pagination = document.getElementById("pagination");

    const API_BASE_URL = "/api/v1/ptag"; // Base URL for the API

    let currentPage = 0;
    const pageSize = 20;

    // **Fetch and display all tags**
    const fetchTags = async (page = 0) => {
        try {
            const response = await fetch(`${API_BASE_URL}/?page=${page}&size=${pageSize}&sortBy=title&direction=desc`);
            if (!response.ok) {
                throw new Error("Failed to fetch tags");
            }
            const data = await response.json();
            displayTags(data.content); // Assuming the response has `content` key for the tag list
            handlePagination(data);
        } catch (error) {
            console.error("Error fetching tags:", error);
        }
    };

    // **Search tags by title**
    const searchTags = async (title, page = 0) => {
        try {
            const response = await fetch(`${API_BASE_URL}/search?title=${title}`);
            if (!response.ok) {
                throw new Error("Failed to fetch tags");
            }
            const data = await response.json();
            displayTags(data.content); // Assuming the response has `content` key for the tag list
            handlePagination(data);
        } catch (error) {
            console.error("Error searching tags:", error);
        }
    };

    // **Display tags in the grid container**
    const displayTags = (tags) => {
        tagContainer.innerHTML = ""; // Clear the container
        tags.forEach((tag) => {
            const tagElement = document.createElement("a");
            tagElement.href = `/recommendation/ptag/${tag.title}`;
            tagElement.className = "flex items-center rounded-lg border border-gray-200 bg-white px-4 py-2 hover:bg-gray-50 dark:border-gray-700 dark:bg-gray-800 dark:hover:bg-gray-700";
            tagElement.innerHTML = `<span class="text-sm font-medium text-gray-900 dark:text-white">${tag.title}</span>`;
            tagContainer.appendChild(tagElement);
        });
    };

    // **Handle pagination**
    const handlePagination = (data) => {
        const totalPages = data.totalPages;
        pagination.innerHTML = "";

        for (let i = 0; i < totalPages; i++) {
            const button = document.createElement("button");
            button.innerText = i + 1;
            button.classList.add("px-4", "py-2", "bg-gray-200", "rounded-lg", "hover:bg-gray-300", "transition", "duration-300");
            if (i === currentPage) {
                button.classList.add("bg-blue-500", "text-white");
            }
            button.addEventListener("click", () => changePage(i));
            pagination.appendChild(button);
        }
    };

    // **Change page for pagination**
    const changePage = (page) => {
        currentPage = page;
        if (searchInput.value.trim()) {
            searchTags(searchInput.value.trim(), page); // Search tags when there's a search term
        } else {
            fetchTags(page); // Fetch all tags when there's no search term
        }
    };

    // **Search event listener**
    searchInput.addEventListener("input", () => {
        const searchTerm = searchInput.value;
        if (searchTerm) {
            searchTags(searchTerm);
        } else {
            fetchTags(currentPage); // Reload all tags if search input is cleared
        }
    });

    // **Initialize the page**
    fetchTags(currentPage); // Load the first page of tags on page load
});