package com.uma.example.springuma.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uma.example.springuma.integration.base.AbstractIntegration;
import com.uma.example.springuma.model.Medico;

public class MedicoControllerMockMvcIT extends AbstractIntegration {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Medico medico;

    @BeforeEach
    void setUp() {
        medico = new Medico();
        medico.setId(1L);
        medico.setDni("835");
        medico.setNombre("Miguel");
        medico.setEspecialidad("Ginecologia");
    }

    private void crearMedico(Medico medico) throws Exception {
        this.mockMvc.perform(post("/medico")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(medico)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Crear un médico correctamente")
    void crearMedicoYRecuperarlo() throws Exception{
        crearMedico(this.medico);
        this.mockMvc.perform(get("/medico/" + this.medico.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nombre").value(this.medico.getNombre()));
    }

    @Test
    @DisplayName("Borrar un médico correctamente")
    void borrarMedico() throws Exception{
        crearMedico(this.medico);
        this.mockMvc.perform(delete("/medico/" + this.medico.getId()))
        .andExpect(status().isOk());
        this.mockMvc.perform(get("/medico/" + this.medico.getId()))
        .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Modificar un médico correctamente")
    void modificarMedico() throws Exception{
        crearMedico(this.medico);
        this.medico.setNombre("Manolo");
        this.mockMvc.perform(put("/medico")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(this.medico)))
        .andExpect(status().isNoContent());
        this.mockMvc.perform(get("/medico/" + this.medico.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nombre").value(this.medico.getNombre()));
    }

    @Test
    @DisplayName("Obtener médico por DNI")
    void obtenerMedicoDNI() throws Exception{
        crearMedico(this.medico);
        this.mockMvc.perform(get("/medico/dni/" + this.medico.getDni()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nombre").value(this.medico.getNombre()));
    }

}
