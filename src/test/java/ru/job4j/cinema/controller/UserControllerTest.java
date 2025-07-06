package ru.job4j.cinema.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.UserService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {
    private UserService userService;
    private UserController userController;

    @BeforeEach
    public void initServices() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    void whenLoginUserReturnSuccessLogin() {
        var user = new User(1, "name", "email", "password");
        var model = new ConcurrentModel();
        var request = mock(HttpServletRequest.class);
        var session = mock(HttpSession.class);

        when(request.getSession()).thenReturn(session);
        when(userService.findByEmailAndPassword(user.getEmail(), user.getPassword()))
                .thenReturn(Optional.of(user));
        var view = userController.loginUser(user, model, request);

        assertThat(view).isEqualTo("redirect:/index");
        verify(session).setAttribute("user", user);
    }

    @Test
    void whenLoginUserReturnFailLogin() {
        var user = new User(1, "name", "email", "password");
        var model = new ConcurrentModel();
        var request = mock(HttpServletRequest.class);
        var session = mock(HttpSession.class);

        when(request.getSession()).thenReturn(session);
        when(userService.findByEmailAndPassword(user.getEmail(), user.getPassword()))
                .thenReturn(Optional.empty());

        var view = userController.loginUser(user, model, request);

        assertThat(view).isEqualTo("users/login");
        assertThat(model.getAttribute("error"))
                .isEqualTo("Почта или пароль введены неверно");
    }

    @Test
    void whenRegisterThenRegisterSuccess() {
        var user = new User(1, "name", "email", "password");
        var model = new ConcurrentModel();

        when(userService.save(user)).thenReturn(Optional.of(user));
        var view = userController.register(model, user);

        assertThat(view).isEqualTo("redirect:/index");
    }

    @Test
    void whenRegisterThenRegisterFail() {
        var user = new User(1, "name", "email", "password");
        var model = new ConcurrentModel();

        when(userService.save(user)).thenReturn(Optional.empty());
        var view = userController.register(model, user);

        assertThat(view).isEqualTo("errors/404");
        assertThat(model.getAttribute("message"))
                .isEqualTo("Пользователь с такой почтой уже существует");
    }
}