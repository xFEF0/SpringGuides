package com.xfef0.restful.controller;

import com.xfef0.restful.Status;
import com.xfef0.restful.assembler.OrderModelAssembler;
import com.xfef0.restful.entity.Order;
import com.xfef0.restful.exception.OrderNotFoundException;
import com.xfef0.restful.repository.OrderRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class OrderController {

    private final OrderRepository repository;
    private final OrderModelAssembler assembler;

    public OrderController(OrderRepository repository, OrderModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    @GetMapping("/orders")
    public CollectionModel<EntityModel<Order>> all() {
        List<EntityModel<Order>> orders = repository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(orders,
                linkTo(methodOn(OrderController.class).all()).withSelfRel());
    }

    @GetMapping("/orders/{id}")
    public EntityModel<Order> one(@PathVariable Long id) {
        Order order = repository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return assembler.toModel(order);
    }

    @PostMapping("/orders")
    public ResponseEntity<?> newOrder(@RequestBody Order order) {
        order.setStatus(Status.IN_PROGRESS);
        EntityModel<Order> entityModel = assembler.toModel(repository.save(order));

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @DeleteMapping("/orders/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable  Long id) {
        Order order = repository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        if (Status.IN_PROGRESS.equals(order.getStatus())) {
            order.setStatus(Status.CANCELLED);
            return ResponseEntity.ok(assembler.toModel(repository.save(order)));
        }
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(Problem.create()
                        .withTitle("Method not allowed")
                        .withDetail(
                                String.format("You can't cancel an order that is in the %s status",
                                        order.getStatus())
                        )
                );
    }

    @PutMapping("/orders/{id}/complete")
    public ResponseEntity<?> complete(@PathVariable  Long id) {
        Order order = repository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        if (Status.IN_PROGRESS.equals(order.getStatus())) {
            order.setStatus(Status.COMPLETED);
            return ResponseEntity.ok(assembler.toModel(repository.save(order)));
        }

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(Problem.create()
                        .withTitle("Method not allowed")
                        .withDetail(
                                String.format("You can't complete an order that is in the %s status",
                                order.getStatus())
                        )
                );
    }
}
