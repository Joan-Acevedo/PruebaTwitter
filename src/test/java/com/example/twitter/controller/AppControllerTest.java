package com.example.twitter.controller;

import com.example.twitter.model.Post;
import com.example.twitter.services.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AppControllerTest {

        private MockMvc mockMvc;

        @Mock
        private PostService postService;

        @InjectMocks
        private AppController appController;

        private ObjectMapper objectMapper;

        @BeforeEach
        void setUp() {
                MockitoAnnotations.openMocks(this);
                objectMapper = new ObjectMapper();
                objectMapper.findAndRegisterModules(); // Para manejar LocalDate

                mockMvc = MockMvcBuilders.standaloneSetup(appController)
                                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                                .build();
        }

        @Test
        void createPost() throws Exception {
                // Arrange
                Post newPost = new Post("user123", "Test post content");
                newPost.setId("post123");
                newPost.setCreationDate(LocalDate.now());

                // Configurar el mock para que devuelva el post esperado cuando se llame al
                // método con esos parámetros específicos
                when(postService.createPost(eq("user123"), eq("Test post content"), isNull()))
                                .thenReturn(newPost);

                // Crear el objeto de post para la solicitud
                Post requestPost = new Post();
                requestPost.setUserId("user123");
                requestPost.setContent("Test post content");

                // Act & Assert
                mockMvc.perform(post("/posts/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestPost)))
                                .andDo(print()) // Para debug - muestra la respuesta
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.id").value("post123"))
                                .andExpect(jsonPath("$.userId").value("user123"))
                                .andExpect(jsonPath("$.content").value("Test post content"));

                verify(postService, times(1)).createPost(eq("user123"), eq("Test post content"), isNull());
        }

        @Test
        void getReplies() throws Exception {
                // Arrange
                String postId = "post123";
                List<Post> replies = Arrays.asList(
                                new Post("user1", "Reply 1 content"),
                                new Post("user2", "Reply 2 content"));
                replies.get(0).setId("reply1");
                replies.get(1).setId("reply2");

                when(postService.getReplies(postId)).thenReturn(replies);

                // Act & Assert
                mockMvc.perform(get("/posts/{id}/replies", postId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").value("reply1"))
                                .andExpect(jsonPath("$[1].id").value("reply2"))
                                .andExpect(jsonPath("$[0].content").value("Reply 1 content"))
                                .andExpect(jsonPath("$[1].content").value("Reply 2 content"));

                verify(postService, times(1)).getReplies(postId);
        }

        @Test
        void deleteStream() throws Exception {
                // Arrange
                String postId = "post123";
                doNothing().when(postService).deletePost(postId);

                // Act & Assert
                mockMvc.perform(delete("/posts/{id}", postId))
                                .andExpect(status().isOk());

                verify(postService, times(1)).deletePost(postId);
        }
}
