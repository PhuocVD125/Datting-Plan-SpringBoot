function convertToISOFromFullString(fullDateString) {
    // Remove the 'Start Date: ' or 'End Date: ' part from the input
    const dateString = fullDateString.split(': ')[1];

    // Split the date string into time and date parts
    const [time, dayMonthYear] = dateString.split(" ");

    // Reformat the date part to yyyy-mm-dd
    const [day, month, year] = dayMonthYear.split('/');
    const formattedDate = `${year}-${month.padStart(2, '0')}-${day.padStart(2, '0')}`;

    // Combine formatted date and time and return as ISO 8601 string
    return `${formattedDate}T${time}:00`;
}

$(document).ready(function () {
    fetchPosts();
    $("#addToPlan").click(function () {
        // Giả sử rec là đối tượng bạn nhận được từ backend hoặc đã có trong code.
        const rec = {
            id: recId, // ID recommendation
            location: $('#location').text().split(":")[1].trim(),
            title: $('#title').text(),
            maxBudget: $('#minBudget').text().slice(1),
            minBudget: $('#maxBudget').text().slice(1),
            description: "", // Mô tả
            startDate: $('#startDate').text().trim() ? convertToISOFromFullString($('#startDate').text()) : null,
            endDate: $('#endDate').text().trim() ? convertToISOFromFullString($('#endDate').text()) : null,
            startTime:"00:00",
            endTime:"23:59",
            address: $('#address').text().split("Address:")[1].trim(),
            recStartTime:$('#recStartTime').text().split("Opening Time:")[1].trim(),
            recEndTime:$('#recEndTime').text().split("Closed Time:")[1].trim()
        };
        console.log(rec);
        // Mở dropdown kế hoạch
        const dropdown = $(".plan-dropdown");
        dropdown.removeClass("hidden");  // Đảm bảo loại bỏ class "hidden" để hiển thị dropdown
        const planList = dropdown.find("#plan-list");
        planList.empty(); // Xóa danh sách cũ nếu có

        // Lấy danh sách kế hoạch từ localStorage (nếu có)
        const plans = JSON.parse(localStorage.getItem("plans")) || [];
        console.log(plans.length);

        // Nếu không có kế hoạch nào, chỉ hiển thị tùy chọn tạo sub-plan mới
        if (plans.length === 0) {
            console.log(rec);
            const newSubPlanItem = $("<li>").addClass("p-2 hover:bg-gray-100 cursor-pointer text-blue-500")
                    .text("+ Create New Sub-Plan")
                    .click(function () {
                        const dateNow = new Date(Date.now());
                        const day = String(dateNow.getDate()).padStart(2, '0');
                        const month = String(dateNow.getMonth() + 1).padStart(2, '0');
                        const year = dateNow.getFullYear();
                        const formattedDate = `${year}-${month}-${day}`;
                        const newPlan = {
                            title: "New Plan",
                            date: formattedDate,
                            subPlans: [],
                        };

                        plans.push(newPlan);
                        localStorage.setItem("plans", JSON.stringify(plans));

                        createNewSubPlan(plans.length - 1, rec);
                        dropdown.addClass("hidden"); // Đóng dropdown sau khi tạo sub-plan
                    });
            planList.append(newSubPlanItem);
        } else {
            // Nếu có kế hoạch, hiển thị danh sách sub-plans
            plans.forEach(function (plan, planIndex) {
                plan.subPlans.forEach(function (subPlan, subPlanIndex) {
                    const listItem = $("<li>").addClass("p-2 hover:bg-gray-100 cursor-pointer")
                            .text(`${plan.title} - ${subPlan.title}`)
                            .click(function () {
                                addToSubPlan(planIndex, subPlanIndex, rec);
                                dropdown.addClass("hidden");
                            });
                    planList.append(listItem);
                });

                const newSubPlanItem = $("<li>").addClass("p-2 hover:bg-gray-100 cursor-pointer text-blue-500")
                        .text(`+ Create New Sub-Plan for ${plan.title}`)
                        .click(function () {
                            createNewSubPlan(planIndex, rec);
                            dropdown.addClass("hidden");
                        });
                planList.append(newSubPlanItem);
            });
        }
    });
    $("#voteBtn").on("click", function () {
        // Get the user ID

        // Create the LikeRequest object
        var voteRequest = {
            userId: userId,
            recId: recId
        };

        // Send the AJAX request to the backend to like the post
        $.ajax({
            url: '/api/v1/recommendations/vote', // Adjust the URL according to your backend API endpoint
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(voteRequest), // Send the likeRequest object as JSON
            success: function (response) {
                console.log(response);
                fetchRecommendation();
                // Handle the response here if needed (e.g., update the like count, change the button color, etc.)
            },
            error: function (xhr, status, error) {
                console.error('Error voting:', error);
                // Handle the error if needed
            }
        });
    });
// Đóng dropdown khi nhấn nút "Close"
    $(".close-dropdown").click(function () {
        $(".plan-dropdown").addClass("hidden");
    });
    

// Đóng dropdown khi click vào nút "Close"
    $(".close-dropdown").click(function () {
        $(".plan-dropdown").addClass("hidden");
    });

// Hàm tạo mới sub-plan
    function createNewSubPlan(planIndex, rec) {
        const plans = JSON.parse(localStorage.getItem("plans")) || [];
        const selectedPlan = plans[planIndex];

        // Tạo mới một sub-plan với recommendation đã chọn
        const newSubPlan = {
            title: `New Sub-Plan ${selectedPlan.subPlans.length + 1}`, // Tiêu đề sub-plan mới
            condition: "None",
            planItems: [{
                    recId: rec.id,
                    location: rec.location,
                    title: rec.title,
                    max_budget: rec.maxBudget,
                    min_budget: rec.minBudget,
                    description: "",
                    startDate: rec.startDate,
                    startTime:"00:00",
                    endTime:"23:59",
                    endDate: rec.endDate,
                    address: rec.address,
                    recStartTime:rec.recStartTime,
            recEndTime:rec.recEndTime,
                }]
        };

        // Thêm sub-plan mới vào kế hoạch
        selectedPlan.subPlans.push(newSubPlan);

        // Lưu kế hoạch cập nhật vào localStorage
        localStorage.setItem("plans", JSON.stringify(plans));

        alert(`New sub-plan "${newSubPlan.title}" created under "${selectedPlan.title}" and item added!`);
    }

// Hàm thêm item vào sub-plan đã có
    function addToSubPlan(planIndex, subPlanIndex, rec) {
        const plans = JSON.parse(localStorage.getItem("plans")) || [];
        const selectedPlan = plans[planIndex];
        const selectedSubPlan = selectedPlan.subPlans[subPlanIndex];

        // Kiểm tra nếu item đã có trong sub-plan


        // Thêm item vào sub-plan
        const newItem = {
            recId: rec.id,
            title: rec.title,
            location: rec.location,
            min_budget: rec.minBudget,
            max_budget: rec.maxBudget,
            description: "",
            startDate: rec.startDate,
            endDate: rec.endDate,
            recStartTime:rec.recStartTime,
            recEndTime:rec.recEndTime,
            startTime:"00:00",
            endTime:"23:59",
            address: rec.address,
            
        };

        selectedSubPlan.planItems.push(newItem);

        // Lưu kế hoạch đã cập nhật vào localStorage
        localStorage.setItem("plans", JSON.stringify(plans));

        alert(`Item added to: ${selectedPlan.title} - ${selectedSubPlan.title}`);
    }
    // Fetch data using AJAX
    console.log(userId)
    function fetchRecommendation() {
        $.ajax({
            url: `/api/v1/recommendations/${recId}`, // Thay thế bằng URL API của bạn
            method: 'GET',
            dataType: 'json',
            success: function (data) {
                renderRecommendation(data);
                checkIfVoted()
            },
            error: function (xhr, status, error) {
                console.error('Error fetching recommendation:', error);
                $('#recommendationContainer').html('<p class="text-red-500">Error loading recommendations.</p>');
            }
        });
    }
    $('#submitRating').click(function () {
        // Lấy giá trị từ biểu mẫu
        const ratingValue = $('input[name="rating"]:checked').val();
        const content = $('#commentTxt').val();
        const recommendationId = recId; // Thay thế bằng ID thực tế của recommendation

        // Kiểm tra dữ liệu
        if (!ratingValue || !content.trim()) {
            alert('Please fill in all fields.');
            return;
        }

        // Gửi dữ liệu qua AJAX
        $.ajax({
            url: `/api/v1/recommendations/${recId}/ratings`,
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                userId: userId, // Thay thế bằng ID của người dùng thực tế
                ratingValue: ratingValue,
                content: content.trim()
            }),
            success: function (response) {
                alert(response);
                location.reload();
//                location.reload(); // Làm mới trang nếu cần thiết
            },
            error: function (xhr, status, error) {
                console.error('Error adding rating:', error);
                alert('Failed to add rating. Please try again.');
            }
        });
    });
    function checkIfVoted() {
        $.ajax({
            url: `/api/v1/recommendations/${recId}/isVoted/${userId}`,
            type: 'GET',
            success: function (response) {
                if (response) {
                    
                    $("#voteBtn").addClass("text-green-600");  // Example of adding a liked class
                }else {
                $("#voteBtn").removeClass("text-green-600");
                console.log(response);
            }
            },
            error: function (xhr, status, error) {
                console.error("Error checking like status:", error);
            }
        });
    }
    // Function to render fetched data
    function renderRecommendation(data) {
        const {address, title, email, location, minBudget, maxBudget, description, rating, ratingSize, image, tags, ratings, startDate, endDate,startTime,endTime} = data;

        // Update Title, Location, Budget, and Description
        $('#title').text(title);
        $('#location').text("Location: " + location);
        $('#address').text("Address: " + address);
        $('#minBudget').text(`$${minBudget}`);
        $('#maxBudget').text(`$${maxBudget}`);
        $('#description').text(description);
        $('#address').text('Address: ' + address);
        $('#email').text("Contact: " + email);
        $("#voteCount").text(rating);
        $('#recStartTime').text("Opening Time: " + startTime);
        $('#recEndTime').text("Closed Time: " + endTime);
        if (startDate) {
            $('#startDate').text("Start Date: " + formatDateTime(startDate));
        } else {
            
            $('#startDate').addClass('hidden')
        }

        if (endDate) {
            $('#endDate').text("End Date: " + formatDateTime(endDate));
        } else {
            $('#endDate').addClass('hidden')
        }
        // Update Main Image
        if (image && image.length > 0) {
            $('#mainImage').attr('src', image[0]); // Set the first image as main image
        }

        // Render Thumbnails
        $('#thumbnailContainer').empty(); // Clear existing thumbnails
        if (image) {
            image.forEach(imgSrc => {
                const thumbnail = $(`
                    <img src="${imgSrc}" alt="Thumbnail" class="size-16 sm:size-20 object-cover rounded-md cursor-pointer opacity-60 hover:opacity-100 transition duration-300">
                `);
                thumbnail.click(() => changeImage(imgSrc)); // Attach click event
                $('#thumbnailContainer').append(thumbnail);
            });
        }
        $('#tags').empty(); // Clear existing tags
        
    if (tags && tags.length > 0) {
        tags.forEach(tag => {
            const tagElement = $(`
                <a href="/recommendation/ptag/${tag.title}" class="px-2 py-1 bg-blue-200 text-blue-800 rounded-md mr-2 mb-2">#${tag.title}</a>
            `);
            $('#tags').append(tagElement);
        });
    }
    }
    function formatDateTime(isoString) {
        const date = new Date(isoString);

        const hours = date.getHours().toString().padStart(2, "0");
        const minutes = date.getMinutes().toString().padStart(2, "0");
        const day = date.getDate().toString().padStart(2, "0");
        const month = (date.getMonth() + 1).toString().padStart(2, "0");
        const year = date.getFullYear();

        return `${hours}:${minutes} ${day}/${month}/${year}`;
    }
    // Change Main Image on Thumbnail Click
    function changeImage(src) {
        $('#mainImage').attr('src', src);
    }

    // Initialize on Page Load
    fetchRecommendation();
});
function formatDate(dateString) {
    const date = new Date(dateString);
    const day = String(date.getDate()).padStart(2, "0");
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const year = date.getFullYear();
    return `${day}/${month}/${year}`;
}
function createNewSubPlan(planIndex, rec) {
    const plans = JSON.parse(localStorage.getItem("plans")) || [];
    const selectedPlan = plans[planIndex];

    // Create a new sub-plan with the recommendation included
    const newSubPlan = {
        title: `New Sub-Plan ${selectedPlan.subPlans.length + 1}`, // Give it a unique title
        condition: "None",
        planItems: [
            {
                recId: recId,
                title: rec.title,
                location: rec.location,
                min_budget: rec.minBudget,
                max_budget: rec.maxBudget,
                description: "",
            },
        ],
    };

    // Add the new sub-plan to the selected plan
    selectedPlan.subPlans.push(newSubPlan);

    // Save updated plans to localStorage
    localStorage.setItem("plans", JSON.stringify(plans));

    alert(`New sub-plan "${newSubPlan.title}" created under "${selectedPlan.title}" and item added!`);
}
document.getElementById("addToPlan").addEventListener("click", () => {
    const dropdown = document.querySelector(".plan-dropdown");
    dropdown.classList.toggle("hidden");

    const planList = dropdown.querySelector("#plan-list");
    planList.innerHTML = ""; // Xóa danh sách cũ nếu có

    const plans = JSON.parse(localStorage.getItem("plans")) || [];

    if (plans.length === 0) {
        // Nếu không có kế hoạch nào
        const newSubPlanItem = document.createElement("li");
        newSubPlanItem.className = "p-2 hover:bg-gray-100 cursor-pointer text-blue-500";
        newSubPlanItem.textContent = "+ Create New Sub-Plan";
        newSubPlanItem.addEventListener("click", () => {
            const dateNow = new Date(Date.now());
            const day = String(dateNow.getDate()).padStart(2, '0');
            const month = String(dateNow.getMonth() + 1).padStart(2, '0');
            const year = dateNow.getFullYear();
            const formattedDate = `${year}-${month}-${day}`;
            const newPlan = {
                title: "New Plan",
                date: formattedDate,
                subPlans: [],
            };

            plans.push(newPlan);
            localStorage.setItem("plans", JSON.stringify(plans));

            createNewSubPlan(plans.length - 1);
            dropdown.classList.add("hidden");
        });
        planList.appendChild(newSubPlanItem);
    } else {
        // Hiển thị danh sách các kế hoạch
        plans.forEach((plan, planIndex) => {
            plan.subPlans.forEach((subPlan, subPlanIndex) => {
                const listItem = document.createElement("li");
                listItem.className = "p-2 hover:bg-gray-100 cursor-pointer";
                listItem.textContent = `${plan.title} - ${subPlan.title}`;
                listItem.addEventListener("click", () => {
                    addToSubPlan(planIndex, subPlanIndex);
                    dropdown.classList.add("hidden");
                });
                planList.appendChild(listItem);
            });

            // Tùy chọn tạo sub-plan mới
            const newSubPlanItem = document.createElement("li");
            newSubPlanItem.className = "p-2 hover:bg-gray-100 cursor-pointer text-blue-500";
            newSubPlanItem.textContent = `+ Create New Sub-Plan for ${plan.title}`;
            newSubPlanItem.addEventListener("click", () => {
                createNewSubPlan(planIndex);
                dropdown.classList.add("hidden");
            });
            planList.appendChild(newSubPlanItem);
        });
    }
});

