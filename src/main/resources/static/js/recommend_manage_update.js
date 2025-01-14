const API_BASE_URL = '/api/v1/recommendations';
const TAGS_API_URL = '/api/v1/ptag';
const IMAGES_API_URL = '/api/v1/recommendations/images';
const PAGE_SIZE = 20;
console.log("update js")
let existingImages = []; // Lưu trữ danh sách ảnh từ DB

// Fetch and display tags
const fetchTags = async (page = 0) => {
    try {
        const response = await fetch(`${TAGS_API_URL}/?page=${page}&size=${PAGE_SIZE}&sortBy=title&direction=desc`);
        if (!response.ok)
            throw new Error("Failed to fetch tags");

        const {content: tags} = await response.json();
        displayTags(tags);
    } catch (error) {
        console.error("Error fetching tags:", error);
}
};

// Display tags in the container
const displayTags = (tags) => {
    const tagsContainer = document.getElementById('tags-container');
    tagsContainer.innerHTML = '';

    if (tags?.length) {
        tags.forEach(tag => {
            const label = document.createElement('label');
            label.classList.add('block', 'text-gray-700');

            const checkbox = document.createElement('input');
            checkbox.type = 'checkbox';
            checkbox.value = tag.id;
            checkbox.id = `tag-${tag.id}`;
            checkbox.classList.add('mr-2', 'form-checkbox');

            const span = document.createElement('span');
            span.textContent = tag.title;

            label.append(checkbox, span);
            tagsContainer.appendChild(label);
        });
    } else {
        tagsContainer.innerHTML = '<p>No tags found.</p>';
    }
};

