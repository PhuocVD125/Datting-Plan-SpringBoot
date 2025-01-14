const urlParams = new URLSearchParams(window.location.search);

// Extract the value of 'keyword'
const keyword = urlParams.get('keyword');
function addToSubPlan(planIndex, subPlanIndex, rec) {
    const plans = JSON.parse(localStorage.getItem("plans")) || [];
    const selectedPlan = plans[planIndex];
    const selectedSubPlan = selectedPlan.subPlans[subPlanIndex];

    // Check if the item already exists in the selected sub-plan
        

    // Add the item to the sub-plan
    const newItem = {
        recId: rec.id,
        title: rec.title,
        address:rec.address,
        location: rec.location,
        min_budget: rec.minBudget,
        max_budget: rec.maxBudget,
        description: "",
        startDate:rec.startDate,
        endDate:rec.endDate,
        startTime:"00:00",
        endTime:"23:59",
        recStartTime:rec.startTime,
        recEndTime:rec.endTime,
    };
    selectedSubPlan.planItems.push(newItem);

    // Save updated plans to localStorage
    localStorage.setItem("plans", JSON.stringify(plans));

    alert(`Item added to: ${selectedPlan.title} - ${selectedSubPlan.title}`);
}

// Function to create a new sub-plan and add an item to it
function createNewSubPlan(planIndex, rec) {
    const plans = JSON.parse(localStorage.getItem("plans")) || [];
    const selectedPlan = plans[planIndex];

    // Create a new sub-plan with the recommendation included
    const newSubPlan = {
        title: `New Sub-Plan ${selectedPlan.subPlans.length + 1}`, // Give it a unique title
        condition: "None",
        planItems: [
            {
                recId: rec.id,
                address:rec.address,
                title: rec.title,
                location: rec.location,
                min_budget: rec.minBudget,
                max_budget: rec.maxBudget,
                description: "",
                startDate:rec.startDate,
                endDate:rec.endDate,
                startTime:"00:00",
                endTime:"23:59",
                recStartTime:rec.startTime,
                recEndTime:rec.endTime,
            },
        ],
    };

    // Add the new sub-plan to the selected plan
    selectedPlan.subPlans.push(newSubPlan);

    // Save updated plans to localStorage
    localStorage.setItem("plans", JSON.stringify(plans));

    alert(`New sub-plan "${newSubPlan.title}" created under "${selectedPlan.title}" and item added!`);
}

// Bind click events to "Add to Plan" buttons
function bindAddToPlanButton(rec) {
    const button = document.getElementById(`addToPlan-${rec.id}`);
    if (button) {
        button.addEventListener("click", (e) => {
            e.preventDefault(); // Prevent default link behavior
            togglePlanDropdown(rec, button);
        });
    }
}

// Toggle the dropdown and populate it dynamically
function togglePlanDropdown(rec, button) {
    const dropdown = document.querySelector(`.plan-dropdown-${rec.id}`);
    dropdown.classList.toggle("hidden");

    const planList = dropdown.querySelector("#plan-list");
    planList.innerHTML = ""; // Clear previous options

    const plans = JSON.parse(localStorage.getItem("plans")) || [];

    if (plans.length === 0) {
        // Nếu không có kế hoạch nào, chỉ hiển thị tùy chọn tạo sub-plan mới
        const newSubPlanItem = document.createElement("li");
        newSubPlanItem.className = "p-2 hover:bg-gray-100 cursor-pointer text-blue-500";
        newSubPlanItem.textContent = `+ Create New Sub-Plan`;
        newSubPlanItem.addEventListener("click", () => {
            // Nếu không có kế hoạch nào, ta sẽ tạo một kế hoạch mới trước
            const dateNow = new Date(Date.now());

            // Định dạng ngày thành dd/mm/yyyy
            const day = String(dateNow.getDate()).padStart(2, '0');
            const month = String(dateNow.getMonth() + 1).padStart(2, '0'); // Tháng bắt đầu từ 0
            const year = dateNow.getFullYear();

            // Gán vào định dạng dd/mm/yyyy
            const formattedDate = `${day}/${month}/${year}`;
            const inputDateFormat = `${year}-${month}-${day}`
            const newPlan = {
                title: "New Plan",
                date: inputDateFormat,
                subPlans: [],
            };
            plans.push(newPlan);
            localStorage.setItem("plans", JSON.stringify(plans));

            // Sau đó tạo Sub-Plan mới
            createNewSubPlan(plans.length - 1, rec);
            dropdown.classList.add("hidden"); // Close dropdown after selection
        });
        planList.appendChild(newSubPlanItem);
    } else {
        // Nếu có các kế hoạch đã có sẵn, hiển thị các Sub-Plan
        plans.forEach((plan, planIndex) => {
            // Add existing sub-plans
            plan.subPlans.forEach((subPlan, subPlanIndex) => {
                const listItem = document.createElement("li");
                listItem.className = "p-2 hover:bg-gray-100 cursor-pointer";
                listItem.textContent = `${plan.title} - ${subPlan.title}`;
                listItem.addEventListener("click", () => {
                    addToSubPlan(planIndex, subPlanIndex, rec);
                    dropdown.classList.add("hidden"); // Close dropdown after selection
                });
                planList.appendChild(listItem);
            });

            // Add "New Sub-Plan" option
            const newSubPlanItem = document.createElement("li");
            newSubPlanItem.className = "p-2 hover:bg-gray-100 cursor-pointer text-blue-500";
            newSubPlanItem.textContent = `+ Create New Sub-Plan for ${plan.title}`;
            newSubPlanItem.addEventListener("click", () => {
                createNewSubPlan(planIndex, rec);
                dropdown.classList.add("hidden"); // Close dropdown after selection
            });
            planList.appendChild(newSubPlanItem);
        });
    }

    // Close dropdown button
    document.querySelectorAll(".close-dropdown").forEach((button) => {
        button.addEventListener("click", (e) => {
            dropdown.classList.add("hidden");
        });
    });
}


