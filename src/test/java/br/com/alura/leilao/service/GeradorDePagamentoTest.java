package br.com.alura.leilao.service;

import br.com.alura.leilao.dao.PagamentoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Pagamento;
import br.com.alura.leilao.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class GeradorDePagamentoTest {

    private GeradorDePagamento geradorDePagamento;

    @Mock
    private PagamentoDao pagamentoDaoMock;
    @Mock
    private Clock clockMock;

    //Capturar um Objeto que foi passado pra um método de um Mock
    //Em nosso caso, seria o pagamento, pois, ele é gerado no método gerarPagamento da classe GeradorDePagamento
    //Resumindo, o Objeto está sendo criado na classe a ser testada, e não dentro da classe de teste
    @Captor
    private ArgumentCaptor<Pagamento> pagamentoCaptor;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        this.geradorDePagamento = new GeradorDePagamento(pagamentoDaoMock, clockMock);
    }

    @Test
    void deveriaGerarUmPagamentoParaVencedorDoLeilao(){

        Leilao leilao = leilao();
        Lance lanceVencedor = leilao.getLanceVencedor();

        LocalDate data = LocalDate.of(2024, 02, 27);

        Instant instant  = data.atStartOfDay(ZoneId.systemDefault()).toInstant();

        Mockito.when(clockMock.instant()).thenReturn(instant);
        Mockito.when(clockMock.getZone()).thenReturn(ZoneId.systemDefault());

        geradorDePagamento.gerarPagamento(lanceVencedor);

        Mockito.verify(pagamentoDaoMock).salvar(pagamentoCaptor.capture());

        Pagamento pagamento = pagamentoCaptor.getValue();

        assertEquals(LocalDate.now().plusDays(1), pagamento.getVencimento());
        assertEquals(lanceVencedor.getValor(), pagamento.getValor());
        assertFalse(pagamento.getPago());
        assertEquals(lanceVencedor.getUsuario(), pagamento.getUsuario());
        assertEquals(leilao, pagamento.getLeilao());
    }

    private Leilao leilao() {
        List<Leilao> lista = new ArrayList<>();

        Leilao leilao = new Leilao("Celular",
                new BigDecimal("500"),
                new Usuario("Fulano"));

        Lance primeiro = new Lance(new Usuario("Beltrano"),
                new BigDecimal("600"));

        leilao.propoe(primeiro);
        leilao.setLanceVencedor(primeiro); //Forçando o primeiro lance a ser o vencedor

        return leilao;
    }
}
