package br.com.artemis.plataformas;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class PlataformasApiTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    private String token(String email, String senha) throws Exception {
        String body = mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"senha\":\"" + senha + "\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        JsonNode n = json.readTree(body);
        return "Bearer " + n.get("token").asText();
    }

    @Test
    void semTokenRetorna401() throws Exception {
        mvc.perform(get("/categorias")).andExpect(status().isUnauthorized());
    }

    @Test
    void loginComSenhaErradaRetorna401() throws Exception {
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"admin@artemis.com\",\"senha\":\"errada\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void usuarioComumLeMasNaoEscreve() throws Exception {
        String comum = token("comum@artemis.com", "comum123");
        mvc.perform(get("/categorias").header("Authorization", comum)).andExpect(status().isOk());
        mvc.perform(post("/categorias").header("Authorization", comum)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"Tesoura\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void fluxoCompletoComoAdmin() throws Exception {
        String admin = token("admin@artemis.com", "admin123");

        String cat = mvc.perform(post("/categorias").header("Authorization", admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Tesoura elétrica\",\"descricao\":\"Elevação vertical\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long catId = json.readTree(cat).get("id").asLong();

        String corpo = "{\"codigo\":\"pta.1\",\"modelo\":\"Genie GS-1932\",\"alturaTrabalho\":7.8,"
                + "\"capacidadeKg\":230,\"status\":\"DISPONIVEL\",\"categoriaId\":" + catId + "}";
        String pl = mvc.perform(post("/plataformas").header("Authorization", admin)
                        .contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo", is("PTA.1")))
                .andReturn().getResponse().getContentAsString();
        long plId = json.readTree(pl).get("id").asLong();

        // código duplicado -> 409
        mvc.perform(post("/plataformas").header("Authorization", admin)
                        .contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isConflict());

        // validação -> 400
        mvc.perform(post("/plataformas").header("Authorization", admin)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"codigo\":\"\"}"))
                .andExpect(status().isBadRequest());

        // filtro no path
        mvc.perform(get("/categorias/" + catId + "/plataformas").header("Authorization", admin))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));

        // múltiplos parâmetros
        mvc.perform(get("/plataformas?modelo=genie&status=DISPONIVEL&alturaMin=5").header("Authorization", admin))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
        mvc.perform(get("/plataformas?modelo=genie&status=LOCADA").header("Authorization", admin))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));

        // não exclui categoria com plataformas -> 409
        mvc.perform(delete("/categorias/" + catId).header("Authorization", admin))
                .andExpect(status().isConflict());

        // atualização
        String atualizado = corpo.replace("DISPONIVEL", "LOCADA");
        mvc.perform(put("/plataformas/" + plId).header("Authorization", admin)
                        .contentType(MediaType.APPLICATION_JSON).content(atualizado))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status", is("LOCADA")));

        // locada não pode ser excluída -> 409
        mvc.perform(delete("/plataformas/" + plId).header("Authorization", admin))
                .andExpect(status().isConflict());

        // volta a disponível, exclui plataforma e depois a categoria
        mvc.perform(put("/plataformas/" + plId).header("Authorization", admin)
                .contentType(MediaType.APPLICATION_JSON).content(corpo)).andExpect(status().isOk());
        mvc.perform(delete("/plataformas/" + plId).header("Authorization", admin)).andExpect(status().isNoContent());
        mvc.perform(get("/plataformas/" + plId).header("Authorization", admin)).andExpect(status().isNotFound());
        mvc.perform(delete("/categorias/" + catId).header("Authorization", admin)).andExpect(status().isNoContent());
    }
}
