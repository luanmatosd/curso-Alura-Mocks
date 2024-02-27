import br.com.alura.leilao.dao.LeilaoDao;
import br.com.alura.leilao.model.Leilao;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.Assert.assertTrue;

//O método "buscarTodos() da classe "LeilaoDao" faz uma consulta no banco e devolve uma Lista dos Leilões
//Em nosso caso, a variável "daoList" vai receber uma Lista vazia do tipo "Leilao"
//A validação é feita se for verdade que essa Lista está vazia
//Ou seja, estamos simulando um comportamento de um método dessa Classe

public class HelloWorldMockito {

    @Test
    void hello(){
        LeilaoDao mock = Mockito.mock(LeilaoDao.class);
        List<Leilao> daoList = mock.buscarTodos();
        assertTrue(daoList.isEmpty());
    }
}
