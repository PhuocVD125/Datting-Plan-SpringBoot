let postUserId;
const IMAGES_API_URL = '/api/v1/recommendations/images';
let postImage;
let existingImages = [];
let images = [];
$(document).ready(function () {

    const searchInput = $("#searchRecInput");
    searchInput.on("input", function () {
        const keyword = searchInput.val(); // Get the keyword value from input
        populateRecommendation(keyword); // Call function to populate recommendations
    });

    // Function to fetch post details by ID
    function fetchPostById(postId) {
        $.ajax({
            url: `/api/v1/post/${postId}`,
            method: 'GET',
            success: function (data) {
                postUserId = data.userId;
                if (data.userId !== userId && userRole !== 'ADMIN') {
                    $('#body').addClass("hidden");
                    window.location.href = "/social/posts";
                }

                // Populate data
                $('#userAvatar').attr('src', data.userImage || '/default-avatar.jpg');
                $('#userName').text(data.username);
                $('#title').val(data.title);
                $('#content').val(data.content);
                populateRecommendations(data.recommendationId, "");

                existingImages = data.image || [];
                renderImageList();

                renderTags(data.tags);
            },
            error: function (error) {
                console.error('Error fetching post:', error);
            }
        });
    }



    // Populate recommendations dropdown
    function populateRecommendations(selectedRecommendationId, keyword) {
        $.ajax({
            url: `/api/v1/recommendations/search?keyword=${keyword}`, // Adjust to your recommendations endpoint
            method: 'GET',
            success: function (recommendations) {
                const recommendationDropdown = $('#recommendationId');
                recommendationDropdown.empty(); // Clear existing options
                recommendations.forEach((rec) => {
                    const option = `<option value="${rec.id}" ${rec.id === selectedRecommendationId ? 'selected' : ''}>
                        ${rec.title + " - " + rec.location}
                    </option>`;
                    recommendationDropdown.append(option);
                });
            },
            error: function (error) {
                console.error('Error fetching recommendations:', error);
            }
        });
    }
    function populateRecommendation(keyword) {
        $.ajax({
            url: `/api/v1/recommendations/search?keyword=${keyword}`, // Adjust to your recommendations endpoint
            method: 'GET',
            success: function (recommendations) {
                const recommendationDropdown = $('#recommendationId');
                recommendationDropdown.empty(); // Clear existing options
                recommendations.forEach((rec) => {
                    const option = `<option value="${rec.id}">
                        ${rec.title + " - " + rec.location}
                    </option>`;
                    recommendationDropdown.append(option);
                });
            },
            error: function (error) {
                console.error('Error fetching recommendations:', error);
            }
        });
    }
    // Tags management
    let tagsArray = [];

// Function to render tags
    function renderTags(tags) {
        if (typeof tags === 'string') {
            tagsArray = tags.split(' ');
        } else {
            tagsArray = tags || [];
        }
        const tagContainer = $('#tagContainer');
        tagContainer.empty(); // Clear existing tags

        // Render each tag with a remove button
        tagsArray.forEach((tag, index) => {
            const tagElement = $(`
                <span class="inline-flex items-center bg-gray-200 text-gray-700 rounded-full px-3 py-1 m-1" data-index="${index}">
                    #${tag}
                    <button class="ml-2 text-red-500 remove-tag-btn">x</button>
                </span>
            `);
            tagContainer.append(tagElement);
        });

        // Add the "+" button to create new tags
        const addButton = $(`
            <button id="addTagButton" class="inline-flex items-center bg-blue-500 text-white rounded-full px-3 py-1 m-1">
                + Add Tag
            </button>
        `);
        addButton.on('click', addTag);
        tagContainer.append(addButton);
    }

    // Event delegation for dynamically added remove buttons
    $('#tagContainer').on('click', '.remove-tag-btn', function () {
        const index = $(this).parent().data('index'); // Get the index from parent element
        tagsArray.splice(index, 1); // Remove tag
        renderTags(tagsArray); // Re-render tags
    });


    function renderImageList() {
        const imageContainer = $('.grid.grid-cols-3.gap-4');
        imageContainer.empty();

        // Render existing images (from server)
        existingImages.forEach((image, index) => {
            const imageElement = $(`
                <div class="relative group">
                    <img src="${image}" alt="Image preview" class="w-full h-100 object-cover rounded-md">
                    <button 
                        class="absolute top-2 right-2 bg-red-500 text-white rounded-full p-1 opacity-0 group-hover:opacity-100 remove-image-btn" 
                        data-type="existing" data-index="${index}">
                        ✕
                    </button>
                </div>
            `);
            imageContainer.append(imageElement);
        });

        // Render new images (base64 previews)
        images.forEach((image, index) => {
            const imageElement = $(`
                <div class="relative group">
                    <img src="${image}" alt="Image preview" class="w-full h-100 object-cover rounded-md">
                    <button 
                        class="absolute top-2 right-2 bg-red-500 text-white rounded-full p-1 opacity-0 group-hover:opacity-100 remove-image-btn" 
                        data-type="new" data-index="${index}">
                        ✕
                    </button>
                </div>
            `);
            imageContainer.append(imageElement);
        });
    }

    // Add new images
    $('.add-image-input').on('change', function (e) {
        const files = e.target.files;
        Array.from(files).forEach(file => {
            const reader = new FileReader();
            reader.onload = function (event) {
                images.push(event.target.result); // Add to new images
                renderImageList();
            };
            reader.readAsDataURL(file);
        });

        // Update input files
        updateFileList(e.target.files);
    });

    // Handle remove image
    $(document).on('click', '.remove-image-btn', function () {
        const type = $(this).data('type');
        const index = $(this).data('index');

        if (type === 'existing') {
            existingImages.splice(index, 1); // Xóa ảnh cũ từ server
        } else if (type === 'new') {
            images.splice(index, 1); // Xóa ảnh mới
            updateFileList(); // Cập nhật input file
        }

        renderImageList();
    });

    // Update input file list
    function updateFileList(newFiles = null) {
        const fileInput = document.getElementById('images');
        const dataTransfer = new DataTransfer();

        // Chuyển ảnh mới vào FileList
        if (newFiles) {
            Array.from(newFiles).forEach(file => dataTransfer.items.add(file));
        } else {
            // Xóa file mới đã bị xóa khỏi mảng `images`
            const currentFiles = Array.from(fileInput.files);
            currentFiles.filter((_, i) => i < images.length).forEach(file => {
                dataTransfer.items.add(file);
            });
        }

        fileInput.files = dataTransfer.files;
    }


// Event listener for adding a new imageS
// Function to remove a tag
    function removeTag(index) {
        tagsArray.splice(index, 1); // Remove the tag at the given index
        renderTags(tagsArray); // Re-render tags
    }

// Function to add a tag with a custom prompt
    function addTag() {

        const customPrompt = $('#customPrompt');
        const customPromptInput = $('#customPromptInput');
        const customPromptCancel = $('#customPromptCancel');
        const customPromptSubmit = $('#customPromptSubmit');

        // Show the modal
        customPrompt.removeClass('hidden');

        // Clear the input field
        customPromptInput.val('');

        // Handle cancel action
        customPromptCancel.off('click').on('click', function () {
            customPrompt.addClass('hidden'); // Hide the modal
        });

        // Handle submit action
        customPromptSubmit.off('click').on('click', function () {
            const newTag = customPromptInput.val().trim();
            if (newTag) {
                tagsArray.push(newTag); // Add the new tag to the array
                renderTags(tagsArray); // Re-render the tags
            }
            customPrompt.addClass('hidden'); // Hide the modal
        });
    }
    // Fetch post by ID
    fetchPostById(postId);
    $('#cancelSavePost').on('click', function () {
        window.history.back();
    });
    // Save post
    const fetchImageAsFile = async (fileName) => {
        return new Promise((resolve, reject) => {
            $.ajax({
                url: `${IMAGES_API_URL}/${fileName}`, // Endpoint lấy file từ server
                method: 'GET',
                xhrFields: {
                    responseType: 'blob' // Đảm bảo nhận dữ liệu kiểu blob
                },
                success: function (blob) {
                    try {
                        const file = new File([blob], fileName, {type: blob.type});
                        resolve(file); // Trả về file khi thành công
                    } catch (error) {
                        console.error("Error creating file object:", error);
                        reject(error);
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    console.error(`Error fetching file: ${fileName}`, textStatus, errorThrown);
                    reject(new Error(`Failed to fetch file: ${fileName}`));
                }
            });
        });
    };

    $('#savePost').on('click', async function () {
        const newFiles = document.getElementById('images').files;

        const postData = {
            title: $('#title').val(),
            content: $('#content').val(),
            recommendationId: $('#recommendationId').val(),
            tagTitle: tagsArray
        };

        const formData = new FormData();
        formData.append("pd", JSON.stringify(postData));

        // Append new files
        Array.from(newFiles).forEach(file => {
            formData.append("image", file);
        });

        // Append existing images as files
        for (const fileUrl of existingImages) {

            const cleanFileName = fileUrl.replace('/images/', '');
            const file = await fetchImageAsFile(cleanFileName);
            formData.append("image", file);

        }
        $.ajax({
            url: `/api/v1/post/${postId}`,
            method: 'PUT',
            processData: false, // Do not process the FormData
            contentType: false,
            data: formData,
            success: function () {
                alert('Post updated successfully!');
                window.location.href = "/social/posts"; // Redirect after update
            },
            error: function (error) {
                console.error('Error updating post:', error);
                alert('Failed to update post.');
            }
        });
    });
});
    