package com.example.productsfromusa.services.data;

import com.example.productsfromusa.models.Category;
import com.example.productsfromusa.models.Post;
import com.example.productsfromusa.repositories.PostRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService implements PostServiceImpl {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private EntityManager entityManager;

    @Transactional
    public boolean addPost(Post post) {
        postRepository.save(post);
        return existsById(post.getId());
    }

    public Post getPostById(String id) {
        return postRepository.findPostById(id);
    }

    public Post getPostByNumber(int number) {
        return postRepository.findPostByNumber(number);
    }

    public Post getLastPost(Category category) {
        if(category != null) {
            LocalDateTime currentDateTime = LocalDateTime.now();
            LocalDateTime thresholdDateTime = currentDateTime.minusHours(48);

            List<Post> list = getAll();
            Post maxPost = null;
            int maxNumber = Integer.MIN_VALUE;

            for (Post post : list) {
                if (post.getCategory().getId().equals(category.getId()) && post.getCreationDate().isAfter(thresholdDateTime)) {
                    int currentNumber = post.getNumber();
                    if (currentNumber > maxNumber) {
                        maxNumber = currentNumber;
                        maxPost = post;
                    }
                }
            }

            return maxPost;
        } else {
            LocalDateTime currentDateTime = LocalDateTime.now();
            LocalDateTime thresholdDateTime = currentDateTime.minusHours(48);

            List<Post> list = getAll();
            Post maxPost = null;
            int maxNumber = Integer.MIN_VALUE;

            for (Post post : list) {
                if (post.getCreationDate().isAfter(thresholdDateTime)) {
                    int currentNumber = post.getNumber();
                    if (currentNumber > maxNumber) {
                        maxNumber = currentNumber;
                        maxPost = post;
                    }
                }
            }

            return maxPost;
        }
    }
    @Transactional
    public void savePost(Post post) {
        postRepository.save(post);
    }
    @Transactional
    public Post mergePost(Post post) {
        return entityManager.merge(post);
    }
    @Transactional
    public void removePostById(String id) {
        postRepository.deletePostById(id);
    }

    public List<Post> getAll() {
        return postRepository.findAll();
    }

    @Override
    public boolean existsById(String id) {
        return postRepository.existsById(id);
    }
}
