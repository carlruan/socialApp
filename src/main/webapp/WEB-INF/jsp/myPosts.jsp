<!DOCTYPE html> <%@page language="java"
import="com.kaifengruan.socialapp.POJO.User,
com.kaifengruan.socialapp.POJO.Post, java.util.*" %> <%@ taglib uri =
"http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<script type="text/javascript">
  $("#submit").click(function () {
    $("#pushPost").submit(function (e) {
      e.preventDefault(); // avoid to execute the actual submit of the form.
      var form = $(this);
      var actionUrl = form.attr("action");
      $.ajax({
        type: "POST",
        url: actionUrl,
        data: form.serialize(), // serializes the form's elements.
      });
    });
  });

  $("#delete").click(function () {
    $("#deleteForm").submit(function (e) {
      e.preventDefault(); // avoid to execute the actual submit of the form.
      var form = $(this);
      var actionUrl = form.attr("action");
      $.ajax({
        type: "POST",
        url: actionUrl,
        data: form.serialize(), // serializes the form's elements.
      });
    });
  });

</script>

<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <title>My posts</title>
    <% User user = (User)session.getAttribute("user");
    request.getSession().setAttribute("user", user); %>
  </head>
  <body>
    <table>
      <tr>
        <td><a href="myFollows.htm">My Follows</a></td>
        <td><a href="worldPost.htm">World Posts</a></td>
        <td><a href="myPost.htm">My posts</a></td>
      </tr>
    </table>

    <form id="pushPost" action="pushPost.htm" method="post">
      <textarea name="content" id="content" cols="30" rows="10"></textarea>
      <button id="submit">Post</button>
    </form>
      <c:forEach items="${requestScope.posts}" var="post">
      <form id="deleteForm" action="/deletePost.htm" method="post">
        <input type="hidden" name="postId" value=${post.postId}>
        <td>${post.user.username}</td>
          <br>
          <td>${post.content}</td>
          <br>
          <td>${post.post_created}</td>
          <br>
          <button id="delete">Delete</button>
          <hr>
        
        </form>
          
      </c:forEach>
    
  </body>
</html>
