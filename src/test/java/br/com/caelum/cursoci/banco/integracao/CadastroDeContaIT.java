package br.com.caelum.cursoci.banco.integracao;

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

@RunWith(Arquillian.class)
public class CadastroDeContaIT {

	private static final String HTTP_LOCALHOST_8888 = "http://localhost:8888";
	private WebDriver driver;

	@Deployment
	public static WebArchive createWar() {

		MavenDependencyResolver resolver = DependencyResolvers
				.use(MavenDependencyResolver.class)
				.loadMetadataFromPom("pom.xml");

		WebArchive webArchive = ShrinkWrap
				.create(WebArchive.class, "ROOT.war")
				.addPackages(true, "br.com.caelum.cursoci.banco")
				.addAsLibraries(
						resolver.artifact("br.com.caelum:vraptor")
								.artifact("javax.servlet:jstl")
								.resolveAs(GenericArchive.class))
				.as(ExplodedImporter.class)
				.importDirectory(new File("src/main/webapp"))
				.as(WebArchive.class);

		return webArchive;
	}

	@Before
	public void iniciaNavegador() {
		DesiredCapabilities dc = new DesiredCapabilities();
		dc.setJavascriptEnabled(true);
		dc.setCapability(
				PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
				System.getProperty("phantomjs.path"));
		driver = new PhantomJSDriver(dc);
	}

	@After
	public void desligaNavegador() {
		driver.quit();
	}
	
	@Test
	public void contaFoiCadastrada() {

		// preenche formulario de cadastro
		driver.navigate().to(HTTP_LOCALHOST_8888 + "/conta/formulario");

		WebElement campoCliente = driver.findElement(By.name("cliente"));
		WebElement campoCpf = driver.findElement(By.name("cpf"));

		String nome = "Joaquim Manoel";

		campoCliente.sendKeys(nome);
		campoCpf.sendKeys("123.456.789-10");

		campoCliente.submit();

		// verifica a pagina de OK
		String titulo = driver.findElement(By.tagName("h1")).getText();
		assertThat(titulo, containsString("sucesso"));

		Integer numero = extraiNumero(titulo);

		// navega pra pagina da Conta
		driver.findElement(By.tagName("a")).click();
		titulo = driver.findElement(By.tagName("h1")).getText();

		assertThat(titulo, containsString(nome));
		assertThat(titulo, containsString(numero.toString()));

		// verifica listagem
		driver.navigate().to(HTTP_LOCALHOST_8888 + "/conta/lista");
		driver.findElement(By.xpath("//td[text()='" + nome + "']"));
		driver.findElement(By.xpath("//td[text()='" + numero + "']"));
	}

	// auxiliar
	private int extraiNumero(String titulo) {
		Matcher matcher = Pattern.compile("\\d+").matcher(titulo);
		if (matcher.find()) {
			return Integer.parseInt(matcher.group());
		}
		throw new RuntimeException("Nao achei numero");
	}
}
