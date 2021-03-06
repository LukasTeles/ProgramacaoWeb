package testesDeUnidade;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.br.uepb.business.CaronaBusiness;
import com.br.uepb.business.SessaoBusiness;
import com.br.uepb.business.UsuarioBusiness;
import com.br.uepb.constants.MensagensErro;
import com.br.uepb.dao.impl.CaronaDAOImpl;
import com.br.uepb.dao.impl.InteresseEmCaronaDAOImpl;
import com.br.uepb.dao.impl.PontoDeEncontroDAOImpl;
import com.br.uepb.dao.impl.SessaoDAOImpl;
import com.br.uepb.dao.impl.SolicitacaoVagaDAOImpl;
import com.br.uepb.dao.impl.UsuarioDAOImpl;
import com.br.uepb.domain.CaronaDomain;
import com.br.uepb.domain.UsuarioDomain;
import com.br.uepb.exceptions.ProjetoCaronaException;

public class CaronaUnitTest {

	private UsuarioBusiness usuarioBusiness;
	private SessaoBusiness sessaoBusiness;
	private CaronaBusiness caronaBusiness;
	
	//parâmetros
	private String idSessao;	
	private String origem;
	private String destino;
	private String data;
	private String hora;
	private int vagas;
	private String idCarona;
		
	@Before
	public void iniciaBusiness(){
		usuarioBusiness = new UsuarioBusiness();
		sessaoBusiness = new SessaoBusiness();		
		caronaBusiness = new CaronaBusiness();		
		
		//limpa os dados antes de iniciar
		SolicitacaoVagaDAOImpl.getInstance().apagaSolicitacoes();
		PontoDeEncontroDAOImpl.getInstance().apagaPontosEncontro();
		CaronaDAOImpl.getInstance().apagaCaronas();		
		UsuarioDAOImpl.getInstance().apagaUsuarios();
		SessaoDAOImpl.getInstance().apagaSessoes();
		InteresseEmCaronaDAOImpl.getInstance().apagaInteresses();
		
		//define parâmetros default
		idSessao = "Mark";
		origem = "Campina Grande";
		destino = "João Pessoa";
		data = "23/06/2013";
		hora="16:00";
		vagas = 4;
		
		//inicializa a sessao e o usuario
		try {
			usuarioBusiness.criarUsuario(idSessao, idSessao, "Mark", "Rua", "mark@gmail.com");
			sessaoBusiness.abrirSessao(idSessao, idSessao);
		} catch (Exception e) {
			fail();
		}
		
	}
	
	@Test
	public void testCadastrarCarona() throws Exception{
		//Verificar qualquer falha no fluxo normal
		try {			
			idCarona = caronaBusiness.cadastrarCarona(idSessao, origem, destino, data, hora, vagas);				
			CaronaDomain carona1 = caronaBusiness.getCarona(idCarona);			
			
			//verifica se armazenou os valores realmente corretos
			Assert.assertEquals(idCarona, carona1.getId());
			Assert.assertEquals(idSessao, carona1.getIdSessao());
			Assert.assertEquals(origem, carona1.getOrigem());
			Assert.assertEquals(destino, carona1.getDestino());
			Assert.assertEquals(data, carona1.getData());
			Assert.assertEquals(hora, carona1.getHora());
			Assert.assertEquals(vagas, carona1.getVagas());
			
		} catch (Exception e) {
			fail();
		}
		
		try{
			caronaBusiness.cadastrarCarona("", origem, destino, data, hora, vagas);
		}catch(ProjetoCaronaException e){
			assertEquals(MensagensErro.SESSAO_INVALIDA, e.getMessage());
		}catch (Exception e) {
			fail();
		}
		
		try{
			caronaBusiness.cadastrarCarona(null, origem, destino, data, hora, vagas);
		}catch(ProjetoCaronaException e){
			assertEquals(MensagensErro.SESSAO_INVALIDA, e.getMessage());
		}catch (Exception e) {
			fail();
		}
		
		try{
			caronaBusiness.cadastrarCarona(idSessao, "", destino, data, hora, vagas);
		}catch(ProjetoCaronaException e){
			assertEquals(MensagensErro.ORIGEM_INVALIDA, e.getMessage());
		}catch (Exception e) {
			fail();
		}
		
		try{
			caronaBusiness.cadastrarCarona(idSessao, null, destino, data, hora, vagas);
		}catch(ProjetoCaronaException e){
			assertEquals(MensagensErro.ORIGEM_INVALIDA, e.getMessage());
		}catch (Exception e) {
			fail();
		}
		
		try{
			caronaBusiness.cadastrarCarona(idSessao, origem, "", data, hora, vagas);
		}catch(ProjetoCaronaException e){
			assertEquals(MensagensErro.DESTINO_INVALIDO, e.getMessage());
		}catch (Exception e) {
			fail();
		}
		
		try{
			caronaBusiness.cadastrarCarona(idSessao, origem, null, data, hora, vagas);
		}catch(ProjetoCaronaException e){
			assertEquals(MensagensErro.DESTINO_INVALIDO, e.getMessage());
		}catch (Exception e) {
			fail();
		}
		
		try{
			caronaBusiness.cadastrarCaronaMunicipal(idSessao, origem, destino, "", data, hora, vagas);
		}catch(ProjetoCaronaException e){
			assertEquals(MensagensErro.CIDADE_INEXISTENTE, e.getMessage());
		}catch (Exception e) {
			fail();
		}
		
		try{
			caronaBusiness.cadastrarCaronaMunicipal(idSessao, origem, destino, null, data, hora, vagas);
		}catch(ProjetoCaronaException e){
			assertEquals(MensagensErro.CIDADE_INEXISTENTE, e.getMessage());
		}catch (Exception e) {
			fail();
		}
		
		try{
			caronaBusiness.cadastrarCaronaRelampago(idSessao, origem, destino, "09/12/2015", "01/01/2016", 0, hora);
		}catch(ProjetoCaronaException e){
			assertEquals(MensagensErro.MINIMO_CARONEIROS_INVALIDO, e.getMessage());
		}catch (Exception e) {
			fail();
		}
		
	}
	
