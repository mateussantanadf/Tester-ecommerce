package homepage;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import base.BaseTests;
import pages.CarrinhoPage;
import pages.CheckoutPage;
import pages.LoginPage;
import pages.ModalProdutoPage;
import pages.PedidoPage;
import pages.ProdutoPage;
import util.Funcoes;

public class HomePageTests extends BaseTests {

	@Test
	public void testContarProdutos_oitoProdutosDiferentes() {
		carregarPaginaInicial();
		assertThat(homePage.contarProdutos(), is(8));
	}

	@Test
	public void testValidarCarrinhoZerado_ZeroItensCarrinho() {
		int produtosNoCarrinho = homePage.obterQuantidadeProdutosNoCarrinho();
		assertThat(produtosNoCarrinho, is(0));
	}

	ProdutoPage produtoPage;
	String nomeProduto_ProdutoPage;

	@Test
	public void testValidarDetalhesDoProduto_DescricaoEValorIguais() {
		int indice = 0;
		String nomeProduto_HomePage = homePage.obterNomeProduto(indice);
		String precoProduto_HomePage = homePage.obterPrecoProduto(indice);

		System.out.println(nomeProduto_HomePage);
		System.out.println(precoProduto_HomePage);

		produtoPage = homePage.clicarProduto(indice);

		nomeProduto_ProdutoPage = produtoPage.obterNomeProduto();
		String precoProduto_ProdutoPage = produtoPage.obterPrecoProduto();

		System.out.println(nomeProduto_ProdutoPage);
		System.out.println(precoProduto_ProdutoPage);

		assertThat(nomeProduto_HomePage.toUpperCase(), is(nomeProduto_ProdutoPage.toUpperCase()));
		assertThat(precoProduto_HomePage, is(precoProduto_ProdutoPage));
	}

	LoginPage loginPage;

	@Test
	public void testLoginComSucesso_UsuarioLogado() {
		// Clicar no botão Sign In na home page
		loginPage = homePage.clicarBotaoSignIn();

		// Preencher usuario e senha
		loginPage.preeencherEmail("leste@teste.com");
		loginPage.preencherPassword("123456");

		// Clicar no botão Sign In para Logar
		loginPage.clicarBotaoSignIn();

		// Validar se o usuario está logado
		assertThat(homePage.estaLogado("Mateus Leste"), is(true));

		carregarPaginaInicial();
	}

	@ParameterizedTest
	@CsvFileSource(resources = "/massaTeste_Login.csv", numLinesToSkip = 1, delimiter = ',')
	public void testLogin_UsuarioLogadoComDadosValidos(String nomeTeste, String email, String password, String nomeUsuario,
			String resultado) {
		// Clicar no botão Sign In na home page
		loginPage = homePage.clicarBotaoSignIn();

		// Preencher usuario e senha
		loginPage.preeencherEmail(email);
		loginPage.preencherPassword(password);

		// Clicar no botão Sign In para Logar
		loginPage.clicarBotaoSignIn();
		
		boolean esperado_loginOk;
		if (resultado.equals("positivo"))
			esperado_loginOk = true;
		else
			esperado_loginOk = false;

		// Validar se o usuario está logado
		assertThat(homePage.estaLogado(nomeUsuario), is(esperado_loginOk));
		
		capturarTela(nomeTeste, resultado);
		
		if (esperado_loginOk)
			homePage.clicarBotaoSignOut();

		carregarPaginaInicial();
	}

	ModalProdutoPage modalProdutoPage;

