package com.eraport.domain.services.users;

import com.eraport.domain.entities.User;
import java.util.UUID;

public interface UserCommandService {
    User create(User user, UUID createdBy);
    User update(User user, UUID updatedBy);
    void delete(UUID id, UUID deletedBy);
    void revokeUser(UUID userId, UUID revokedBy);
    void activateUser(UUID userId, UUID activatedBy);
}
