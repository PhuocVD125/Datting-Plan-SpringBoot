let post_userid;

$(document).ready(function () {
    // Example function to fetch post data
    function fetchPostById(postId) {
        $.ajax({
            url: `/api/v1/post/${postId}`, // Adjust URL to your backend endpoint
            method: 'GET',
            success: function (data) {
                console.log(data); // Log the response for debugging
                post_userid = data.userId;
                // Populate the fields with the data
                $('#userAvatar').attr('src', '' + data.userImage); // Update user avatar
                $('#userName').text(data.username); // Update username
                $('#postTitle').text(data.title); // Update post title
                $('#postRecommendation').attr('href', '/recommendation/' + data.recommendationId);
                $('#postRecommendation').text(data.recommendation);
                $('#postContent').text(data.content);
                $('#postTags').html(data.tags.split(' ').map(tag => `<a href="/social/posts/tag/${tag.trim()}" class="text-blue-600">#${tag}</a>`).join(' ')); // Update tags
//                $('#postImage').attr('src', '' + data.image); // Update image
                $('#likeCount').text(`${data.likeCount} ${data.likeCount > 1 ? 'Likes' : 'Like'}`);
                $('#commentCount').text(`${data.commentCount} ${data.commentCount > 1 ? 'Comments' : 'Comment'}`);
                if (data.userId === userId||userRole==='ADMIN') {
                    $('#btnDandE').removeClass('hidden');
                }

                updateSlider(data.image);
                checkIfLiked(postId, userId);

                // Render comments and replies
                renderComments(data.comments);
                attachReplyButtonClick();
            },
            error: function (error) {
                console.error('Error fetching post:', error);
            }
        });
    }
    $("#deleteBtn").off("click").on("click", function () {
        if (confirm("Are you sure you want to delete this post?")){
            $.ajax({
            url: `/api/v1/post/${postId}`, // API endpoint for deleting the post
            type: 'DELETE',
            success: function (response) {
                
                alert("Post deleted successfully: " + response);
                // Optionally, refresh the page or remove the deleted item from the DOM
                window.location.href = "/social/posts";
            },
            error: function (xhr, status, error) {
                alert("Failed to delete post: " + xhr.responseText || error);
            }
        });
        
        }
        

    });
    $("#editBtn").off("click").on("click", function () {
        window.location.href = `/social/update/${postId}`;
            
    });
    function updateSlider(images) {
        const slider = $('#slider');
        slider.empty(); // Clear existing slides

        // Loop through the list of images and add each image as a slide
        images.forEach((imageSrc, index) => {
            const slide = `<div class="flex-shrink-0 w-full flex items-center justify-center">
                        <img src="${imageSrc}" alt="Slide ${index + 1}" class="w-600 h-400 object-cover">
                      </div>`;
            slider.append(slide);
        });

        // If there's more than one image, initialize the slider navigation buttons
        if (images.length > 1) {
            initializeSliderNavigation();
        }
    }

    function initializeSliderNavigation() {
        let currentSlide = 0;
        const slides = $('#slider .flex-shrink-0');
        const totalSlides = slides.length;

        $('#prevBtn').on('click', function () {
            currentSlide = (currentSlide === 0) ? totalSlides - 1 : currentSlide - 1;
            updateSliderPosition();
        });

        $('#nextBtn').on('click', function () {
            currentSlide = (currentSlide === totalSlides - 1) ? 0 : currentSlide + 1;
            updateSliderPosition();
        });

        function updateSliderPosition() {
            $('#slider').css('transform', `translateX(-${currentSlide * 100}%)`);
        }
    }
    function renderComments(comments) {
        const commentContainer = $('#listComment');
        commentContainer.empty(); // Clear any existing comments

        comments.forEach(comment => {
            const isOwner = comment.userId === parseInt(userId) || post_userid === parseInt(userId)||userRole==='ADMIN'; // Kiểm tra quyền xóa
            const deleteButtonHtml = isOwner ? `<button class="text-red-600 delete-btn" data-id="comment-${comment.id}">Delete</button>` : '';

            const commentHtml = `
            <div id="comment-${comment.id}">
                <div class="flex items-center space-x-2 mt-5">
                    <img src="${comment.userImage}" alt="User Avatar" class="w-8 h-8 rounded-full">
                    <div>
                        <p class="text-gray-800 font-semibold">${comment.username}</p>
                        <p class=" text-xl break-words max-w-[151ch]">${comment.content}</p>
                        <p class="text-xs text-gray-800 font-semibold">${timeAgo(comment.time)}</p>

                        <button class='text-blue-600 reply-btn' data-username="${comment.username}" id='comment-${comment.id}'>Reply</button>
            ${deleteButtonHtml}
                    </div>
                </div>
                <div class="ml-8">${renderReplies(comment.replys)}</div>
            </div>
        `;
            commentContainer.append(commentHtml);
        });

        attachDeleteButtonClick(); // Gắn sự kiện xóa
    }
    function truncateWords(text, wordLimit) {
        const words = text;
        if (words.length > wordLimit) {
            return {
                truncated: words.slice(0, wordLimit) + '...',
                full: text
            };
        }
        return { truncated: text, full: text };
    }

    function renderReplies(replies) {
        let repliesHtml = '';

        replies.forEach(reply => {
            const isOwner = reply.userId === parseInt(userId) || post_userid === parseInt(userId)||userRole==='ADMIN'; // Kiểm tra quyền xóa
            const deleteButtonHtml = isOwner ? `<button class="text-red-600 delete-btn" data-id="reply-${reply.id}">Delete</button>` : '';
            const replyingTo = truncateWords(reply.commentReply, 15).truncated;
            const replyHtml = `
            <div class="flex items-center space-x-2 mt-2 ml-8">
                <img src="${reply.userImage}" alt="User Avatar" class="w-8 h-8 rounded-full">
                <div>
                    <p class="text-gray-800 font-semibold">${reply.username} -> ${reply.parentUsername}</p>
                    <p class="bg-gray-200 font-semibold">Replying to ${replyingTo}</p>
                    <p class=" text-xl break-words max-w-[151ch]">
                                            ${reply.content}
                                        </p>
                    <p class="text-xs text-gray-800 font-semibold">${timeAgo(reply.time)}</p>

                    <button class='text-blue-600 reply-btn' data-username="${reply.username}" id='reply-${reply.id}'>Reply</button>
            ${deleteButtonHtml}
                </div>
            </div>
                ${renderChildReplies(reply.childReplies)} <!-- Render child replies recursively -->
        `;
            repliesHtml += replyHtml;
        });

        return repliesHtml;
    }

    function renderChildReplies(childReplies) {
        let childRepliesHtml = '';
        childReplies.forEach(childReply => {
            const isOwner = childReply.userId === parseInt(userId) || post_userid === parseInt(userId)||userRole==='ADMIN'; // Kiểm tra quyền xóa
            const deleteButtonHtml = isOwner ? `<button class="text-red-600 delete-btn" data-id="reply-${childReply.id}">Delete</button>` : '';
            const replyingTo = truncateWords(childReply.commentReply, 15).truncated;
            const childReplyHtml = `
                <div class="flex items-center space-x-2 mt-2 ml-8">
                    <img src="${childReply.userImage}" alt="User Avatar" class="w-8 h-8 rounded-full">
                    <div>
                        <p class="text-gray-800 font-semibold">${childReply.username} -> ${childReply.parentUsername}</p>
                        <p class="bg-gray-200 font-semibold">Replying to ${replyingTo}</p>
                        <p class=" text-xl break-words max-w-[151ch]">${childReply.content}</p>
            <p class="text-xs text-gray-800 font-semibold">${timeAgo(childReply.time)}</p>
                        <button class='text-blue-600 reply-btn' data-username="${childReply.username}" id='reply-${childReply.id}'>Reply</button>
            ${deleteButtonHtml}
                    </div>

                </div>
            ${renderChildReplies(childReply.childReplies)} <!-- Recursively render child replies -->
            `;
            childRepliesHtml += childReplyHtml;
        });
        return childRepliesHtml;
    }
    function checkIfLiked(postId, userId) {
        $.ajax({
            url: `/api/v1/post/${postId}/isLiked/${userId}`,
            type: 'GET',
            success: function (response) {
                if (response) {
                    let html = '<path d="M12 21.35l-1.45-1.32C6.11 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-4.11 6.86-8.55 11.54L12 21.35z" style="fill:#ff0707"/>'
                    console.log("The user has liked this post.");
                    $("#isLiked").html(html)  // Example of adding a liked class
                } else {
                    console.log("The user has not liked this post.");
                    let html = '<path d="M12 21.35l-1.45-1.32C6.11 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-4.11 6.86-8.55 11.54L12 21.35z" />';
                    $("#isLiked").html(html)
                }
            },
            error: function (xhr, status, error) {
                console.error("Error checking like status:", error);
            }
        });
    }
    $("#likeBtn").on("click", function () {
        // Get the user ID

        // Create the LikeRequest object
        var likeRequest = {
            userId: userId,
            postId: postId
        };

        // Send the AJAX request to the backend to like the post
        $.ajax({
            url: '/api/v1/post/like', // Adjust the URL according to your backend API endpoint
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(likeRequest), // Send the likeRequest object as JSON
            success: function (response) {
                console.log(response);
                fetchPostById(postId);
                // Handle the response here if needed (e.g., update the like count, change the button color, etc.)
            },
            error: function (xhr, status, error) {
                console.error('Error liking the post:', error);
                // Handle the error if needed
            }
        });
    });
    function setReplyIndicator(username) {
        const replyIndicator = $('#replyIndicator');
        replyIndicator.empty(); // Clear existing message if any
        const messageHtml = `
            <p class="bg-gray-200 text-gray-700 text-sm rounded p-2 mb-2 flex justify-between items-center">
                Replying to @${username}
                <button id="removeReplyBtn" class="text-red-500 ml-2">Remove</button>
            </p>
        `;
        replyIndicator.html(messageHtml);
//        const reply = $('.reply-btn').attr('id');
//        $("#commentTxt").attr('data-post', reply);
//        console.log(reply);
        // Add event listener to remove the reply indicator
        $('#removeReplyBtn').on('click', function () {
            replyIndicator.empty();
            $('#commentTxt').val(''); // Clear the textarea
        });
    }
    // Fetch post with ID 1 (for example)
    function attachReplyButtonClick() {
        $('.reply-btn').off('click').on('click', function () {
            const username = $(this).data('username');
            setReplyIndicator(username);
            const textarea = $("#commentTxt");
            let reply = $(this).attr('id');
            $("#commentTxt").attr('data-post', reply);
            console.log(reply);
            // Target the comment form's textarea

            textarea.focus();
            // Focus the textarea
        });
    }
    $('#buttonComment').on('click', function () {
        const textarea = $('textarea');
        const content = textarea.val().trim();
        let replyId = $("#commentTxt").data('post'); // Get reply ID if available
        const replyIndicator = $('#replyIndicator');

        if (replyIndicator.children().length > 0) {
            let idSend = replyId.split('-')[1];
            let commentData;
            console.log(idSend);
            if (replyId.startsWith('reply')) {
                commentData = {
                    userId: userId, // Replace with the actual user ID
                    content: content,
                    parentReplyUserId: idSend // Null if it's a root-level comment
                };
                console.log("reply a reply");

            } else if (replyId.startsWith('comment')) {
                commentData = {

                    userId: userId, // Replace with the actual user ID
                    content: content,
                    commentId: idSend // Null if it's a root-level comment
                };
                console.log("reply a comment");
            }
            $.ajax({
                url: '/api/v1/reply/add', // Adjust the URL
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(commentData),
                success: function (response) {
                    console.log('Reply posted:', response);
                    // Clear the textarea and remove reply context
                    textarea.val('');

                    // Refresh the comments section
                    fetchPostById(postId); // Reload the post and comments
                    window.location.reload();
                },
                error: function (xhr, status, error) {
                    console.log(error);
                    console.error('Error posting comment:', error);
                }
            });

        } else {
            let commentData = {
                postId: postId, // Replace with actual post ID
                userId: userId, // Replace with actual user ID
                content: content
            };
            $.ajax({
                url: '/api/v1/comment/add', // Adjust the URL
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(commentData),
                success: function (response) {
                    console.log('Comment posted:', response);

                    // Clear the textarea and remove reply context
                    textarea.val('');

                    // Refresh the comments section
                    fetchPostById(postId); // Reload the post and comments
                    window.location.reload();
                },
                error: function (xhr, status, error) {
                    console.error('Error posting comment:', error);
                }
            });
        }
        replyId = $("#commentTxt").data('post');
    });
    fetchPostById(postId);
    function timeAgo(dateTime) {
        const now = new Date();
        const targetDate = new Date(dateTime);

        // Calculate the difference in years, months, and days
        const years = now.getFullYear() - targetDate.getFullYear();
        const months = now.getMonth() - targetDate.getMonth() + (years * 12); // Total number of months
        const days = Math.floor((now - targetDate) / (1000 * 60 * 60 * 24)); // Difference in days

        // If the difference is more than a month but less than a year, show the local date
        if (months > 1) {
            return targetDate.toLocaleString(); // Return localized date and time
        }

        // If the difference is more than 1 year
        if (years > 0) {
            return `${years} year${years > 1 ? "s" : ""} ago`;
        }

        // If the difference is more than 30 days but less than 1 month, show months
        if (months > 0) {
            return `${months} month${months > 1 ? "s" : ""} ago`;
        }

        // If the difference is more than 1 day, show days
        if (days >= 1) {
            return `${days} day${days > 1 ? "s" : ""} ago`;
        }

        // For periods less than a day, use the standard time intervals (hour, minute, second)
        const diff = Math.floor((now - targetDate) / 1000); // Difference in seconds
        const timeIntervals = [
            {label: "hour", seconds: 3600},
            {label: "minute", seconds: 60},
            {label: "second", seconds: 1},
        ];

        const timeInterval = timeIntervals.find(i => diff >= i.seconds) || {label: "second", seconds: 1}; // Fallback to seconds
        const count = Math.floor(diff / timeInterval.seconds);

        return `${count} ${timeInterval.label}${count !== 1 ? "s" : ""} ago`;
    }
    function attachDeleteButtonClick() {
        $('.delete-btn').off('click').on('click', function () {
            const id = $(this).data('id'); // Lấy ID của bình luận hoặc trả lời
            if (id.startsWith('comment')) {
                if (confirm('You sure you want to delete?')) {
                    $.ajax({
                        url: `/api/v1/comment/${id.split('-')[1]}`, // Đường dẫn API xóa
                        type: 'DELETE',
                        success: function () {
                            
                            fetchPostById(postId); // Refresh comments section
                        },
                        error: function (xhr, status, error) {
                            console.error('Lỗi khi xóa:', error);
                        }
                    });
                }
            } else if (id.startsWith('reply'))
            {
                if (confirm('You sure you want to delete?')) {
                    $.ajax({
                        url: `/api/v1/reply/${id.split('-')[1]}`, // Đường dẫn API xóa
                        type: 'DELETE',
                        success: function () {
                            
                            fetchPostById(postId); // Refresh comments section
                        },
                        error: function (xhr, status, error) {
                            console.error('Lỗi khi xóa:', error);
                        }
                    });
                }
            }

        });
    }
});

