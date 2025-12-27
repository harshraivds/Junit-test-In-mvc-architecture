package com.mvc_unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.web.servlet.MockMvc;

import com.mvc_unit.controller.UserController;
import com.mvc_unit.entity.User;
import com.mvc_unit.service.UserService;



@WebMvcTest(UserController.class)
public class UserControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean

	private UserService service; 
	
	@Test
    void getUser_found_returns200() throws Exception {
		when(service.getUserName(1)).thenReturn("Alice");

        mockMvc.perform(get("/users/1"))
               .andExpect(status().isOk())
               .andExpect(content().string("Alice"));
    }
	
	@Test
    void getUser_notFound_returns404() throws Exception {
        when(service.getUserName(99)).thenReturn("Unknown User");

        mockMvc.perform(get("/users/99"))
               .andExpect(status().isNotFound());
    }
	
	@Test
	void createUser_found_returns201() throws Exception{
		when(service.createUser(any(User.class))).thenReturn(new User(10, "Bob"));
		
		mockMvc.perform(post("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"id\":10,\"name\":\"Bob\"}")
			)
		.andExpect(status().isCreated())
    .andExpect(jsonPath("$.id").value(10))
    .andExpect(jsonPath("$.name").value("Bob"));
	}
	
	@Test
    void updateUser_found_returns200() throws Exception {
        when(service.updateUser(eq(5), any(User.class)))
                .thenReturn(Optional.of(new User(5, "Charlie")));

        mockMvc.perform(put("/users/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":5,\"name\":\"Charlie\"}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(5))
               .andExpect(jsonPath("$.name").value("Charlie"));
    }

    @Test
    void updateUser_notFound_returns404() throws Exception {
        when(service.updateUser(eq(6), any(User.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/users/6")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":6,\"name\":\"X\"}"))
               .andExpect(status().isNotFound());
    }
    
    @Test
    void deleteUser_returns204_whenDeleted() throws Exception {
        when(service.deleteUser(12)).thenReturn(true);

        mockMvc.perform(delete("/users/12"))
               .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_returns404_whenMissing() throws Exception {
        when(service.deleteUser(13)).thenReturn(false);

        mockMvc.perform(delete("/users/13"))
               .andExpect(status().isNotFound());
    }
    
    @Test
    void testGetAllUsers_ReturnsUsers() throws Exception {
        when(service.findAllUsers())
            .thenReturn(Arrays.asList(new User(1, "Alice"), new User(2, "Bob")));

        mockMvc.perform(get("/users"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].name").value("Alice"))
               .andExpect(jsonPath("$[1].name").value("Bob"));
    }

    @Test
    void testGetAllUsers_NoContent() throws Exception {
        when(service.findAllUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users"))
               .andExpect(status().isNoContent());
    }

}