// Nút đóng dropdown
document.querySelector(".close-dropdown").addEventListener("click", () => {
    const dropdown = document.querySelector(".plan-dropdown");
    dropdown.classList.add("hidden");
});
const postsContainer = document.getElementById('posts');
const paginationContainer = document.getElementById('pagination');
const api = `/api/v1/post/rec/${recId}`;
function fetchPosts(page = 0) {
    console.log(api);
    fetch(`${api}?page=${page}`)
            .then(response => response.json())
            .then(data => {
                // Clear previous posts
                postsContainer.innerHTML = '';

                // Loop through the posts and insert them into the DOM
                const MAX_LENGTH = 50;  // Set the character limit

                data.content.forEach(post => {
                    const postDiv = document.createElement('div');
                    postDiv.classList.add('bg-white', 'p-4', 'shadow-md', 'rounded-lg', 'w-full'); // Ensure the div takes full width

                    // Truncate content if it exceeds the MAX_LENGTH
                    let truncatedContent = post.content;
                    if (truncatedContent.length > MAX_LENGTH) {
                        truncatedContent = truncatedContent.slice(0, MAX_LENGTH) + '...'; // Add ellipsis
                    }

                    // Set inner HTML with content
                    postDiv.innerHTML = `
        <a href="/social/posts/${post.id}">
            <h3 class="text-3xl font-semibold">${post.title}</h3>
            <p class="text-2xl text-gray-600 mt-2 break-words">${truncatedContent}</p>  <!-- Use break-words for long text -->
            <p class="text-xl mt-4">${post.likeCount} Like${post.likeCount > 1 ? "s" : ""}</p>
            <p class="text-xl mt-4">${post.commentCount} Comment${post.commentCount > 1 ? "s" : ""}</p>
            <p class="text-xl text-blue-500 mt-4">- ${post.username}</p>
        </a>
    `;

                    // Append to the container
                    postsContainer.appendChild(postDiv);
                });

                // Create pagination links
                paginationContainer.innerHTML = ''; // Xóa nội dung cũ

                for (let i = 0; i < data.totalPages; i++) {
                    const pageLink = document.createElement('a');
                    pageLink.href = '#';
                    pageLink.textContent = i + 1;

                    // Áp dụng các lớp Tailwind
                    pageLink.className = `
        px-4 py-2 border rounded-lg mx-1
        text-gray-700 hover:text-white hover:bg-blue-500
        transition duration-200 ease-in-out
    `;

                    // Xử lý sự kiện khi nhấn vào liên kết
                    pageLink.onclick = (e) => {
                        e.preventDefault();
                        currentPage = i;
                        fetchPosts(i);
                    };

                    paginationContainer.appendChild(pageLink);
                }
            });
}

// Initial fetch


