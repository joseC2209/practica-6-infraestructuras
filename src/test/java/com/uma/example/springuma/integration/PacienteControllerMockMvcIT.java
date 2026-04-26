package com.uma.example.springuma.integration;

import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uma.example.springuma.integration.base.AbstractIntegration;
import com.uma.example.springuma.model.Medico;
import com.uma.example.springuma.model.MedicoService;
import com.uma.example.springuma.model.Paciente;

public class PacienteControllerMockMvcIT extends AbstractIntegration {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MedicoService medicoService;

    Paciente paciente;
    Medico medico;

    @BeforeEach
    void setUp() {
        medico = new Medico();
        medico.setNombre("Miguel");
        medico.setId(1L);
        medico.setDni("835");
        medico.setEspecialidad("Ginecologo");

        paciente = new Paciente();
        paciente.setId(1L);
        paciente.setNombre("Maria");
        paciente.setDni("888");
        paciente.setEdad(20);
        paciente.setCita("Ginecologia");
        paciente.setMedico(this.medico);
    }
    private void crearMedico(Medico medico) throws Exception {
        this.mockMvc.perform(post("/medico")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(medico)))
                .andExpect(status().isCreated());
    }
    private void crearPaciente(Paciente paciente) throws Exception {
        mockMvc.perform(post("/paciente")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(paciente)))
                .andExpect(status().isCreated());
    }

    private void getPacienteById(Long id, Paciente expected) throws Exception {
        mockMvc.perform(get("/paciente/" + id))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").value(expected));
    }

    @Test
    @DisplayName("Crear paciente y recuperarlo por ID pasado por parametro")
    void savePaciente_RecuperaPacientePorId() throws Exception {
        crearMedico(medico);
        crearPaciente(paciente);

        this.mockMvc.perform(get("/paciente/" + this.paciente.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nombre").value(this.paciente.getNombre()));        
    }

    @Test
    @DisplayName("Asociar médico a paciente")
    void asociarMedicoPaciente() throws Exception{
        crearMedico(medico);
        crearPaciente(paciente);
        this.paciente.setMedico(this.medico);
        this.mockMvc.perform(get("/paciente/" + this.paciente.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.medico.id").value(this.medico.getId()));
    }

    @Test
    @DisplayName("Cambiar médico a paciente")
    void cambiarMedicoPaciente() throws Exception{
        crearMedico(medico);
        crearPaciente(paciente);
        Medico medico2 = new Medico();
        medico2.setNombre("Arnau");
        medico2.setId(2L);
        medico2.setDni("877");
        medico2.setEspecialidad("Fisiología");
        crearMedico(medico2);
        this.paciente.setMedico(this.medico);
        this.mockMvc.perform(get("/paciente/" + this.paciente.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.medico.id").value(this.medico.getId()));
        this.paciente.setMedico(medico2);
        this.mockMvc.perform(put("/paciente")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(this.paciente)))
        .andExpect(status().isNoContent());
        this.mockMvc.perform(get("/paciente/" + this.paciente.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.medico.id").value(medico2.getId()));
    }

}
