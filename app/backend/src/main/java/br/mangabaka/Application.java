package br.mangabaka;

import br.mangabaka.api.controller.ExampleResource;
import br.mangabaka.api.mapper.NotFoundExceptionMapper;
import jakarta.ws.rs.ApplicationPath;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/v1")
public class Application extends jakarta.ws.rs.core.Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(ExampleResource.class);
        classes.add(NotFoundExceptionMapper.class);

        return classes;
    }
}