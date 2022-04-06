package com.kaifengruan.socialapp.controller;

import com.kaifengruan.socialapp.POJO.Connect;
import com.kaifengruan.socialapp.POJO.Post;
import com.kaifengruan.socialapp.POJO.User;
import com.kaifengruan.socialapp.repository.ConnectRepository;
import com.kaifengruan.socialapp.repository.PostRepository;
import com.kaifengruan.socialapp.repository.UserRepository;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.net.http.HttpRequest;
import java.util.*;

@Controller
public class HomeController {

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ConnectRepository connectRepository;

    @RequestMapping(value = "/", method= RequestMethod.GET)
    public String index(){
        return "login";
    }

    @GetMapping("/create.htm")
    public ModelAndView createUser(){
        return new ModelAndView("create");
    }

    @PostMapping("/register.htm")
    public ModelAndView register(@RequestParam String email, @RequestParam String username, @RequestParam String password){
        if(email.equals("") || username.equals("") || password.equals("")){
            String msg = "Attribute can not be empty!";
            return new ModelAndView("registerErr", "msg", msg);
        }
        if(!EmailValidator.getInstance().isValid(email)){
            String msg = "Email is invalid!";
            return new ModelAndView("registerErr", "msg", msg);
        }
        if(userRepository.findByEmail(email) != null){
            String msg = "Email has been registered!";
            return new ModelAndView("registerErr", "msg", msg);
        }
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(password);
        userRepository.save(user);
        return new ModelAndView("registerSuc", "user", user.getUsername());
    }

    @PostMapping("/login.htm")
    public ModelAndView login(@RequestParam String email, @RequestParam String password, HttpServletRequest request){
        if(email.equals("") || password.equals("")){
            String msg = "Attribute can not be empty!";
            return new ModelAndView("loginErr", "msg", msg);
        }
        if(!EmailValidator.getInstance().isValid(email) || userRepository.findByEmail(email) == null){
            String msg = "Email is invalid!";
            return new ModelAndView("loginErr", "msg", msg);
        }
        User user = userRepository.findByEmail(email);
        if(!user.getPassword().equals(password)){
            String msg = "Password wrong!";
            return new ModelAndView("loginErr", "msg", msg);
        }
        request.getSession().setAttribute("user", user);
        List<Connect> conn = user.getFollows();
        List<Post> posts = new ArrayList<>();
        for(Connect cn : conn){
            String connectorId = cn.getConnectorId();
            for(Post p : userRepository.findByUserId(connectorId).getPosts()){
                posts.add(p);
            }
        }
        Collections.sort(posts, (Post a, Post b)->{if(a.getPost_updated().after(b.getPost_updated())) return -1; return 1;});
        request.getSession().setAttribute("posts", posts);
        return new ModelAndView("myFollows");
    }


    @GetMapping("/myPost.htm")
    public ModelAndView myPost(HttpServletRequest request){
        User user = (User)request.getSession().getAttribute("user");
        if(user == null) {
            String msg = "No user information!";
            return new ModelAndView("loginErr", "msg", msg);
        }
        request.getSession().setAttribute("user", user);
        //List<Post> posts = user.getPosts();
        List<Post> posts = postRepository.findAllByUser(user);
        Collections.sort(posts, (Post a, Post b)->{if(a.getPost_created().after(b.getPost_created())) return -1; return 1;});
        return new ModelAndView("myPosts", "posts", posts);
    }

    @GetMapping("/myFollows.htm")
    public ModelAndView myFollows(HttpServletRequest request){
        User user = (User)request.getSession().getAttribute("user");
        if(user == null) {
            String msg = "No user information!";
            return new ModelAndView("loginErr", "msg", msg);
        }
        request.getSession().setAttribute("user", user);
        List<Connect> conn = connectRepository.findAllByUser(user);
        List<Post> posts = new ArrayList<>();
        for(Connect cn : conn){
            String connectorId = cn.getConnectorId();
            List<Post> l = postRepository.findAllByUser(userRepository.findByUserId(connectorId));
            for(Post p : l){
                posts.add(p);
            }
        }
        Collections.sort(posts, (Post a, Post b)->{if(a.getPost_updated().after(b.getPost_updated())) return -1; return 1;});
        return new ModelAndView("myFollows", "posts", posts);
    }

    @GetMapping("/worldPost.htm")
    public ModelAndView worldPost(HttpServletRequest request){
        User user = (User)request.getSession().getAttribute("user");
        if(user == null) {
            String msg = "No user information!";
            return new ModelAndView("loginErr", "msg", msg);
        }
        List<Connect> conn = connectRepository.findAllByUser(user);
        Set<String> set = new HashSet<>();
        for(Connect cn : conn){
            set.add(cn.getConnectorId());
        }
        set.add(user.getUserId());
        List<Post> posts = postRepository.findAll();
        List<Post> psts = new ArrayList<>();
        for(Post p : posts){
            String curId = p.getUser().getUserId();
            if(set.contains(curId)) continue;
            psts.add(p);
        }
        Collections.sort(psts, (Post a, Post b)->{if(a.getPost_updated().after(b.getPost_updated())) return -1; return 1;});
        request.getSession().setAttribute("user", user);
        return new ModelAndView("worldPosts", "posts", psts);
    }

