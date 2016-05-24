package br.com.caelum.cursoci.banco.tributacao;

import java.math.BigDecimal;

import br.com.caelum.cursoci.banco.modelo.Conta;

/**
 * Aplica uma certa taxa de juros ao valor negativo do saldo da Conta. 
 */
public class DescontaJuros implements Tributo {

	private final BigDecimal taxaDeJuros;
	public DescontaJuros(BigDecimal taxaDeJuros) {
		this.taxaDeJuros = taxaDeJuros;
	}
	
	public BigDecimal calculaDesconto(Conta conta) {
		BigDecimal desconto = BigDecimal.ZERO;
		
		if (conta.estaNegativa()) {
			BigDecimal valorEmprestado = conta.getSaldo().abs();
			desconto = valorEmprestado.multiply(taxaDeJuros);
		}
		
		return desconto;
	}
}
