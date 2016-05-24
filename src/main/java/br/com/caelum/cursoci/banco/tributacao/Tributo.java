package br.com.caelum.cursoci.banco.tributacao;

import java.math.BigDecimal;

import br.com.caelum.cursoci.banco.modelo.Conta;

public interface Tributo {

	/**
	 * Calcula o desconto que deve ser aplicado a essa Conta
	 * @param conta
	 * @return Desconto a ser efetuado
	 */
	public abstract BigDecimal calculaDesconto(Conta conta);

}