    @PostMapping("/pushPost.htm")
    public ModelAndView pushPost(HttpServletRequest request){
        User user = (User)request.getSession().getAttribute("user");
        if(user == null) {
            String msg = "No user information!";
            return new ModelAndView("loginErr", "msg", msg);
        }
        String content = request.getParameter("content");
        Post post = new Post();
        post.setContent(content);
        post.setUser(user);
        postRepository.save(post);
        request.getSession().setAttribute("user", user);
        List<Post> posts = postRepository.findAllByUser(user);
        Collections.sort(posts, (Post a, Post b)->{if(a.getPost_created().after(b.getPost_created())) return -1; return 1;});
        return new ModelAndView("myPosts", "posts", posts);

    }

    @PostMapping("/connect.htm")
    public ModelAndView connect(HttpServletRequest request){
        User user = (User)request.getSession().getAttribute("user");
        if(user == null) {
            String msg = "Connect ERR!";
            return new ModelAndView("loginErr", "msg", msg);
        }
        String connectId = request.getParameter("connector_id");
        Connect conn = new Connect();
        conn.setUser(user);
        conn.setConnectorId(connectId);
        user.getFollows().add(conn);
        connectRepository.save(conn);
        request.getSession().setAttribute("user", user);
        List<Connect> con = connectRepository.findAllByUser(user);
        List<Post> posts = new ArrayList<>();
        for(Connect cn : con){
            String connectorId = cn.getConnectorId();
            List<Post> l = postRepository.findAllByUser(userRepository.findByUserId(connectorId));
            for(Post p : l){
                posts.add(p);
            }
        }
        Collections.sort(posts, (Post a, Post b)->{if(a.getPost_updated().after(b.getPost_updated())) return -1; return 1;});
        return new ModelAndView("myFollows", "posts", posts);

    }

    @PostMapping("/deletePost.htm")
    public ModelAndView deletePost(HttpServletRequest request){
        User user = (User)request.getSession().getAttribute("user");
        if(user == null) {
            String msg = "Connect ERR!";
            return new ModelAndView("loginErr", "msg", msg);
        }
        String postId = request.getParameter("postId");
        Post post = postRepository.findByPostId(postId);
        user.getPosts().remove(post);
        postRepository.delete(post);
        request.getSession().setAttribute("user", user);
        //List<Post> posts = user.getPosts();
        List<Post> posts = postRepository.findAllByUser(user);
        Collections.sort(posts, (Post a, Post b)->{if(a.getPost_created().after(b.getPost_created())) return -1; return 1;});
        return new ModelAndView("myPosts", "posts", posts);
    }

    @PostMapping("/unfollow.htm")
    public ModelAndView unfollow(HttpServletRequest request){
        User user = (User)request.getSession().getAttribute("user");
        if(user == null) {
            String msg = "Connect ERR!";
            return new ModelAndView("loginErr", "msg", msg);
        }
        String connectId = request.getParameter("connector_id");
        Connect conn = connectRepository.findByConnectorIdAndUser(connectId, user);
        if(conn != null){
            connectRepository.delete(conn);
            user.getFollows().remove(conn);
        }

        List<Connect> con = connectRepository.findAllByUser(user);
        List<Post> posts = new ArrayList<>();
        for(Connect cn : con){
            String connectorId = cn.getConnectorId();
            List<Post> l = postRepository.findAllByUser(userRepository.findByUserId(connectorId));
            for(Post p : l){
                posts.add(p);
            }
        }
        Collections.sort(posts, (Post a, Post b)->{if(a.getPost_updated().after(b.getPost_updated())) return -1; return 1;});
        request.getSession().setAttribute("user", user);

        return new ModelAndView("myFollows", "posts", posts);
    }

    @GetMapping("/test")
    public ModelAndView test(){
        User user = new User();
//        List<Post> posts = new ArrayList<>();
//        Post post = new Post();
//        post.setUser(user);
//        post.setContent("For testing");
//        posts.add(post);
//        user.setPosts(posts);
        user.setUsername("admin");
        user.setPassword("admin");
        user.setEmail("admin@test.com");
        //postRepository.save(post);
        userRepository.save(user);
        return new ModelAndView("registerSuc", "user", user);
    }

    @GetMapping("/post")
    public ModelAndView post(){
        //User user = userRepository.findByEmail("admin@test.com");
        User user = userRepository.findByEmail("carlruan981024@gmail.com");
        Post post = new Post();
        post.setUser(user);
        post.setContent("carl ruan post");
        postRepository.save(post);
        user.getPosts().add(post);
        //userRepository.save(user);
        return new ModelAndView("login");
    }

//    @GetMapping("/connect")
//    public ModelAndView connect(){
//        User user1 = userRepository.findByEmail("admin@test.com");
//        User user2 = userRepository.findByEmail("carlruan981024@gmail.com");
//        Connect conn = new Connect();
//        conn.setUser(user2);
//        conn.setConnector_id(user1.getUserId());
//        System.out.println(conn.getConnector_id());
//        connectRepository.save(conn);
//        user2.getFollows().add(conn);
//        //userRepository.save(user);
//        return new ModelAndView("login");
//    }

}
