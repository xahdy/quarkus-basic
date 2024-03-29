package com.github.xahdy.quarkusbasic.application;
import com.github.xahdy.quarkusbasic.application.requestdto.UpdatePost;

import com.github.xahdy.quarkusbasic.entity.Comment;
import com.github.xahdy.quarkusbasic.entity.Post;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.bson.Document;
import org.bson.types.ObjectId;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Path("/posts")
public class ReactivePostResource {


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<Post> list() {
        return Post.streamAllPosts();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> addPost(Post post) {
        post.creationDate = LocalDateTime.now();
        return post.<Post>persist().map(v ->
                Response.created(URI.create("/posts/" + v.id.toString()))
                        .entity(post).build());
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Post> update(@PathParam("id") String id, UpdatePost updatePost) {
        return Post.updatePost(id, updatePost);
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Post> getPost(@PathParam("id") String id) {
        return Post.findById(new ObjectId(id));
    }

    @DELETE
    @Path("/{id}")
    public Uni<Void> deletePost(@PathParam("id") String id) {
        return Post.deletePost(id);
    }

    @GET
    @Path("/search")
    public Uni<List<Post>> search(@QueryParam("author") String author, @QueryParam("title") String title,
                                  @QueryParam("dateFrom") String dateFrom, @QueryParam("dateTo") String dateTo) {
        if (author != null) {
            return Post.find("{'author': ?1,'title': ?2}", author, title).list();
        }
        return Post
                .find("{'creationDate': {$gte: ?1}, 'creationDate': {$lte: ?2}}", ZonedDateTime.parse(dateFrom).toLocalDateTime(),
                        ZonedDateTime.parse(dateTo).toLocalDateTime()).list();
    }

    @GET
    @Path("/search2")
    public Uni<List<Post>> searchCustomQueries(@QueryParam("authors") List<String> authors) {

        // using Document
        return Post.find(new Document("author", new Document("$in", authors))).list();

        // using a raw JSON query
        //Post.find("{'$or': {'author':John Doe, 'author':Grace Kelly}}");
        //Post.find("{'author': {'$in': [John Doe, Grace Kelly]}}");

        // using Panache QL
        //Post.find("author in (John Doe,Grace Kelly)");

    }

    @PUT
    @Path("/{id}/comment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> addCommentToPost(@PathParam("id") String id, Comment comment) {
        return Post.addCommentToPost(comment, id).map(v -> Response.accepted(v).build());
    }

}