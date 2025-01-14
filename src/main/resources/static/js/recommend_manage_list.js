$(document).ready(function () {
    const recommendationList = $("#recommendationList");
    const searchInput = $("#searchInput");

    let currentPage = 0;
    const pageSize = 6;

    // Function to fetch recommendations
    function fetchRecommendations(keyword = "") {
        $.ajax({
            url: `/api/v1/recommendations/search-pageable`,
            method: "GET",
            data: {
                keyword: keyword,
                page: currentPage,
                size: pageSize,
                sortBy: "rating",
                direction: "desc",
            },
            success: function (data) {
                renderRecommendations(data.content);
                renderPagination(data.totalPages);
            },
            error: function (error) {
                alert("Error fetching recommendations. Please try again later.");
                console.error("Error fetching recommendations:", error);
            },
        });
    }

    // Function to render recommendations in the table
    function renderRecommendations(recommendations) {
        recommendationList.empty();
        recommendations.forEach((rec) => {
            recommendationList.append(`
                <tr class="bg-gray-100">
                    <td class="px-4 py-2">${rec.id}</td>
                    <td class="px-4 py-2">${rec.title}</td>
                    <td class="px-4 py-2">${rec.location}</td>
                    <td class="px-4 py-2">${rec.address}</td>
                    <td class="px-4 py-2">${rec.email}</td>
                    <td class="px-4 py-2">${rec.startTime || "-"}</td>
                    <td class="px-4 py-2">${rec.endTime || "-"}</td>
                    <td class="px-4 py-2">${formatDateString(rec.startDate) || "-"}</td>
                    <td class="px-4 py-2">${formatDateString(rec.endDate) || "-"}</td>
                    <td class="px-4 py-2">${rec.recommendTime|| "-"}</td>
                    <td class="px-4 py-2">${rec.minBudget ? rec.minBudget.toLocaleString() : "-"}</td>
                    <td class="px-4 py-2">${rec.maxBudget ? rec.maxBudget.toLocaleString() : "-"}</td>
                    <td class="px-4 py-2">${rec.isActive ? "Yes" : "No"}</td>
                    <td class="px-4 py-2">
                        ${rec.tags ? rec.tags.map(tag => tag.title).join(", ") : "-"}
                    </td>
                    <td class="px-4 py-2 text-center">
                        <a href="/admin/recommendation/update?id=${rec.id}" class="text-blue-600 hover:underline">Edit</a> |
                        <button data-id="${rec.id}" class="text-red-600 hover:underline deleteBtn">Delete</button>
                        <button class="text-yellow-500 hover:text-yellow-700 mx-1 unactive-btn ${rec.isActive ? 'btn-danger' : 'btn-success'}" data-id="${rec.id}">
                            ${rec.isActive ? 'Unactivate' : 'Activate'}
                        </button>
                    </td>
                </tr>
            `);
        });
    }
    function formatDateString(dateString) {
    if (!dateString)
        return '';  // Handle empty or invalid date string
    return dateString.split('T')[0];  // Extract only the date part
}
    // Function to render pagination buttons
    function renderPagination(totalPages) {
        const pagination = $("#pagination");
        pagination.empty();
        for (let i = 0; i < totalPages; i++) {
            pagination.append(`
                <button class="px-3 py-1 bg-gray-200 hover:bg-gray-300 rounded-lg ${i === currentPage ? "bg-blue-500 text-white" : ""}" 
                        data-page="${i}">
                    ${i + 1}
                </button>
            `);
        }
    }

    // Event listener for pagination buttons
    $("#pagination").on("click", "button", function () {
        currentPage = $(this).data("page");
        fetchRecommendations(searchInput.val());
    });
   
    
    recommendationList.on("click", ".unactive-btn", function () {
    const id = $(this).data("id");
    $.ajax({
        url: `/api/v1/recommendations/toggleRecActive/${id}`,
        method: "PUT",
        success: function () {
            alert("Status toggled successfully.");
            fetchRecommendations(searchInput.val());
        },
        error: function (error) {
            alert("Error toggling status. Please try again later.");
            console.error("Error toggling status:", error);
        },
    });
});
    // Event listener for delete button
    recommendationList.on("click", ".deleteBtn", function () {
        const id = $(this).data("id");
        if (confirm("Are you sure you want to delete this recommendation?")) {
            $.ajax({
                url: `/api/v1/recommendations/${id}`,
                method: "DELETE",
                success: function () {
                    alert("Recommendation deleted successfully.");
                    fetchRecommendations(searchInput.val());
                },
                error: function (error) {
                    alert("Error deleting recommendation. Please try again later.");
                    console.error("Error deleting recommendation:", error);
                },
            });
        }
    });

    // Event listener for search input
    searchInput.on("input", function () {
        fetchRecommendations(searchInput.val());
    });

    // Initial fetch
    fetchRecommendations();
});

