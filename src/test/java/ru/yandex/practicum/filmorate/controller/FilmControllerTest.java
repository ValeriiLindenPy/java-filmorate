package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;

import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.utils.FilmsListTypeToken;
import ru.yandex.practicum.filmorate.model.utils.LocalDateTypeAdapter;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FilmControllerTest {
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter()).create();
    private final URI url = URI.create("http://localhost:8080/films");


    @Test
    void filmCreate201() throws Exception {
        Film film = Film.builder().name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100).build();

        String jsonBody = gson.toJson(film);


        HttpRequest request = HttpRequest.newBuilder().uri(url).header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
    }

    @Test
    void filmCreateFailName() throws Exception {
        Film film = Film.builder().name("")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100).build();

        String jsonBody = gson.toJson(film);


        HttpRequest request = HttpRequest.newBuilder().uri(url).header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
    }

    @Test
    void filmCreateFailDescription() throws Exception {
        Film film = Film.builder().name("name")
                .description("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. " +
                        "Здесь они хотят разыскать господина Огюста Куглова, который задолжал им " +
                        "деньги, а именно 20 миллионов. " +
                        "о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100).build();

        String jsonBody = gson.toJson(film);


        HttpRequest request = HttpRequest.newBuilder().uri(url).header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
    }

    @Test
    void filmCreateFailReleaseDate() throws Exception {
        Film film = Film.builder().name("name")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1890, 3, 25))
                .duration(100).build();

        String jsonBody = gson.toJson(film);


        HttpRequest request = HttpRequest.newBuilder().uri(url).header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(500, response.statusCode());
    }

    @Test
    void filmCreateFailDuration() throws Exception {
        Film film = Film.builder().name("name")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(-100).build();

        String jsonBody = gson.toJson(film);


        HttpRequest request = HttpRequest.newBuilder().uri(url).header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(500, response.statusCode());
    }


    @Test
    void filmGetAll() throws Exception {
        Film film = Film.builder().name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100).build();

        String jsonBody = gson.toJson(film);

        HttpRequest request = HttpRequest.newBuilder().uri(url).header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();

        client.send(request, HttpResponse.BodyHandlers.ofString());

        HttpRequest getRequest = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        List<Film> films = gson.fromJson(getResponse.body(), new FilmsListTypeToken().getType());

        assertEquals("nisi eiusmod", films.get(0).getName(), "Некорректное имя задачи");
    }


    @Test
    void filmUpdate() throws Exception {
        Film film = Film.builder().name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100).build();

        String jsonBody = gson.toJson(film);

        HttpRequest request = HttpRequest.newBuilder().uri(url).header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();

        client.send(request, HttpResponse.BodyHandlers.ofString());

        HttpRequest getRequest = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        List<Film> films = gson.fromJson(getResponse.body(), new FilmsListTypeToken().getType());

        Film oldFilm = films.get(0);

        Film updatedFilm = Film.builder().name("Heat")
                .id(oldFilm.getId()).description(oldFilm.getDescription())
                .releaseDate(oldFilm.getReleaseDate())
                .duration(oldFilm.getDuration())
                .build();

        String jsonUpdatedFilm = gson.toJson(updatedFilm);

        HttpRequest putRequest = HttpRequest.newBuilder().uri(url).header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonUpdatedFilm)).build();

        HttpResponse<String> putResponse = client.send(putRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, putResponse.statusCode());
    }

    @Test
    void filmUpdateFailUnknown() throws Exception {
        Film film = Film.builder().name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100).build();

        String jsonBody = gson.toJson(film);

        HttpRequest request = HttpRequest.newBuilder().uri(url).header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();

        client.send(request, HttpResponse.BodyHandlers.ofString());

        HttpRequest getRequest = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        List<Film> films = gson.fromJson(getResponse.body(), new FilmsListTypeToken().getType());

        Film oldFilm = films.get(0);

        Film updatedFilm = Film.builder().name("Heat")
                .id(20L).description(oldFilm.getDescription())
                .releaseDate(oldFilm.getReleaseDate())
                .duration(oldFilm.getDuration())
                .build();

        String jsonUpdatedFilm = gson.toJson(updatedFilm);

        HttpRequest putRequest = HttpRequest.newBuilder().uri(url).header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonUpdatedFilm)).build();

        HttpResponse<String> putResponse = client.send(putRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(500, putResponse.statusCode());
    }
}
