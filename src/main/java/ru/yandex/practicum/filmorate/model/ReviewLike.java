package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(exclude = {"isLike"})
public class ReviewLike {

    final Long reviewId;

    final Long userId;

    @Builder.Default
    final boolean isLike = true;
}
