// Tải kế hoạch từ localStorage và hiển thị trên giao diện
function loadPlanFromLocalStorage() {
    const plans = JSON.parse(localStorage.getItem("plans")) || {};
    const plan = plans[0];
    const datePlan = document.getElementById("planDate");
    datePlan.value = plan.date;
    datePlan.addEventListener("change", () => {
        updatePlanDate(datePlan.value);
    });
    const listContainer = document.getElementById("planListContainer");
    listContainer.innerHTML = ""; // Clear old content
    const planTitle = document.getElementById("planTitle");
    planTitle.addEventListener("input", (event) => {
        const newTitle = event.target.value;
        handleTitleChange(newTitle);
    });
    if (plan.title) {
        planTitle.value = plan.title;
    } else {
        planTitle.value = "My Plan";
    }
    const addSubPlanButton = document.createElement('button');
    addSubPlanButton.textContent = "Add SubPlan";
    addSubPlanButton.classList.add('add-subplan-btn', 'px-4', 'py-2', 'bg-blue-500', 'text-white', 'rounded', 'mb-4');
    addSubPlanButton.addEventListener('click', addSubPlan);
    listContainer.appendChild(addSubPlanButton);
    if (plan.subPlans && plan.subPlans.length > 0) {
        plan.subPlans.forEach((subPlan, subPlanIndex) => {
            const subPlanItem = document.createElement("div");
            subPlanItem.classList.add("sub-plan-item", "p-4", "mb-4", "bg-white", "shadow-md", "rounded-lg");

            subPlanItem.innerHTML = `
                <p class="text-lg font-medium text-gray-900">Title</p>
                <input type="text" value="${subPlan.title}" class="sub-plan-title text-2xl text-gray-500 mb-4" data-subplan-index="${subPlanIndex}" />
                <p class="text-lg font-medium text-gray-900">Case</p>
                <input type="text" value="${subPlan.condition}" class="sub-plan-condition text-xl text-gray-500 mb-4" data-subplan-index="${subPlanIndex}" />
            <button 
        class="delete-subplan-btn m-3 px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600 transition ml-2">
        Delete SubPlan
    </button>
                <button 
        class="add-plan-item-btn m-3 px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600 transition">
        Add Plan Item
    </button>
    
                <ul id="planSubListContainer" class="plan-items-list space-y-4"></ul>
            `;

            // Lắng nghe sự kiện thay đổi của tiêu đề và điều kiện
            const titleInput = subPlanItem.querySelector(".sub-plan-title");
            const conditionInput = subPlanItem.querySelector(".sub-plan-condition");

            titleInput.addEventListener("input", (e) => handleSubPlanTitleChange(e, subPlanIndex));
            conditionInput.addEventListener("input", (e) => handleSubPlanConditionChange(e, subPlanIndex));
            const addPlanItemButton = subPlanItem.querySelector(".add-plan-item-btn");
            addPlanItemButton.addEventListener('click', () => addPlanItem(subPlanIndex));

            // Xử lý xóa subPlan
            const deleteSubPlanButton = subPlanItem.querySelector(".delete-subplan-btn");
            deleteSubPlanButton.addEventListener('click', () => deleteSubPlan(subPlanIndex, subPlanItem));

            const planItemsList = subPlanItem.querySelector(".plan-items-list");

            subPlan.planItems.forEach((item, index) => {
                const listItem = document.createElement("li");
                listItem.classList.add("plan-item", "py-4", "px-4", "border", "border-gray-300", "rounded-lg", "flex", "justify-between", "items-center", "cursor-move");
                listItem.draggable = true;
                listItem.id = `plan-item-${item.recId}`;
                if (item.location !== "") {
                    listItem.innerHTML = `
                    <div class="flex-1">
                        <p class="title_location text-lg font-medium text-gray-900">${item.title} - ${item.location}</p>
                        <p class="address text-lg font-medium text-gray-900">Address: ${item.address}</p>
                        <textarea data-item-index="${index}" placeholder="What are you plan to do"
                            class="m-3 editable text-sm text-gray-600 w-full bg-transparent mt-1 focus:ring-2 focus:ring-blue-500 focus:outline-none rounded-md"
                            rows="3" 
                            wrap="soft">${item.description || ''}</textarea>
                    </div>
                    <div class="flex items-center space-x-2 m-5" >
                        <input type="time" class="start-time border-gray-300 rounded-lg text-sm px-2 py-1 focus:ring-2 focus:ring-blue-500" value="${item.startTime || "09:00"}">
                        <span class="text-gray-500">-</span>
                        <input type="time" class="end-time border-gray-300 rounded-lg text-sm px-2 py-1 focus:ring-2 focus:ring-blue-500" value="${item.endTime || "10:00"}">
                        <input type="time" class="hidden rec-start-time border-gray-300 rounded-lg text-sm px-2 py-1 focus:ring-2 focus:ring-blue-500" value="${item.recStartTime}">
                        <span class="text-gray-500">-</span>
                        <input type="time" class="hidden rec-end-time border-gray-300 rounded-lg text-sm px-2 py-1 focus:ring-2 focus:ring-blue-500" value="${item.recEndTime }">
                    </div>
                    <button class="delete-btn text-red-500 ml-4 hover:text-red-700 transition">Delete</button>
                `;
                } else {
                    listItem.innerHTML = `
                    <div class="flex-1">
                        <p class="title_location text-lg font-medium text-gray-900">${item.title}</p>
                        <p class="address text-lg font-medium text-gray-900">Address: ${item.address || "None"}</p>
                        <textarea data-item-index="${index}"
                            class="m-3 editable text-sm text-gray-600 w-full bg-transparent mt-1 focus:ring-2 focus:ring-blue-500 focus:outline-none rounded-md" 
                            rows="3" 
                            wrap="soft" >${item.description || ''}</textarea>
                    </div>
                    <div class="flex items-center space-x-2 m-5">
                        <input type="time" class="start-time border-gray-300 rounded-lg text-sm px-2 py-1 focus:ring-2 focus:ring-blue-500" value="${item.startTime || "09:00"}">
                        <span class="text-gray-500">-</span>
                        <input type="time" class="end-time border-gray-300 rounded-lg text-sm px-2 py-1 focus:ring-2 focus:ring-blue-500" value="${item.endTime || "10:00"}">
                    </div>
                    <button class="delete-btn text-red-500 ml-4 hover:text-red-700 transition">Delete</button>
                `;
                }
                const startTimeInput = listItem.querySelector(".start-time");
                startTimeInput.addEventListener("change", (e) => {
                    handleTimeChange(subPlanIndex, index, "startTime", e.target.value);
                });

                // Gắn sự kiện lưu thay đổi cho thời gian kết thúc
                const endTimeInput = listItem.querySelector(".end-time");
                endTimeInput.addEventListener("change", (e) => {
                    handleTimeChange(subPlanIndex, index, "endTime", e.target.value);
                });

                const textArea = listItem.querySelector("textarea");
                textArea.addEventListener("input", () => handleTextChange(subPlanIndex, index, textArea.value));

                const deleteBtn = listItem.querySelector(".delete-btn");
                deleteBtn.addEventListener("click", () => deletePlanItem(subPlanIndex, index, listItem));

                // Enable drag and drop for plan item
                enableDragAndDrop(listItem);
                planItemsList.appendChild(listItem);
            });

            // Append the sub-plan item to the container
            document.getElementById("planListContainer").appendChild(subPlanItem);
        });
    }
}
function handleTimeChange(subPlanIndex, itemIndex, key, value) {
    const plans = JSON.parse(localStorage.getItem("plans")) || [];

    // Tìm và cập nhật giá trị tương ứng
    if (plans.length > 0 && plans[0].subPlans) {
        const subPlan = plans[0].subPlans[subPlanIndex];
        if (subPlan && subPlan.planItems && subPlan.planItems[itemIndex]) {
            subPlan.planItems[itemIndex][key] = value;
        }
    }
    console.log(value);
    // Lưu kế hoạch cập nhật vào localStorage
    localStorage.setItem("plans", JSON.stringify(plans));
    console.log("Updated plans:", plans);
}
function addSubPlan() {
    const plans = JSON.parse(localStorage.getItem("plans")) || {};
    const newSubPlan = {
        title: "New SubPlan",
        condition: "None",
        planItems: [],
    };

    plans[0].subPlans.push(newSubPlan);
    localStorage.setItem("plans", JSON.stringify(plans));
    loadPlanFromLocalStorage(); // Reload the plan to reflect the new subPlan
}
function addPlanItem(subPlanIndex) {
    const plans = JSON.parse(localStorage.getItem("plans")) || {};
    const subPlan = plans[0].subPlans[subPlanIndex];

    const newItem = {
        recId: null,
        // Use current timestamp as unique ID
        title: "Your Own Thing You want to do",
        location: "",
        description: "",
        startTime: "09:00",
        endTime: "10:00",
        address: ""
    };
    console.log(subPlan);
    subPlan.planItems.push(newItem);
    localStorage.setItem("plans", JSON.stringify(plans));
    loadPlanFromLocalStorage(); // Reload the plan to reflect the new plan item
}
function updatePlanDate(newDate) {
    const plans = JSON.parse(localStorage.getItem("plans")) || {};
    plans[0].date = newDate; // Cập nhật ngày
    console.log(plans[0].date)
    localStorage.setItem("plans", JSON.stringify(plans));
    console.log("Updated plan:", plans);
}
// Xóa SubPlan
function deleteSubPlan(subPlanIndex, subPlanItem) {
    // Lấy dữ liệu từ localStorage
    const plans = JSON.parse(localStorage.getItem("plans")) || {};

    // Xóa subPlan khỏi mảng
    plans[0].subPlans.splice(subPlanIndex, 1);

    // Lưu lại dữ liệu đã cập nhật vào localStorage
    localStorage.setItem("plans", JSON.stringify(plans));

    // Xóa subPlan khỏi giao diện
    subPlanItem.remove();

    // Cập nhật chỉ số của tất cả subPlan còn lại trong giao diện
    const subPlanItems = document.querySelectorAll(".sub-plan-item");
    subPlanItems.forEach((item, index) => {
        // Cập nhật chỉ số subPlanIndex trong DOM
        const titleInput = item.querySelector(".sub-plan-title");
        const conditionInput = item.querySelector(".sub-plan-condition");
        titleInput.setAttribute("data-subplan-index", index);
        conditionInput.setAttribute("data-subplan-index", index);

        // Cập nhật sự kiện xóa
        const deleteButton = item.querySelector(".delete-subplan-btn");
        deleteButton.onclick = () => deleteSubPlan(index, item);
    });
    loadPlanFromLocalStorage();
}