// Bind events for all "Add to Plan" buttons
function bindAllAddToPlanButtons(recommendations) {
    recommendations.forEach(rec => {
        bindAddToPlanButton(rec);
    });
}
function formatDate(dateString) {
    const date = new Date(dateString);
    const day = String(date.getDate()).padStart(2, "0");
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const year = date.getFullYear();
    return `${day}/${month}/${year}`;
}
// Cập nhật `renderRecommendations` để gọi bindAllAddToPlanButtons
function renderRecommendations(recommendations) {
    
    const $container = $("#recommendationContainer"); // Lấy container bằng jQuery
    $container.empty(); // Xóa nội dung cũ

    recommendations.forEach((rec) => {
        let dateRange = "";
        if (rec.startDate && rec.endDate) {
            dateRange = ` (${formatDate(rec.startDate)} - ${formatDate(rec.endDate)})`;
        } else if (rec.startDate) {
            dateRange = ` (${formatDate(rec.startDate)})`;
        }
        const articleHTML = `
        <article class="rounded-xl bg-white p-3 shadow-lg hover:shadow-xl relative">
    <a href="/recommendation/${rec.id}">
        <div class="relative flex items-end overflow-hidden rounded-xl">
            <img src="${rec.image[0]}" alt="${rec.title}" />
            <div class="absolute bottom-3 left-3 inline-flex items-center rounded-lg bg-white p-2 shadow-md">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-yellow-400" viewBox="0 0 20 20" fill="currentColor">
                    <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                </svg>
                <span class="text-slate-400 ml-1 text-sm">${rec.rating}</span>
            </div>
        </div>
    </a>
    <div class="mt-1 p-2">
        <h2 class="text-slate-700">${rec.title} ${dateRange}</h2>
        <p class="text-slate-400 mt-1 text-sm">${rec.location}</p>
        ${rec.tags
        .map(tag => `<a href="/recommendation/ptag/${tag.title}" class="text-blue-600"> #${tag.title} </a>`)
        .join('')}
        <div class="mt-3 flex items-end justify-between">
            <p>
                <span class="text-lg font-bold text-blue-500">$${rec.minBudget}</span>
                <span class="text-slate-400 text-sm">Min</span>
            </p>
            <p>
                <span class="text-lg font-bold text-blue-500">$${rec.maxBudget}</span>
                <span class="text-slate-400 text-sm">Max</span>
            </p>
            <div class="group inline-flex rounded-xl bg-blue-100 p-2 hover:bg-blue-200">
                <button id="addToPlan-${rec.id}">
                    <svg xmlns="http://www.w3.org/2000/svg" class="group-hover:text-blue-500 h-4 w-4 text-blue-400" viewBox="0 0 20 20" fill="currentColor">
                        <path d="M5 4a2 2 0 012-2h6a2 2 0 012 2v14l-5-2.5L5 18V4z" />
                    </svg>
                </button>
            </div>
        </div>
    </div>
    <!-- Plan Dropdown -->
    <div class="plan-dropdown-${rec.id} hidden absolute bg-gray-50 shadow rounded max-h-64 overflow-y-auto z-10">
        <ul id="plan-list" class="p-2">
            <!-- Populated dynamically -->
        </ul>
        <button style="position: sticky; bottom: 0; z-index: 10;" class="close-dropdown bg-red-500 text-white px-4 py-2 rounded w-full mt-2 hover:bg-red-600">
            Close
        </button>
    </div>
</article>
        `;
        $container.append(articleHTML); // Thêm bài viết vào container
    });

    // Gắn sự kiện cho các nút "Add to Plan"
    bindAllAddToPlanButtons(recommendations);
}

function renderPagination(totalPages, currentPage) {
    const paginationElement = document.getElementById('pagination');
    paginationElement.innerHTML = ''; // Clear previous pagination

    const paginationList = document.createElement('ul');
    paginationList.classList.add('flex', 'items-center', '-space-x-px', 'h-8', 'text-sm');

    // Add Previous button
    const prevItem = document.createElement('li');
    const prevLink = document.createElement('a');
    prevLink.href = '#';
    prevLink.textContent = 'Previous';
    prevLink.classList.add('flex', 'items-center', 'justify-center', 'px-3', 'h-8', 'leading-tight', 'text-gray-500', 'bg-white', 'border', 'border-gray-300', 'hover:bg-gray-100', 'hover:text-gray-700');
    if (currentPage === 0) {
        prevLink.classList.add('opacity-50', 'pointer-events-none'); // Disable if on the first page
    } else {
        prevLink.addEventListener('click', (e) => {
            e.preventDefault();
            fetchRecommendations(currentPage - 1); // Go to the previous page
        });
    }
    prevItem.appendChild(prevLink);
    paginationList.appendChild(prevItem);

    // Calculate page range
    let startPage = Math.max(0, currentPage - 2);
    let endPage = Math.min(totalPages, startPage + 5);

    // Adjust range if less than 5 pages are displayed
    if (endPage - startPage < 5) {
        startPage = Math.max(0, endPage - 5);
    }

    // Add page numbers
    for (let i = startPage; i < endPage; i++) {
        const pageItem = document.createElement('li');
        const pageLink = document.createElement('a');
        pageLink.href = '#';
        pageLink.classList.add('flex', 'items-center', 'justify-center', 'px-3', 'h-8', 'leading-tight', 'text-gray-500', 'bg-white', 'border', 'border-gray-300', 'hover:bg-gray-100', 'hover:text-gray-700');
        pageLink.textContent = (i + 1).toString();
        if (i === currentPage) {
            pageLink.classList.add('z-10', 'text-blue-600', 'bg-blue-50', 'border-blue-300');
        }
        pageLink.addEventListener('click', (e) => {
            e.preventDefault();
            fetchRecommendations(i); // Fetch posts for this page
        });
        pageItem.appendChild(pageLink);
        paginationList.appendChild(pageItem);
    }

    // Add Next button
    const nextItem = document.createElement('li');
    const nextLink = document.createElement('a');
    nextLink.href = '#';
    nextLink.textContent = 'Next';
    nextLink.classList.add('flex', 'items-center', 'justify-center', 'px-3', 'h-8', 'leading-tight', 'text-gray-500', 'bg-white', 'border', 'border-gray-300', 'hover:bg-gray-100', 'hover:text-gray-700');
    if (currentPage === totalPages - 1) {
        nextLink.classList.add('opacity-50', 'pointer-events-none'); // Disable if on the last page
    } else {
        nextLink.addEventListener('click', (e) => {
            e.preventDefault();
            fetchRecommendations(currentPage + 1); // Go to the next page
        });
    }
    nextItem.appendChild(nextLink);
    paginationList.appendChild(nextItem);

    paginationElement.appendChild(paginationList);
}
const api = "/api/v1/recommendations/search-pageable?keyword="+keyword;
async function fetchRecommendations(page) {
  const response = await fetch(`${api}&page=${page}`);
  const data = await response.json();
  renderRecommendations(data.content);  // Assuming 'content' contains the posts
  renderPagination(data.totalPages, page);  // Assuming 'totalPages' contains total number of pages
}
fetchRecommendations(0);





