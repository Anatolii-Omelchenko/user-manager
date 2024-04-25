package com.clearsolutions.usermanager.exceptions.custom;

import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends BasicApplicationException {

    /**
     * Constructs a new EntityNotFoundException with the specified
     * entity type and details about the missing entity.
     *
     * @param entityType The type of entity that was expected to be found.
     * @param details    The field or property by which the entity was searched,
     *                   providing additional details about the missing entity.
     */
    public EntityNotFoundException(final String entityType, final String details) {
        super(String.format("%s with `%s` was not found!", entityType, details), HttpStatus.NOT_FOUND);
    }
}
