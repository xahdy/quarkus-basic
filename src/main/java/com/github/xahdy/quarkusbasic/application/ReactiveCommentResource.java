package com.github.xahdy.quarkusbasic.application;

import com.github.xahdy.quarkusbasic.application.requestdto.UpdateComment;
import com.github.xahdy.quarkusbasic.entity.Comment;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.bson.types.ObjectId;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/comments")
public class ReactiveCommentResource {


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<Comment> list() {
        return Comment.streamAllComments();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Comment> getComment(@PathParam("id") String id) {
        return Comment.findById(new ObjectId(id));
    }

    @DELETE
    @Path("/{id}")
    public Uni<Void> deleteComment(@PathParam("id") String id) {
        return Comment.deleteComment(id);
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Comment> update(@PathParam("id") String id, UpdateComment updateComment) {
        return Comment.updateComment(id, updateComment);
    }

}