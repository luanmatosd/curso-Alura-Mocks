package br.com.alura.leilao.service;

import br.com.alura.leilao.dao.LeilaoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Usuario;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FinalizarLeilaoServiceTest {

    private FinalizarLeilaoService finalizarLeilaoService;

    @Test
    void deveriaFinalizarUmLeilao(){
        LeilaoDao leilaoDaoMock = Mockito.mock(LeilaoDao.class); //Mock da classe LeilaoDao
        finalizarLeilaoService = new FinalizarLeilaoService(leilaoDaoMock); //Instanciando a classe finalizarLeilaoService

        List<Leilao> leilaoList = leiloes();

        Mockito.when(leilaoDaoMock.buscarLeiloesExpirados()).thenReturn(leilaoList);

        //Chamando o método finalizarLeiloesExpirados();
        //E dentro dessa chamada, ele chama o método buscarLeiloesExpirados() acima
        finalizarLeilaoService.finalizarLeiloesExpirados();

        Leilao leilao = leilaoList.get(0); //Pegando o leilão criado abaixo

        assertTrue(leilao.isFechado()); //Conferindo essa parte do método = "leilao.fechar()"
        assertEquals(new BigDecimal("900"), leilao.getLanceVencedor().getValor()); //Conferindo essa parte do método = "leilao.setLanceVencedor(maiorLance);"
        Mockito.verify(leilaoDaoMock).salvar(leilao); //Conferindo essa parte do método = "leiloes.salvar(leilao)"
    }

    //Esse método foi criado, pois, não quero que no teste seja devolvida uma lista vazia
    //na chamada do método "finalizarLeiloesExpirados()"
    //Método que cria uma lista de leilões em memória
    //Método criado para popular alguns dados para serem utilizados como exemplo no teste
    private List<Leilao> leiloes() {
        List<Leilao> lista = new ArrayList<>();

        Leilao leilao = new Leilao("Celular",
                new BigDecimal("500"),
                new Usuario("Fulano"));

        Lance primeiro = new Lance(new Usuario("Beltrano"),
                new BigDecimal("600"));
        Lance segundo = new Lance(new Usuario("Ciclano"),
                new BigDecimal("900"));

        leilao.propoe(primeiro);
        leilao.propoe(segundo);

        lista.add(leilao);

        return lista;
    }
}