	@Test
	public void testIncluirProdutoNoCarrinho_ProdutoIncluidoComSucesso() {

		String tamanhoProduto = "M";
		String corProduto = "Black";
		int quantidadeProduto = 2;

		// --Pré-cindição
		// usuário logado
		if (!homePage.estaLogado("Mateus Leste")) {
			testLoginComSucesso_UsuarioLogado();
		}

		// --Teste
		// Selecionando produto
		testValidarDetalhesDoProduto_DescricaoEValorIguais();

		// Selecionar tamanho
		List<String> listaOpcoes = produtoPage.obterOpcoesSelecionadas();

		System.out.println(listaOpcoes.get(0));
		System.out.println("Tamanho da lista: " + listaOpcoes.size());

		produtoPage.selecionarOpcaoDropDown(tamanhoProduto);
		listaOpcoes = produtoPage.obterOpcoesSelecionadas();

		System.out.println(listaOpcoes.get(0));
		System.out.println("Tamanho da lista: " + listaOpcoes.size());

		// Selecionar cor
		produtoPage.selecionarCorPreta();

		// Selecionar quantidade
		produtoPage.alterarQuantidade(quantidadeProduto);

		// Adicionar no carrinho
		modalProdutoPage = produtoPage.clicarBotaoAddToCart();

		// Validações

		assertTrue(modalProdutoPage.obterMensagemProdutoAdicionado()
				.endsWith("Product successfully added to your shopping cart"));

		assertThat(modalProdutoPage.obterDescricaoProduto().toUpperCase(), is(nomeProduto_ProdutoPage.toUpperCase()));

		String precoProdutoString = modalProdutoPage.obterPrecoProduto();
		precoProdutoString = precoProdutoString.replace("$", "");
		Double precoProduto = Double.parseDouble(precoProdutoString);

		assertThat(modalProdutoPage.obterTamanhoProduto(), is(tamanhoProduto));
		assertThat(modalProdutoPage.obterCorProduto(), is(corProduto));
		assertThat(modalProdutoPage.obterQuantidadeProduto(), is(Integer.toString(quantidadeProduto)));

		String subtotalString = modalProdutoPage.obterSubtotal();
		subtotalString = subtotalString.replace("$", "");
		Double subtotal = Double.parseDouble(subtotalString);

		Double subtotalCalculado = quantidadeProduto * precoProduto;

		assertThat(subtotal, is(subtotalCalculado));

	}

	// Valores esperados

	String esperado_nomeProduto = "Hummingbird printed t-shirt";
	Double esperado_precoProduto = 19.12;
	String esperado_tamanhoProduto = "M";
	String esperado_corProduto = "Black";
	int esperado_input_quantidadeProduto = 2;
	Double esperado_subtotalProduto = esperado_precoProduto * esperado_input_quantidadeProduto;

	int esperado_numeroItensTotal = esperado_input_quantidadeProduto;
	Double esperado_subtotalTotal = esperado_subtotalProduto;
	Double esperado_shippingTotal = 7.00;
	Double esperado_totalTaxExclTotal = esperado_subtotalTotal + esperado_shippingTotal;
	Double esperado_totalTaxInclTotal = esperado_totalTaxExclTotal;
	Double esperado_taxesTotal = 0.00;

	String esperado_nomeCliente = "Mateus Leste";

	CarrinhoPage carrinhoPage;