// Xử lý thay đổi mô tả của plan item
function handleTextChange(subPlanIndex, itemIndex, newDescription) {
    // Parse the stored plans data from localStorage or initialize as an empty array if not found
    const plans = JSON.parse(localStorage.getItem("plans")) || [];
    console.log(`Updating description for subPlan ${subPlanIndex}, planItem ${itemIndex}`);

    // Check if the provided indices are valid
    if (
            plans.length > 0 &&
            plans[0].subPlans &&
            plans[0].subPlans[subPlanIndex] &&
            plans[0].subPlans[subPlanIndex].planItems &&
            plans[0].subPlans[subPlanIndex].planItems[itemIndex]
            ) {
        // Update the description of the specified plan item
        plans[0].subPlans[subPlanIndex].planItems[itemIndex].description = newDescription;
    } else {
        console.error("Invalid subPlanIndex or itemIndex.");
        return;
    }

    // Save the updated plans data back to localStorage
    localStorage.setItem("plans", JSON.stringify(plans));
    console.log("Updated plans saved to localStorage:", plans);
}

// Xóa một plan item
function deletePlanItem(subPlanIndex, itemIndex, listItem) {
    // Parse the stored plans data from localStorage or initialize as an empty array if not found
    const plans = JSON.parse(localStorage.getItem("plans")) || [];
    console.log(`Deleting item from subPlan ${subPlanIndex}, itemIndex ${itemIndex}`);

    // Ensure the provided indices are valid
    if (
            plans.length > 0 &&
            plans[0].subPlans &&
            plans[0].subPlans[subPlanIndex] &&
            plans[0].subPlans[subPlanIndex].planItems &&
            plans[0].subPlans[subPlanIndex].planItems[itemIndex]
            ) {
        // Remove the item from the specified subPlan
        plans[0].subPlans[subPlanIndex].planItems.splice(itemIndex, 1);

        // Save the updated plans data back to localStorage
        localStorage.setItem("plans", JSON.stringify(plans));
        console.log("Updated plans after deletion:", plans);
        loadPlanFromLocalStorage();
        // Remove the item from the DOM
        listItem.remove();
    } else {
        console.error("Invalid subPlanIndex or itemIndex.");
    }
}