// Fetch recommendation details and populate the form
const fetchRecommendationForUpdate = async (id) => {
    try {
        const response = await fetch(`${API_BASE_URL}/${id}`);
        if (!response.ok)
            throw new Error("Failed to fetch recommendation");

        const recommendation = await response.json();
        
        // Gán danh sách ảnh từ recommendation
        existingImages = recommendation.image || []; // Nếu không có ảnh, trả về mảng rỗng
        console.log(existingImages);
        populateUpdateForm(recommendation);
    } catch (error) {
        console.error("Error fetching recommendation:", error);
    }
};
function formatDateString(dateString) {
    if (!dateString)
        return '';  // Handle empty or invalid date string
    return dateString.split('T')[0];  // Extract only the date part
}
// Populate the update form with recommendation data
const populateUpdateForm = (recommendation) => {
    const {
        id, location, address, email, title, startTime, endTime, startDate, endDate,
        recommendTime, description, minBudget, maxBudget, isActive, tags, image
    } = recommendation;
    var citis = document.getElementById("city");
    var districts = document.getElementById("district");

    const inputString = location; // The input you want to parse

    // Cấu hình API
    var Parameter = {
        url: "https://raw.githubusercontent.com/kenzouno1/DiaGioiHanhChinhVN/master/data.json",
        method: "GET",
        responseType: "application/json",
    };

    // Gọi API và xử lý dữ liệu
    axios(Parameter).then(function (result) {
        renderCity(result.data);
    });

    // Render danh sách tỉnh thành
    function renderCity(data) {
        for (const x of data) {
            citis.options[citis.options.length] = new Option(x.Name, x.Id);
        }

        // Khi chọn tỉnh thành
        citis.onchange = function () {
            districts.length = 1; // Reset danh sách quận/huyện
            if (this.value !== "") {
                const result = data.filter(n => n.Id === this.value); // Lấy tỉnh được chọn

                for (const k of result[0].Districts) {
                    districts.options[districts.options.length] = new Option(k.Name, k.Id);
                }
            }
        };

        // Set the city and district based on the input string
        setCityAndDistrict(data, inputString);
    }

    // Set the city and district from input string
    function setCityAndDistrict(data, inputString) {
        const [districtName, cityName] = inputString.split(" - ").map(part => part.trim()); // Split input string into district and city

        // Find the matching city from the data
        const selectedCity = data.find(city => city.Name.includes(cityName));
        if (selectedCity) {
            // Set the city dropdown
            citis.value = selectedCity.Id;
            citis.dispatchEvent(new Event('change')); // Trigger change event to populate districts

            // Find the matching district in the selected city's districts
            const selectedDistrict = selectedCity.Districts.find(district => district.Name.includes(districtName));
            if (selectedDistrict) {
                // Set the district dropdown
                districts.value = selectedDistrict.Id;
            }
        }
    }

    const formatTime = (time) => time.slice(0, 5);
    const currentDate = new Date().toISOString().split('T')[0];
    console.log(formatDateString(startDate));
    document.getElementById('recommendation-id').value = id;
    document.getElementById('address').value = address;
    document.getElementById('email').value = email;
    document.getElementById('title').value = title;
    document.getElementById('start-date').value = formatDateString(startDate);
    document.getElementById('start-time').value = formatTime(startTime);
    document.getElementById('end-date').value = formatDateString(endDate);
    document.getElementById('end-time').value = formatTime(endTime);
    document.getElementById('recommend-time').value = recommendTime;
    document.getElementById('description').value = description;
    document.getElementById('min-budget').value = minBudget;
    document.getElementById('max-budget').value = maxBudget;
    document.getElementById('is-active').checked = isActive;

    tags?.forEach(tag => {
        const checkbox = document.getElementById(`tag-${tag.id}`);
        if (checkbox)
            checkbox.checked = true;
    });

    const previewContainer = document.getElementById('image-preview-container');
    previewContainer.innerHTML = '';
    let i=0;
    // Display existing images with remove button
    (image).forEach(imageUrl => {
        const imgElement = document.createElement('img');
        imgElement.src = imageUrl;
        imgElement.classList.add('w-full', 'h-32', 'object-cover', 'rounded');
        imgElement.id = i;
        const removeButton = document.createElement('button');
        removeButton.textContent = 'X';
        removeButton.classList.add('absolute', 'top-0', 'right-0', 'bg-red-500', 'text-white', 'rounded-full', 'w-6', 'h-6', 'flex', 'items-center', 'justify-center', 'cursor-pointer');
        removeButton.onclick = () => removeImageFromPreview(i, imgElement);

        const imgContainer = document.createElement('div');
        imgContainer.classList.add('relative');
        imgContainer.appendChild(imgElement);
        imgContainer.appendChild(removeButton);

        previewContainer.appendChild(imgContainer);
    });
};
const removeImageFromPreview = (i, imgElement) => {
    const previewContainer = document.getElementById('image-preview-container');
    previewContainer.removeChild(imgElement.parentNode);  // Remove the image element with the button

    // Remove from the existing images array
    existingImages.splice(i,1);
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

// Fetch image as file
const fetchImageAsFile = async (fileName) => {
    try {
        const response = await fetch(`${IMAGES_API_URL}/${fileName}`);
        if (!response.ok)
            throw new Error(`Failed to fetch file: ${fileName}`);

        const blob = await response.blob();
        return new File([blob], fileName, {type: blob.type});
    } catch (error) {
        console.error("Error fetching image file:", error);
        throw error;
    }
};
function formatDateToISO(dateString) {
    const date = new Date(dateString);
    return date.toISOString().split(".")[0]; // Trims milliseconds
}
// Prepare and submit the update form
const submitForm = async (e) => {
    e.preventDefault();

    const recommendationId = document.getElementById('recommendation-id').value;
    const formData = new FormData();
    const newFiles = document.getElementById('images').files;
    const imgList = [];
    const city = document.getElementById('city').options[document.getElementById('city').selectedIndex].text || '';  // Get the text of the selected option
    const district = document.getElementById('district').options[document.getElementById('district').selectedIndex].text || '';  // Get the text of the selected option

    let location = district.trim() + " - " + city.trim();
    if (location==="Select District - Select City") {
        location = "None";
    }
    const recommendationData = {
        location: location,
        address: document.getElementById('address').value,
        email: document.getElementById('email').value || "None",
        title: document.getElementById('title').value,
        startTime: document.getElementById('start-time').value || "00:00",
        endTime: document.getElementById('end-time').value || "23:59",
        startDate: document.getElementById('start-date').value ? formatDateToISO(document.getElementById('start-date').value) : "",
        endDate: document.getElementById('end-date').value ? formatDateToISO(document.getElementById('end-date').value) : "",
        recommendTime: document.getElementById('recommend-time').value,
        description: document.getElementById('description').value,
        minBudget: document.getElementById('min-budget').value,
        maxBudget: document.getElementById('max-budget').value,
        isActive: document.getElementById('is-active').checked,
        listPreferenceTagId: Array.from(document.querySelectorAll('#tags-container input[type="checkbox"]:checked'))
                .map(checkbox => checkbox.value)
    };

    formData.append("rd", JSON.stringify(recommendationData));
    console.log(newFiles)
    if (newFiles.length) {
        Array.from(newFiles).forEach(file => {
            imgList.push(file.name);
            formData.append("image", file);
        });
    }
    for (const fileName of existingImages) {
        // Loại bỏ tiền tố '/images/'
        const cleanFileName = fileName.replace('/images/', '');
        try {
            const file = await fetchImageAsFile(cleanFileName);
            imgList.push(file.name);
            formData.append("image", file);
        } catch (error) {
            console.error("Lỗi khi lấy hình ảnh:", error);
            alert(`Không thể lấy hình ảnh: ${fileName}`);
            return;
        }
    }
    console.log(formData.getAll("image"));
    try {
        const response = await fetch(`${API_BASE_URL}/${recommendationId}`, {
            method: 'PUT',
            body: formData
        });

        if (response.ok) {
            alert('Recommendation updated successfully!');
            window.location.href = '/admin/recommendation';
        } else {
            const errorText = await response.text();
            console.error("Error updating recommendation:", errorText);
            alert(`Failed to update recommendation: ${errorText}`);
        }
    } catch (error) {
        console.error("Error submitting form:", error);
        alert('An error occurred while updating the recommendation.');
    }
};

// Event listeners
document.getElementById('images').addEventListener('change', handleImageUpload);
document.getElementById('update-form').addEventListener('submit', submitForm);

// Initialization
fetchTags();
const recommendationId = new URLSearchParams(window.location.search).get('id');
if (recommendationId)
    fetchRecommendationForUpdate(recommendationId);
