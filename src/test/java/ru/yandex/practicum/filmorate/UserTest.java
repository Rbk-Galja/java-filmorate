package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.WhiteSpace;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserTest {

    private static final Validator validator;
    static UserController userController;
    User user;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @BeforeEach
    public void createUser() {
        userController = new UserController();
        user = User.builder()
                .name("Галина")
                .login("login")
                .email("rbkgalja@yandex.ru")
                .birthday(LocalDate.of(1986, 12, 11))
                .build();
    }

    @Test
    @DisplayName("Создание валидного объекта User")
    void createValidUser() {
        User expectedUser = userController.addUser(user);
        assertEquals(expectedUser.getId(), user.getId(), "Возвращает неверный объект");
        assertEquals(expectedUser, user, "Сохраняет неверный объект");
    }

    @Test
    @DisplayName("Обновление валидного User")
    void updateValidUser() {
        userController.addUser(user);
        User updateUsers = User.builder()
                .id(user.getId())
                .name("new name")
                .login("newlogin")
                .email("rbkgalja@yandex.ru")
                .birthday(LocalDate.of(1986, 10, 10))
                .build();

        User expectedUser = userController.updateUser(updateUsers);
        assertEquals(user.getName(), expectedUser.getName(), "Не обновляет поля");
        assertEquals(user.getId(), expectedUser.getId(), "Неверный id User");
    }

    @Test
    @DisplayName("Замена User name логином при указании пустого name")
    void nameReplacementLogin() {
        user.setName("");
        userController.addUser(user);
        assertEquals("login", user.getName(), "Не заменяет имя логином");
    }

    @Test
    @DisplayName("Проверка валидации пустого User login")
    void validateEmptyLoginUser() {
        user.setLogin("");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Исключения не найдены");
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals(NotBlank.class, violation.getConstraintDescriptor().getAnnotation().annotationType(),
                "Неправильный тип ошибки");
        assertEquals("login", violation.getPropertyPath().toString(), "Неверное имя поля");
    }

    @Test
    @DisplayName("Валидация User login с пробелами")
    void validateLoginWithWhiteSpace() {
        user.setLogin("lo gin");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Исключения не найдены");
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals(WhiteSpace.class, violation.getConstraintDescriptor().getAnnotation().annotationType(),
                "Неправильный тип ошибки");
        assertEquals("login", violation.getPropertyPath().toString(), "Неверное имя поля");
    }

    @Test
    @DisplayName("Валидация User email")
    void validateEmailUser() {
        user.setEmail("rbkgalja.ru");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Исключения не найдены");
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals(Email.class, violation.getConstraintDescriptor().getAnnotation().annotationType(),
                "Неправильный тип ошибки");
        assertEquals("email", violation.getPropertyPath().toString(), "Неверное имя поля");
    }

    @Test
    @DisplayName("Валидация User email с пробелами")
    void validateEmailUserWithWhiteSpace() {
        user.setEmail("rbk galja@yandex.ru");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Исключения не найдены");
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals(Email.class, violation.getConstraintDescriptor().getAnnotation().annotationType(),
                "Неправильный тип ошибки");
        assertEquals("email", violation.getPropertyPath().toString(), "Неверное имя поля");
    }

    @Test
    @DisplayName("Валидация даты рождения")
    void validateBirthdayUser() {
        user.setBirthday(LocalDate.of(2035, 10, 10));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Исключения не найдены");
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals(Past.class, violation.getConstraintDescriptor().getAnnotation().annotationType(),
                "Неправильный тип ошибки");
        assertEquals("birthday", violation.getPropertyPath().toString(), "Неверное имя поля");
    }

    @Test
    @DisplayName("Проверка исключения при обновлении User с несуществующим id")
    void exceptionIfIdUserNotExist() {
        userController.addUser(user);
        User userUpdate = User.builder()
                .id(105L)
                .name("Галина")
                .login("login")
                .email("rbkgalja@yandex.ru")
                .birthday(LocalDate.of(1986, 12, 11))
                .build();
        assertThrows(ValidationException.class, () -> userController.updateUser(userUpdate));
    }
}