// Cập nhật lại thứ tự các mục trong kế hoạch khi kéo thả
function updatePlanOrderInLocalStorage() {
    const planListContainer = document.getElementById("planListContainer");
    if (!planListContainer) {
        console.error("Plan list container not found.");
        return;
    }

    const subPlanElements = planListContainer.querySelectorAll(".sub-plan-item");

    // Map through subPlan elements to construct the updated subPlans array
    const updatedSubPlans = Array.from(subPlanElements).map((subPlanElement, subPlanIndex) => {
        const title = subPlanElement.querySelector(".sub-plan-title")?.value || "";
        const condition = subPlanElement.querySelector(".sub-plan-condition")?.value || "";

        // Process plan items within each subPlan
        const planItemElements = subPlanElement.querySelectorAll(".plan-item");

        const planItems = Array.from(planItemElements).map((planItemElement) => {
            const idString = planItemElement.id.replace("plan-item-", "");
            const recId = idString && !isNaN(parseInt(idString, 10)) ? parseInt(idString, 10) : null;
            const titleAndLocation = planItemElement.querySelector(".title_location")?.textContent.split(" - ") || [];
            const title = titleAndLocation[0]?.trim() || "Your Own Thing You want to do";
            const location = titleAndLocation.slice(1).join(" - ").trim();
            const description = planItemElement.querySelector("textarea")?.value || "";
            const startTime = planItemElement.querySelector(".start-time")?.value || "";
            const endTime = planItemElement.querySelector(".end-time")?.value || "";
            const recStartTime = planItemElement.querySelector(".rec-start-time")?.value || "";
            const recEndTime = planItemElement.querySelector(".rec-end-time")?.value || "";
            const address = planItemElement.querySelector(".address")?.value || "";
            return {
                recId,
                title,
                location,
                description,
                startTime,
                endTime,
                address,
                recStartTime,
                recEndTime
            };
        });

        return {
            title,
            condition,
            planItems,
        };
    });

    // Update the plans structure
    const plans = JSON.parse(localStorage.getItem("plans")) || [];
    if (plans.length > 0) {
        plans[0].subPlans = updatedSubPlans; // Update subPlans for the first plan
    } else {
        plans.push({title: "New Plan", date: new Date().toISOString().split("T")[0], subPlans: updatedSubPlans});
    }

    // Save the updated plans to localStorage
    localStorage.setItem("plans", JSON.stringify(plans));
}



