package com.kaifengruan.socialapp.repository;

import com.kaifengruan.socialapp.POJO.Post;
import com.kaifengruan.socialapp.POJO.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
    List<Post> findAllByUser(User user);

    Post findByPostId(String postId);
}
