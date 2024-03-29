package br.com.alura.leilao.service;

import br.com.alura.leilao.dao.LeilaoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FinalizarLeilaoServiceTest {

    private FinalizarLeilaoService finalizarLeilaoService;

    @Mock //Mock da classe LeilaoDao
    private LeilaoDao leilaoDaoMock;

    @Mock //Mock da classe emailsDaoMock
    private  EnviadorDeEmails emailsDaoMock;

    @BeforeEach
    public void beforeEach() {
        MockitoAnnotations.initMocks(this); // Inicializa os mocks marcados com "@Mock"f
        this.finalizarLeilaoService = new FinalizarLeilaoService(leilaoDaoMock, emailsDaoMock); //Instanciando a classe finalizarLeilaoService
    }

    @Test
    void deveriaFinalizarUmLeilao(){
        List<Leilao> leilaoList = leiloes();

        //Quando o método buscarLeiloesExpirados() for chamado, ele precisa retornar um leilaoList
        Mockito.when(leilaoDaoMock.buscarLeiloesExpirados()).thenReturn(leilaoList);

        //Chamando o método finalizarLeiloesExpirados();
        //E dentro dessa chamada, ele chama o método buscarLeiloesExpirados() acima
        finalizarLeilaoService.finalizarLeiloesExpirados();

        Leilao leilao = leilaoList.get(0); //Pegando o leilão criado abaixo

        assertTrue(leilao.isFechado()); //Conferindo essa parte do método = "leilao.fechar()"
        assertEquals(new BigDecimal("900"), leilao.getLanceVencedor().getValor()); //Conferindo essa parte do método = "leilao.setLanceVencedor(maiorLance);"
        //Foi usado "Mockito.verify", pois, desejo verificar se o método do meu leilaoDaoMock funcionou!
        Mockito.verify(leilaoDaoMock).salvar(leilao); //Conferindo essa parte do método = "leiloes.salvar(leilao)"
    }

    @Test
    void deveriaEnviarEmailParaVencedorDoLeilao(){
        List<Leilao> leilaoList = leiloes();
        Mockito.when(leilaoDaoMock.buscarLeiloesExpirados()).thenReturn(leilaoList);
        finalizarLeilaoService.finalizarLeiloesExpirados();

        Leilao leilao = leilaoList.get(0);
        Lance lanceVencedor = leilao.getLanceVencedor();
        Mockito.verify(emailsDaoMock).enviarEmailVencedorLeilao(lanceVencedor);
    }

    @Test
    void naoDeveEnviarEmailCasoExistaFalhasNoSalvamentoDoLeilao(){
        List<Leilao> leilaoList = leiloes();
        Mockito.when(leilaoDaoMock.buscarLeiloesExpirados()).thenReturn(leilaoList);
        finalizarLeilaoService.finalizarLeiloesExpirados();

        //Forçando uma Exception no método de salvar leilões
        //Traduzindo: Mockito, quando eu quiser salvar qualquer coisa, lance uma Exception
        Mockito.when(leilaoDaoMock.salvar(Mockito.any())).thenThrow(RuntimeException.class);

        try {
            finalizarLeilaoService.finalizarLeiloesExpirados();
            Mockito.verifyZeroInteractions(emailsDaoMock);
        }catch (Exception e){}
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