	@Test
	public void testIrParaCarrinho_InformacoesPersistidas() {
		// --Pré-condições
		// Produto incluído na tela ModalProdutoPage
		testIncluirProdutoNoCarrinho_ProdutoIncluidoComSucesso();

		carrinhoPage = modalProdutoPage.clicarBotaoProceedTpCheckuot();

		// Teste

		// Validar todos elementos da tela
		System.out.println("---------Itens do Carrinho--------");
		System.out.println(carrinhoPage.obter_nomeProduto());
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_precoProduto()));
		System.out.println(carrinhoPage.obter_tamanhoProduto());
		System.out.println(carrinhoPage.obter_corProduto());
		System.out.println(carrinhoPage.obter_input_quantidadeProduto());
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_subtotalProduto()));

		System.out.println("--------Itens de Totais---------");
		System.out.println(Funcoes.removeTextoItemsDevolveInt(carrinhoPage.obter_numeroItensTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_subtotalTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_shippingTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_totalTaxExclTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_totalTaxInclTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_taxesTotal()));

		// Asserções Hamcrest
		assertThat(carrinhoPage.obter_nomeProduto(), is(esperado_nomeProduto));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_precoProduto()), is(esperado_precoProduto));
		assertThat(carrinhoPage.obter_tamanhoProduto(), is(esperado_tamanhoProduto));
		assertThat(carrinhoPage.obter_corProduto(), is(esperado_corProduto));
		assertThat(Integer.parseInt(carrinhoPage.obter_input_quantidadeProduto()),
				is(esperado_input_quantidadeProduto));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_subtotalProduto()),
				is(esperado_subtotalProduto));

		assertThat(Funcoes.removeTextoItemsDevolveInt(carrinhoPage.obter_numeroItensTotal()),
				is(esperado_numeroItensTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_subtotalTotal()), is(esperado_subtotalTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_shippingTotal()), is(esperado_shippingTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_totalTaxExclTotal()),
				is(esperado_totalTaxExclTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_totalTaxInclTotal()),
				is(esperado_totalTaxInclTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_taxesTotal()), is(esperado_taxesTotal));

		// Asserções JUnit
		/*
		 * assertEquals(esperado_nomeProduto, carrinhoPage.obter_nomeProduto());
		 */
	}

	CheckoutPage checkoutPage;

	@Test
	public void testIrParaCheckout_FreteMeioPagamentoEnderecoListadosOk() {
		// Pré-condições

		// Produto disponível no carrinho de compras
		testIrParaCarrinho_InformacoesPersistidas();

		// Teste

		// Clicar no botão
		checkoutPage = carrinhoPage.clicarBotaoProceedToCheckuot();

		// Preencher infomações

		// Validar Informções na tela
		assertThat(Funcoes.removeCifraoDevolveDouble(checkoutPage.obter_totalTaxIncTotal()),
				is(esperado_totalTaxInclTotal));
		// assertThat(checkoutPage.obter_nomeCliente(), is(esperado_nomeCliente));
		assertTrue(checkoutPage.obter_nomeCliente().startsWith(esperado_nomeCliente));

		checkoutPage.clicarBotaoContinueAddress();

		String encontrado_shippingValor = checkoutPage.obter_shippingValor();
		encontrado_shippingValor = Funcoes.removeTexto(encontrado_shippingValor, " tax excl.");
		Double encotrado_shippingValor_Double = Funcoes.removeCifraoDevolveDouble(encontrado_shippingValor);
		assertThat(encotrado_shippingValor_Double, is(esperado_shippingTotal));

		checkoutPage.clicarBotaoContinueShipping();

		// Selecionar opção "Pay by Chack"
		checkoutPage.selecionarRadioPayByCheck();
		// Validar valor do cheque (amount)
		String encontrado_amountPayByCheck = checkoutPage.obter_amountPayByCheck();
		encontrado_amountPayByCheck = Funcoes.removeTexto(encontrado_amountPayByCheck, " (tax incl.)");
		Double encontrado_amountPayByCheck_Double = Funcoes.removeCifraoDevolveDouble(encontrado_amountPayByCheck);
		assertThat(encontrado_amountPayByCheck_Double, is(esperado_totalTaxInclTotal));
		// Clicar na opção "I agree"
		checkoutPage.selecionarCheckboxIAgree();
		assertTrue(checkoutPage.estaSelecioinadoCheckboxIAgree());

	}

	@Test
	public void testFinalizarPedido_pedidoFinalizadoComSucesso() {
		// Pré-condições
		// Checkout completamente concluído
		testIrParaCheckout_FreteMeioPagamentoEnderecoListadosOk();

		// Teste
		// Clicar no botão para confirmar o pedido
		PedidoPage pedidoPage = checkoutPage.clicarBotaoConfirmaPedido();

		// Validar valores da tela
		assertTrue(pedidoPage.obter_textoPedidoConfirmado().endsWith("YOUR ORDER IS CONFIRMED"));
		// assertThat(pedidoPage.obter_textoPedidoConfirmado().toUpperCase(), is("YOUR
		// ORDER IS CONFIRMED"));

		assertThat(pedidoPage.obter_email(), is("leste@teste.com"));

		assertThat(pedidoPage.obter_totalProdutos(), is(esperado_subtotalProduto));

		assertThat(pedidoPage.obter_totalTaxIncl(), is(esperado_totalTaxInclTotal));

		assertThat(pedidoPage.obter_metodoPagamento(), is("check"));
	}

}