// Cập nhật tiêu đề kế hoạch
function handleTitleChange(newTitle) {
    const plans = JSON.parse(localStorage.getItem("plans")) || {};

    // Cập nhật tiêu đề chính
    plans[0].title = newTitle;

    // Lưu lại dữ liệu đã cập nhật vào localStorage
    localStorage.setItem("plans", JSON.stringify(plans));

    console.log("Updated main plan title:", newTitle);
}

// Xử lý kéo thả các plan items
function enableDragAndDrop(item) {
    item.addEventListener("dragstart", handleDragStart);
    item.addEventListener("dragover", handleDragOver);
    item.addEventListener("drop", handleDrop);
    item.addEventListener("dragend", handleDragEnd);
}

let draggedItem = null;

function handleDragStart(e) {
    draggedItem = this;
    e.dataTransfer.effectAllowed = "move";
    this.style.opacity = "0.5"; // Fade the dragged item
}

function handleDragOver(e) {
    e.preventDefault(); // Allow drop
    e.dataTransfer.dropEffect = "move";
    this.classList.add("drag-over"); // Add drag-over effect
}

function handleDrop(e) {
    e.stopPropagation(); // Prevent default behavior
    this.classList.remove("drag-over");

    if (draggedItem !== this) {
        const listContainer = document.getElementById("planListContainer");
        const children = Array.from(listContainer.children);

        // Insert the dragged item at the new position
        const draggedIndex = children.indexOf(draggedItem);
        const targetIndex = children.indexOf(this);

        if (draggedIndex < targetIndex) {
            this.insertAdjacentElement("afterend", draggedItem);
        } else {
            this.insertAdjacentElement("beforebegin", draggedItem);
        }

        // Update order in localStorage
        updatePlanOrderInLocalStorage();
    }
}

