document.addEventListener("DOMContentLoaded", () => {
    const tagList = document.getElementById("tagList");
    const newTagInput = document.getElementById("newTag");
    const cancelBtn = document.getElementById("cancelBtn");
    const saveTagBtn = document.getElementById("saveTagBtn");
    const pagination = document.getElementById("pagination");
    const searchInput = document.getElementById("searchInput"); // Tìm kiếm input

    const API_BASE_URL = "/api/v1/tag"; // Base URL for the API

    let currentPage = 0;
    const pageSize = 20;
    let editingTagId = null; // Store the ID of the tag currently being edited

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
            const response = await fetch(`${API_BASE_URL}/search?title=${title}&page=${page}&size=${pageSize}`);
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

    // **Display tags in the table**
    const displayTags = (tags) => {
        tagList.innerHTML = ""; // Clear the table
        tags.forEach((tag) => {
            const row = document.createElement("tr");
            row.innerHTML = `
                <td class="px-4 py-2 text-gray-700">${tag.id}</td>
                <td class="px-4 py-2 text-gray-700">${tag.title}</td>
                <td class="px-4 py-2 text-center">
                    <button class="bg-blue-600 text-white px-4 py-2 rounded-lg shadow-md hover:bg-blue-700 transition duration-300 edit-btn" data-id="${tag.id}">Edit</button>
                    <button class="bg-red-600 text-white px-4 py-2 rounded-lg shadow-md hover:bg-red-700 transition duration-300 delete-btn" data-id="${tag.id}">Delete</button>
                </td>`;
            tagList.appendChild(row);
        });

        // Add event listeners for Edit and Delete buttons
        document.querySelectorAll(".delete-btn").forEach((btn) => {
            btn.addEventListener("click", () => deleteTag(btn.dataset.id));
        });

        document.querySelectorAll(".edit-btn").forEach((btn) => {
            btn.addEventListener("click", () => editTag(btn.dataset.id));
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

    // **Add a new tag**
    

    // **Save changes to an existing tag**
    const saveTag = async () => {
        const tagName = newTagInput.value.trim();
        console.log(editingTagId);
        if (!tagName) {
            alert("Tag name cannot be empty");
            return;
        }

        try {
            let response;
            response = await fetch(`${API_BASE_URL}/${editingTagId}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ title: tagName }),
            });

            if (!response.ok) {
                throw new Error("Failed to update tag");
            }
                        else console.log(response);
            newTagInput.value = ""; // Clear the input
            editingTagId = null; // Reset the editing tag
            saveTagBtn.classList.add("hidden"); // Hide the save button
//            addTagBtn.classList.remove("hidden"); // Show the add button
            fetchTags(currentPage); // Refresh the tag list
        } catch (error) {
            console.error("Error updating tag:", error);
        }
    };

    // **Delete a tag**
    const deleteTag = async (id) => {
        if (!confirm("Are you sure you want to delete this tag?")) return;

        try {
            const response = await fetch(`${API_BASE_URL}/${id}`, {
                method: "DELETE",
            });

            if (response.ok) {
                fetchTags(currentPage); // Refresh the tag list
            } else {
                throw new Error("Failed to delete tag");
            }
        } catch (error) {
            console.error("Error deleting tag:", error);
        }
    };

    // **Edit a tag**
    const editTag = (id) => {
        editingTagId = id;
        const tagTitle = document.querySelector(`button[data-id="${id}"]`).parentElement.previousElementSibling.textContent;
        newTagInput.value = tagTitle;

        // Toggle visibility of buttons
        cancelBtn.classList.remove("hidden"); // Hide Add button
        saveTagBtn.classList.remove("hidden"); // Show Save button
    };
    
    // **Search input listener**
    searchInput.addEventListener("input", () => {
        const searchTerm = searchInput.value.trim();
        if (searchTerm) {
            searchTags(searchTerm, 0); // Start search from page 0
        } else {
            fetchTags(0); // Fetch all tags when search is cleared
        }
    });
    const cancelEdit = () => {
    newTagInput.value = ""; // Clear the input field
    editingTagId = null; // Reset the editing tag ID
    cancelBtn.classList.add("hidden"); // Hide the Cancel button
    saveTagBtn.classList.add("hidden"); // Hide the Save button
};

// Add event listener for the Cancel button
cancelBtn.addEventListener("click", cancelEdit);
    // **Initialize the page**
//    addTagBtn.addEventListener("click", addTag);
    saveTagBtn.addEventListener("click", saveTag);

    // Load the first page of tags on page load
    fetchTags(currentPage);
});
