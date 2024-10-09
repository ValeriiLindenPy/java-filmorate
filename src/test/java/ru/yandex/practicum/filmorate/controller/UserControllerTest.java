package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.utils.LocalDateTypeAdapter;
import ru.yandex.practicum.filmorate.model.utils.UsersListTypeToken;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTest {
	private final HttpClient client = HttpClient.newHttpClient();
	private final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter()).create();
	private final URI url = URI.create("http://localhost:8080/users");

	@Autowired
	private UserController userController;

	@BeforeEach
	void setup() {
		userController.users.clear();
	}

	@Test
	void userCreate201() throws Exception {
		User user = User.builder().login("login")
				.email("mail@mail.ru")
				.name("Nick Name")
				.birthday(LocalDate.of(1946, 8, 20))
				.build();

		String jsonBody = gson.toJson(user);

		HttpRequest request = HttpRequest.newBuilder().uri(url).header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		assertEquals(201, response.statusCode());
	}

	@Test
	void userCreateFailLogin() throws Exception {
		User user = User.builder().login("")
				.email("mail@mail.ru")
				.birthday(LocalDate.of(1946, 8, 20))
				.build();

		String jsonBody = gson.toJson(user);

		HttpRequest request = HttpRequest.newBuilder().uri(url).header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		assertEquals(400, response.statusCode());
	}

	@Test
	void userCreateFailBirthday() throws Exception {
		User user = User.builder().login("dolore")
				.email("mail@mail.ru")
				.birthday(LocalDate.of(2446, 8, 20))
				.build();

		String jsonBody = gson.toJson(user);

		HttpRequest request = HttpRequest.newBuilder().uri(url).header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		assertEquals(500, response.statusCode());
	}

	@Test
	void userCreateFailEmail() throws Exception {
		User user = User.builder().login("dolore")
				.email("mail.ru")
				.birthday(LocalDate.of(1996, 8, 20))
				.build();

		String jsonBody = gson.toJson(user);

		HttpRequest request = HttpRequest.newBuilder().uri(url).header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		assertEquals(400, response.statusCode());
	}

	@Test
	void userCreateWithEmptyName() throws Exception {
		User user = User.builder().login("dolore")
				.email("mail@mail.ru")
				.birthday(LocalDate.of(1996, 8, 20))
				.build();

		String jsonBody = gson.toJson(user);

		HttpRequest request = HttpRequest.newBuilder().uri(url).header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();

		client.send(request, HttpResponse.BodyHandlers.ofString());

		HttpRequest getRequest = HttpRequest.newBuilder().uri(url).GET().build();
		HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

		List<User> users = gson.fromJson(getResponse.body(), new UsersListTypeToken().getType());

		assertEquals(user.getLogin(), users.get(0).getName());
	}


	@Test
	void userGetAll() throws Exception {
		User user = User.builder().login("dolore")
				.email("mail@mail.ru")
				.birthday(LocalDate.of(1996, 8, 20))
				.build();

		String jsonBody = gson.toJson(user);

		HttpRequest request = HttpRequest.newBuilder().uri(url).header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();

		client.send(request, HttpResponse.BodyHandlers.ofString());

		HttpRequest getRequest = HttpRequest.newBuilder().uri(url).GET().build();
		HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

		List<User> films = gson.fromJson(getResponse.body(), new UsersListTypeToken().getType());

		assertEquals(user.getLogin(), films.get(0).getLogin());
	}


	@Test
	void userUpdate() throws Exception {
		User user = User.builder().login("dolore")
				.email("mail@mail.ru")
				.birthday(LocalDate.of(1996, 8, 20))
				.build();

		String jsonBody = gson.toJson(user);

		HttpRequest request = HttpRequest.newBuilder().uri(url).header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();

		client.send(request, HttpResponse.BodyHandlers.ofString());

		HttpRequest getRequest = HttpRequest.newBuilder().uri(url).GET().build();
		HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

		List<User> users = gson.fromJson(getResponse.body(), new UsersListTypeToken().getType());

		User oldUser = users.get(0);

		User updatedUser = User.builder().login("newlogin")
						.id(oldUser.getId()).birthday(oldUser.getBirthday())
						.name(oldUser.getName()).build();

		String jsonUpdatedUser = gson.toJson(updatedUser);

		HttpRequest putRequest = HttpRequest.newBuilder().uri(url).header("Content-Type", "application/json")
				.PUT(HttpRequest.BodyPublishers.ofString(jsonUpdatedUser)).build();

		HttpResponse<String> putResponse = client.send(putRequest, HttpResponse.BodyHandlers.ofString());

		assertEquals(200, putResponse.statusCode());
	}

	@Test
	void userUpdateFailUnknown() throws Exception {
		User user = User.builder().login("dolore")
				.email("mail@mail.ru")
				.birthday(LocalDate.of(1996, 8, 20))
				.build();

		String jsonBody = gson.toJson(user);

		HttpRequest request = HttpRequest.newBuilder().uri(url).header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();

		client.send(request, HttpResponse.BodyHandlers.ofString());

		HttpRequest getRequest = HttpRequest.newBuilder().uri(url).GET().build();
		HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

		List<User> users = gson.fromJson(getResponse.body(), new UsersListTypeToken().getType());

		User oldUser = users.get(0);

		User updatedUser = User.builder().login("newlogin")
				.id(20L).birthday(oldUser.getBirthday())
				.name(oldUser.getName()).build();

		String jsonUpdatedFilm = gson.toJson(updatedUser);

		HttpRequest putRequest = HttpRequest.newBuilder().uri(url).header("Content-Type", "application/json")
				.PUT(HttpRequest.BodyPublishers.ofString(jsonUpdatedFilm)).build();

		HttpResponse<String> putResponse = client.send(putRequest, HttpResponse.BodyHandlers.ofString());

		assertEquals(500, putResponse.statusCode());
	}
}
