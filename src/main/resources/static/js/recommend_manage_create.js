const API_BASE_URL = '/api/v1/ptag';  // Đảm bảo URL chính xác
const pageSize = 20;  // Số lượng tag mỗi trang

// Function to fetch all tags
const fetchTags = async (page = 0) => {
    try {
        const response = await fetch(`${API_BASE_URL}/?page=${page}&size=${pageSize}&sortBy=title&direction=desc`);
        if (!response.ok) {
            throw new Error("Failed to fetch tags");
        }
        const data = await response.json();
        displayTags(data.content);  // Hiển thị tags
    } catch (error) {
        console.error("Error fetching tags:", error);
}
};

// Function to display tags in the container
const displayTags = (tags) => {
    const tagsContainer = document.getElementById('tags-container');
    tagsContainer.innerHTML = '';  // Xóa nội dung cũ trước khi thêm tag mới

    if (tags && tags.length > 0) {
        tags.forEach(tag => {
            const label = document.createElement('label');
            label.classList.add('block', 'text-gray-700');

            const checkbox = document.createElement('input');
            checkbox.type = 'checkbox';
            checkbox.value = tag.id;  // Sử dụng ID của tag làm giá trị
            checkbox.id = `tag-${tag.id}`;
            checkbox.classList.add('mr-2', 'form-checkbox');

            const span = document.createElement('span');
            span.textContent = tag.title;  // Hiển thị tiêu đề tag

            label.appendChild(checkbox);
            label.appendChild(span);
            tagsContainer.appendChild(label);
        });
    } else {
        tagsContainer.innerHTML = '<p>No tags found.</p>';
    }
};
function formatDateToISO(dateString) {
    const date = new Date(dateString);
    return date.toISOString().split(".")[0]; // Trims milliseconds
}
// Form submission handler
document.getElementById('create-form').addEventListener('submit', function (e) {
    e.preventDefault();

    // Collect form data
    const city = document.getElementById('city').options[document.getElementById('city').selectedIndex].text || '';  // Get the text of the selected option
    const district = document.getElementById('district').options[document.getElementById('district').selectedIndex].text || '';  // Get the text of the selected option
    let location = district.trim() + " - " + city.trim();
    if (location==="Select District - Select City") {
        location = "None";
    }

    console.log(location);
    const email = document.getElementById('email').value || "None";
    const title = document.getElementById('title').value;
    const startTime = document.getElementById('start-time').value || "00:00"; // Mặc định 00:00 nếu không có
    const startDate = document.getElementById('start-date').value || ""; // Không hiển thị nếu trống
    const endTime = document.getElementById('end-time').value || "23:59"; // Mặc định 23:59 nếu không có
    const endDate = document.getElementById('end-date').value || ""; // Không hiển thị nếu trống
    const description = document.getElementById('description').value;
    const address = document.getElementById('address').value;
    const minBudget = document.getElementById('min-budget').value;
    const maxBudget = document.getElementById('max-budget').value;
    const isActive = document.getElementById('is-active').checked;
    const recommendTime = document.getElementById('recommend-time').value;
    console.log(document.getElementById('start-date').value);
    // Get selected tag IDs and map them to listPreferenceTagId
    const listPreferenceTagId = Array.from(document.querySelectorAll('#tags-container input[type="checkbox"]:checked'))
            .map(checkbox => checkbox.value);  // Lấy giá trị ID của tag

    // Create the JSON object for the form data
    const recommendationData = {
        location: location,
        address:address,
        email: email,
        title: title,
        startTime: startTime,
        endTime: endTime,
        startDate: startDate ? formatDateToISO(startDate) : "", // Nếu có startDate, định dạng; nếu không, để chuỗi trống
        endDate: endDate ? formatDateToISO(endDate) : "", // Tương tự cho endDate
        description: description,
        minBudget: minBudget,
        maxBudget: maxBudget,
        isActive: isActive,
        listPreferenceTagId: listPreferenceTagId, // Sử dụng listPreferenceTagId thay vì tags
        recommendTime: recommendTime
    };

    // Create a FormData object to send the data as multipart/form-data
    const formData = new FormData();
    formData.append("rd", JSON.stringify(recommendationData));  // Gửi dữ liệu JSON của recommendation

    // Append images to formData
    const files = document.getElementById('images').files;
    for (let i = 0; i < files.length; i++) {
        formData.append("image", files[i]);
    }

    // Send the data using fetch
    fetch('/api/v1/recommendations/add', {
        method: 'POST',
        body: formData
    })
            .then(response => response.text())  // Dùng .text() để xử lý chuỗi
            .then(data => {
                console.log('Received response:', data);  // In ra chuỗi phản hồi
                console.log('Comparison result:', data.trim() === "New Recommendation Created");
                if (data.trim() === "New Recommendation Created") {
                    alert('Recommendation created successfully!');
                    window.location.href = '/admin/recommendation';  // Chuyển hướng đến trang quản lý
                } else {
                    console.error('Unexpected response:', data);
                    alert('Unexpected response: ' + data);
                }
            })
            .catch(error => {
                console.error('Error creating recommendation:', error);
                alert('Error creating recommendation.');
            });
});
const removeImageFromPreview = (imageUrl, imgElement) => {
    const previewContainer = document.getElementById('image-preview-container');
    previewContainer.removeChild(imgElement.parentNode);  // Remove the image element with the button

    // Remove from the existing images array
    existingImages = existingImages.filter(img => img !== imageUrl);

    // Update FormData by removing the corresponding file if it's not already removed
    const formData = new FormData(document.getElementById('update-form'));
    formData.delete('image', imageUrl);  // Assuming imageUrl is used as a unique identifier for files
};
// Handle image upload preview
const handleImageUpload = () => {
    const input = document.getElementById('images');
    const previewContainer = document.getElementById('image-preview-container');
    
    // Create an array to track the selected files
    let filesArray = Array.from(input.files);

    // Append new images to the container
    filesArray.forEach(file => {
        const reader = new FileReader();
        reader.onload = (e) => {
            // Create a container for the image and remove button
            const imageWrapper = document.createElement('div');
            imageWrapper.classList.add('image-wrapper', 'relative', 'inline-block', 'mr-2');

            const imgElement = document.createElement('img');
            imgElement.src = e.target.result;
            imgElement.classList.add('w-full', 'h-32', 'object-cover', 'rounded');

            // Create a remove button (X)
            const removeButton = document.createElement('button');
            removeButton.textContent = 'X';
            removeButton.classList.add('absolute', 'top-0', 'right-0', 'bg-red-500', 'text-white', 'rounded-full', 'w-6', 'h-6', 'flex', 'items-center', 'justify-center', 'cursor-pointer');

            // Event listener to remove image when X is clicked
            removeButton.addEventListener('click', () => {
                // Remove the file from the files array (tracked in filesArray)
                const index = filesArray.findIndex(f => f.name === file.name);
                if (index !== -1) {
                    filesArray.splice(index, 1); // Remove the file from the array
                }

                // Remove the image wrapper from the preview container
                imageWrapper.remove();

                // Update the input's files list (important for submitting form)
                updateInputFiles();
            });

            // Append the image and remove button to the wrapper
            imageWrapper.appendChild(imgElement);
            imageWrapper.appendChild(removeButton);

            // Append the wrapper to the preview container
            previewContainer.appendChild(imageWrapper);
        };
        reader.readAsDataURL(file);
    });

    // Update input files list when an image is removed
    function updateInputFiles() {
        const dataTransfer = new DataTransfer(); // Create a new DataTransfer object
        filesArray.forEach(file => {
            dataTransfer.items.add(file); // Add each file back into the DataTransfer object
        });

        // Update the input files list with the new files
        input.files = dataTransfer.files;
    }
};
// Initially fetch all tags
fetchTags();
document.getElementById('images').addEventListener('change', handleImageUpload);
