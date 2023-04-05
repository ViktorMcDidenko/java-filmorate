package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private int filmId;
    @NotBlank
    private String title;
    @Size(max = 200)
    private String description;
    private LocalDate releaseDate;
    @Positive
    private long duration;
    Set<Integer> likes = new HashSet<>();

    public Film(String title, String description, LocalDate releaseDate, int duration) {
        this.title = title;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public void like(int id) {
        likes.add(id);
    }

    public void unlike(int id) {
        if(!likes.contains(id)) {
            throw new NotFoundException(String.format("User with id %d hasn't liked the film %s yet.",
                    id, getTitle()));
        }
        likes.remove(id);
    }
}