function handleDragEnd() {
    this.style.opacity = "1"; // Reset opacity
    document.querySelectorAll(".drag-over").forEach((item) => {
        item.classList.remove("drag-over");
    });
}

// Lưu kế hoạch vào server hoặc localStorage
document.getElementById("savePlan").addEventListener("click", async () => {
    const plans = JSON.parse(localStorage.getItem("plans")) || {};
    const planData = plans[0];
    const planId = JSON.parse(localStorage.getItem("planId"));
    const today = new Date();
    const planDate = new Date(planData.date);

    if (planDate < today.setHours(0, 0, 0, 0)) {
        alert("You cannot save a plan with a past date.");
        return;
    }
    if (userId === null) {
        alert("You need to login to save a plan");
        return;
    }
    const formattedTime = `${planData.date}T00:00:00`;

    // Kiểm tra ngày của kế hoạch với startDate và endDate của các mục
    for (const subPlan of planData.subPlans) {
        for (const item of subPlan.planItems) {


            if (item.recId) {
                const startDate = new Date(item.startDate);
                const endDate = new Date(item.endDate);
                const planDate = new Date(planData.date);


// Convert both times to Date objects (use a fixed date as the reference)
                const referenceDate = '1970-01-01'; // Use a reference date (e.g., Unix epoch)
                console.log(new Date('1970-01-01' + `T` + `${item.recStartTime}`))
                const recStartTime = new Date(`${referenceDate}T${item.recStartTime}`);
                const startTime = new Date(`${referenceDate}T${item.startTime}`);
                const recEndTime = new Date(`${referenceDate}T${item.recEndTime}`);
                const endTime = new Date(`${referenceDate}T${item.endTime}`);
                let confirmMessage = "";
                if (item.startDate && item.endDate) {
                    if (!(planDate >= startDate && planDate <= endDate)) {
                        confirmMessage = `The day for plan ${item.title} may  have not been between event start date and end date . Do you want to continue?`;
                    }
                }

                if (!(startTime >= recStartTime && endTime <= recEndTime)) {
                    confirmMessage = `The plan time for ${item.title} may  have not been between event start time and end time. Do you want to continue?`;
                }
                if (confirmMessage !== "") {
                    const userConfirmed = confirm(confirmMessage);
                    if (!userConfirmed) {
                        return; // Dừng thực hiện nếu người dùng chọn "Cancel"
                    }
                }


            }

        }
    }
    let planningRequest = "";

    if (planId) {
        planningRequest = {
            title: planData.title.trim(),
            time: formattedTime,
            planDetails: planData.subPlans.map(subPlan => ({
                    title: subPlan.title,
                    plan_condition: subPlan.condition,
                    recs: subPlan.planItems.map(item => ({
                            recId: item.recId,
                            description: item.description,
                            startTime: item.startTime || "09:00",
                            endTime: item.endTime || "10:00",
                        })),
                })),
        };
        console.log(planningRequest);
        try {
            const response = await fetch(`/api/v1/planning/${planId}`, {
                method: "PUT",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(planningRequest),
            });

            if (response.ok) {
                clearPlan();
                alert("Plan saved successfully");
                location.reload();


            } else {
                alert(response);
            }
        } catch (error) {
            console.error("Error saving the plan:", error);
            alert("An error occurred. Please check the console for details.");
        }
    } else {

        planningRequest = {
            title: planData.title.trim(),
            time: formattedTime,
            userId: userId,
            planDetails: planData.subPlans.map(subPlan => ({
                    title: subPlan.title,
                    plan_condition: subPlan.condition,
                    recs: subPlan.planItems.map(item => ({
                            recId: item.recId,
                            description: item.description,
                            startTime: item.startTime || "09:00",
                            endTime: item.endTime || "10:00",
                        })),
                })),
        };

        try {
            const response = await fetch("/api/v1/planning/add", {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(planningRequest),
            });

            if (response.ok) {
                location.reload();
                clearPlan();
                alert("Plan saved successfully");
            } else {
                alert("Failed to save the plan. Please try again.");
            }
        } catch (error) {
            console.error("Error saving the plan:", error);
            alert("An error occurred. Please check the console for details.");
        }
    }
});
function handleSubPlanTitleChange(e, subPlanIndex) {
    const newTitle = e.target.value;
    const plans = JSON.parse(localStorage.getItem("plans")) || {};
    plans[0].subPlans[subPlanIndex].title = newTitle; // Cập nhật tiêu đề của sub-plan

    localStorage.setItem("plans", JSON.stringify(plans));
}

