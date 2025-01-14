$(document).ready(function () {

    const path = window.location.pathname; // "/planning/123"
    const regex = /\/planning\/(\d+)/;  // Matches "/planning/{id}" where id is a number
    const match = path.match(regex);
    const planId = match[1];  // Extract the plan ID from the URL
    console.log("Plan ID: ", planId);


    // Fetch plan details using AJAX
    $.ajax({
        url: `/api/v1/planning/${planId}`, // Replace with your API endpoint
        method: "GET",
        success: function (data) {
            console.log(data);
            renderPlan(data);
        },
        error: function (err) {
            console.log(err);
        }
    });

    function renderPlan(plan) {
        $("#planContainer").empty();

        // Render tiêu đề kế hoạch chính
        const headerHtml = `
        <div class="mt-10 bg-white shadow-md rounded-lg p-6 mb-6">
            <h1 class="text-2xl font-bold text-gray-800">Plan Title: ${plan.title}</h1>
            <p class="text-sm text-gray-500 mt-2">Plan Time: ${formatDate(plan.time)}</p>
            <button id="editPlan" class="bg-transparent hover:bg-blue-500 text-blue-700 font-semibold hover:text-white py-2 px-4 border border-blue-500 hover:border-transparent rounded">
                Edit
            </button>
            <button id="deletePlan" class="ml-4 bg-transparent hover:bg-red-500 text-red-700 font-semibold hover:text-white py-2 px-4 border border-red-500 hover:border-transparent rounded">
                Delete
            </button>
        </div>
    `;
        $("#planContainer").append(headerHtml);

        // Lặp qua từng kế hoạch con
        plan.planDetailDtos.forEach(subPlan => {

            const subPlanHtml = `
            <div class="mt-6 bg-gray-50 shadow-md rounded-lg p-4">
                <h2 class="text-xl font-semibold text-gray-700">${subPlan.title} - ${subPlan.planCondition}</h2>
                <p class="text-gray-600 mt-2">Budget: ${subPlan.budget ? `$${subPlan.budget}` : 'None'}</p>
                <div class="space-y-4">
                    ${subPlan.recs.map(activity => `
                        <div class="bg-white shadow-md rounded-lg p-4">
                            <h3 class="text-lg font-semibold text-gray-700">${activity.title || "Your own thing you want to do"}</h3>
                            <p class=" mt-2">Location: ${activity.location || 'None'}</p>
                            <p class=" mt-2">Address: ${activity.address || 'None'}</p>
                            <p class=" mt-2">Description: ${activity.description || "No description available."}</p>
                            <div class="flex items-center justify-between text-sm text-gray-500 mt-4">
                                <span>Min Budget: ${activity.minBudget ? `$${activity.minBudget}` : 'None'}</span>
                                <span>Max Budget: ${activity.maxBudget ? `$${activity.maxBudget}` : 'None'}</span>
                            </div>
                            <div class="flex items-center justify-between text-sm text-gray-500 mt-4">
                                <span>Start Time: ${formatTime(activity.startTime)}</span>
                                <span>End Time: ${formatTime(activity.endTime)}</span>

                            </div>
                             ${activity.recId !== null && activity.email !== "None" ? `
    <div class="flex items-center justify-between text-sm text-gray-500 mt-4">
        <span>Status: ${activity.isBooked ? 'Booked' : 'Not Booked'}</span>
        ${activity.isBooked
                        ? '<span class="text-green-500">Booked</span>'
                        : ((((new Date(plan.time) >= new Date(activity.startDate) && (new Date(plan.time) <= new Date(activity.endDate)))
                                && (new Date('1970-01-01' + `T` + `${activity.startTime}`) >= new Date('1970-01-01' + `T` + `${activity.recStartTime}`) && new Date('1970-01-01' + `T` + `${activity.endTime}`) <= new Date('1970-01-01' + `T` + `${activity.recEndTime}`)))
                                || (activity.startDate === null && activity.endDate === null)
                                )
                                ? `
                    <button
                        class="book-btn bg-transparent hover:bg-green-500 text-green-700 font-semibold hover:text-white py-2 px-4 border border-green-500 hover:border-transparent rounded"
                        data-activity-recId="${activity.recId}"
                        data-activity-prId="${activity.id}"
                        data-start-time="${activity.startTime}" 
                        data-end-time="${activity.endTime}" 
                        data-booking-date="${formatISOToDate(plan.time)}">
                        Book
                    </button>
                `
                                : '<span class="text-gray-400">Booking not available</span>'
                                )
                        }
    </div>
` : ''}
                        </div>
                    `).join("")}
                </div>
            </div>
        `;
            $("#planContainer").append(subPlanHtml);
        });

        // Xử lý sự kiện "Edit Plan"
        $("#editPlan").click(function () {
            localStorage.setItem("planId", JSON.stringify(plan.id));
            localStorage.setItem("userId", JSON.stringify(plan.userId));
            const formattedPlan = {
                title: plan.title,
                date: formatISOToDate(plan.time), // Format to dd/mm/yyyy
                subPlans: plan.planDetailDtos.map((subPlan) => ({
                        title: subPlan.title,
                        condition: subPlan.planCondition,
                        planItems: subPlan.recs.map((item) => ({
                                recId: item.recId,
                                title: item.title || "Your own thing you want to do",
                                address: item.address || "",
                                location: item.location || "",
                                startDate: item.startDate,
                                endDate: item.endDate,
                                description: item.description || "",
                                startTime: item.startTime,
                                endTime: item.endTime,
                                recStartTime:item.recStartTime,
                                recEndTime:item.recEndTime,
                            })),
                    })),
            };
            console.log(plan.time);
            localStorage.setItem("plans", JSON.stringify([formattedPlan]));
            window.location.href = "/planning";
        });

        // Xử lý sự kiện "Delete Plan"
        $("#deletePlan").click(function () {
            if (confirm("Are you sure you want to delete this plan?")) {
                $.ajax({
                    url: `/api/v1/planning/${plan.id}`,
                    method: 'DELETE',
                    success: function () {
                        alert("Plan deleted successfully!");
                        history.back();
                    },
                    error: function (err) {
                        console.error("Failed to delete the plan:", err);
                        alert("Error deleting plan. Please try again.");
                    }
                });
            }
        });

        // Xử lý sự kiện "Book"
        $(".book-btn").click(function () {
            const recId = $(this).data("activity-recid");
            const prId = $(this).data("activity-prid");
            const startTime = $(this).data("start-time");
            const endTime = $(this).data("end-time");
            const bookingDate = $(this).data("booking-date");

            // Find all activities with the same id and startTime
            const activitiesToBook = $(".book-btn")
                    .filter((_, btn) => {
                        const btnRecId = $(btn).data("activity-recid");
                        const btnStartTime = $(btn).data("start-time");
                        const btnEndTime = $(btn).data("end-time");
                        return btnRecId === recId && btnStartTime === startTime&&btnEndTime === endTime;
                    })
                    .map((_, btn) => ({
                            recommendationId: $(btn).data("activity-recid"),
                            planningRecId: $(btn).data("activity-prid"),
                            bookingStartTime: $(btn).data("start-time"),
                            bookingEndTime: $(btn).data("end-time"),
                            bookingDate: $(btn).data("booking-date"),
                        }))
                    .get();

            if (!activitiesToBook.length) {
                alert("No activities found to book.");
                return;
            }

            console.log("Booking these activities:", activitiesToBook);

            // Create a booking request for all matched activities
            const bookingRequest = {
                userId: userId, // Ensure userId is defined in the scope
                recommendationId:recId,
                activities: activitiesToBook,
                bookingDate:bookingDate
            };
            console.log(bookingRequest);
            $.ajax({
                url: `/api/v1/booking`,
                method: "POST",
                contentType: "application/json",
                data: JSON.stringify(bookingRequest),
                success: function (response) {
                    alert("Activity booked successfully!");
                    location.reload();
                },
                error: function (err) {
                    console.error("Failed to book activity:", err);
                    alert("Error booking activity. Please try again.");
                }
            });
        });

        function formatISOToDate(inputTime) {
            if (!inputTime) {
                console.error("Input time is undefined or null.");
                return null;
            }

            // Parse the input time as a Date object
            const date = new Date(inputTime);

            if (isNaN(date)) {
                console.error("Invalid date format.");
                return null;
            }

            // Format as yyyy-MM-dd
            const year = date.getFullYear();
            const month = String(date.getMonth() + 1).padStart(2, '0'); // Months are 0-based
            const day = String(date.getDate()).padStart(2, '0');

            return `${year}-${month}-${day}`;
        }

        function formatDate(dateTime) {
            const date = new Date(dateTime);
            const day = String(date.getDate()).padStart(2, '0');
            const month = String(date.getMonth() + 1).padStart(2, '0'); // Tháng bắt đầu từ 0
            const year = date.getFullYear();
            return `${day}/${month}/${year}`;
        }

        function formatTime(time) {
            const [hour, minute] = time.split(":");
            const date = new Date();
            date.setHours(hour, minute);
            return date.toLocaleTimeString([], {hour: '2-digit', minute: '2-digit'});
        }
    }

});
