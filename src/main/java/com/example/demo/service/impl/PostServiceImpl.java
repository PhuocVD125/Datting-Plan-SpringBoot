/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.service.impl;

import com.example.demo.dto.CommentDto;
import com.example.demo.dto.PostCardDto;
import com.example.demo.dto.request.PostCreateDto;
import com.example.demo.dto.PostDto;
import com.example.demo.dto.ReplyDto;
import com.example.demo.dto.request.PostUpdateDto;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Post;
import com.example.demo.entity.Recommendation;
import com.example.demo.entity.Reply;
import com.example.demo.entity.Tag;
import com.example.demo.entity.User;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.RecommendationRepository;
import com.example.demo.repository.TagRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.PostService;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author pc
 */
@Service
public class PostServiceImpl implements PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final RecommendationRepository recommendationRepo;
    private final ModelMapper modelMapper;

    @Autowired

    public PostServiceImpl(UserRepository userRepository, PostRepository postRepository, TagRepository tagRepository, RecommendationRepository recommendationRepo, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
        this.recommendationRepo = recommendationRepo;
        this.modelMapper = modelMapper;
    }

    @Override
    public String createPost(PostCreateDto postDto) {
        Optional<User> u = userRepository.findById(postDto.getUserId());
        Optional<Recommendation> r = recommendationRepo.findById(postDto.getRecommendationId());
        if (u.isPresent() && r.isPresent()) {
            Set<Tag> tags = new HashSet<>();
            for (String i : postDto.getTagTitle()) {
                Optional<Tag> t = tagRepository.findByTitle(i);
                if (t.isEmpty()) {
                    // Create a new Tag if it does not exist
                    Tag temp = new Tag();
                    temp.setTitle(i);
                    tagRepository.save(temp);
                    tags.add(temp);
                    System.out.println(temp.getTitle());
                } else {
                    tags.add(t.get());
                }

            }
            Post newPost = new Post();
            newPost.setRecommendationPost(r.get());
            newPost.setTitle(postDto.getTitle());
            newPost.setContent(postDto.getContent());
            newPost.setImage(postDto.getImage());
            newPost.setTags(tags);
            newPost.setTime(postDto.getTime());
            newPost.setUserPost(u.get());
            newPost.setUserPost(u.get());
            postRepository.save(newPost);
            return "New Post Created";
        }
        return "Post Cant be created without user";
    }

    @Override
    public String updatePost(Long postId, PostUpdateDto postDto) {
        Optional<Post> p = postRepository.findById(postId);
        Recommendation r = recommendationRepo.findById(postDto.getRecommendationId()).get();
        p.get().setRecommendationPost(r);
        Set<Tag> tags = new HashSet<>();
        for (String i : postDto.getTagTitle()) {
            Optional<Tag> t = tagRepository.findByTitle(i);
            if (t.isEmpty()) {
                // Create a new Tag if it does not exist
                Tag temp = new Tag();
                temp.setTitle(i);
                tagRepository.save(temp);
                tags.add(temp);
                System.out.println(temp.getTitle());
            } else {
                tags.add(t.get());
            }

        }
        p.get().setTags(tags);
        p.get().setTitle(postDto.getTitle());
        p.get().setContent(postDto.getContent());
        p.get().setImage(postDto.getImage());
        postRepository.save(p.get());
        return "Update post successfully";
    }

    @Override
    public String deletePost(Long postId) {
        Optional<Post> p = postRepository.findById(postId);
        postRepository.delete(p.get());
        return "Deleted Post";
    }

    @Override
    public Page<PostDto> getAllPostPageable(Pageable pageable) {
        return postRepository.findAll(pageable).map(this::convertToDto);
    }

    @Override
    public PostCardDto getPostById(Long id) {
        Post post = postRepository.findById(id).get();

        // Map to PostCardDto
        PostCardDto postCardDto = mapToPostCardDto(post);
        return postCardDto;
    }

    @Override
    public Page<PostDto> searchPost(Pageable pageable, String keyword) {
        return postRepository.searchPosts(keyword, pageable).map(this::convertToDto);
    }

    private PostDto convertToDto(Post post) {
        // Concatenate all tags into a single string prefixed by #
        String tags = post.getTags().stream()
                .map(tag -> "#" + tag.getTitle())
                .collect(Collectors.joining(" ")); // Join with a space

        return new PostDto(
                post.getId(),
                post.getRecommendationPost().getTitle() + " - " + post.getRecommendationPost().getLocation(),
                post.getTitle(),
                post.getContent(),
                post.getImage()!= null ?post.getImage().get(0):null,
                post.getTime(),
                post.getUserPost() != null ? post.getUserPost().getId() : null,
                tags,
                post.getUserPost() != null ? post.getUserPost().getAccount().getUsername() : null,
                post.getUserPost().getAvatar(),
                post.getLikes() != null ? post.getLikes().size() : 0,
                post.getComments() != null ? post.getComments().size() : 0
        );
    }

    @Override
    public List<PostDto> getTop8MostLikedPosts() {
        // Fetch the top 8 posts
        List<Post> posts = postRepository.findTop8MostLikedPosts(PageRequest.of(0, 8));

        // Convert each Post entity to PostDto
        return posts.stream()
                .map(post -> convertToDto(post))
                .collect(Collectors.toList());
    }

    private PostCardDto mapToPostCardDto(Post post) {
        PostCardDto postCardDto = modelMapper.map(post, PostCardDto.class);

        // Map comments to CommentDto
        List<CommentDto> commentDtos = post.getComments()
                .stream()
                .map(this::mapToCommentDto)
                .toList();

        // Generate tags string starting with '#' and separated by spaces
        String tags = post.getTags()
                .stream()
                .map(tag -> tag.getTitle()) // Extract name from Tag object
                .collect(Collectors.joining(" ")); // Join with space separator

        // Populate PostCardDto fields
        postCardDto.setRecommendationId(post.getRecommendationPost().getId());
        postCardDto.setRecommendation(post.getRecommendationPost().getTitle() + " - " + post.getRecommendationPost().getLocation());
        postCardDto.setUsername(post.getUserPost().getAccount().getUsername());
        postCardDto.setTags(tags);
        postCardDto.setUserImage(post.getUserPost().getAvatar());
        postCardDto.setComments(commentDtos);
        postCardDto.setLikeCount(post.getLikes().size());
        postCardDto.setCommentCount(postCardDto.getComments().size());
        return postCardDto;
    }

    private CommentDto mapToCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setContent(comment.getContent());
        commentDto.setTime(comment.getTime());
        commentDto.setUserId(comment.getUser().getId());
        commentDto.setUsername(comment.getUser().getAccount().getUsername());
        commentDto.setUserImage(comment.getUser().getAvatar());
        // Map replies to ReplyDto
        List<ReplyDto> replyDtos = comment.getReplys().stream()
                .map(this::mapToReplyDto)
                .collect(Collectors.toList());
        commentDto.setReplys(replyDtos);

        return commentDto;
    }

    private ReplyDto mapToReplyDto(Reply reply) {
        ReplyDto replyDto = new ReplyDto();
        replyDto.setId(reply.getId());
        replyDto.setContent(reply.getContent());
        replyDto.setTime(reply.getTime());
        replyDto.setUserId(reply.getUserReply().getId());
        replyDto.setUsername(reply.getUserReply().getAccount().getUsername());
        replyDto.setUserImage(reply.getUserReply().getAvatar());
        // Include parent username (if applicable)
        if (reply.getParentReply() != null) {
            replyDto.setParentUserId(reply.getParentReply().getUserReply().getId());
            replyDto.setParentUsername(reply.getParentReply().getUserReply().getAccount().getUsername());
            replyDto.setCommentReply(reply.getParentReply().getContent()); // Set the content of the parent reply
        } else if (reply.getParentComment() != null) {
            replyDto.setParentUserId(reply.getParentComment().getUser().getId());
            replyDto.setParentUsername(reply.getParentComment().getUser().getAccount().getUsername());
            replyDto.setCommentReply(reply.getParentComment().getContent()); // Set the content of the parent comment
        }

        // Recursively map child replies
        List<ReplyDto> childReplies = reply.getChildReplies().stream()
                .map(this::mapToReplyDto)
                .collect(Collectors.toList());
        replyDto.setChildReplies(childReplies);

        return replyDto;
    }

    @Override
    public Page<PostDto> getPostsByTagName(String tagName, Pageable pageable) {
        return postRepository.findPostsByTagName(tagName, pageable).map(this::convertToDto);
    }

    @Override
    public Page<PostDto> getPostRecIdPageable(Long recId, Pageable pageable) {
        return postRepository.findPostsByRecommendationId(recId, pageable).map(this::convertToDto);
    }

    @Override
    public Page<PostDto> getPostUserIdPageable(Long userId, Pageable pageable) {
        return postRepository.findPostsByUserId(userId, pageable).map(this::convertToDto);
    }
}