	@Test
	public void testeCaronaSessaoInvalida() throws Exception{				
		//cadastrar carona com idSessao vazia
		try {
			caronaBusiness.cadastrarCarona("", origem, destino, data, hora, vagas);			
		} catch (ProjetoCaronaException projetoCaronaErro) {
			assertEquals(MensagensErro.SESSAO_INVALIDA, projetoCaronaErro.getMessage());
		} catch (Exception e) {
			fail();
		}		
		
		//cadastrar carona com idSessao null
		try {
			caronaBusiness.cadastrarCarona(null, origem, destino, data, hora, vagas);	
		} catch (ProjetoCaronaException projetoCaronaErro) {
			assertEquals(MensagensErro.SESSAO_INVALIDA, projetoCaronaErro.getMessage());
		} catch (Exception e) {
			fail();
		}	
		
		//cadastrar carona com idSessao nas cadastrada
		try {
			caronaBusiness.cadastrarCarona("sessao1", origem, destino, data, hora, vagas);			
		} catch (ProjetoCaronaException projetoCaronaErro) {
			assertEquals(MensagensErro.SESSAO_INEXISTENTE, projetoCaronaErro.getMessage());
		} catch (Exception e) {
			fail();
		}	
	}
		
	@Test
	public void testeCaronaOrigemInvalida() throws Exception{				
		//cadastrar carona com origem vazia
		try {
			caronaBusiness.cadastrarCarona(idSessao, "", destino, data, hora, vagas);
		} catch (ProjetoCaronaException projetoCaronaErro) {
			assertEquals(MensagensErro.ORIGEM_INVALIDA, projetoCaronaErro.getMessage());
		} catch (Exception e) {
			fail();
		}
		
		//cadastrar carona com origem null
		try {
			caronaBusiness.cadastrarCarona(idSessao, null, destino, data, hora, vagas);
		} catch (ProjetoCaronaException projetoCaronaErro) {
			assertEquals(MensagensErro.ORIGEM_INVALIDA, projetoCaronaErro.getMessage());
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testeCaronaDestinoInvalido() throws Exception{		
		//cadastrar carona com destino vazio
		try {
			caronaBusiness.cadastrarCarona(idSessao, origem, "", data, hora, vagas);
		} catch (ProjetoCaronaException projetoCaronaErro) {
			assertEquals(MensagensErro.DESTINO_INVALIDO, projetoCaronaErro.getMessage());
		} catch (Exception e) {
			fail();
		}
		
		//cadastrar carona com destino null
		try {
			caronaBusiness.cadastrarCarona(idSessao, origem, "", data, hora, vagas);
			caronaBusiness.cadastrarCarona(idSessao, origem, null, data, hora, vagas);
		} catch (ProjetoCaronaException projetoCaronaErro) {
			assertEquals(MensagensErro.DESTINO_INVALIDO, projetoCaronaErro.getMessage());
		} catch (Exception e) {
			fail();
		}		
	}
	
	@Test
	public void testeCaronaDataInvalida() throws Exception{		
		//cadastrar carona com data vazia
		try {
			caronaBusiness.cadastrarCarona(idSessao, origem, destino, "", hora, vagas);
		} catch (ProjetoCaronaException projetoCaronaErro) {
			assertEquals(MensagensErro.DATA_INVALIDA, projetoCaronaErro.getMessage());
		} catch (Exception e) {
			fail();
		}
		
		//cadastrar carona com data null
		try {
			caronaBusiness.cadastrarCarona(idSessao, origem, destino, null, hora, vagas);
		} catch (ProjetoCaronaException projetoCaronaErro) {
			assertEquals(MensagensErro.DATA_INVALIDA, projetoCaronaErro.getMessage());
		} catch (Exception e) {
			fail();
		}
		
		//cadastrar carona com mes incorreto
		try {
			caronaBusiness.cadastrarCarona(idSessao, origem, destino, "04/13/2015", hora, vagas);
		} catch (ProjetoCaronaException projetoCaronaErro) {
			assertEquals(MensagensErro.DATA_INVALIDA, projetoCaronaErro.getMessage());
		} catch (Exception e) {
			fail();
		}
		
		//cadastrar carona com dia incorreto
		try {
			caronaBusiness.cadastrarCarona(idSessao, origem, destino, "31/02/2015", hora, vagas);
		} catch (ProjetoCaronaException projetoCaronaErro) {
			assertEquals(MensagensErro.DATA_INVALIDA, projetoCaronaErro.getMessage());
		} catch (Exception e) {
			fail();
		}		
		
		//cadastrar carona com dia incorreto
		try {
			caronaBusiness.cadastrarCarona(idSessao, origem, destino, "35/03/2015", hora, vagas);
		} catch (ProjetoCaronaException projetoCaronaErro) {
			assertEquals(MensagensErro.DATA_INVALIDA, projetoCaronaErro.getMessage());
		} catch (Exception e) {
			fail();
		}		
	}
			
	@Test
	public void testCaronaHoraInvalida() throws Exception{
		//cadastrar carona com hora vazia
		try {
			caronaBusiness.cadastrarCarona(idSessao, origem, destino, data, "", vagas);				
		} catch (ProjetoCaronaException projetoCaronaErro) {
			assertEquals(MensagensErro.HORA_INVALIDA, projetoCaronaErro.getMessage());
		} catch (Exception e) {
			fail();
		}
		
		//cadastrar carona com hora null
		try {
			caronaBusiness.cadastrarCarona(idSessao, origem, destino, data, null, vagas);		
		} catch (ProjetoCaronaException projetoCaronaErro) {
			assertEquals(MensagensErro.HORA_INVALIDA, projetoCaronaErro.getMessage());
		} catch (Exception e) {
			fail();
		}		
		
		//cadastrar carona com hora incorreta
		try {
			caronaBusiness.cadastrarCarona(idSessao, origem, destino, data, "8hs", vagas);	
		} catch (ProjetoCaronaException projetoCaronaErro) {
			assertEquals(MensagensErro.HORA_INVALIDA, projetoCaronaErro.getMessage());
		} catch (Exception e) {
			fail();
		}	
		
		//cadastrar carona com hora incorreta
		try {
			caronaBusiness.cadastrarCarona(idSessao, origem, destino, data, "oito", vagas);			
		} catch (ProjetoCaronaException projetoCaronaErro) {
			assertEquals(MensagensErro.HORA_INVALIDA, projetoCaronaErro.getMessage());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testGetAtributoCarona(){
		//cadastrar carona 
		try {
			idCarona = caronaBusiness.cadastrarCarona(idSessao, origem, destino, data, hora, vagas);				
		} catch (Exception e) {
			fail();
		}
		
		//verifica se a funcao getAtributoCarona retorna os Valores Corretos
		try {		
			Assert.assertEquals(origem, caronaBusiness.getAtributoCarona(idCarona, "origem"));
			Assert.assertEquals(destino, caronaBusiness.getAtributoCarona(idCarona, "destino"));
			Assert.assertEquals(data, caronaBusiness.getAtributoCarona(idCarona, "data"));
			Assert.assertEquals(vagas+"", caronaBusiness.getAtributoCarona(idCarona, "vagas"));
		} catch (Exception e) {			
			fail();
		}
		
		//atributo vazio
		try {		
			caronaBusiness.getAtributoCarona(idCarona, "");
		} catch (ProjetoCaronaException projetoCaronaErro) {
			assertEquals(MensagensErro.ATRIBUTO_INVALIDO, projetoCaronaErro.getMessage());
		} catch (Exception e) {	
			fail();
		}
		
		//atributo null
		try {		
			caronaBusiness.getAtributoCarona(idCarona, null);
		} catch (ProjetoCaronaException projetoCaronaErro) {
			assertEquals(MensagensErro.ATRIBUTO_INVALIDO, projetoCaronaErro.getMessage());
		} catch (Exception e) {	
			fail();
		}
		
		//carona vazia
		try {		
			caronaBusiness.getAtributoCarona("", "origem");
		} catch (ProjetoCaronaException projetoCaronaErro) {
			assertEquals(MensagensErro.IDENTIFICADOR_INVALIDO, projetoCaronaErro.getMessage());
		} catch (Exception e) {	
			fail();
		}
		
		//carona null
		try {		
			caronaBusiness.getAtributoCarona(null, "destino");
		} catch (ProjetoCaronaException projetoCaronaErro) {
			assertEquals(MensagensErro.IDENTIFICADOR_INVALIDO, projetoCaronaErro.getMessage());
		} catch (Exception e) {	
			fail();
		}
		
		//carona que nao existe
		try {		
			caronaBusiness.getAtributoCarona("carona1", "origem");
		} catch (ProjetoCaronaException projetoCaronaErro) {
			assertEquals(MensagensErro.ITEM_INEXISTENTE, projetoCaronaErro.getMessage());
		} catch (Exception e) {	
			fail();
		}
		
		//atributo que nao existe
		try {		
			caronaBusiness.getAtributoCarona(idCarona, "atributo");
		} catch (ProjetoCaronaException projetoCaronaErro) {
			assertEquals(MensagensErro.ATRIBUTO_INEXISTENTE, projetoCaronaErro.getMessage());
		} catch (Exception e) {	
			fail();
		}	
	}
	
	@Test
	public void testGetTrajeto(){		
		try {
			idCarona = caronaBusiness.cadastrarCarona(idSessao, origem, destino, data, hora, vagas);
			Assert.assertEquals(origem+" - "+destino, caronaBusiness.getTrajeto(idCarona));
		} catch (Exception e) {			
			fail();
		}
		
		//carona null
		try {		
			caronaBusiness.getTrajeto(null);
		} catch (ProjetoCaronaException projetoCaronaErro) {
			assertEquals(MensagensErro.TRAJETO_INVALIDO, projetoCaronaErro.getMessage());
		} catch (Exception e) {	
			fail();
		}
		
		//carona vazia
		try {		
			caronaBusiness.getTrajeto("");
		} catch (ProjetoCaronaException projetoCaronaErro) {
			assertEquals(MensagensErro.TRAJETO_INEXISTENTE, projetoCaronaErro.getMessage());
		} catch (Exception e) {	
			fail();
		}		
	
		//carona inexistente
		try {		
			caronaBusiness.getTrajeto("Carona1");
		} catch (ProjetoCaronaException projetoCaronaErro) {
			assertEquals(MensagensErro.TRAJETO_INEXISTENTE, projetoCaronaErro.getMessage());
		} catch (Exception e) {	
			fail();
		}		
	}

	@Test
	public void testGetCarona(){				
		//carona null			
		try {		
			caronaBusiness.getCarona(null);
		} catch (ProjetoCaronaException projetoCaronaErro) {
			assertEquals(MensagensErro.CARONA_INVALIDA, projetoCaronaErro.getMessage());
		} catch (Exception e) {	
			fail();
		}
		
		//carona vazia			
		try {		
			caronaBusiness.getCarona("");
		} catch (ProjetoCaronaException projetoCaronaErro) {
			assertEquals(MensagensErro.CARONA_INEXISTENTE, projetoCaronaErro.getMessage());
		} catch (Exception e) {	
			fail();
		}
		
		//carona existente		
		try {
			idCarona = caronaBusiness.cadastrarCarona(idSessao, origem, destino, data, hora, vagas);
			Assert.assertEquals(origem+" - "+destino, caronaBusiness.getTrajeto(idCarona));
		} catch (Exception e) {			
			fail();
		}
	}
	
	@Test
	public void testGetCaronaUsuario(){		
		try {
			idCarona = caronaBusiness.cadastrarCarona(idSessao, origem, destino, data, hora, vagas);
			Assert.assertEquals(origem+" - "+destino, caronaBusiness.getTrajeto(idCarona));
		} catch (Exception e) {			
			fail();
		}
		
		//retorna a carona 1
		try {		
			caronaBusiness.getCaronaUsuario("Mark", 1);			
		} catch (Exception e) {	
			fail();
		}
	
		//informar indice invalido
		try {		
			caronaBusiness.getCaronaUsuario("Mark", 2);
		} catch (ProjetoCaronaException projetoCaronaErro) {
			assertEquals(MensagensErro.INDICE_INVALIDO, projetoCaronaErro.getMessage());
		} catch (Exception e) {	
			fail();
		}
		
		//informar sessao vazia
		try {		
			caronaBusiness.getCaronaUsuario("", 1);
		} catch (ProjetoCaronaException projetoCaronaErro) {
			assertEquals(MensagensErro.SESSAO_INVALIDA, projetoCaronaErro.getMessage());
		} catch (Exception e) {	
			fail();
		}

		//informar sessao null
		try {		
			caronaBusiness.getCaronaUsuario(null, 1);
		} catch (ProjetoCaronaException projetoCaronaErro) {
			assertEquals(MensagensErro.SESSAO_INVALIDA, projetoCaronaErro.getMessage());
		} catch (Exception e) {	
			fail();
		}		
		
		//informar sessao que nao existe
		try {		
			caronaBusiness.getCaronaUsuario("Lukas", 1);
			caronaBusiness.getCaronaUsuario(null, 1);
		} catch (ProjetoCaronaException projetoCaronaErro) {
			assertEquals(MensagensErro.SESSAO_INEXISTENTE, projetoCaronaErro.getMessage());
		} catch (Exception e) {	
			fail();
		}
	}

	@Test
	public void testLocalizarCarona(){
		String idCarona2 = "";
		String idCarona3 = "";
		String caronaID4 = "";
		String caronaID5 = "";
		String caronaID6 = "";
		try {
			idCarona = caronaBusiness.cadastrarCarona(idSessao, "Campina Grande", "Joao Pessoa", "12/05/2015", "10:00", 3);
			idCarona2 = caronaBusiness.cadastrarCarona(idSessao, "Campina Grande", "Araruna", "12/05/2015", "10:00", 3);
			idCarona3 = caronaBusiness.cadastrarCarona(idSessao, "Campina Grande", "Joao Pessoa", "12/05/2015", "10:00", 3);
			caronaID4 = caronaBusiness.cadastrarCarona(idSessao, "João Pessoa", "Araruna", "12/05/2015", "10:00", 3);
			caronaID5 = caronaBusiness.cadastrarCarona(idSessao, "Araruna", "Alagoa Nova", "12/05/2015", "10:00", 3);
			caronaID6 = caronaBusiness.cadastrarCarona(idSessao, "Araruna", "Joao Pessoa", "12/05/2015", "10:00", 3);
		} catch (Exception e) {			
			fail();
		}
		
		//localiza todas as caronas
		try {
			ArrayList<CaronaDomain> listTodascaronas = new ArrayList<CaronaDomain>();
			listTodascaronas.add(caronaBusiness.getCarona(idCarona));
			listTodascaronas.add(caronaBusiness.getCarona(idCarona2));
			listTodascaronas.add(caronaBusiness.getCarona(idCarona3));
			listTodascaronas.add(caronaBusiness.getCarona(caronaID4));
			listTodascaronas.add(caronaBusiness.getCarona(caronaID5));
			listTodascaronas.add(caronaBusiness.getCarona(caronaID6));
			//Assert.assertEquals(listTodascaronas, caronaBusiness.localizarCarona("Mark", "", ""));
			comparaListas(listTodascaronas, caronaBusiness.localizarCarona("Mark", "", ""));
		} catch (Exception e) {	
			fail();
		}
	
		//localiza todas as caronas de origem=Campina Grande
		try {
			ArrayList<CaronaDomain> listTodascaronas = new ArrayList<CaronaDomain>();
			listTodascaronas.add(caronaBusiness.getCarona(idCarona));
			listTodascaronas.add(caronaBusiness.getCarona(idCarona2));
			listTodascaronas.add(caronaBusiness.getCarona(idCarona3));
			//Assert.assertEquals(listTodascaronas, caronaBusiness.localizarCarona("Mark", "Campina Grande", ""));
			comparaListas(listTodascaronas, caronaBusiness.localizarCarona("Mark", "Campina Grande", ""));
		} catch (Exception e) {	
			fail();
		}
		
		//localiza todas as caronas de destino=Araruna
		try {
			ArrayList<CaronaDomain> listTodascaronas = new ArrayList<CaronaDomain>();
			listTodascaronas.add(caronaBusiness.getCarona(idCarona2));
			listTodascaronas.add(caronaBusiness.getCarona(caronaID4));
			//Assert.assertEquals(listTodascaronas, caronaBusiness.localizarCarona("Mark", "", "Araruna"));
			comparaListas(listTodascaronas, caronaBusiness.localizarCarona("Mark", "", "Araruna"));
		} catch (Exception e) {	
			fail();
		}
		
		//localiza todas as caronas de Campina Grande a João Pessoa
		try {
			ArrayList<CaronaDomain> listTodascaronas = new ArrayList<CaronaDomain>();
			listTodascaronas.add(caronaBusiness.getCarona(idCarona));
			listTodascaronas.add(caronaBusiness.getCarona(idCarona3));
			//Assert.assertEquals(listTodascaronas, caronaBusiness.localizarCarona("Mark", "Campina Grande", "Joao Pessoa"));
			comparaListas(listTodascaronas, caronaBusiness.localizarCarona("Mark", "Campina Grande", "Joao Pessoa"));
		} catch (Exception e) {	
			fail();
		}
		
		//informar origem nao existente
		try {
			ArrayList<CaronaDomain> listTodascaronas = new ArrayList<CaronaDomain>();
			//Assert.assertEquals(listTodascaronas, caronaBusiness.localizarCarona("Mark", "Campina", "Joao Pessoa"));
			comparaListas(listTodascaronas, caronaBusiness.localizarCarona("Mark", "Campina", "Joao Pessoa"));
		} catch (Exception e) {	
			fail();
		}
		
		//informar login não cadastrado
		try {
			caronaBusiness.localizarCarona("Mark", "", "Joao Pessoa");
		} catch (ProjetoCaronaException projetoCaronaErro) {
			assertEquals(MensagensErro.SESSAO_INEXISTENTE, projetoCaronaErro.getMessage());
		}  catch (Exception e) {	
			fail();
		}
		
		//informar login não invalido 
		try {
			caronaBusiness.localizarCarona("", "", "Joao Pessoa");
			caronaBusiness.localizarCarona(null, "", "Joao Pessoa");
		} catch (ProjetoCaronaException projetoCaronaErro) {
			assertEquals(MensagensErro.SESSAO_INVALIDA, projetoCaronaErro.getMessage());
		}  catch (Exception e) {	
			fail();
		}
		
		//informar login null
		try {
			caronaBusiness.localizarCarona(null, "", "Joao Pessoa");
		} catch (ProjetoCaronaException projetoCaronaErro) {
			assertEquals(MensagensErro.SESSAO_INVALIDA, projetoCaronaErro.getMessage());
		}  catch (Exception e) {	
			fail();
		}
		
		//informar origem invalida
		try {
			caronaBusiness.localizarCarona("Mark", "?", "Joao Pessoa");
		} catch (ProjetoCaronaException projetoCaronaErro) {
			assertEquals(MensagensErro.ORIGEM_INVALIDA, projetoCaronaErro.getMessage());
		}  catch (Exception e) {	
			fail();
		}
		
		//informar destino invalido
		try {
			caronaBusiness.localizarCarona("Mark", "Campina Grande", "()");
		} catch (ProjetoCaronaException projetoCaronaErro) {
			assertEquals(MensagensErro.DESTINO_INVALIDO, projetoCaronaErro.getMessage());
		}  catch (Exception e) {	
			fail();
		}
		
		try{
			String[] usuarios = {};
			assertEquals(usuarios.length, caronaBusiness.getUsuariosByCarona(idCarona).length);
		}catch(Exception e){
			fail();
		}
		
	}
	
	@Test
	public void testeCaronaMunicipal(){
		//cadastra carona municipal
		String idCarona2 = "";
		String idCarona3 = "";
		String idCarona4 = "";
		try{
			idCarona = caronaBusiness.cadastrarCaronaMunicipal(idSessao, "Açude Velho", "Partage", "Campina Grande", "05/06/15", "18:00", 2);
			idCarona2 = caronaBusiness.cadastrarCaronaMunicipal(idSessao, "Açude Velho", "Partage", "Campina Grande", "06/06/15", "19:00", 3);
			idCarona3 = caronaBusiness.cadastrarCaronaMunicipal(idSessao, "Parque do povo", "Hiper Bompreço", "Campina Grande", "05/06/15", "18:00", 2);
			idCarona4 = caronaBusiness.cadastrarCarona(idSessao, "Campina Grande", "João Pessoa", "05/06/15", "16:00", 2);
			
		}catch(Exception e){
			fail();
		}
		
		//get atributo carona
		try {
			assertEquals("true", caronaBusiness.getAtributoCarona(idCarona, "ehMunicipal"));
			assertEquals("false", caronaBusiness.getAtributoCarona(idCarona4, "ehMunicipal"));
		}catch (Exception e) {
			fail();
		}
		
		//localizarCaronaMunicipal
		try{
			List<CaronaDomain> listaCaronaMunicipais = new ArrayList<CaronaDomain>();
			listaCaronaMunicipais.add(caronaBusiness.getCarona(idCarona));
			listaCaronaMunicipais.add(caronaBusiness.getCarona(idCarona2));
			comparaListas(listaCaronaMunicipais, caronaBusiness.localizarCaronaMunicipal(idSessao, "Campina Grande", "Açude Velho", "Partage"));
			
			listaCaronaMunicipais.add(caronaBusiness.getCarona(idCarona3));
			comparaListas(listaCaronaMunicipais, caronaBusiness.localizarCaronaMunicipal(idSessao, "Campina Grande", "", ""));
		}catch(Exception e){
			fail();
		}
		
		//
		//excecoes
		//
		
		//localizar carona municipal
		try{
			caronaBusiness.localizarCaronaMunicipal(idSessao, "Campina Grande", "!", "Partage");
		}catch (ProjetoCaronaException e){
			assertEquals(MensagensErro.ORIGEM_INVALIDA, e.getMessage());
		}catch (Exception e) {
			fail();
		}
		
		try{
			caronaBusiness.localizarCaronaMunicipal(idSessao, "Campina Grande", "Açude Velho", "!");
		}catch (ProjetoCaronaException e){
			assertEquals(MensagensErro.DESTINO_INVALIDO, e.getMessage());
		}catch (Exception e) {
			fail();
		}
		
		try{
			caronaBusiness.localizarCaronaMunicipal(idSessao, "Camalaú", "", "Partage");
		}catch (ProjetoCaronaException e){
			assertEquals(MensagensErro.CIDADE_INEXISTENTE, e.getMessage());
		}catch (Exception e) {
			fail();
		}
		
	}
	
	@Test
	public void testeCaronaRelampago(){
		//carona relâmpago
		String idcarona2, idcarona3;
		try{
			idCarona = caronaBusiness.cadastrarCaronaRelampago(idSessao, "São Paulo", "Rio", "06/06/15", "09/06/15", 3, "09:00");
			assertEquals("São Paulo", caronaBusiness.getAtributoCaronaRelampago(idCarona, "origem"));
			assertEquals("Rio", caronaBusiness.getAtributoCaronaRelampago(idCarona, "destino"));
			assertEquals("06/06/15", caronaBusiness.getAtributoCaronaRelampago(idCarona, "dataIda"));
			assertEquals("09/06/15", caronaBusiness.getAtributoCaronaRelampago(idCarona, "dataVolta"));
			assertEquals("3", caronaBusiness.getAtributoCaronaRelampago(idCarona, "minimoCaroneiros"));
			assertEquals("false", caronaBusiness.getAtributoCaronaRelampago(idCarona, "expired"));
		}catch(Exception e){
			fail();
		}
		//excecoes getAtributoCarona()
		try{
			caronaBusiness.getAtributoCaronaRelampago(idCarona, "");
		}catch(ProjetoCaronaException e){
			assertEquals(MensagensErro.ATRIBUTO_INVALIDO, e.getMessage());
		}catch (Exception e) {
			fail();
		}
		
		try{
			caronaBusiness.getAtributoCaronaRelampago(idCarona, null);
		}catch(ProjetoCaronaException e){
			assertEquals(MensagensErro.ATRIBUTO_INVALIDO, e.getMessage());
		}catch (Exception e) {
			fail();
		}
	
		try{
			caronaBusiness.getAtributoCaronaRelampago("", "origem");
		}catch(ProjetoCaronaException e){
			assertEquals(MensagensErro.IDENTIFICADOR_INVALIDO, e.getMessage());
		}catch (Exception e) {
			fail();
		}
				
		try{
			caronaBusiness.getAtributoCaronaRelampago(null, "origem");
		}catch(ProjetoCaronaException e){
			assertEquals(MensagensErro.IDENTIFICADOR_INVALIDO, e.getMessage());
		}catch (Exception e) {
			fail();
		}
		
		try{
			caronaBusiness.getAtributoCaronaRelampago("HakunaMatata", "origem");
		}catch(ProjetoCaronaException e){
			assertEquals(MensagensErro.ITEM_INEXISTENTE, e.getMessage());
		}catch (Exception e) {
			fail();
		}
		
		try{
			caronaBusiness.getAtributoCaronaRelampago(idCarona, "HakunaMatata");
		}catch(ProjetoCaronaException e){
			assertEquals(MensagensErro.ATRIBUTO_INEXISTENTE, e.getMessage());
		}catch (Exception e) {
			fail();
		}
		
		try{
			assertEquals(3, caronaBusiness.getMinimoCaroneiros(idCarona));
		}catch(Exception e){
			fail();
		}
		
		try{
			idcarona2 = caronaBusiness.cadastrarCaronaRelampago(idSessao, "Aqui", "La", ""+DateTime.now().toString("dd/MM/yyyy"), "01/01/2016", 5, "00:00");
			assertEquals(idcarona2, caronaBusiness.setCaronaRelampagoExpired(idcarona2));
		}catch(Exception e){
			fail();
		}
		
		try{
			List<UsuarioDomain> listaUsuarios = new ArrayList<UsuarioDomain>();
			idcarona3 = caronaBusiness.cadastrarCaronaRelampago(idSessao, "Outro", "Mais um", "09/12/2015", "01/01/2016", 5, "00:00");
			assertEquals(false, caronaBusiness.isCaronaPreferencial(idcarona3));
			caronaBusiness.definirCaronaPreferencial(idcarona3);
			assertEquals(true, caronaBusiness.isCaronaPreferencial(idcarona3));
			assertEquals(listaUsuarios.size(), caronaBusiness.getUsuariosPreferenciaisCarona(idcarona3).size());
		}catch(Exception e){
			fail();
		}
	}
	
	public void comparaListas(List<CaronaDomain> lista, List<CaronaDomain> listaComp){ 
		if(lista.size() == listaComp.size()){
			int length = lista.size();
			CaronaDomain carona;
			for (int i = 0; i < length; i++) {
				carona = null;
				for (CaronaDomain caronaDomain : listaComp) {
					if(lista.get(i).getId().equals(caronaDomain.getId())){
						carona = caronaDomain;
					}
				}
				if(carona == null){
					fail();
				}
				assertEquals(lista.get(i).getId(), carona.getId());
				assertEquals(lista.get(i).getOrigem(), carona.getOrigem());
				assertEquals(lista.get(i).getDestino(), carona.getDestino());
				assertEquals(lista.get(i).getCidade(), carona.getCidade());
				assertEquals(lista.get(i).getHora(), carona.getHora());
				assertEquals(lista.get(i).getData(), carona.getData());
				assertEquals(lista.get(i).getDataVolta(), carona.getDataVolta());
				assertEquals(lista.get(i).getVagas(), carona.getVagas());
				assertEquals(lista.get(i).getMinimoCaroneiros(), carona.getMinimoCaroneiros());
				assertEquals(lista.get(i).isCaronaRelampagoExpirada(), carona.isCaronaRelampagoExpirada());
				assertEquals(lista.get(i).isPreferencial(), carona.isPreferencial());
				assertEquals(lista.get(i).getTipoCarona(), carona.getTipoCarona());
			}
		}else{
			fail();
		}
	}
	
}