// Xử lý thay đổi điều kiện của sub-plan
function handleSubPlanConditionChange(e, subPlanIndex) {
    const newCondition = e.target.value;
    const plans = JSON.parse(localStorage.getItem("plans")) || {};
    plans[0].subPlans[subPlanIndex].condition = newCondition; // Cập nhật điều kiện của sub-plan

    localStorage.setItem("plans", JSON.stringify(plans));
}
// Xóa kế hoạch khỏi localStorage và giao diện
function clearPlan() {
    const plans = [];
    const dateNow = new Date(Date.now());

    // Định dạng ngày thành dd/mm/yyyy
    const day = String(dateNow.getDate()).padStart(2, '0');
    const month = String(dateNow.getMonth() + 1).padStart(2, '0'); // Tháng bắt đầu từ 0
    const year = dateNow.getFullYear();

    // Gán vào định dạng dd/mm/yyyy
    const formattedDate = `${day}/${month}/${year}`;
    const inputDateFormat = `${year}-${month}-${day}`;
    localStorage.removeItem("plans");
    const newPlan = {
        title: "New Plan",
        date: inputDateFormat,
        subPlans: [],
    };
    plans.push(newPlan);
    localStorage.setItem("plans", JSON.stringify(plans));
    localStorage.removeItem("planId");
    const listContainer = document.getElementById("planListContainer");

    listContainer.innerHTML = "";

    location.reload();
}

