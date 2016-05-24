package br.com.caelum.cursoci.banco.integracao;

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

public class CadastroDeContaTest {
 
	private static WebDriver driver;

	@BeforeClass
	public static void iniciaNavegador() {
		DesiredCapabilities dc = new DesiredCapabilities();
		dc.setJavascriptEnabled(true);
		dc.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "/home/romulo/java/phantomjs-2.1.1-linux-x86_64/bin/phantomjs");
		driver = new PhantomJSDriver(dc);
	}
	
	@Test
	public void contaFoiCadastrada() {
		
		// preenche formulurio de cadastro
		driver.navigate().to("http://localhost:8080/banco2/conta/formulario");
		
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
		driver.navigate().to("http://localhost:8080/banco2/conta/lista");
		driver.findElement(By.xpath("//td[text()='" + nome + "']"));
		driver.findElement(By.xpath("//td[text()='" + numero + "']"));
	}
	
	
	@AfterClass
	public static void desligaNavegador() {
		driver.quit();
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
