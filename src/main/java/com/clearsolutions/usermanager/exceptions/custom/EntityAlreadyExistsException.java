package com.clearsolutions.usermanager.exceptions.custom;

import org.springframework.http.HttpStatus;

public class EntityAlreadyExistsException extends BasicApplicationException {

    /**
     * Constructs a new EntityAlreadyExistsException with
     * the specified entity type and unique constraint details.
     *
     * @param entityType The type of entity for which the duplication occurred.
     * @param details    The field or property by which uniqueness was checked,
     *                   providing additional details about the duplicated entity.
     */
    public EntityAlreadyExistsException(String entityType, String details) {
        super(String.format("%s with '%s' already exists!", entityType, details), HttpStatus.BAD_REQUEST);
    }
}
