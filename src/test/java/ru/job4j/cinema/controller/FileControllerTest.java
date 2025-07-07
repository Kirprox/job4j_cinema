package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.job4j.cinema.dto.FileDto;
import ru.job4j.cinema.service.FileService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileControllerTest {

    private FileService fileService;
    private FileController fileController;

    @BeforeEach
    void init() {
        fileService = mock(FileService.class);
        fileController = new FileController(fileService);
    }

    @Test
    void whenFileFoundThenReturnContent() {
        byte[] fileContent = {1, 2, 3};
        var fileDto = new FileDto("example.txt", fileContent);

        when(fileService.getFileById(1)).thenReturn(Optional.of(fileDto));

        ResponseEntity<?> response = fileController.getById(1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(fileContent);
    }

    @Test
    void whenFileNotFoundThenReturnNotFound() {
        when(fileService.getFileById(99)).thenReturn(Optional.empty());

        ResponseEntity<?> response = fileController.getById(99);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}