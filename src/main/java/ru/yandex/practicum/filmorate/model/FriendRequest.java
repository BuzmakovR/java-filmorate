package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(exclude = "isConfirmed")
public class FriendRequest {

    final Long userId;

    final Long friendId;

    @Builder.Default
    boolean isConfirmed = false;
}