document.addEventListener("DOMContentLoaded", () => {
    if (userId !== JSON.parse(localStorage.getItem("userId")) && JSON.parse(localStorage.getItem("userId"))) {
        const plans = [];
        const dateNow = new Date(Date.now());

        // Định dạng ngày thành dd/mm/yyyy
        const day = String(dateNow.getDate()).padStart(2, '0');
        const month = String(dateNow.getMonth() + 1).padStart(2, '0'); // Tháng bắt đầu từ 0
        const year = dateNow.getFullYear();

        // Gán vào định dạng dd/mm/yyyy
        const formattedDate = `${day}/${month}/${year}`;
        const inputDateFormat = `${year}-${month}-${day}`;
        localStorage.removeItem("plans");
        const newPlan = {
            title: "New Plan",
            date: inputDateFormat,
            subPlans: [],
        };
        plans.push(newPlan);
        localStorage.setItem("plans", JSON.stringify(plans));
        localStorage.removeItem("planId");
        const listContainer = document.getElementById("planListContainer");
        localStorage.removeItem("userId");
        listContainer.innerHTML = "";
    }
    loadPlanFromLocalStorage();
    const planId = JSON.parse(localStorage.getItem("planId"));
    const headerPlan = document.getElementById("header_plan");
    console.log(planId)
    if (planId) {
        headerPlan.textContent = "You are currently on updating a plan.";
    } else if (!planId) {
        headerPlan.textContent = "Current Plans.";
    }


    // Thêm sự kiện cho nút Cancel
    const cancel = document.getElementById("cancelPlanBtn");
    cancel.addEventListener('click', clearPlan);
});
$(document).ready(function () {
    let currentPage = 0;
    const pageSize = 5;

    function fetchPlanning(page = 0) {
        $.ajax({
            url: `/api/v1/planning/user/${userId}`,
            method: "GET",
            data: {page, size: pageSize},
            success: function (data) {
                renderPlanningList(data.content);
                renderPagination(data.totalPages, data.number);
            },
            error: function (error) {
                console.error("Error fetching planning data:", error);
            },
        });
    }

    function renderPlanningList(plans) {
        const container = $("#your-plan");
        if (plans.length === 0) {
            container.html("<p class='text-gray-500'>No plans found.</p>");
            return;
        }

        const listHtml = plans
                .map(
                        (plan) => `
                <div class="mt-10 bg-white shadow-md rounded-lg p-6 mb-6">
                    <a href="/planning/${plan.id}"  class="text-2xl font-bold text-gray-800">${plan.title}-${new Date(plan.time).toLocaleDateString()}</a>
                    <p class="text-sm text-gray-500 mt-2">Last Modify at: ${new Date(plan.modifyAt).toLocaleDateString()}</p>
                </div>
            `
                )
                .join("");
        container.html(listHtml);
    }

    function renderPagination(totalPages, currentPage) {
        const paginationContainer = $("#pagination ul");
        paginationContainer.empty(); // Clear existing pagination

        const groupSize = 5; // Số lượng trang hiển thị trong một nhóm
        const startPage = Math.max(0, currentPage - Math.floor(groupSize / 2)); // Trang đầu của nhóm
        const endPage = Math.min(totalPages, startPage + groupSize); // Trang cuối của nhóm

        // Ensure the range stays within bounds
        const adjustedStartPage = Math.max(0, Math.min(startPage, totalPages - groupSize));
        const adjustedEndPage = Math.min(totalPages, adjustedStartPage + groupSize);

        // Previous Button
        paginationContainer.append(
                `<li>
            <button 
                class="prev-page px-3 py-2 text-gray-700 bg-white border border-gray-300 rounded-l-lg hover:bg-gray-100"
                ${currentPage === 0 ? "disabled" : ""}
            >Previous</button>
        </li>`
                );

        // Page Numbers
        for (let i = adjustedStartPage; i < adjustedEndPage; i++) {
            paginationContainer.append(
                    `<li>
                <button 
                    class="page-number px-3 py-2 ${i === currentPage ? "bg-blue-500 text-white" : "text-gray-700 bg-white"} border border-gray-300 hover:bg-gray-100"
                    data-page="${i}"
                >${i + 1}</button>
            </li>`
                    );
        }

        // Next Button
        paginationContainer.append(
                `<li>
            <button 
                class="next-page px-3 py-2 text-gray-700 bg-white border border-gray-300 rounded-r-lg hover:bg-gray-100"
                ${currentPage === totalPages - 1 ? "disabled" : ""}
            >Next</button>
        </li>`
                );

        // Add event listeners for navigation
        $(".prev-page").click(function () {
            if (currentPage > 0)
                fetchPlanning(currentPage - 1);
        });

        $(".next-page").click(function () {
            if (currentPage < totalPages - 1)
                fetchPlanning(currentPage + 1);
        });

        $(".page-number").click(function () {
            const page = $(this).data("page");
            fetchPlanning(page);
        });
    }


    // Fetch the initial planning data
    fetchPlanning();
});
loadPlanFromLocalStorage();